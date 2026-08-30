pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        // Backend
        DOCKER_IMAGE_BACKEND = "lindseyeloi/mon-app-springboot"
        DOCKER_TAG_BACKEND = "${env.BUILD_NUMBER}"
        APP_PORT_BACKEND = '9091'
        DB_PORT = '5432'
        DB_NAME = 'centre_medical'
        BACKEND_CONTAINER = 'backend-app'
        DB_CONTAINER = 'postgres-db'
        NETWORK_NAME = 'app-network'

        // Frontend Swing
        DOCKER_IMAGE_FRONTEND = "lindseyeloi/centre-medical-swing"
        DOCKER_TAG_FRONTEND = "${env.BUILD_NUMBER}"
        APP_PORT_FRONTEND = '8082'
        FRONTEND_CONTAINER = 'swing-app'
    }

    stages {
        stage('Checkout Backend') {
            steps {
                echo "=== Récupération du backend ==="
                checkout scm
            }
        }

        stage('Checkout Frontend') {
            steps {
                echo "=== Récupération du frontend ==="
                dir('frontend') {
                    git branch: 'master',
                        url: 'https://github.com/LindseyEloi/Gloga-frontend.git'
                }
            }
        }

        stage('Build Backend') {
            steps {
                echo "=== Build du backend ==="
                sh 'mvn clean package'
            }
        }

        stage('Build Frontend Swing') {
            steps {
                echo "=== Build du frontend Swing ==="
                dir('frontend') {
                    sh 'mvn clean package'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "=== Login Docker Hub ==="
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                        echo "=== Build image backend ==="
                        docker build -t ${DOCKER_IMAGE_BACKEND}:${DOCKER_TAG_BACKEND} .
                        docker tag ${DOCKER_IMAGE_BACKEND}:${DOCKER_TAG_BACKEND} ${DOCKER_IMAGE_BACKEND}:latest
                    '''
                }

                echo "=== Build image frontend Swing avec VNC ==="
                dir('frontend') {
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh '''
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                            docker build -t ${DOCKER_IMAGE_FRONTEND}:${DOCKER_TAG_FRONTEND} .
                            docker tag ${DOCKER_IMAGE_FRONTEND}:${DOCKER_TAG_FRONTEND} ${DOCKER_IMAGE_FRONTEND}:latest
                        '''
                    }
                }
            }
        }

        stage('Push to Registry') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "=== Login Docker Hub ==="
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                        echo "=== Push image backend ==="
                        docker push ${DOCKER_IMAGE_BACKEND}:${DOCKER_TAG_BACKEND}
                        docker push ${DOCKER_IMAGE_BACKEND}:latest
                        echo "✅ Backend poussé avec succès"

                        echo "=== Push image frontend Swing (avec retry) ==="

                        # Push avec retry pour l'image frontend
                        MAX_RETRIES=3
                        RETRY_COUNT=0
                        PUSH_SUCCESS=false

                        while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
                            echo "Tentative $((RETRY_COUNT+1)) de push..."

                            if docker push ${DOCKER_IMAGE_FRONTEND}:${DOCKER_TAG_FRONTEND}; then
                                echo "✅ Push frontend réussi !"
                                PUSH_SUCCESS=true
                                break
                            else
                                echo "❌ Tentative $((RETRY_COUNT+1)) échouée"
                                RETRY_COUNT=$((RETRY_COUNT+1))

                                if [ $RETRY_COUNT -lt $MAX_RETRIES ]; then
                                    echo "Attente de 15 secondes avant la prochaine tentative..."
                                    sleep 15
                                fi
                            fi
                        done

                        if [ "$PUSH_SUCCESS" = true ]; then
                            echo "=== Push latest tag ==="
                            docker push ${DOCKER_IMAGE_FRONTEND}:latest
                            echo "✅ Frontend poussé avec succès"
                        else
                            echo "⚠️ Échec du push frontend après $MAX_RETRIES tentatives"
                            echo "Utilisation de l'image locale pour le déploiement"
                        fi
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'postgres-creds',
                    usernameVariable: 'DB_USER',
                    passwordVariable: 'DB_PASSWORD'
                )]) {
                    sh '''
                        echo "=== Nettoyage ==="
                        docker stop ${BACKEND_CONTAINER} 2>/dev/null || true
                        docker rm ${BACKEND_CONTAINER} 2>/dev/null || true
                        docker stop ${FRONTEND_CONTAINER} 2>/dev/null || true
                        docker rm ${FRONTEND_CONTAINER} 2>/dev/null || true
                        docker stop ${DB_CONTAINER} 2>/dev/null || true
                        docker rm ${DB_CONTAINER} 2>/dev/null || true

                        echo "=== Création du réseau ==="
                        docker network create ${NETWORK_NAME} 2>/dev/null || true

                        echo "=== Démarrage PostgreSQL ==="
                        docker run -d \
                            --name ${DB_CONTAINER} \
                            --network ${NETWORK_NAME} \
                            -e POSTGRES_DB=${DB_NAME} \
                            -e POSTGRES_USER=${DB_USER} \
                            -e POSTGRES_PASSWORD=${DB_PASSWORD} \
                            -p ${DB_PORT}:5432 \
                            -v pgdata:/var/lib/postgresql/data \
                            --restart unless-stopped \
                            postgres:16-alpine

                        echo "=== Attente PostgreSQL ==="
                        sleep 15

                        echo "=== Démarrage backend ==="
                        docker run -d \
                            --name ${BACKEND_CONTAINER} \
                            --network ${NETWORK_NAME} \
                            -e DB_HOST=${DB_CONTAINER} \
                            -e DB_PORT=5432 \
                            -e DB_NAME=${DB_NAME} \
                            -e DB_USER=${DB_USER} \
                            -e DB_PASSWORD=${DB_PASSWORD} \
                            -p ${APP_PORT_BACKEND}:8080 \
                            --restart unless-stopped \
                            ${DOCKER_IMAGE_BACKEND}:${DOCKER_TAG_BACKEND}

                        echo "=== Démarrage frontend Swing avec VNC ==="
                        docker run -d \
                            --name ${FRONTEND_CONTAINER} \
                            --network ${NETWORK_NAME} \
                            -p ${APP_PORT_FRONTEND}:8080 \
                            --restart unless-stopped \
                            ${DOCKER_IMAGE_FRONTEND}:latest

                        echo "=== Conteneurs ==="
                        docker ps
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    echo "=== Attente ==="
                    sleep 10

                    echo "=== Test backend ==="
                    curl -f http://localhost:${APP_PORT_BACKEND}/ || true

                    echo "=== Test frontend Swing VNC ==="
                    curl -f http://localhost:${APP_PORT_FRONTEND}/vnc.html || true

                    echo "=== Logs backend ==="
                    docker logs ${BACKEND_CONTAINER} --tail 20

                    echo "=== Logs frontend Swing ==="
                    docker logs ${FRONTEND_CONTAINER} --tail 20
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline réussi !"
            echo "Backend API: http://localhost:${APP_PORT_BACKEND}"
            echo "Frontend Swing: http://localhost:${APP_PORT_FRONTEND}/vnc.html"
        }
        failure {
            echo "❌ Échec"
        }
        always {
            sh 'docker image prune -f || true'
        }
    }
}
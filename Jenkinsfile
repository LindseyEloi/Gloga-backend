pipeline {
    agent any

    tools { maven 'Maven' }

    environment {
        DOCKER_HUB_USER = 'lindseyeloi'
        BACKEND_IMAGE = "${DOCKER_HUB_USER}/centre-medical-backend"
        FRONTEND_IMAGE = "${DOCKER_HUB_USER}/centre-medical-client"
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        APP_PORT = '9091'          // Port exposé pour le backend
        FRONTEND_PORT = '9092'     // Port exposé pour le frontend (éviter conflits)
        DB_PORT = '5432'
    }

    stages {
        stage('Checkout') {
            steps {
                // On suppose que le dépôt contient les deux dossiers
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                dir('centre-medical-backend') {
                    sh 'mvn clean package'
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('centre-medical-client') {
                    sh 'mvn clean package'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                        # Backend
                        docker build -t ${BACKEND_IMAGE}:${DOCKER_TAG} ./centre-medical-backend
                        docker tag ${BACKEND_IMAGE}:${DOCKER_TAG} ${BACKEND_IMAGE}:latest

                        # Frontend
                        docker build -t ${FRONTEND_IMAGE}:${DOCKER_TAG} ./centre-medical-client
                        docker tag ${FRONTEND_IMAGE}:${DOCKER_TAG} ${FRONTEND_IMAGE}:latest
                    '''
                }
            }
        }

        stage('Push to Registry') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${BACKEND_IMAGE}:${DOCKER_TAG}
                        docker push ${BACKEND_IMAGE}:latest
                        docker push ${FRONTEND_IMAGE}:${DOCKER_TAG}
                        docker push ${FRONTEND_IMAGE}:latest
                    '''
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'postgres-creds', usernameVariable: 'DB_USER', passwordVariable: 'DB_PASSWORD')]) {
                    sh '''
                        # Nettoyage
                        docker stop backend-app frontend-app postgres-db 2>/dev/null || true
                        docker rm backend-app frontend-app postgres-db 2>/dev/null || true
                        docker network create app-network 2>/dev/null || true

                        # Démarrer PostgreSQL
                        docker run -d --name postgres-db --network app-network \
                            -e POSTGRES_DB=centre_medical \
                            -e POSTGRES_USER=${DB_USER} \
                            -e POSTGRES_PASSWORD=${DB_PASSWORD} \
                            -p ${DB_PORT}:5432 \
                            -v pgdata:/var/lib/postgresql/data \
                            --restart unless-stopped \
                            postgres:16-alpine

                        sleep 15

                        # Démarrer backend
                        docker run -d --name backend-app --network app-network \
                            -e DB_HOST=postgres-db -e DB_PORT=5432 -e DB_NAME=centre_medical \
                            -e DB_USER=${DB_USER} -e DB_PASSWORD=${DB_PASSWORD} \
                            -p ${APP_PORT}:8080 \
                            --restart unless-stopped \
                            ${BACKEND_IMAGE}:${DOCKER_TAG}

                        # Démarrer frontend (en supposant qu'il écoute sur 8081 en interne)
                        docker run -d --name frontend-app --network app-network \
                            -e BACKEND_URL=http://backend-app:8080/api \
                            -p ${FRONTEND_PORT}:8081 \
                            --restart unless-stopped \
                            ${FRONTEND_IMAGE}:${DOCKER_TAG}

                        docker ps
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    sleep 20
                    echo "Backend :"
                    curl -f http://localhost:${APP_PORT}/ || true
                    echo "\nFrontend :"
                    curl -f http://localhost:${FRONTEND_PORT}/ || true
                    docker logs backend-app --tail 20
                    docker logs frontend-app --tail 20
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline réussi !"
            echo "Backend : http://localhost:${APP_PORT}"
            echo "Frontend : http://localhost:${FRONTEND_PORT}"
        }
        failure {
            echo "❌ Échec"
        }
        always {
            sh 'docker image prune -f || true'
        }
    }
}
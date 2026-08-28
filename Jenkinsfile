pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        DOCKER_IMAGE = "lindseyeloi/mon-app-springboot"
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        APP_PORT = '9091'  // Port externe pour l'application
        DB_PORT = '5432'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                        docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                    '''
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
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker push ${DOCKER_IMAGE}:latest
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
                        echo "=== Nettoyage des anciens conteneurs ==="
                        docker stop backend-app 2>/dev/null || true
                        docker rm backend-app 2>/dev/null || true
                        docker stop postgres-db 2>/dev/null || true
                        docker rm postgres-db 2>/dev/null || true

                        echo "=== Création du réseau ==="
                        docker network create app-network 2>/dev/null || true

                        echo "=== Démarrage de PostgreSQL ==="
                        docker run -d \
                            --name postgres-db \
                            --network app-network \
                            -e POSTGRES_DB=centre_medical \
                            -e POSTGRES_USER=${DB_USER} \
                            -e POSTGRES_PASSWORD=${DB_PASSWORD} \
                            -p ${DB_PORT}:5432 \
                            -v pgdata:/var/lib/postgresql/data \
                            --restart unless-stopped \
                            postgres:16-alpine

                        echo "=== Attente du démarrage de PostgreSQL ==="
                        sleep 15

                        echo "=== Démarrage de l'application sur le port ${APP_PORT} ==="
                        docker run -d \
                            --name backend-app \
                            --network app-network \
                            -e DB_HOST=postgres-db \
                            -e DB_PORT=5432 \
                            -e DB_NAME=centre_medical \
                            -e DB_USER=${DB_USER} \
                            -e DB_PASSWORD=${DB_PASSWORD} \
                            -e SERVER_PORT=8080 \
                            -p ${APP_PORT}:8080 \
                            --restart unless-stopped \
                            ${DOCKER_IMAGE}:${DOCKER_TAG}

                        echo "=== Conteneurs en cours d'exécution ==="
                        docker ps
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    echo "=== Attente du démarrage de l'application ==="
                    sleep 20

                    echo "=== Test de l'application sur le port ${APP_PORT} ==="
                    curl -f http://localhost:${APP_PORT}/ || true

                    echo "=== Logs de l'application ==="
                    docker logs backend-app --tail 50
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline terminé avec succès !"
            echo "📱 Application déployée sur http://localhost:${APP_PORT}"
            echo "🐘 PostgreSQL sur localhost:${DB_PORT}"
            echo "🏗️  Image Docker: ${DOCKER_IMAGE}:${DOCKER_TAG}"
        }
        failure {
            echo "❌ Le pipeline a échoué."
            echo "Vérifiez les logs pour plus de détails."
        }
        always {
            // Nettoyer les anciennes images Docker
            sh 'docker image prune -f || true'
        }
    }
}
pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        DOCKER_IMAGE = "lindseyeloi/mon-app-springboot"
        DOCKER_TAG = "${env.BUILD_NUMBER}"
        APP_PORT = '8081'  // Port externe pour l'application
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
                        # Nettoyer les anciens conteneurs
                        docker stop backend-app 2>/dev/null || true
                        docker rm backend-app 2>/dev/null || true
                        docker stop postgres-db 2>/dev/null || true
                        docker rm postgres-db 2>/dev/null || true

                        # Créer le réseau
                        docker network create app-network 2>/dev/null || true

                        # Démarrer PostgreSQL
                        docker run -d \
                            --name postgres-db \
                            --network app-network \
                            -e POSTGRES_DB=centre_medical \
                            -e POSTGRES_USER=${DB_USER} \
                            -e POSTGRES_PASSWORD=${DB_PASSWORD} \
                            -p 5432:5432 \
                            -v pgdata:/var/lib/postgresql/data \
                            --restart unless-stopped \
                            postgres:16-alpine

                        # Attendre que PostgreSQL soit prêt
                        echo "Attente du démarrage de PostgreSQL..."
                        sleep 15

                        # Démarrer l'application sur le port 8081
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

                        # Vérifier les conteneurs
                        echo "Conteneurs en cours d'exécution :"
                        docker ps
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    # Attendre que l'application démarre
                    sleep 20

                    # Vérifier que l'application répond
                    echo "Test de l'application..."
                    curl -f http://localhost:8081/actuator/health || curl -f http://localhost:8081/ || true

                    # Voir les logs
                    echo "Logs de l'application :"
                    docker logs backend-app --tail 30
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline terminé avec succès !"
            echo "Application déployée sur http://localhost:8081"
        }
        failure {
            echo "❌ Le pipeline a échoué."
        }
        always {
            // Nettoyer les anciennes images
            sh 'docker image prune -f || true'
        }
    }
}
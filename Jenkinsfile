pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        DOCKER_IMAGE = "lindseyeloi/mon-app-springboot"
        DOCKER_TAG = "${env.BUILD_NUMBER}"
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
                        # Login à Docker Hub AVANT le build
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                        # Build de l'image
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
                        # Login à Docker Hub AVANT le push
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                        # Push des images
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
                        # Arrêter les anciens conteneurs
                        docker compose down || true

                        # Pull la nouvelle image
                        docker compose pull || true

                        # Démarrer avec les nouvelles images
                        DB_PASSWORD="$DB_PASSWORD" docker compose up -d
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline terminé avec succès ! Image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
        }
        failure {
            echo 'Le pipeline a échoué.'
        }
        always {
            // Nettoyer les anciennes images
            sh 'docker image prune -f || true'
        }
    }
}
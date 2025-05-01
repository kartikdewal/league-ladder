pipeline {
    agent any

        environment {
            MAVEN_HOME = tool 'Maven'
            JAVA_HOME = tool 'JDK 21'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checkout GitHub repository..."
                checkout([$class: 'GitSCM', branches: [[name: '*/main']], userRemoteConfigs: [[url: 'git@github.com:kartikdewal/league-ladder.git']]])
            }
        }

        stage('OWASP Dependency-Check') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn org.owasp:dependency-check-maven:check"
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "${MAVEN_HOME}/bin/mvn sonar:sonar"
                }
            }
        }

        stage('Test') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn test"
            }
        }

        stage('Install Trivy') {
            steps {
                sh '''
                curl -sfL https://aquasecurity.github.io/trivy-repo/install.sh | sh
                '''
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build('league-ladder:latest', '.')
                }
            }
        }

        stage('Scan Docker Image') {
            steps {
                sh '''
                trivy image --exit-code 1 --severity CRITICAL,HIGH league-ladder:latest
                '''
            }
        }


        stage('Package') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean package"
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying the app..."
            }
        }
    }

    post {
        success {
            echo 'Build and deployment completed successfully!'
        }
        failure {
            echo 'Build or deployment failed!'
        }
    }
}
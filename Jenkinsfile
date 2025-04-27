pipeline {
    agent any

        environment {
            MAVEN_HOME = tool 'Maven'
            JAVA_HOME = tool 'JDK 21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean compile"
            }
        }

        stage('Test') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn test"
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "${MAVEN_HOME}/bin/mvn sonar:sonar"
                }
            }
        }

        stage('OWASP Dependency-Check') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn org.owasp:dependency-check-maven:check"
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
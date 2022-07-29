pipeline {
    agent any
    triggers {
        pollSCM "H/01 * * * *"
    }
    stages {
        stage('Building') {
            steps {
                echo '=== Building ==='
                bat 'mvn -B -DskipTests clean package'
            }
        }
        stage('Unit Testing') {
            steps {
                echo '=== Unit Testing ==='
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Integration Testing') {
            steps {
                echo '=== Integration Testing ==='
                bat 'mvn failsafe:integration-test'
            }
        }
    }
}
pipeline {
    agent any
       triggers {
        pollSCM "H/01 * * * *"
       }
    stages {
        stage('Building') {
            steps {
                echo '=== Building ==='
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Unit Testing') {
            steps {
                echo '=== Unit Testing ==='
                sh 'mvn test'
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
                sh 'mvn failsafe:integration-test'
            }
        }
    }
}
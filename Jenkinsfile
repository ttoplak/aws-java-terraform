pipeline {
    agent any
    tools {
        jdk 'jdk-11.0.2'
        maven 'Maven 3.8.6'
    }
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
        stage('Initialize & validate IaC') {
            steps {
                echo '=== Validating IaC ==='
                bat 'tf init'
                bat 'tf validate'
            }
        }
        stage('Deploying code') {
            steps {
                echo '=== Deploying to AWS ==='
                bat 'tf apply'
            }
        }
    }
}
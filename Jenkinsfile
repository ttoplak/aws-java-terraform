pipeline {
    agent any
    tools {
        jdk 'jdk-11.0.2'
        maven 'Maven 3.8.6'
        terraform 'terraform-1.2.5'
        nodejs 'node-16.16.0'
    }
    triggers {
        pollSCM "H/02 * * * *"
    }
    stages {
        stage('Building') {
            steps {
                echo 'npm list -g'
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
                bat 'terraform init'
                bat 'terraform validate'
            }
        }
        stage('Deploying code') {
            steps {
                echo '=== Deploying to AWS ==='
                withCredentials([[
                                         $class           : 'AmazonWebServicesCredentialsBinding',
                                         credentialsId    : 'a88038e1-d328-4aaa-af47-1efd1717e0d3',
                                         accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                                         secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                                 ]]) {
                    bat 'terraform apply --auto-approve'
                }
            }
        }
        stage('E2E test deployed code') {
            steps {
                echo '=== E2E testing AWS code ==='
                bat 'newman run AWS_Playground.postman_collection.json'
            }
        }
    }
}
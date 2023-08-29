PROJECT_VERSION = "1.0.0-SNAPSHOT"
BUILD_VERSION = ""

pipeline {
    agent { label 'jenkins-agent1' }

    stages {
        stage('Generate Version') {
            steps {
                script {
                    BUILD_VERSION = "${PROJECT_VERSION}-${env.BUILD_NUMBER}"
                    println("PROJECT_VERSION: " + PROJECT_VERSION)
                    println("BUILD_VERSION: " + BUILD_VERSION)
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    println("Building project version: " + BUILD_VERSION)
                    sh './mvnw -DskipTests clean package'
                }
            }
        }
    }
}
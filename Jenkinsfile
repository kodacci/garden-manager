pipeline {
    agent { label 'jenkins-agent1' }

    stages {
        stage('Determine Version') {
            steps {
                script {
                    withMaven {
                        PROJECT_VERSION = sh 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout'
                        BUILD_VERSION = "${PROJECT_VERSION}-${env.BUILD_NUMBER}"
                        println("PROJECT_VERSION: " + PROJECT_VERSION)
                        println("BUILD_VERSION: " + BUILD_VERSION)
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    println("Building project version: " + BUILD_VERSION)

                    withMaven {
                        sh 'mvn -DskipTests clean package'
                    }

                    println("Build finished")
                }
            }
        }

        stage('Test') {
            steps: {
                script {
                    println("Starting build verification")
                    withMaven {
                        sh 'mvn verify'
                    }
                    println("Verification finished")
                }
            }
        }
    }
}
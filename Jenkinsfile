def PROJECT_VERSION

pipeline {
    agent { label 'jenkins-agent1' }

    options {
        ansiColor('xterm')
    }

    stages {
        stage('Determine Version') {
            steps {
                script {
                    withMaven {
                        PROJECT_VERSION = sh(
                                returnStdout: true,
                                script: 'mvn help:evaluate "-Dexpression=project.version" --batch-mode -q -DforceStdout'
                        ).trim()
                        echo "Project version: '${PROJECT_VERSION}'"
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    println("Building project version: " + PROJECT_VERSION)

                    withMaven {
                        sh 'mvn -DskipTests clean package'
                    }

                    println("Build finished")
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    println("Starting build verification")
                    withMaven {
                        sh 'mvn verify'
                    }
                    println("Verification finished")
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        sh 'mvn -DskipTests deploy'
                    }

                    println("Deploying to nexus finished")
                }
            }
        }
    }
}
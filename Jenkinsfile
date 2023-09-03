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
                        PROJECT_VERSION = sh(returnStdout: true, script: 'mvn help:evaluate "-Dexpression=project.version" -q -DforceStdout').trim()
                        sh 'PROJECT_VERSION=$(mvn help:evaluate "-Dexpression=project.version" -q -DforceStdout)'
                        echo "Project version: '${PROJECT_VERSION}'"
                        echo env.PROJECT_VERSION
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

        stage('Deploy') {
            steps {
                script {
                    println("Deploying snapshot")

                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        sh 'mvn deploy'
                    }

                    println("Deploying snapshot finished")
                }
            }
        }

        stage('Deploy release') {
            when {
                branch 'release/*'
            }
            steps {
                script {
                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        sh 'mvn nexus-staging:release'
                    }
                }
            }
        }
    }
}
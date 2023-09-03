pipeline {
    agent { label 'jenkins-agent1' }

    stages {
        stage('Determine Version') {
            steps {
                script {
                    withMaven {
                        def PROJECT_VERSION = sh 'mvn help:evaluate "-Dexpression=project.version" -q -DforceStdout'
                        echo "Project version: ${PROJECT_VERSION}"
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
                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        sh 'mvn deploy'
                    }
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
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
                                encoding: 'UTF-8',
                                returnStdout: true,
                                script: 'mvn help:evaluate "-Dexpression=project.version" -B -Dsytle.color=never -q -DforceStdout'
                        ).trim()
                        PROJECT_VERSION = PROJECT_VERSION.substring(3, PROJECT_VERSION.length() - 4)
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

        stage('Build docker image') {
            steps {
                script {
                    docker.withServer('tcp://docker.ra-tech.pro:2375', 'jenkins-client-cert') {
                        def image = docker.build(
                                "garden-manager:$PROJECT_VERSION",
                                "-e DATABASE_URL=${DATABASE_URL}",
                                "-e DATABASE_USERNAME=${DATABASE_USERNAME}",
                                "-e DATABASE_PASSWORD=${DATABASE_PASSWORD}",
                                "-e TEST_DATABASE_URL=${TEST_DATABASE_URL}",
                                "-e TEST_DATABASE_USERNAME=${TEST_DATABASE_USERNAME}",
                                "-e TEST_DATABASE_PASSWORD=${TEST_DATABASE_PASSWORD}"
                        )
                        docker.withRegistry('https://nexus.ra-tech.pro/repository/docker-snaphots') {
                            image.push()
                        }
                    }
                }
            }
        }
    }
}
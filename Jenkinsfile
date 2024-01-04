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
                        sh 'mvn -DskipTests -Dskip.jooq.generation=true -Dskip.unit.tests clean package'
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
                        docker.withServer(DOCKER_HOST, 'jenkins-client-cert') {
                            sh 'mvn verify'
                        }
                    }
                    jacoco(
                            execPattern: '**/target/*.exec',
                            classPattern: '**/target/classes',
                            sourcePattern: '**/src/main/java',
                            exclusionPattern: '**/database/schema/**/*,**/src/test'
                    )

                    println("Verification finished")
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        sh "mvn -DskipTests deploy"
                    }

                    println("Deploying to nexus finished")
                }
            }
        }

        stage('Build docker image') {
            steps {
                script {
                    docker.withServer(DOCKER_HOST, 'jenkins-client-cert') {
                        def image = docker.build(
                                "ru.ra-tech.garden-manager:$PROJECT_VERSION",
                                "--build-arg DATABASE_URL=${DATABASE_URL} " +
                                        "--build-arg DATABASE_USERNAME=${DATABASE_USERNAME} " +
                                        "--build-arg DATABASE_PASSWORD=${DATABASE_PASSWORD} " +
                                        "--build-arg TEST_DATABASE_URL=${TEST_DATABASE_URL} " +
                                        "--build-arg TEST_DATABASE_USERNAME=${TEST_DATABASE_USERNAME} " +
                                        "--build-arg TEST_DATABASE_PASSWORD=${TEST_DATABASE_PASSWORD} " +
                                        "."
                        )

                        docker.withRegistry('https://nexus.ra-tech.pro:8887', 'nexus_deployer') {
                            image.push()
                        }
                    }
                }
            }
        }
    }
}
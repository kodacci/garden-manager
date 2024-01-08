def PROJECT_VERSION
def GIT_BRANCH_NAME

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
                        GIT_BRANCH_NAME = sh(encoding: 'UTF-8', returnStdout: true, script: 'git name-rev --name-only HEAD').trim().tokenize('/').last()
                        echo "Project version: '${PROJECT_VERSION}'"
                        echo "Branch name: '${GIT_BRANCH_NAME}'"
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
                            sh 'mvn verify -Dskip.jooq.generation'
                        }
                    }

                    println("Verification finished")
                }
            }
        }

        stage('Analise with sonarqube') {
            when {
                branch 'main'
            }

            steps {
                withSonarQubeEnv('Sonar RA-Tech') {
                    sh 'mvn sonar:sonar -Dskip.jooq.generation'
                }
            }
        }

        stage('Quality gate') {
            when {
                branch 'main'
            }

            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        sh "mvn -DskipTests -Dskip.unit.tests -Dskip.jooq.generation deploy"
                    }

                    println("Deploying to nexus finished")
                }
            }
        }

        stage('Build docker image') {
            steps {
                script {
                    docker.withServer(DOCKER_HOST, 'jenkins-client-cert') {
                        def imageTag = 'ru.ra-tech:' + GIT_BRANCH_NAME + ':garden-manager-core:' + PROJECT_VERSION
                        echo "Building image with tag '$imageTag'"
                        def image = docker.build(imageTag)

                        docker.withRegistry('https://nexus.ra-tech.pro:8887', 'nexus_deployer') {
                            image.push()
                        }
                    }
                }
            }
        }
    }
}
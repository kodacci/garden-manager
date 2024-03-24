def PROJECT_VERSION
def DEPLOY_GIT_SCOPE

pipeline {
    agent { label 'jenkins-agent1' }

    options {
        ansiColor('xterm')
    }

    stages {
        stage('Determine Version') {
            steps {
                script {
                    withMaven(maven: 'maven-ra-tech') {
                        PROJECT_VERSION = sh(
                                encoding: 'UTF-8',
                                returnStdout: true,
                                script: 'mvn help:evaluate "-Dexpression=project.version" -B -Dsytle.color=never -q -DforceStdout'
                        ).trim()
                        PROJECT_VERSION = PROJECT_VERSION.substring(3, PROJECT_VERSION.length() - 4)
                        DEPLOY_GIT_SCOPE =
                                sh(encoding: 'UTF-8', returnStdout: true, script: 'git name-rev --name-only HEAD')
                                        .trim()
                                        .tokenize('/')
                                        .last()
                                        .toLowerCase()
                        echo "Project version: '${PROJECT_VERSION}'"
                        echo "Git branch scope: '${DEPLOY_GIT_SCOPE}'"
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    println("Building project version: " + PROJECT_VERSION)

                    withMaven {
                        sh './mvnw -DskipTests -Dskip.jooq.generation=true -Dskip.unit.tests clean package'
                    }

                    println("Build finished")
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    println("Starting build verification")

//                    withMaven {
                        docker.withServer(DOCKER_HOST, 'jenkins-client-cert') {
                            sh './mvnw verify -Dskip.jooq.generation'
                        }
//                    }

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
                    sh './mvnw sonar:sonar -Dskip.jooq.generation -DskipTests -Dskip.unit.tests'
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

        stage('Deploy to Nexus Snapshots') {
            when {
                not {
                    branch 'release/*'
                }
            }

            steps {
                script {
                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        sh "mvn deploy -Drevision=$PROJECT_VERSION-$DEPLOY_GIT_SCOPE-SNAPSHOT -DskipTests -Dskip.unit.tests -Dskip.jooq.generation"
                    }

                    println('Deploying to nexus finished')
                }
            }
        }

        stage('Build docker image') {
            steps {
                script {
                    docker.withServer(DOCKER_HOST, 'jenkins-client-cert') {
                        def imageTag = 'pro.ra-tech/garden-manager/' + DEPLOY_GIT_SCOPE + '/garden-manager-core:' + PROJECT_VERSION
                        def groupId = 'ru.ra-tech.garden-manager'
                        def artifactId = 'garden-manager-core'
                        def version = PROJECT_VERSION + '-' + DEPLOY_GIT_SCOPE + '-SNAPSHOT'

                        echo "Building image with tag '$imageTag'"
                        def image = docker.build(imageTag, "--build-arg REPO=maven-snapshots --build-arg GROUP_ID=$groupId --build-arg ARTIFACT_ID=$artifactId --build-arg VERSION=$version .")

                        docker.withRegistry(SNAPSHOTS_DOCKER_REGISTRY_HOST, 'vault-nexus-deployer') {
                            image.push()
                            image.push('latest')
                        }
                    }
                }
            }
        }
    }
}
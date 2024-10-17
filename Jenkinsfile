def PROJECT_VERSION
def DEPLOY_GIT_SCOPE
def IMAGE_TAG

pipeline {
    agent { label 'jenkins-agent1' }

    options {
        ansiColor('xterm')
    }

    stages {
        stage('Determine Version') {
            steps {
                script {
                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        PROJECT_VERSION = sh(
                                encoding: 'UTF-8',
                                returnStdout: true,
                                script: './mvnw help:evaluate "-Dexpression=project.version" -B -Dsytle.color=never -q -DforceStdout'
                        ).trim()
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
                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        sh './mvnw -DskipTests -Dskip.jooq.generation=true clean package'
                    }
                    println("Build finished")
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    println("Starting build verification")

                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        docker.withServer(DOCKER_HOST, 'jenkins-client-cert') {
                            sh './mvnw verify -Dskip.jooq.generation'
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
                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        sh './mvnw sonar:sonar -Dskip.jooq.generation -DskipTests'
                    }
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
                        sh "./mvnw deploy -Drevision=$PROJECT_VERSION-$DEPLOY_GIT_SCOPE-SNAPSHOT -DskipTests -Dskip.jooq.generation"
                    }

                    println('Deploying to nexus finished')
                }
            }
        }

        stage('Build docker image') {
            steps {
                script {
                    docker.withServer(DOCKER_HOST, 'jenkins-client-cert') {
                        IMAGE_TAG = 'pro.ra-tech/garden-manager/' +
                                DEPLOY_GIT_SCOPE +
                                '/garden-manager-core:' +
                                PROJECT_VERSION + '-' + currentBuild.number

                        echo "Building image with tag '$IMAGE_TAG'"
                        def image = docker.build(IMAGE_TAG)

                        docker.withRegistry(SNAPSHOTS_DOCKER_REGISTRY_HOST, 'vault-nexus-deployer') {
                            image.push()
                            image.push('latest')
                        }
                    }
                }
            }
        }

        stage('Trigger deploy pipeline') {
            steps {
                script {
                    def path = BRANCH_NAME.replaceAll("/", "%2F")
                    build(job: "Garden Manager Deploy Backend/$path", wait: false, parameters: [imageTag(name: 'core_image', imageTag: IMAGE_TAG)])
                }
            }
        }
    }
}
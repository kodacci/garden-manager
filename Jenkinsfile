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
                        sh './mvnw --log-file ./build.log -DskipTests -Dskip.jooq.generation=true clean package'
                    }
                    archiveArtifacts('build.log')
                    sh 'rm ./build.log'
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
                            sh './mvnw --log-file ./test.log verify -Dskip.jooq.generation'
                        }
                    }
                    archiveArtifacts('test.log')
                    sh 'rm ./test.log'
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
                        sh "./mvnw --log-file ./deploy.log deploy -Drevision=$PROJECT_VERSION-$DEPLOY_GIT_SCOPE-SNAPSHOT -DskipTests -Dskip.jooq.generation"
                    }
                    archiveArtifacts('deploy.log')
                    sh 'rm ./deploy.log'

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
                    build(job: "Garden Manager Deploy Backend/$path", wait: false, parameters: [string(name: 'core_image', value: IMAGE_TAG)])
                }
            }
        }
    }
}
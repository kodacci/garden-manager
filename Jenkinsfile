def PROJECT_VERSION
def DEPLOY_GIT_SCOPE

pipeline {
    options {
        ansiColor('xterm')
    }

    stages {
        stage('Determine Version') {
            agent { label 'jenkins-agent1' }

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
            agent { label 'jenkins-agent1' }

            steps {
                script {
                    println("Building project version: " + PROJECT_VERSION)
                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        sh './mvnw -DskipTests -Dskip.jooq.generation=true -Dskip.unit.tests clean package'
                    }
                    println("Build finished")
                }
            }
        }

        stage('Test') {
            agent { label 'jenkins-agent1' }

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
            agent { label 'jenkins-agent1' }

            when {
                branch 'main'
            }

            steps {
                withSonarQubeEnv('Sonar RA-Tech') {
                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        sh './mvnw sonar:sonar -Dskip.jooq.generation -DskipTests -Dskip.unit.tests'
                    }
                }
            }
        }

        stage('Quality gate') {
            agent { label 'jenkins-agent1' }

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
            agent { label 'jenkins-agent1' }

            when {
                not {
                    branch 'release/*'
                }
            }

            steps {
                script {
                    withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                        sh "./mvnw deploy -Drevision=$PROJECT_VERSION-$DEPLOY_GIT_SCOPE-SNAPSHOT -DskipTests -Dskip.unit.tests -Dskip.jooq.generation"
                    }

                    println('Deploying to nexus finished')
                }
            }
        }

        stage('Build docker image') {
            agent { label 'jenkins-agent1' }

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

        stage('Request to deploy') {
            agent none

            options {
                timeout time: 1, unit: 'HOURS'
            }

            input {
                message: 'Deploy to k8s?'
                ok: 'Yes'
            }

            steps {
                script {
                    env.DO_DEPLOY = 'yes'
                }
            }
        }

        stage('Deploy to test environment') {
            agent { label 'jenkins-agent1' }

            when {
                beforeAgent: true
                environment name: 'DO_DEPLOY', value: 'yes'
            }

            steps {
                script {
                    withPythonEnv('python') {
                        sh 'pip install -U jinja2-cli'
                        sh "jinja2 -D branch=$DEPLOY_GIT_SCOPE distrib/templates/deployment.yaml"
                    }
                }
            }
        }
    }
}
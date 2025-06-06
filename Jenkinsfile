def PROJECT_VERSION
def DEPLOY_GIT_SCOPE
def CORE_APP_IMAGE_TAG
def CORE_DB_MIGRATE_IMAGE_TAG

static def genImageTag(name, scope, version, buildNumber) {
    return 'pro.ra-tech/garden-manager/' +
            scope + '/' + name + ':' +
            version + '-' + buildNumber
}

def buildImage(name, dockerFilePath, scope, version, buildNumber) {
    def tag = genImageTag(name, scope, version, buildNumber)

    docker.withServer(DOCKER_HOST, 'jenkins-client-cert') {
        echo "Building image with tag '$tag'"
        def image = docker.build(tag, '-f ' + dockerFilePath + ' .')

        docker.withRegistry(SNAPSHOTS_DOCKER_REGISTRY_HOST, 'vault-nexus-deployer') {
            image.push()
            image.push('latest')
        }
    }

    return tag
}

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
                                script: './mvnw --global-settings \$GLOBAL_MVN_SETTINGS help:evaluate "-Dexpression=project.version" -B -Dsytle.color=never -q -DforceStdout'
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
                    def logFileName = env.BUILD_TAG + '-build.log'
                    try {
                        withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                            sh "./mvnw --global-settings \$GLOBAL_MVN_SETTINGS --log-file \"$logFileName\" -DskipTests -Dskip.jooq.generation=true clean package"
                        }
                    } finally {
                        archiveArtifacts(logFileName)
                        sh "rm \"$logFileName\""
                    }
                    println("Build finished")
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    println("Starting build verification")
                    def logFileName = env.BUILD_TAG + '-test.log'
                    try {
                        withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                            docker.withServer(DOCKER_HOST, 'jenkins-client-cert') {
                                sh "./mvnw --global-settings \$GLOBAL_MVN_SETTINGS --log-file \"$logFileName\" verify -Dskip.jooq.generation"
                            }
                        }

                        recordCoverage(
                                tools: [[parser: 'JACOCO', pattern: 'code-coverage/target/site/jacoco-aggregate/jacoco.xml']],
                                id: 'core_jacoco',
                                name: 'Core JaCoCo Coverage',
                                sourceCodeRetention: 'LAST_BUILD',
                                sourceDirectories: [[path: '**/src/main/java']],
                                qualityGates: [
                                        [threshold: 80.0, metric: 'LINE', baseline: 'PROJECT', criticality: 'FAILURE']
                                ]
                        )
                    } finally {
                        archiveArtifacts(logFileName)
                        sh "rm \"$logFileName\""
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
                        sh './mvnw --global-settings \$GLOBAL_MVN_SETTINGS sonar:sonar -Dskip.jooq.generation -DskipTests'
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
                    def logFileName = env.BUILD_TAG + '-deploy.log'
                    try {
                        withMaven(globalMavenSettingsConfig: 'maven-config-ra-tech') {
                            sh "./mvnw --global-settings \$GLOBAL_MVN_SETTINGS --log-file \"$logFileName\" deploy -Drevision=$PROJECT_VERSION-$DEPLOY_GIT_SCOPE-SNAPSHOT -DskipTests -Dskip.jooq.generation"
                        }
                    } finally {
                        archiveArtifacts(logFileName)
                        sh "rm \"$logFileName\""
                    }

                    println('Deploying to nexus finished')
                }
            }
        }

        stage('Build core db migrations docker image') {
            steps {
                script {
                    CORE_DB_MIGRATE_IMAGE_TAG = buildImage(
                            'core-db-migrate',
                            'distrib/docker/db-migrate/Dockerfile',
                            DEPLOY_GIT_SCOPE,
                            PROJECT_VERSION,
                            currentBuild.number
                    )
                }
            }
        }

        stage('Build core docker image') {
            steps {
                script {
                    CORE_APP_IMAGE_TAG = buildImage(
                            'garden-manager-core',
                            'distrib/docker/core/Dockerfile',
                            DEPLOY_GIT_SCOPE,
                            PROJECT_VERSION,
                            currentBuild.number
                    )
                }
            }
        }

        stage('Trigger deploy pipeline') {
            steps {
                script {
                    def path = BRANCH_NAME.replaceAll("/", "%2F")
                    build(
                            job: "Garden Manager Deploy Backend/$path",
                            wait: false,
                            parameters: [
                                    string(name: 'core_app_image', value: CORE_APP_IMAGE_TAG),
                                    string(name: 'core_db_migrate_image', value: CORE_DB_MIGRATE_IMAGE_TAG)
                            ]
                    )
                }
            }
        }
    }
}
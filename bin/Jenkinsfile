@Library(['git_essalud_lib','common_essalud_lib','docker_essalud_lib','maven_essalud_lib']) _

def gitLib = new git_essalud_lib()
def commonLib = new common_essalud_lib()
def dockerLib = new docker_essalud_lib()
def mavenLib = new maven_essalud_lib()

pipeline {

    agent any

    tools {
        'org.jenkinsci.plugins.docker.commons.tools.DockerTool' 'docker-tool'
        maven 'maven-tool'
    }

    environment {
        GROUP_ID = '47a9c151-9643-4efd-b66d-92b98ba5896a'
        APPLICATION_ID = '83e2d5c2-2f92-47cd-a749-8a18f1b6aed5'
    }

    options {
        skipStagesAfterUnstable()
        disableConcurrentBuilds abortPrevious: true
        buildDiscarder(logRotator(numToKeepStr: "${JOB_MAX_DAYS}", daysToKeepStr: "${JOB_MAX_BUILDS}"))
    }

    stages {

        stage('Check Tools') {
            steps {
                script {
                    commonLib.msgJobBuildStarted()
                    mavenLib.showToolVersion()
                    dockerLib.showToolVersion()
                 }
            }
        }

        stage('Clone Repository') {
            steps {
                checkout scm

                script {
                    gitLib.cloneEnv()
                    commonLib.showWsFiles()
                }
            }
        }

        stage('Build Project') {
            steps {
                script {
                    mavenLib.buildProject()
                    commonLib.showWsFiles()
                }
            }
        }

        stage('Build Image') {
            steps {
                script { dockerLib.buildImage() }
            }
        }

        stage('Push Image') {
            steps {
                script { dockerLib.pushImage() }
            }
        }

        stage('Deploy') {
            steps {
                script { dockerLib.runContainer() }
            }
        }

    }

    post {
        always { cleanWs() }
        success { script { commonLib.msgJobBuildSuccess() } }
        failure { script { commonLib.msgJobBuildFailed() } }
        unstable { script { commonLib.msgJobBuildUnstable() } }
    }

}
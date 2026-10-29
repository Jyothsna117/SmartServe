// ==============================================================================
// SmartServe Declarative CI/CD Pipeline
// Jenkins Orchestration for Complete DevOps Lifecycle:
// Git SCM -> Maven Build & Test -> JAR Packaging -> Docker Build -> K8s Deploy -> Verification
// ==============================================================================

pipeline {
    agent any

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['local-minikube', 'docker-standalone', 'staging-cluster'], description: 'Target deployment environment')
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'Skip Maven unit and integration tests (emergency builds only)')
        string(name: 'IMAGE_TAG_OVERRIDE', defaultValue: '', description: 'Custom Docker tag (defaults to smartserve:${BUILD_NUMBER})')
    }

    environment {
        APP_NAME = 'smartserve'
        IMAGE_NAME = 'smartserve'
        APP_VERSION = '1.0.0'
        DOCKER_TAG = "${params.IMAGE_TAG_OVERRIDE ? params.IMAGE_TAG_OVERRIDE : env.BUILD_NUMBER}"
        FULL_IMAGE_NAME = "${env.IMAGE_NAME}:${env.DOCKER_TAG}"
        LATEST_IMAGE_NAME = "${env.IMAGE_NAME}:latest"
        
        // Jenkins Credentials ID placeholders (Configure in Jenkins Credentials Manager)
        // DOCKER_REGISTRY_CRED_ID = 'dockerhub-credentials'
        // KUBECONFIG_CRED_ID = 'k8s-kubeconfig'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '5'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
        ansiColor('xterm')
    }

    stages {
        // ----------------------------------------------------------------------
        // Stage 1: Checkout Source Code
        // ----------------------------------------------------------------------
        stage('1. Checkout SCM') {
            steps {
                script {
                    echo "=========================================================="
                    echo "Checking out SmartServe source code from Git repository..."
                    echo "Build Number: ${env.BUILD_NUMBER}"
                    echo "Node Agent: ${env.NODE_NAME}"
                    echo "=========================================================="
                    checkout scm
                }
            }
        }

        // ----------------------------------------------------------------------
        // Stage 2: Maven Compilation & Unit Testing
        // ----------------------------------------------------------------------
        stage('2. Maven Build & Test') {
            steps {
                script {
                    echo "Running Maven clean compile and unit test suite..."
                    if (isUnix()) {
                        sh "chmod +x ./mvnw"
                        if (params.SKIP_TESTS) {
                            sh "./mvnw clean compile -DskipTests"
                        } else {
                            sh "./mvnw clean test"
                        }
                    } else {
                        // Windows Agent
                        if (params.SKIP_TESTS) {
                            bat "mvnw.cmd clean compile -DskipTests"
                        } else {
                            bat "mvnw.cmd clean test"
                        }
                    }
                }
            }
            post {
                always {
                    // Record JUnit test results in Jenkins UI
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        // ----------------------------------------------------------------------
        // Stage 3: Packaging Executable Fat JAR
        // ----------------------------------------------------------------------
        stage('3. Package Executable JAR') {
            steps {
                script {
                    echo "Packaging executable Spring Boot JAR artifact..."
                    if (isUnix()) {
                        sh "./mvnw package -DskipTests"
                    } else {
                        bat "mvnw.cmd package -DskipTests"
                    }
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        // ----------------------------------------------------------------------
        // Stage 4: Docker Image Build & Tagging
        // ----------------------------------------------------------------------
        stage('4. Docker Build & Tag') {
            steps {
                script {
                    echo "Building Docker container image: ${env.FULL_IMAGE_NAME} and ${env.LATEST_IMAGE_NAME}..."
                    if (isUnix()) {
                        sh "docker build -t ${env.FULL_IMAGE_NAME} -t ${env.LATEST_IMAGE_NAME} -t ${env.IMAGE_NAME}:${env.APP_VERSION} ."
                    } else {
                        bat "docker build -t ${env.FULL_IMAGE_NAME} -t ${env.LATEST_IMAGE_NAME} -t ${env.IMAGE_NAME}:${env.APP_VERSION} ."
                    }
                }
            }
        }

        // ----------------------------------------------------------------------
        // Stage 5: Distribute Image to Cluster / Registry
        // ----------------------------------------------------------------------
        stage('5. Provision Image to Kubernetes') {
            steps {
                script {
                    echo "Making Docker image available to Kubernetes cluster..."
                    if (params.ENVIRONMENT == 'local-minikube') {
                        echo "Target environment is local Minikube. Loading image directly into Minikube VM cache..."
                        if (isUnix()) {
                            sh "minikube image load ${env.FULL_IMAGE_NAME} || docker save ${env.FULL_IMAGE_NAME} | (eval \$(minikube docker-env) && docker load)"
                        } else {
                            bat "minikube image load ${env.FULL_IMAGE_NAME}"
                        }
                    } else {
                        echo "For remote Kubernetes clusters, push image to container registry (e.g. Docker Hub or Harbor)."
                        echo "Skipping push: local demonstration profile active."
                    }
                }
            }
        }

        // ----------------------------------------------------------------------
        // Stage 6: Kubernetes Deployment
        // ----------------------------------------------------------------------
        stage('6. Kubernetes Deployment') {
            steps {
                script {
                    echo "Applying Kubernetes manifests from k8s/ directory..."
                    if (isUnix()) {
                        sh "kubectl apply -f k8s/mongodb-pvc.yaml"
                        sh "kubectl apply -f k8s/mongodb-service.yaml"
                        sh "kubectl apply -f k8s/mongodb-deployment.yaml"
                        sh "kubectl apply -f k8s/configmap.yaml"
                        sh "kubectl apply -f k8s/secret.yaml"
                        sh "kubectl apply -f k8s/service.yaml"
                        // Update deployment image dynamically to the build image tag
                        sh "kubectl set image deployment/smartserve-deployment smartserve-app=${env.FULL_IMAGE_NAME} || kubectl apply -f k8s/deployment.yaml"
                    } else {
                        bat "kubectl apply -f k8s/mongodb-pvc.yaml"
                        bat "kubectl apply -f k8s/mongodb-service.yaml"
                        bat "kubectl apply -f k8s/mongodb-deployment.yaml"
                        bat "kubectl apply -f k8s/configmap.yaml"
                        bat "kubectl apply -f k8s/secret.yaml"
                        bat "kubectl apply -f k8s/service.yaml"
                        bat "kubectl set image deployment/smartserve-deployment smartserve-app=${env.FULL_IMAGE_NAME} || kubectl apply -f k8s/deployment.yaml"
                    }
                }
            }
        }

        // ----------------------------------------------------------------------
        // Stage 7: Rollout Status & Health Verification
        // ----------------------------------------------------------------------
        stage('7. Rollout & Verification') {
            steps {
                script {
                    echo "Monitoring Kubernetes rollout status..."
                    if (isUnix()) {
                        sh "kubectl rollout status deployment/smartserve-deployment --timeout=120s"
                        sh "kubectl get pods -l app=smartserve -o wide"
                        sh "kubectl get service smartserve-service"
                    } else {
                        bat "kubectl rollout status deployment/smartserve-deployment --timeout=120s"
                        bat "kubectl get pods -l app=smartserve -o wide"
                        bat "kubectl get service smartserve-service"
                    }
                    echo "Rollout completed successfully! Application is live and healthy."
                }
            }
        }
    }

    // --------------------------------------------------------------------------
    // Post Actions Notification & Workspace Hygiene
    // --------------------------------------------------------------------------
    post {
        success {
            echo "SUCCESS: SmartServe CI/CD Pipeline executed cleanly across all 7 stages."
        }
        failure {
            echo "FAILURE: Pipeline failed. Check console log above for error diagnostics."
        }
        always {
            cleanWs deleteDirs: false, notFailBuild: true
        }
    }
}

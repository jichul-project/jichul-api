pipeline {
  agent {
    label "mac-mini"
  }

  environment {
    IMAGE_REGISTRY = "ghcr.io"
    IMAGE_NAME     = "ghcr.io/jichul-project/jichul-api"
    GITHUB_CREDS   = credentials("Github")      // usr, psw 자동 주입
  }

  options {
    buildDiscarder(logRotator(numToKeepStr: "10"))
    timeout(time: 30, unit: "MINUTES")
    timestamps()
  }

  stages {
    stage("Checkout") {
      steps {
        checkout scm
      }
    }

    stage("Build and Push") {
      steps {
        sh """
          gradle jib -x test \
            --no-daemon \
            --build-cache \
            --parallel \
            -Djib.console=plain \
            -Djib.to.auth.username=${GITHUB_CREDS_USR} \
            -Djib.to.auth.password=${GITHUB_CREDS_PSW} \
            -Djib.to.image=${IMAGE_NAME}:${env.BUILD_NUMBER} \
            -Djib.to.image=${IMAGE_NAME}:latest
        """
      }
    }

    stage("Cleanup") {
      steps {
        sh """
          docker system prune -f || true
          docker rmi ${IMAGE_NAME}:${env.BUILD_NUMBER} || true
          docker rmi ${IMAGE_NAME}:latest || true
          docker logout ${IMAGE_REGISTRY} || true
        """
      }
    }
  }
}
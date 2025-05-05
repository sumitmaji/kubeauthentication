
pipeline {
    agent {
        kubernetes {
            cloud 'gok-kubernetes'
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: helm
    image: alpine/helm:3.17.2
    tty: true
    command: ["tail"]
    args: ["-f", "/dev/null"]
    resources:
       requests:
          memory: "1Gi"
          cpu: "1"
    volumeMounts:
      - name: docker-credentials
        mountPath: /root/.docker
  - name: jnlp
    image: jenkins/inbound-agent:latest
    tty: true
    resources:
       requests:
          memory: "1Gi"
          cpu: "1"
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    command: ["tail"]
    args: ["-f", "/dev/null"]
    tty: true
    resources:
        requests:
            memory: "1Gi"
            cpu: "1"
    volumeMounts:
      - name: docker-credentials
        mountPath: /kaniko/.docker
  volumes:
    - name: docker-credentials
      secret:
        secretName: registry-credentials
            """
        }
    }
    environment {
        IMAGE_NAME = 'registry.gokcloud.com/kubeauthentication'
        IMAGE_TAG = "${BUILD_ID}"
    }
    stages {
        stage('Build and Push Docker Image') {
            steps {
                container('kaniko') {
                   sh """
                        /kaniko/executor \\
                            --context git://github.com/sumitmaji/kubeauthentication \\
                            --destination ${IMAGE_NAME}:${IMAGE_TAG} \\
                            --destination ${IMAGE_NAME}:latest \\
                            --cache=true \\
                            --insecure \\
                            --skip-tls-verify
                    """
                }
            }
        }
        stage('Halm Package and Push') {
            steps {
                container('helm') {
                   sh """
                        helm version
                        git clone https://github.com/sumitmaji/kubeauthentication.git /workspace/kubeauthentication
                        # Navigate to the Helm chart directory
                        cd /workspace/kubeauthentication/chart

                        # Package the Helm chart
                        helm package .
                        ls -ltr

                        # Push the Helm chart to the OCI registry
                        export HELM_EXPERIMENTAL_OCI=1
                        helm push kubeauthentication-1.tgz oci://registry.gokcloud.com/kubeauthentication --insecure-skip-tls-verify
                        
                    """
                }
            }
        }
        
    }
}

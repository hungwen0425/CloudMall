pipeline {
  agent {
    node {
      label 'maven'
    }
  }

  parameters {
    string(name:'PROJECT_VERSION', defaultValue: '', description:'項目版本')
    string(name:'PROJECT_NAME', defaultValue: '', description:'構建模塊')
  }

  environment {
    DOCKER_CREDENTIAL_ID = 'dockerhub-id'
    GITHUB_CREDENTIAL_ID = 'github-id'
    KUBECONFIG_CREDENTIAL_ID = 'demo-kubeconfig'
    REGISTRY = 'docker.io'
    DOCKERHUB_NAMESPACE = 'hungwen0425'
    GITHUB_ACCOUNT = 'hungwen0425'
    SONAR_CREDENTIAL_ID = 'sonar-qube'
    APP_NAME = 'cloudmall'
  }

  stages {
    stage ('拉取代碼，編譯、打包') {
      steps {
        git(url: 'https://github.com/hungwen0425/cloudmall.git', credentialsId: 'github-id', branch: 'master', changelog: true, poll: false)
        sh 'echo 正在構建 $PROJECT_NAME  版本號：$PROJECT_VERSION 將會提交給 $REGISTRY 鏡像倉庫'
        container ('maven') {
          sh 'mvn clean install -Dmaven.test.skip=true -gs `pwd`/mvn-settings.xml'
        }
      }
    }

    stage('程式碼質量分析') {
      steps {
        container ('maven') {
          withCredentials([string(credentialsId: "$SONAR_CREDENTIAL_ID", variable: 'SONAR_TOKEN')]) {
            withSonarQubeEnv('sonar') {
              sh "echo 當前目錄 `pwd`"
              sh "mvn sonar:sonar -gs `pwd`/mvn-settings.xml -Dsonar.branch=$BRANCH_NAME -Dsonar.login=$SONAR_TOKEN"
            }
          }
          timeout(time: 1, unit: 'HOURS') {
            waitForQualityGate abortPipeline: true
          }
        }
      }
    }

    stage ('構建鏡像 & 推送快照鏡像') {
      steps {
        container ('maven') {
          sh 'mvn -Dmaven.test.skip=true -gs `pwd`/mvn-settings.xml clean package'
          sh 'cd $PROJECT_NAME && docker build -f Dockerfile -t $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER .'
          withCredentials([usernamePassword(passwordVariable : 'DOCKER_PASSWORD' ,usernameVariable : 'DOCKER_USERNAME' ,credentialsId : "$DOCKER_CREDENTIAL_ID" ,)]) {
            sh 'echo "$DOCKER_PASSWORD" | docker login $REGISTRY -u "$DOCKER_USERNAME" --password-stdin'
            sh 'docker push  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER'
          }
        }
      }
    }

    stage('推送最新鏡像'){
      when{
        branch 'master'
      }
      steps{
        container ('maven') {
          sh 'docker tag  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:latest '
          sh 'docker push  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:latest '
        }
      }
    }

    stage('部署到K8S') {
      when{
        branch 'master'
      }
      steps {
        input(id: 'deploy-to-dev-$PROJECT_NAME', message: '是否將$PROJECT_NAME部署到開發環境?')
        kubernetesDeploy(configs: '$PROJECT_NAME/deploy/**', enableConfigSubstitution: true,
        kubeconfigId: "$KUBECONFIG_CREDENTIAL_ID")
      }
    }

    stage('發布版本'){
      when{
        expression{
          return params.PROJECT_VERSION =~ /v.*/
        }
      }
      steps {
        container ('maven') {
          input(id: 'release-image-with-tag', message: '是否發布當前版本?')
          sh 'docker tag  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:SNAPSHOT-$BRANCH_NAME-$BUILD_NUMBER $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:$PROJECT_VERSION '
          sh 'docker push  $REGISTRY/$ALIYUNHUB_NAMESPACE/$PROJECT_NAME:$TAG_NAME '
          withCredentials([usernamePassword(credentialsId: "$GITEE_CREDENTIAL_ID",
          passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
            sh 'git config --global user.email "lemon_wan@aliyun.com" '
            sh 'git config --global user.name "lemon_wan" '
            sh 'git tag -a $PROJECT_NAME-$PROJECT_VERSION -m "$PROJECT_VERSION" '
            sh 'git push http://$GIT_USERNAME:$GIT_PASSWORD@gitee.com/$GITEE_ACCOUNT/gulimall.git --tags --ipv4'
          }
        }
      }
    }
  }
}
pipeline {
  agent any
  
  stages {
    stage("Build"){
      steps {
      echo "Building"
        sh "mvn clean install"
      }
    }
    stage("Test"){
      steps{
        echo "this is fun"
      }
    }
  }
}
  

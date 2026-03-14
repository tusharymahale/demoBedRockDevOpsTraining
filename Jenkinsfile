pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('LocalSonar') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=Demo'
                }
            }
        }
        stage("Quality Gate") {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                waitForQualityGate abortPipeline: true
            }
        }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }           
}

    
   post {
       always {
           junit 'target/surefire-reports/*.xml'
       }
   }
}

// Jenkinsfile for a Spring Boot application with build, SonarQube, Quality Gates, and Surefire reports

pipeline {
    agent {
        // You can specify an agent here. Common options include:
        // agent any // Run on any available agent
        // agent { label 'my-linux-agent' } // Run on an agent with a specific label
        // agent { docker { image 'maven:3.8.5-openjdk-17-slim' } } // Run inside a Docker container
        agent any
    }

tools {
        // This MUST match the 'Name' you gave Maven in 
        // Manage Jenkins -> Global Tool Configuration
        maven 'maven' 
    }

    environment {
        // SonarQube credentials (ensure this is configured in Jenkins Credentials Manager as a "Secret text")
        SONAR_SCANNER_HOME = tool 'SonarScanner' // Name of the SonarScanner tool configured in Jenkins
        // Replace 'your-sonarqube-server-id' with the ID of your SonarQube server configuration in Jenkins
        SONAR_SERVER_ID = 'your-sonarqube-server-id'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    // Check out your source code from SCM
                    checkout scm
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo 'Building the application...'
                    // Use a Maven wrapper if available, otherwise 'mvn'
                    sh 'mvn clean install -DskipTests'
                }
            }
            post {
                failure {
                    echo 'Build failed!'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    echo 'Running SonarQube analysis...'
                    withSonarQubeEnv(installationName: env.SONAR_SERVER_ID) {
                        sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2746:sonar -Dsonar.projectKey=YourSpringBootApp -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_AUTH_TOKEN}"
                        // Replace 'YourSpringBootApp' with your actual SonarQube project key
                    }
                }
            }
            post {
                failure {
                    echo 'SonarQube analysis failed!'
                }
            }
        }

        stage('Quality Gate Check') {
            steps {
                script {
                    echo 'Checking SonarQube Quality Gate status...'
                    // This pauses the pipeline until the Quality Gate status is retrieved from SonarQube.
                    // The 'abortPipeline' parameter will automatically fail the Jenkins build if the Quality Gate fails.
                    timeout(time: 5, unit: 'MINUTES') { // Timeout for waiting for the Quality Gate status
                        waitForQualityGate abortPipeline: true
                    }
                    echo 'SonarQube Quality Gate passed.'
                }
            }
            post {
                unstable {
                    echo 'SonarQube Quality Gate failed (but continue pipeline depending on settings)'
                    // You might want to fail the build explicitly here if 'abortPipeline: false' was used
                }
                failure {
                    echo 'SonarQube Quality Gate failed!'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    echo 'Running unit and integration tests...'
                    // Execute tests. If tests were skipped in the build stage, they run here.
                    // Use '-Dmaven.test.failure.ignore=true' if you want to allow the build to pass
                    // even if some tests fail, though generally failing tests should fail the build.
                    sh 'mvn test'
                }
            }
            post {
                always {
                    // Always archive Surefire reports regardless of test outcome
                    archiveArtifacts artifacts: '**/target/surefire-reports/*.xml', fingerprint: true
                    junit '**/target/surefire-reports/*.xml' // Publish JUnit test results
                }
                failure {
                    echo 'Tests failed!'
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    echo 'Packaging the application (if not already done in Build stage)...'
                    // This stage can be used to create the final executable JAR/WAR.
                    // Often `mvn clean install` already produces the package.
                    // If you ran with -DskipTests earlier and want to package after tests,
                    // you might run `mvn package` here.
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                    echo 'Application packaged successfully!'
                }
                failure {
                    echo 'Packaging failed!'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
            // Clean workspace on all outcomes
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
    }
}


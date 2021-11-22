pipeline{
    agent any
    parameters{
        string(name:'BRANCH',defaultValue:'master')
        string(name:'BRANCH_NAME',defaultValue:'')
        string(name:'BUILD_NUMBER',defaultValue:'')
        string(name:'SERVER_IP',defaultValue:'')
    }
    stages{
        stage("clone the code"){
            steps{
                println"the code is built"
                sh "ls -l"
                checkout([
                    $class:'GitSCM',
                    branches:[[name:'${BRANCH}']],
                    userRemoteConfigs:[[url:'https://github.com/Saraswathirg/branchpubrepo.git']]
                ])
            }
        }
        stage("build the code"){
            steps{
                println"the code is built"
                sh "ls -lart ./*"
                sh "mvn clean package"
            }
        }
        stage("store to s3"){
            steps{
                println"the artifact stored"
                sh "aws s3 cp /target/hello-${BUILD_NUMBER}.war s3://alltime/${BRANCH_NAME}/hello-${BUILD_NUMBER}/"
            }
        }
        stage("download to present location"){
            steps{
                println"the artifact is downloaded"
                sh """
                aws s3 ls
                aws s3 ls s3://alltime/
                aws s3 ls s3://alltime/${BRANCH_NAME}/
                aws s3 cp s3://alltime/${BRANCH_NAME}/hello-${BUILD_NUMBER} ."""
            }
        }
        stage("copied"){
            steps{
                println"the artifact copied"
                sh "scp -o strictHostKeychecking=no -i /tmp/awsaws.pem hello-${BUILD_NUMBER} ec2-user@${SERVER_IP}:/var/lib/tomcat/webapps"
            }
        }
    }
}
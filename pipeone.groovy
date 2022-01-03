//declarative pipeline
pipeline{
    agent any
    parameters{
        string(name:'BRANCH', defaultValue:'master', description:'enter the branch name')
        string(name:'BUILD_NUM', defaultValue:'', description:'enter the buildnumber')
        string(name:'SERVER_IP', defaultValue:'', description:'enter the serverip')
    }

    stages{
        stage("update the code"){
            steps{
                println "the code is updated"
                sh "ls -l"
                checkout([
                    $class: 'GitSCM',
                    branches: [[name:'${BRANCH}']],
                    userRemoteConfigs: [[url:'https://github.com/Saraswathirg/branchpubrepo.git']]
                ])
            }
        }
        stage("build the code"){
            steps{
            println "the code is converted"
            sh "mvn clean package"
            }
        }
        stage("the code is copied to s3"){
            steps{
                println "the code is stored to s3"
                sh "aws s3 cp /var/lib/jenkins/workspace/tetsing/target/hello-${BUILD_NUM}.war s3://alltime/${BRANCH}/${BUILD_NUM}/"
            }
        }
        stage("copy artifact"){
            steps{
                println "the code is copied from s3"
                sh "aws s3 cp s3://alltime/${BRANCH}/${BUILD_NUM}/hello-${BUILD_NUM}.war ."

            }
        }
        stage(" transfer the code to deploy"){
            steps{
                println "the artifact is copied"
                sh "scp -o StrictHostKeyChecking=no -i /tmp/awsaws.pem hello-${BUILD_NUM}.war ec2-user@${SERVER_IP}:/var/lib/tomcat/webapps"
            }
        }

    }
    
}
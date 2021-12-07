pipeline{
    agent any
    parameters{
        string(name:'BRANCH',defaultValue:'master',description:'enter the branch name')
    }
    stages{
        stage("check updates in repo"){
            steps{
                println "here checks for updates"
                checkout([
                    $class:'GitSCM',
                    branches:[[name='${BRANCH}']],
                    userRemoteConfigs:[[url:'https://github.com/Saraswathirg/branchpubrepo.git']]
                ])
            }
        }
        stage("build the code"){
            steps{
                println "the code is built"
                sh"""
                ls -lart./
                mvn clean package"""
            }
        }
        stage("the artifact is stored"){
            steps{
                println "the artifact is stored"
                sh "aws s3 cp target/hello-${BUILD}.war s3://alltime/${BRANCH}/${BUILD}/"
            }
        }
    }

}
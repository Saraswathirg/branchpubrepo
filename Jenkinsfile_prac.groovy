//declarative pipeline
pipeline{
    agent any
    parameters{
        string(name:'BRANCH',defaultValue:'master',description:'enter the branch')
    }
    stages{
        stage("clone the code"){
            steps{
                println "the code is cloned"
                checkout([
                    $class:'GitSCM',
                    branches:[[name:'${BRANCH}']],
                    userRemoteConfigs:[[url:'https://github.com/Saraswathirg/branchpubrepo.git']]
                ])
            }
        }
        stage("build the code"){
            steps{
                println "the code is built"
                sh"""
                ls -lart ./*
                mvn clean package"""
            }
        }
        stage("store to s3"){
            steps{
                println "the artifact stored to s3"
                sh "aws s3 cp target/hello-${BUILD_NUMBER}.war s3://alltime/${BRANCH}/${BUILD_NUMBER}/"
            }
        }
        }
    }

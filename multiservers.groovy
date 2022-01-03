//declarative pipeline
pipeline{
    agent any
    parameters{
        string(name:'BRANCH',defaultValue:'master',description:'enter the branch')
        string(name:'BUILD_NUM',defaultValue:'',description:'enter the build number')
        string(name:'SERVERIP',defaultValue:'',description:'enter the serverips')
    }
    stages{
        stage("the code is upgraded"){
            steps{
                println "the code is updated"
                sh "ls -l"
                checkout ([
                    $class: 'GitSCM',
                    branches: [[name:'${BRANCH}']],
                    userRemoteConfigs:[[url:'https://github.com/Saraswathirg/branchpubrepo.git']]
                ])
            }
        }
        stage("build the code"){
            steps{
                println "the code is built"
                sh "mvn clean package"
            }
        }
        stage("copy to tomservers"){
            steps{
                println "the code is copied to multiple servers"
                sh'''
                ls -l
                IFS = ',' read -ra ADDR <<< "${SERVERIP}"
                do
                echo $ip
                echo "here we can use scp command"

                scp -o StrictHostKeyChecking=no -i /tmp/awsaws.pem target/hello-${BUILD_NUM}.war ec2-user@$ip:/var/lib/tomcat/webapps
                ssh -o StrictHostKeyChecking=no -i /tmp/awsaws.pem ec2-user@$ip "hostname"
                done
                '''
            }
        }


    }
}
pipeline{
    agent any
    parameters{
        string(name:'BRANCH',defaultValue:'master')
        string(name:'BUILD_NUMBER',defaultValue:'')
        string(name:'SERVERIP',defaultValue:'')

    }
    stages{
        stage("multiple servers"){
            steps{
            sh'''
            aws s3 cp s3://alltime/${BRANCH}/${BUILD_NUMBER}/hello-${BUILD_NUMBER}.war .
            ls -l
            IFS=',' read -ra ADDR<<<"${SERVERIP}"
            for ip in \"${ADDR[@]}\";
            do 
            echo $ip
            echo "here er can use scp command"
            scp -o strictHostkeychecking=no -i /tmp/awsaws.pem hello-${BUILD_NUMBER}.war ec2-user@$ip:/var/lib/tomcat/webapps
            done
            '''
            }
        }
    }
}
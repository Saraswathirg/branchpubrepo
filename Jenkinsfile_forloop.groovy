pipeline{
    agent any
    stages{
        stage("multiple servers"){
            sh'''
            aws s3 cp s3://alltime/${BRANCH}/${BUILD_NUMBER}/hello-${BUILD_NUMBER}.war .
            ls -l
            IFS=',' read -ra ADDR<<<"${SERVERIP}"
            for ip in \"${ADDR[@]}\";
            do 
            echo $ip
            echo "here er can use scp command"
            scp -o strictHostKeychecking=no -i /tmp/awsaws.pem hello-${BUILD_NUMBER}.war ec2-user@ip:/var/lib/tomcat/webapps
            done
        }
    }
}
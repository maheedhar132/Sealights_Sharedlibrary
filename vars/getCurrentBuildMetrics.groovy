def call(){
sh "value='cat /var/lib/jenkins/workspace/${JOB_NAME}/buildSessionId'"
bsid= bsId.getText('UTF-8')
println(bsid)
sh """curl --location --request GET 'https://wipro.sealights.co/sl-api/v1/coverage/builds/$bsid' \
--header 'Authorization: Bearer $apiToken' """
}
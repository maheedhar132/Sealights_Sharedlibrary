import groovy.json.*

def getToolDetails(rigUrl,toolName,rigletName) {   // for getting tool details
     def output=sh(returnStdout: true, script: """
       curl -w '%{http_code}' -X POST \
       ${rigUrl}/api/riglets/connectorServerDetails \
       -H 'cache-control: no-cache' \
       -H 'content-type: application/json' \
       -d '{
            "rigletName":"${rigletName}",
            "toolName":"${toolName}"
           }'
    """)
    return output;
}

def statusChange(rigUrl,rigletName,toolName,action,status) {  // for sending status change message
    sh """
        curl --verbose -X POST \
        ${rigUrl}/api/riglets/statusChange \
        -H 'cache-control: no-cache' \
        -H 'content-type: application/json' \
        -d '{
              "rigletName":"${rigletName}",
              "toolName":"${toolName}",
              "action":"${action}",
              "status":"${status}"
            }'
      """
}

def saveToolProjectInfo(rigUrl,rigletName,toolName,projectKey,projectName,projectUrl) { // for saving details of created repository
    sh """
         curl -X POST \
         ${rigUrl}/api/riglets/saveToolProjectInfo \
         -H 'accept: application/json' \
         -H 'cache-control: no-cache' \
         -H 'content-type: application/json' \
         -d '{
               "toolName": "${toolName}",
               "rigletName": "${rigletName}",
               "data": {
                         "projectKey": "${projectKey}",
                         "projectName": "${projectName}",
                         "projectUrl": "${projectUrl}"
                       }
             }'
    """
}


def call() {

  
  getToolDetails(rigUrl,toolName,rigletName)

  statusChange(rigUrl,rigletName,toolName,action,status)

  saveToolProjectInfo(rigUrl,rigletName,toolName,projectKey,projectName,projectUrl)

}

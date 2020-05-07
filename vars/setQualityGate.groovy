def call(jsondata){
def jsonString = jsondata
def jsonObj = readJSON text: jsonString
def mC=jsonObj.sealights.modifiedCoverage
def oC=jsonObj.sealights.overallCoverage
def apiToken=jsonObj.sealights.api_token
sh """ curl --location --request POST 'https://wipro.sealights.co//sl-api/v1/apps/${JOB_NAME}/quality-gates' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer $apiToken' \
--header 'Content-Type: application/json' \
--data-raw '{
  "failedTests": {
    "testStages": [ "stage" ],
    "entireBuild": false
  },
  "modifiedCoverage": {
    "value": $mC,
    "testStages": [ "Combined Across Stages" ],
    "entireBuild": false
  },
  "overallCoverage": {
    "value": $oC,
    "testStages": [ "Combined Across Stages" ],
    "entireBuild": false
  }
}' """

}
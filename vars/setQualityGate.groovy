def call(jsondata,rigEnv){
def env = rigEnv
 def jsonString = jsondata
def jsonObj = readJSON text: jsonString
def jsonString2 = libraryResource 'data.json'
def jsonObj2 = readJSON text: jsonString2
int envCount = jsonObj2.environment.size();
String rigUrl
int a = 0;   //
while (a < envCount)
{
  if (jsonObj2.environment[a].name == env)
   {
    rigUrl=jsonObj2.environment[a].rigUrl
   }
   a++;
}

String code_quality_toolName=jsonObj.code_quality.tool.name
String rigletName=jsonObj.riglet_info.name

def output1 = utils.getToolDetails(rigUrl,code_quality_toolName,rigletName)
    def new_output1 = output1.substring(0, output1.lastIndexOf("}")  + 1)       
    def response_code_status1 = output1.substring(output1.lastIndexOf("}") +1, output1.lastIndexOf("}") +4)    // for getting response code
    if (response_code_status1 != "200")
    {
     println("Failed to reach backend url")
    }
    else
    {
     println("Successfully fetched the tool details")
    }
	def resultJson1 = readJSON text: new_output1
	String agentToken = resultJson1.agentToken
	String apiToken = resultJson1.apiToken
def mC=jsonObj.code_quality.applications.application[0].quality_gate[0].metrics[0].error
println(mC)
def oC=jsonObj.code_quality.applications.application[0].quality_gate[0].metrics[1].error
String appName=jsonObj.scm.projects.project[0].project_name

sh """ curl --location --request POST 'https://wipro.sealights.co//sl-api/v1/apps/${appName}/quality-gates' \
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
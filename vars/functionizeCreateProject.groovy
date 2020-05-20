@NonCPS
generateFunctionizeToken(apiKey,apiSec,roleName){
sh """
curl --location --request POST https://app.virtualautomationengineer.com/partnerapi/account/generatetoken?apiKey='${apiKey}'&apiSecret='${apiSec}'&role='${roleName}' -o funOutput.json
"""

}
@NonCPS
generateSessionToken(userId){
def var = readJSON file: 'funOutput.json' 
def funToken = var.responseData.functionizeToken
sh"""
curl --location --request POST https://app.virtualautomationengineer.com/partnerapi/user/login?functionizeToken='${funToken}'&functionizeUserIdentifier='${userId} -o userSession.json'
"""
}

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
def testing_toolName= jsonObj.ct.tool.name
def rigletName = jsonObj.riglet_info.$name
//String roleName = jsonObj.ct.projects.project.role
String projName = jsonObj.ct.projects.project.$project_name

def output = utils.getToolDetails(rigUrl,testing_toolName,rigletName)
def new_output = output.substring(0, output.lastIndexOf("}")  + 1)       
def response_code_status = output.substring(output.lastIndexOf("}") +1, output.lastIndexOf("}") +4)    // for getting response code
if (response_code_status != "200")
  {
      println("Failed to reach backend url")
  }
  else
  {
      println("Successfully fetched the tool details")
  }
def resultJson = readJSON text: new_output
println(resultJson)
String apiKey = resultJson.apiKey
String apiSec = resultJson.apiSecret
String roleName = resultJson.role
String userId = resultJson.userId

generateFunctionizeToken(apiKey,apiSec,roleName)

generateSessionToken(userId)


def user = readJSON file: 'userSession.json'
def SessionToken = user.responseData.userSessionToken


sh """
curl --location --request GET https://app.virtualautomationengineer.com/partnerapi/project/add?projectName='${projName}'&projectUrl='${toolUrl}'&userSessionToken='${SessionToken}'
"""

}
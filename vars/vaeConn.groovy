//Generate functionizeToken
@NonCPS
generateFunctionizeToken(apiKey,apiSec,roleName,siteUrl){
String apiKey_new = apiKey.replace("\n","")
String apiSec_new = apiSec.replace("\n","")
String url = siteUrl+'/partnerapi/account/generatetoken?apiKey='+apiKey_new+'&apiSecret='+apiSec_new+'&role='+roleName
sh """
curl --location --request POST '${url}' -o funOutput.json
"""
}

//Generate User Session Token
@NonCPS
generatesessionToken(funToken,userId,siteUrl){
funToken = funToken.replace("\n","")
userId = userId.replace("\n","")
gst_url = siteUrl+'/partnerapi/user/login?functionizeToken='+funToken+'&functionizeUserIdentifier='+userId
sh"""
curl --location --request POST '${gst_url}' -o userSession.json
"""
}

//Fetching List of Orchestrations
@NonCPS
fetchOrchDetails(projID,sessionToken){
String fol_url =  'https://app.virtualautomationengineer.com/partnerapi/orchestration/list?projectId='+projID+'&env=live&userSessionToken='+sessionToken
sh """
curl '${fol_url}' -o orchDetail.json
"""
}

//Running Orchestrations
@NonCPS
runOrch(depID,apiKey){
String ro_url='https://app.virtualautomationengineer.com/api/v1?method=processDeployment&'+'actionFor=execute'+'&deploymentid='+depID+'&apiKey='+apiKey
sh """
curl '${ro_url}' -o runOrch.json
"""
}

//Generate Access Token
@NonCPS
getAccessToken(apiKey,apiSec){
String at_url = 'https://app.virtualautomationengineer.com/api/oapi/getAccessToken/?apikey=' + apiKey+'&secret='+apiSec+'&response_type=json'
sh """
curl '${at_url}' -o accessToken.json
"""
}

//Fetch Orchestrations Status
@NonCPS
fetchOrchestrationStatus(accessToken,depId,runID){
String fos_url='https://app.virtualautomationengineer.com/api/oapi/processdeploymentstatusbyrunid/?accesstoken='+accessToken+'&deploymentid='+depId+'&runid='+runID+'&response_type=json'
sh"""
curl '${fos_url}' -o orchStatus.json
"""
}

//Run Orchestrations and also check if it is running.
@NonCPS
triggerOrch(accessToken,depId){

String to_url= 'https://app.virtualautomationengineer.com/api/oapi/rundeployment/?accesstoken='+accessToken+'&deploymentid='+depId+'&response_type=json'

sh """
curl '${to_url}'
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
String appUrl = jsonObj.ct.projects.project.$application_url

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
String siteUrl = resultJson.url
String userId= resultJson.functionizeUserIdentifier



generateFunctionizeToken(apiKey,apiSec,roleName,siteUrl)

	//Read token from the file
	def var = readJSON file: 'funOutput.json' 
	def funToken = var.responseData.functionizeToken



generatesessionToken(funToken,userId,siteUrl)

	//Read sessionToken
	def user = readJSON file: 'userSession.json'
	String sessionToken = user.responseData.userSessionToken
	
	println(sessionToken)
	
fetchOrchDetails('16564',sessionToken)	
	
	def orchdata = readJSON file: 'orchDetail.json'
	def i = 0
	String depID=''
	String runID=''
	while (orchdata.responseData[i].title != 'DigitalRig'){
	i++
	if(orchdata.responseData[i].title != 'DigitalRig'){
	depID=orchdata.responseData[i].orchJenkinId
	runID=orchdata.responseData[i].lastRunId
	}
	
	}
	//runOrch(depID,apiKey)

getAccessToken(apiKey,apiSec)


def accessTokenVar = readJSON file: 'accessToken.json'
def accessToken = accessTokenVar.access_token




//fetchOrchestrationStatus(accessToken,depID,runID)
triggerOrch(accessToken,depID)
}

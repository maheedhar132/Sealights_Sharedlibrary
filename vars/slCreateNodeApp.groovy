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

String toolName=jsonObj.scm.tool.name
String rigletName=jsonObj.riglet_info.name

String projName=jsonObj.scm.projects.project[0].project_name
String projDescription=jsonObj.scm.projects.project[0].project_description
String projId=projName  // TEMPORARY
boolean creation_status=jsonObj.scm.projects.project[0].create


def output = utils.getToolDetails(rigUrl,toolName,rigletName)
def new_output = output.substring(0, output.lastIndexOf("}")  + 1)       
def response_code_status = output.substring(output.lastIndexOf("}") +1, output.lastIndexOf("}") +4)    // for getting response code
if (response_code_status != "200")
  {
      println("Failed to reach backend url")
  }
  else
  {
      println("Successfully fetched the tool details")
  }// function for getting tool details
def resultJson = readJSON text: new_output
String user = resultJson.userName
String pass = resultJson.password
String url = resultJson.url

String projUrlName=projName.toLowerCase()

sh  "sudo rm -rf '${projUrlName}'"

sh "sudo git clone https://'${user}':'${pass}'@gitlab.com/'${user}'/'${projUrlName}'.git"


sh "cd '${projUrlName}' && sudo npm i slnodejs"

sh "cd '${projUrlName}'/client && sudo npm i slnodejs"
		
sh "./'${projUrlName}'/node_modules/.bin/slnodejs config --tokenfile /var/lib/jenkins/workspace/${JOB_NAME}/node_sltoken.txt --appname '${projName}' --branch 'master' --build 0"

sh "export SL_BUILD_SESSION_ID=`cat buildSessionId`"


sh "cd '${projUrlName}'/client && sudo npm install"

sh "cd '${projUrlName}' && sudo npm install"

sh "cd '${projUrlName}'/client && sudo npm run build"

sh 'cd '${projUrlName}'/client && ./node_modules/.bin/slnodejs build --tokenfile /var/lib/jenkins/workspace/${JOB_NAME}/node_sltoken.txt --buildsessionidfile /var/lib/jenkins/workspace/${JOB_NAME}/buildSessionId --instrumentForBrowsers  --workspacepath build --outputpath sl_build --scm none'







}
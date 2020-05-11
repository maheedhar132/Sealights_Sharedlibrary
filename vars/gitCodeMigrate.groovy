def call(jsondata,rigEnv,basecodeUrl,basecodeRepoName){
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


//The Actual Cloning Stage

sh "sudo rm -rf '${basecodeRepoName}'"

sh "sudo git clone '${basecodeUrl}'"

String projUrlName=projName.toLowerCase()

sh "sudo rm -rf '${projName}'"

sh " sudo rm -rf '${projUrlName}'"

sh "sudo git clone https://'${user}':'${pass}'@gitlab.com/'${user}'/'${projUrlName}'.git"

sh "sudo cp -ar '${basecodeRepoName}' '${projName}'"

/*sh "cd /home/'${projName}' &&sudo git add ."

sh "cd /home/'${projName}' &&sudo git commit -am 'migrated'"

sh "cd /home/'${projName}' &&sudo git remote set-url origin git@gitlab.com:'${user}'/'${projUrlName}'.git"


sh "cd /home/'${projName}' &&sudo git push origin master"
*/

 sh """ cd '${projName}'
         git checkout -b master
         npm --no-git-tag-version version minor
         git commit -am 'First Commit'
         git push origin master
      """







}
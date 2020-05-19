import groovy.json.*

@NonCPS
fetchBranchlist(gitlab_url,gitlab_user,projName,api_token)
{
	String	projCopy=projName.replace("\n","")
	String url=gitlab_url+'/api/v4/projects/'+gitlab_user+'%2F'+projCopy+'/repository/branches?private_token='+api_token
    println(url)
	sh "rm -rf branchList.json"
	sh "curl -X GET '${url}' -o branchList.json"
}

createBranch(gitlab_url,gitlab_user,projName,branchName,api_token)
{
    sh """
 curl  -w '%{http_code}' -o response.txt -X POST \
  '${gitlab_url}api/v4/projects/${gitlab_user}%2F${projName}/repository/branches?branch=${branchName}&ref=master' \
  -H 'content-type: application/json' \
  -H 'private-token: ${api_token}' > response_code.txt
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

String toolName=jsonObj.scm.tool.name
String rigletName=jsonObj.riglet_info.$name
String projname = readFile('gitlab_project_name.txt')
println(projname)
String projName=projname.toLowerCase()
//def projName = gitlabcreaterepo.projectInfo()   // the project which is created
String branchName=jsonObj.scm.projects.project[0].branches.branch[0].name
boolean creation_status= jsonObj.scm.projects.project[0].branches.branch[0].create
int branchCount= jsonObj.scm.projects.project[0].branches.branch.size()


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
def result = readJSON text: new_output
String gitlab_user = result.userName
String api_token = result.password
String gitlab_url = result.url

 
fetchBranchlist(gitlab_url,gitlab_user,projName,api_token)
def resultJson = readFile('branchList.json')
println(resultJson);
boolean emptyBranch = true;
int number = 0;
if(resultJson != "[]")
  {
    resultJson = readJSON file: 'branchList.json'
    emptyBranch = false;
    number = resultJson.size();
  }
int j=0; boolean flag = false;
println(branchCount);
println(number);
int i = 0;
def response_code
while(i<branchCount) {
   branchName=jsonObj.scm.projects.project[0].branches.branch[i].name
   println(branchName);
   while( j < number )
     {
       if( branchName == resultJson[j].name )
         {
           utils.statusChange(rigUrl,rigletName,toolName,"Branch creation","Branch "+branchName+" already created")
           flag = true;
         }
         j++;
     }
     j=0;
   if ( flag != true) {
     if( creation_status == true)
       {
         createBranch(gitlab_url,gitlab_user,projName,branchName,api_token)
         response_code = readFile file: 'response_code.txt'
         if ( response_code == "201" || response_code == "200")
          {
            utils.statusChange(rigUrl,rigletName,toolName,"Branch creation","success")
          }
        else {
        utils.statusChange(rigUrl,rigletName,toolName,"Branch creation","failure")
        }
      }
    }
    i++;
    flag = false;
}

}

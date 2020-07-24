import groovy.json.*



 
def call(jsondata,rigEnv){   // def call starts here
    
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

String toolName=jsonObj.scm.tool.name                              // main variables initialized here
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


//sh "curl -v --user '${user}':'${pass}' -H 'ContentType: application/json; charset=UTF-8''${url}'/api/v4/projects"

 
if( creation_status == true)         // if project needs to be created
{
  

  // this is main project creation function
    sh  """ curl -w '%{http_code}' -o response.txt -X POST \
    '${url}api/v4/projects?private_token=${pass}' \
    -H 'content-type: application/json' \
    -d '{
                "name": "${projName}",
                  "description": "${projDescription}",
                  "homepage": "https://gitlab.com",
                  "private": true,
                  "has_issues": true,
                  "has_projects": true,
                  "has_wiki": true
                }' > response_code.txt
                """
     def response_code = readFile file: 'response_code.txt'
 
		 if( response_code == "201" || response_code == "200")
     {
       println(" Project creation success");
      // utils.statusChange(rigUrl,rigletName,toolName,"Project creation","success")

      }
 
	 String projCopy = projName	
	 def i=1
	  while(response_code != "201" ){
		i++
	    sh "rm -rf response_code.txt"
		println("With name "+projName+" already Exists..!!");
		projName = projName+'_'+projCopy
		println ("Creating a Project with "+projName+" instead ;-)")
		
		    sh  """ curl -w '%{http_code}' -o response.txt -X POST \
				'${url}api/v4/projects?private_token=${pass}' \
					-H 'content-type: application/json' \
				-d '{
					"name": "${projName}",
					"description": "${projDescription}",
					"homepage": "https://gitlab.com",
					"private": true,
					"has_issues": true,
					"has_projects": true,
					"has_wiki": true
					}' > response_code.txt
                """
          response_code = readFile file: 'response_code.txt'
			
		 if( response_code == "201" || response_code == "200")
     {
       println(" Project creation success");
      // utils.statusChange(rigUrl,rigletName,toolName,"Project creation","success")
	}
	if(i==3){
	break;
	}

	  }
	 writeFile file: 'gitlab_project_name.txt', text: projName
 	 String projUrl= url+user+ "/"+projName.toLowerCase() + ".git"
	 utils.saveToolProjectInfo(rigUrl,rigletName,toolName,projId,projName,projUrl)
	 sh "rm -rf gitlab_project_name.txt"
	 sh "echo ${projName} > gitlab_project_name.txt"

	
}

} 

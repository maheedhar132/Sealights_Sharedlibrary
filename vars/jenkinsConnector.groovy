@NonCPS
createJob(jenkins_user,jenkins_pass,jenkins_url,jenkins_job_name)
{
    sh """
    curl -s -XPOST '${jenkins_url}createItem?name=${jenkins_job_name}' -u ${jenkins_user}:${jenkins_pass} --data-binary @${jenkins_job_name}.xml -H "Content-Type:text/xml"
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
	String code_quality_toolName=jsonObj.code_quality.tool.name
	String ci_toolName=jsonObj.ci.tool.name
	
	String rigletName=jsonObj.riglet_info.name
	String temp_job=jsonObj.ci.pipelines.pipeline.pipeline_name
	String jenkins_job_name=temp_job.replace("[","").replace("]","")
	println(jenkins_job_name)
	
	

	
	//Fetch CI tool Details
	output = utils.getToolDetails(rigUrl,ci_toolName,rigletName)
     new_output = output.substring(0, output.lastIndexOf("}")  + 1)       
     response_code_status = output.substring(output.lastIndexOf("}") +1, output.lastIndexOf("}") +4)    // for getting response code
      if (response_code_status != "200")
       {
      println("Failed to reach backend url")
       }
      else
       {
      println("Successfully fetched the tool details")
       }
     println(new_output)
     def result = readJSON text: new_output
     String jenkins_user = result.userName
     String jenkins_pass = result.password
	 println(jenkins_pass)
     String jenkins_url = result.url
	
	
	
	
	
	//GIT SCM TOOL Details
	
	String scm_toolName=jsonObj.scm.tool.name
//String rigletName=jsonObj.riglet_info.name

String projName=jsonObj.scm.projects.project[0].project_name
String projUrlName=projName.toLowerCase()

String projDescription=jsonObj.scm.projects.project[0].project_description
String projId=projName  // TEMPORARY
boolean creation_status=jsonObj.scm.projects.project[0].create


def output_scm = utils.getToolDetails(rigUrl,scm_toolName,rigletName)
def new_output_scm = output_scm.substring(0, output_scm.lastIndexOf("}")  + 1)       
def response_code_status_scm = output_scm.substring(output_scm.lastIndexOf("}") +1, output_scm.lastIndexOf("}") +4)    // for getting response code
if (response_code_status_scm != "200")
  {
      println("Failed to reach backend url")
  }
  else
  {
      println("Successfully fetched the tool details")
  }// function for getting tool details
def resultJson_scm = readJSON text: new_output_scm
String user = resultJson_scm.userName
String pass = resultJson_scm.password
String url = resultJson_scm.url
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	def git_scm_url = "https://'${user}':'${pass}'@gitlab.com/'${user}'/'${projUrlName}'.git"
	
	println(git_scm_url)
	
    def git_branch_name = "master"
	def cred = "maheedhar"
    def pipeline_template = WORKSPACE + "/vars/slNodeTemplete.groovy"
    checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'CleanBeforeCheckout']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'mahi-github', url: 'https://github.com/maheedhar132/Sealights_Sharedlibrary.git']]])
	
	
	
	
	def job_list = sh(returnStdout: true, script: """
     curl -XGET ${jenkins_url}api/json?pretty=true --user ${jenkins_user}:${jenkins_pass}
     """)
     def jobs_list = readJSON text: job_list
     int job_number = jobs_list.jobs.size();
     int b = 0; String job_copy = jenkins_job_name;
     boolean name_match = false;
     // for name validation 
      while ( b < job_number )
       {
           if (jobs_list.jobs[b].name == jenkins_job_name)
            {
                jenkins_job_name = jenkins_job_name + "-" + job_copy;
                b = 0; name_match = true;
            }
            else {
                b++;
            }
		}
		
	
	  
       def file = new File(pipeline_template)
       def newConfig = file.text.replace('${job_name}', jenkins_job_name).replace('${branch_name}', git_branch_name).replace('${cred}', cred).replace('${scm_url}', git_scm_url)
       file.text = newConfig
	
	   sh 'curl -O https://repo.jenkins-ci.org/public/org/jenkins-ci/plugins/job-dsl-core/1.77/job-dsl-core-1.77-standalone.jar'
     
       sh "java -jar job-dsl-core-1.77-standalone.jar  vars/slNodeTemplete.groovy"
       
      createJob(jenkins_user,jenkins_pass,jenkins_url,jenkins_job_name)    // for job creation
      String proj_url = jenkins_url + "job" + "/" + jenkins_job_name  // url of the jenkins job
      utils.saveToolProjectInfo(rigUrl,rigletName,ci_toolName,jenkins_job_name,jenkins_job_name,proj_url)
       
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
}
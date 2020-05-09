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
	String jenkins_job_name=jsonObj.ci.pipelines.pipeline.$pipeline_name
	
	
	
	def output = utils.getToolDetails(rigUrl,code_quality_toolName,rigletName)
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
	String agentToken = resultJson.agentToken
	String apiToken = resultJson.apiToken
	/*File file = new File("node_sltoken.txt")
	file.write(agentToken)*/
	sh "echo '${apiToken}' > node_sltoken.txt"
	
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
     String jenkins_url = result.url
	
	def git_scm_url = "https://github.com/maheedhar132/Game.git"
    def git_branch_name = "master"
	def cred = "maheedhar132"
	
	
	
	
	
	def git_scm_url = "https://github.com/maheedhar132/Sealights_Sharedlibrary.git"
    def git_branch_name = "master"
    def cred = "maheedhar132"
    def pipeline_template = WORKSPACE + "/vars/java_pipeline_template.groovy"
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
	
	
       
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
}
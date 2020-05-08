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
	String toolName=jsonObj.code_quality.tool.name
	String rigletName=jsonObj.riglet_info.name
	
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
    }
	println(new_output)
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
}
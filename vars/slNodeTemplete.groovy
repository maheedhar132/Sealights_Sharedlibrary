pipelineJob('${job_name}') {
     definition {
     cps {
      script('''
		node("master")
		{     
			currentBuild.result = 'SUCCESS'
        		stage("Checkout source code")
			{
			   checkout([$class: 'GitSCM', branches: [[name: "*/${branch_name}"]], 
			   doGenerateSubmoduleConfigurations: false, extensions: [],
			   submoduleCfg: [], 
			   extensions: [[$class: 'WipeWorkspace']],
			   userRemoteConfigs: [[credentialsId: "${cred}", 
			   url: "${scm_url}"]]]) 
                    		
        		}
				
        	stage("Build Session ID")
        	{
        	    sh "sudo npm i slnodejs"
				sh "cd client && sudo npm i slnodejs"
				sh "./node_modules/.bin/slnodejs config --tokenfile /var/lib/jenkins/workspace/${JOB_NAME}/${tokenFileName} --appname '${JOB_NAME}' --branch /''${Branch_Name}'/' --build '${BUILD_NUMBER}'"
        	}
        	
			stage('Build'){
			sh "cd client && sudo npm install"
			sh "sudo npm install"
			sh "cd client && sudo npm run build"
			sh 'cd client && ./node_modules/.bin/slnodejs build --tokenfile /var/lib/jenkins/workspace/${JOB_NAME}/${tokenFileName} --buildsessionidfile /var/lib/jenkins/workspace/${JOB_NAME}/buildSessionId --instrumentForBrowsers  --workspacepath build --outputpath sl_build --scm none'
			sh "mv client/build client/old_build"
			sh "mv client/sl_build client/build"
			}
			stage('Run The Application'){
			sh "if forever list | grep 'sl'; then forever stop sl; fi"
			sh "forever -a --uid sl start -c  'npm start'"
		    }
	  '''.stripIndent())
      sandbox()    
     }
     }
	 }

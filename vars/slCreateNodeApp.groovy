def call(jsondata){


def jsonString = jsondata
def jsonObj = readJSON text: jsonString
String a = jsonObj.ci.pipelines.pipeline.pipeline_name
String job_name = a.replace("[","").replace("]","")
//String gitRepoName = jsonObj.scm.projects.project[0].project_name


sh "sudo rm -rf Game"
sh "git clone https://github.com/maheedhar132/Game.git"
sh "cd Game && sudo npm i slnodejs"
sh "cd Game/client && sudo npm i slnodejs"
sh "Game/node_modules/.bin/slnodejs config --tokenfile /var/lib/jenkins/workspace/Game/node_sltoken.txt --appname 'Game' --branch 'master' --build 1"
/*sh "cd Game/client && sudo npm install"
sh "cd Game && sudo npm install"
sh "cd Game/client && sudo npm run build"
sh 'cd Game/client && Game/node_modules/.bin/slnodejs build --tokenfile /var/lib/jenkins/workspace/Game/node_sltoken.txt --buildsessionidfile /var/lib/jenkins/workspace/Game/buildSessionId --instrumentForBrowsers  --workspacepath build --outputpath sl_build --scm none'
sh "mv Game/client/build Game/client/old_build"
sh "mv Game/client/sl_build Game/client/build"*/
}
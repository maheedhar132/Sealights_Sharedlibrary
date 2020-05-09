def call(jsondata){


def jsonString = jsondata
def jsonObj = readJSON text: jsonString
String a = jsonObj.ci.pipelines.pipeline.pipeline_name
String job_name = a.replace("[","").replace("]","")



sh "git clone https://github.com/maheedhar132/Game.git"
sh "cd '${gitRepoName}' && sudo npm i slnodejs"
sh "cd '${gitRepoName}'/client && sudo npm i slnodejs"
sh "./node_modules/.bin/slnodejs config --tokenfile /var/lib/jenkins/workspace/${JOB_NAME}/node_sltoken.txt --appname '${job_name}' --branch 'master' --build 1"
/*sh "cd '${gitRepoName}'/client && sudo npm install"
sh "cd '${gitRepoName}' && sudo npm install"
sh "cd '${gitRepoName}'/client && sudo npm run build"
sh 'cd '${gitRepoName}'/client && ./node_modules/.bin/slnodejs build --tokenfile /var/lib/jenkins/workspace/${JOB_NAME}/node_sltoken.txt --buildsessionidfile /var/lib/jenkins/workspace/${JOB_NAME}/buildSessionId --instrumentForBrowsers  --workspacepath build --outputpath sl_build --scm none'
sh "mv '${gitRepoName}'/client/build '${gitRepoName}'/client/old_build"
sh "mv '${gitRepoName}'/client/sl_build '${gitRepoName}'/client/build"*/
}
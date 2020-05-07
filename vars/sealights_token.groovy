def call(jsondata){
def jsonString = jsondata
def jsonObj = readJSON text: jsonString
String a=jsonObj.sealights.token
  println(a)
  sh "echo a >> node_sltoken.txt"
}

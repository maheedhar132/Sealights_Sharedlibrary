def call(jsondata){
def jsonString = jsondata
def jsonObj = readJSON text: jsonString
def mC=jsonObj.sealights.modifiedCoverage
print(mC)
}
import os, datetime, shutil

#================================================================
choice = raw_input('Environment (1. alpha 2. prod/stage) : ')
server="prod"
if choice == "1":
  server="alpha"
if choice == "2":                                          
  server="prod"

list=['datanucleus_analytics.properties', 'datanucleus_content.properties', 
      'datanucleus_nnuser1.properties', 'datanucleus_nnuser2.properties', 'aws.properties', 
      'memcache.properties', 'queue.properties', 'sns.properties', 'piwik.properties']

for l in list:                                                                         
   src = server + "/" + l                                                
   dst = "../src/main/resources/" + l
   shutil.copyfile(src, dst)


#================================================================                         
version = raw_input('Enter version number : ')
source = open(".svn//entries", "rU")
cnt = 0
for line in source:
  cnt = cnt + 1
  if cnt == 4:
    rev =  line.rstrip()
    break
source.close
print "Revision number:" + rev
now = datetime.datetime.utcnow()
print now
old_file = open("..//src//main//java//com//nncloudtv//web//VersionController.java", "rU")
new_file = open("..//src//main//java//com//nncloudtv//web//VersionController.java.tmp",'w')
for line in old_file:
  if (line.find("String appVersion") > 0):   
     line = "        String appVersion = \"" + version + "\";\n"
  if (line.find("String svn") > 0):   
     line = "        String svn = \"" + rev + "\";\n"	
  if (line.find("String packagedTime") > 0):   
     line = "        String packagedTime = \"" + str(now) + "\";\n"
  new_file.write(line)
old_file.close()
new_file.close()
                                                            
os.remove("..//src//main//java//com//nncloudtv//web//VersionController.java")
os.rename("..//src//main//java//com//nncloudtv//web//VersionController.java.tmp", "..//src//main//java//com//nncloudtv//web//VersionController.java")

#================================================================
os.chdir("..//")                                                           
os.system("mvn clean compile")                                                 
os.system("mvn datanucleus:enhance")
os.system("mvn war:war")

os.chdir("..//nncms")                                                           
os.system("mvn clean compile")                                                 
os.system("mvn war:war")


print "\n--- summary ---\n"
print "Package environment:" + server
print "Version number:" + version
print "Revision number:" + rev


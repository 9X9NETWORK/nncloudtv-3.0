import os, datetime, shutil

#================================================================
choice = raw_input('Environment (1.devel 2.alpha 3.prod/stage) : ')
server="dev"
if choice == "2":
 server="alpha"
if choice == "3":                                          
 server="prod"

src = server + "//datanucleus_analytics.properties"
dst = "..//src//main//resources//datanucleus_analytics.properties"
shutil.copyfile(src, dst)

src = server + "//datanucleus_content.properties"
dst = "..//src//main//resources//datanucleus_content.properties"
shutil.copyfile(src, dst)

src = server + "//datanucleus_nnuser1.properties"
dst = "..//src//main//resources//datanucleus_nnuser1.properties"
shutil.copyfile(src, dst)

src = server + "//datanucleus_nnuser2.properties"
dst = "..//src//main//resources//datanucleus_nnuser2.properties"                                   
shutil.copyfile(src, dst)

src = server + "//aws.properties"
dst = "..//src//main//resources//aws.properties"
shutil.copyfile(src, dst)

src = server + "//memcache.properties"
dst = "..//src//main//resources//memcache.properties"
shutil.copyfile(src, dst)

src = server + "//queue.properties"
dst = "..//src//main//resources//queue.properties"
shutil.copyfile(src, dst)

src = server + "//sns.properties"
dst = "..//src//main//resources//sns.properties"
shutil.copyfile(src, dst)

src = server + "//piwik.properties"
dst = "..//src//main//resources//piwik.properties"
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

print "\n--- mvn clean:clean ---\n"
os.chdir("..//")                                                           
os.system("mvn clean:clean")                     
print "\n--- mvn compile ---\n"
os.system("mvn compile")                            
print "\n--- mvn datanucleus:enhance ---\n"
os.system("mvn datanucleus:enhance")
print "\n--- mvn compile war:war ---\n"
os.system("mvn compile war:war")
                                               
print "\n--- summary ---\n"
print "Package environment:" + server
print "Version number:" + version
print "Revision number:" + rev


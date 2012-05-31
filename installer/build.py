import os, datetime, shutil

version = raw_input('Enter version number : ')
source = open(".svn\\entries", "rU")
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
old_file = open("..\\src\\main\\java\\com\\nncloudtv\\web\\VersionController.java", "rU")
new_file = open("..\\src\\main\\java\\com\\nncloudtv\\web\\VersionController.java.tmp",'w')
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
                                                            
os.remove("..\\src\\main\\java\\com\\nncloudtv\\web\\VersionController.java")
os.rename("..\\src\\main\\java\\com\\nncloudtv\\web\\VersionController.java.tmp", "..\\src\\main\\java\\com\\nncloudtv\\web\\VersionController.java")

print "--- mvn clean:clean ---"
os.chdir("..//")             
#os.system("dir")                                       
os.system("mvn clean:clean")                     
print "--- mvn compile ---"
os.system("mvn compile")                            
print "--- mvn datanucleus:enhance ---"
os.system("mvn datanucleus:enhance")
print "--- mvn compile war:war ---"
os.system("mvn compile war:war")
                                               

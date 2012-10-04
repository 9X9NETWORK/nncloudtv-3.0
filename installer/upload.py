import shutil, os, re, sys, smtplib, commands
from email.MIMEText import MIMEText
from datetime import datetime

# it does
#   1.copies over root.war
#   2.write md5 to root.md5 file
#   3.write version file
#   4.upload files to server:
#     prerequisites: your own key files, pscp(change to your own scp programs)

myos = raw_input('OS (1.Ubuntu 2.Windows) : ')

myfile = open("..//src//main//java//com//nncloudtv//web//VersionController.java", "rU")
version = ""
for line in myfile:
  if (line.find("String appVersion") > 0):
     start = line.find("\"")+1;   
     end = line.rfind("\"");
     version = line[start:end]

if version == "":
  version = raw_input('Enter version number : ') 

print "version number:" + version

# copy over root.war
src = "../target/root.war"
dst = "root.war"
shutil.copyfile(src, dst)                                                                                       

src = "../../nncms/target/cms.war"
dst = "cms.war"
shutil.copyfile(src, dst)                                                                                       

print "--- generate root.war ---"

# generate md5
if myos == "1":
  md5 = os.popen("md5sum root.war").read()
  cmsMd5 = os.popen("md5sum cms.war").read()
if myos == "2":
  md5 = os.popen("tools\md5sums -u root.war").read()
match = re.match("(.*)( .*)", md5)
cmsmatch = re.match("(.*)( .*)", md5)
if match:
  md5 = match.group(1)
if cmsmatch:
  cmsMd5 = cmsmatch.group(1)

print "--- generate md5 = " + md5
print "--- generate cms md5 = " + cmsMd5

dest = open("root.md5", "w")
line = md5 + " " + "root.war\x00\x0a"
dest.write(line)
dest.close()

dest = open("cms.md5", "w")
line = md5 + " " + "cms.war\x00\x0a"
dest.write(line)
dest.close()

# write version file
dest = open("version", "w")
line = version
dest.write(line)
dest.close()
print "--- generate version file ---"

# upload to server
print "\n"                                             
server = raw_input('Server (1.devel1 2.alpha 3.stage 4.deploy 5.exit) : ')
if server != "5":
  print "--- " + datetime.now().strftime("%d/%m/%y %H:%M:%S %Z") + "---"
if server == "1":
  print "--- uploading to devel1 server ---"
  if myos == "1":
    os.system("scp -i ~/keys/dev-west2.pem root.war ubuntu@ec2-50-112-54-59.us-west-2.compute.amazonaws.com:/home/ubuntu/files/root.war")
  if myos == "2":
    os.system("pscp -i dev-west2.ppk root.war ubuntu@ec2-50-112-54-59.us-west-2.compute.amazonaws.com:/home/ubuntu/files")
if server == "2":
  print "--- uploading to alpha server ---"
  folder = raw_input('Folder created? : ')  # to be hooked up with ssh command, used as reminder for now
  if myos == "1":            
    os.system("scp -i ~/keys/dev-west2.pem root.war ubuntu@alpha_log.9x9.tv:/var/www/updates/" + version + "/root.war")
    os.system("scp -i ~/keys/dev-west2.pem root.md5 ubuntu@alpha_log.9x9.tv:/var/www/updates/" + version + "/root.md5")
    os.system("scp -i ~/keys/dev-west2.pem version ubuntu@alpha_log.9x9.tv:/var/www/updates/version")  
    os.system("scp -i ~/keys/dev-west2.pem version ubuntu@alpha_log.9x9.tv:/var/www/updates/" + version + "/version")
  if myos == "2":           
     os.system("pscp -i dev-west2.ppk root.war ubuntu@alpha_log.9x9.tv:/var/www/updates/" + version)
     os.system("pscp -i dev-west2.ppk root.md5 ubuntu@alpha_log.9x9.tv:/var/www/updates/" + version)
     os.system("pscp -i dev-west2.ppk version ubuntu@alpha_log.9x9.tv:/var/www/updates")  
     os.system("pscp -i dev-west2.ppk version ubuntu@alpha_log.9x9.tv:/var/www/updates/" + version)
if server == "3":
  print "--- uploading to stage server ---"
  if myos == "1":             
    os.system("scp -i ~/keys/prod-west2.pem root.war ubuntu@ec2-50-112-111-245.us-west-2.compute.amazonaws.com:/home/ubuntu/files/root.war")
  if myos == "2": 
    os.system("pscp -i prod-west2.ppk root.war ubuntu@ec2-50-112-111-245.us-west-2.compute.amazonaws.com:/home/ubuntu/files")
if server == "4":
  print "--- uploading to deploy server ---"
  folder = raw_input('Folder created? : ')  # to be hooked up with ssh command, used as reminder for now
  if myos == "1":                  
     os.system("scp -i ~/keys/prod-west2.pem root.war ubuntu@ec2-50-112-96-199.us-west-2.compute.amazonaws.com:/var/www/updates/" + version + "/root.war")
     os.system("scp -i ~/keys/prod-west2.pem root.md5 ubuntu@ec2-50-112-96-199.us-west-2.compute.amazonaws.com:/var/www/updates/" + version + "/root.md5")
     os.system("scp -i ~/keys/prod-west2.pem version ubuntu@ec2-50-112-96-199.us-west-2.compute.amazonaws.com:/var/www/updates/version")  
     os.system("scp -i ~/keys/prod-west2.pem version ubuntu@ec2-50-112-96-199.us-west-2.compute.amazonaws.com:/var/www/updates/" + version + "/version")
     
     os.system("scp -i ~/keys/prod-west2.pem cms.war ubuntu@ec2-50-112-96-199.us-west-2.compute.amazonaws.com:/var/www/updates/cms/cms.war")
     os.system("scp -i ~/keys/prod-west2.pem cms.md5 ubuntu@ec2-50-112-96-199.us-west-2.compute.amazonaws.com:/var/www/updates/cms/cms.md5")
if myos == "2": 
     os.system("pscp -i prod-west2.ppk root.war ubuntu@ec2-50-112-96-199.us-west-2.compute.amazonaws.com:/var/www/updates/" + version)
     os.system("pscp -i prod-west2.ppk root.md5 ubuntu@ec2-50-112-96-199.us-west-2.compute.amazonaws.com:/var/www/updates/" + version)
     os.system("pscp -i prod-west2.ppk version ubuntu@ec2-50-112-96-199.us-west-2.compute.amazonaws.com:/var/www/updates")  
     os.system("pscp -i prod-west2.ppk version ubuntu@ec2-50-112-96-199.us-west-2.compute.amazonaws.com:/var/www/updates/" + version)

print "--- " + datetime.now().strftime("%d/%m/%y %H:%M:%S %Z") + "---"  
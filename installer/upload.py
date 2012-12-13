import urllib, urllib2
import shutil, os, re, sys, smtplib, commands
import paramiko
from email.MIMEText import MIMEText
from datetime import datetime
                                               
# it does
#   1.copies over root.war             
#   2.write md5 to root.md5 file
#   3.write version file
#   4.upload files to server:
#     prerequisites: your own key files

def getSSH(server):
   host = "stage.9x9.tv"
   if server == "pdr":
     host = "v32d.9x9.tv"
   if server == "prod":    
     host = "moveout-log.9x9.tv"     
   
   privatekeyfile = os.path.expanduser('~/keys/prod-west2.pem')
   mykey = paramiko.RSAKey.from_private_key_file(privatekeyfile)
   ssh = paramiko.SSHClient()
   ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
   ssh.connect(host, username = 'ubuntu', pkey = mykey)
   return ssh

#---- write version file ----  
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

#---- copy war files ----       
src = "../target/root.war"         
dst = "root.war"
shutil.copyfile(src, dst)                                                                                       

src = "../../nncms/target/cms.war"
dst = "cms.war"
shutil.copyfile(src, dst)                                                                                       

#---- generate md5 ----            
md5 = os.popen("md5sum root.war").read()
match = re.match("(.*)( .*)", md5)       
if match:                          
  md5 = match.group(1)

cmsMd5 = os.popen("md5sum cms.war").read()
cmsmatch = re.match("(.*)( .*)", cmsMd5)
if cmsmatch:   
  cmsMd5 = cmsmatch.group(1)

print "--- generate md5 = " + md5
print "--- generate cms md5 = " + cmsMd5

dest = open("root.md5", "w")      
line = md5 + " " + "root.war\x00\x0a"
dest.write(line)
dest.close()             

dest = open("cms.md5", "w")      
line = cmsMd5 + " " + "cms.war\x00\x0a"
dest.write(line)
dest.close()                           
                   
#---- generate  version file ----              
print "--- generate version file ---"
dest = open("version", "w")
line = version
dest.write(line)
dest.close()

#--------------- upload to server
print "\n"                                             
server = raw_input('Server (1.stage 2.prod 3.exit) : ')
if server == "1":      
  print "--- uploading to stage server ---"                                                 
  os.system("scp -i ~/keys/prod-west2.pem root.war ubuntu@stage.9x9.tv:/home/ubuntu/files/root.war")
  os.system("scp -i ~/keys/prod-west2.pem cms.war ubuntu@stage.9x9.tv:/home/ubuntu/files/cms.war")
if server == "2":     
  print "--- uploading to pdr server ---"                                                 
  os.system("scp -i ~/keys/prod-west2.pem root.war ubuntu@v32d.9x9.tv:/home/ubuntu/files/root.war")
  os.system("scp -i ~/keys/prod-west2.pem cms.war ubuntu@v32d.9x9.tv:/home/ubuntu/files/cms.war")
  
  print "--- uploading to deploy server ---"                             
  ssh = getSSH("prod")  
  stdin, stdout, stderr = ssh.exec_command('mkdir /var/www/updates/root/' + version)
  stdin, stdout, stderr = ssh.exec_command('mkdir /var/www/updates/cms/' + version)  
  print "[moveout-log] ls /var/www/updates/root"
  stdin, stdout, stderr = ssh.exec_command('ls /var/www/updates/root/')
  print stdout.readlines()
  print "[moveout-log] ls /var/www/updates/cms"
  stdin, stdout, stderr = ssh.exec_command('ls /var/www/updates/cms/')
  print stdout.readlines()
  ssh.close()                                  
                                  
  os.system("scp -i ~/keys/prod-west2.pem root.war ubuntu@moveout-log.9x9.tv:/var/www/updates/root/" + version + "/root.war")
  os.system("scp -i ~/keys/prod-west2.pem root.md5 ubuntu@moveout-log.9x9.tv:/var/www/updates/root/" + version + "/root.md5")
  os.system("scp -i ~/keys/prod-west2.pem version ubuntu@moveout-log.9x9.tv:/var/www/updates/root/version")
  os.system("scp -i ~/keys/prod-west2.pem version ubuntu@moveout-log.9x9.tv:/var/www/updates/root/" + version + "/version")
                                  
  os.system("scp -i ~/keys/prod-west2.pem cms.war ubuntu@moveout-log.9x9.tv:/var/www/updates/cms/" + version + "/cms.war")
  os.system("scp -i ~/keys/prod-west2.pem cms.md5 ubuntu@moveout-log.9x9.tv:/var/www/updates/cms/" + version + "/cms.md5")     
  os.system("scp -i ~/keys/prod-west2.pem version ubuntu@moveout-log.9x9.tv:/var/www/updates/cms/version")
  os.system("scp -i ~/keys/prod-west2.pem version ubuntu@moveout-log.9x9.tv:/var/www/updates/cms/" + version + "/version")
                                                                                   
#---- deploy ----
print "\n"                                                       
server = raw_input('Deploy? (1.stage 2.prod 3.exit) : ')
if server == "1":      
  print "--- deploying on stage server ---"
  ssh = getSSH("stage")
  stdin, stdout, stderr = ssh.exec_command('cd files;sh installer.sh')
  print stdout.readlines()                                                   
  ssh.close()
if server == "2":  
  print "--- deploying to pdr server ---"
  ssh = getSSH("pdr")
  stdin, stdout, stderr = ssh.exec_command('cd files;sh installer.sh')
  print "--- deploying to prod server ---"
  ssh = getSSH("prod")
  stdin, stdout, stderr = ssh.exec_command('cd bin/v32;./deploy_all_wars.sh')
  print stdout.readlines()
  ssh.close()                                  
  
#---- memcache ----
print "\n"                                                       
answer = raw_input('Clean memcache? (1.yes 2.exit) : ')
if answer == "1":
   url = "http://www.9x9.tv/playerAPI/flush"                                                         
   urllib2.urlopen(url).read()
   print "--- cache cleaned ---"

  

import shutil

choice = raw_input('Environment (1.devel 2.alpha 3.prod 4.exit) : ')
server="dev"
if choice == "2":
 server="alpha"
if choice == "3":
 print "prod"
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


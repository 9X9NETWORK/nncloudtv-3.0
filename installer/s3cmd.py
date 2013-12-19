### this script (s3cmd.py) may not be used ###

import os

choice = raw_input('Environment (1.alpha 2.prod ) : ')

if choice == "1":
   os.system("s3cmd sync --acl-public --exclude '**.svn**' ../src/main/webapp/images/ s3://9x9ui/war/v1/images/")
   os.system("s3cmd sync --acl-public --exclude '**.svn**' ../src/main/webapp/javascripts/ s3://9x9ui/war/v1/javascripts/")
   os.system("s3cmd sync --acl-public --exclude '**.svn**' ../src/main/webapp/stylesheets/ s3://9x9ui/war/v1/stylesheets/")
 
if choice == "2":
   os.system("s3cmd sync --acl-public --exclude '**.svn**' ../src/main/webapp/images/ s3://9x9ui/war/v0/images/")
   os.system("s3cmd sync --acl-public --exclude '**.svn**' ../src/main/webapp/javascripts/ s3://9x9ui/war/v0/javascripts/")
   os.system("s3cmd sync --acl-public --exclude '**.svn**' ../src/main/webapp/stylesheets/ s3://9x9ui/war/v0/stylesheets/")

# s3cmd setacl --acl-public --recursive s3://9x9ui/war/v0/

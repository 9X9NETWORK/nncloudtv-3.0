import os

os.system("s3cmd sync --exclude '**.svn**' ../src/main/webapp/images/ s3://9x9ui/war/v0/images/")
os.system("s3cmd sync --exclude '**.svn**' ../src/main/webapp/javascripts/ s3://9x9ui/war/v0/javascripts/")
os.system("s3cmd sync --exclude '**.svn**' ../src/main/webapp/stylesheets/ s3://9x9ui/war/v0/stylesheets/")

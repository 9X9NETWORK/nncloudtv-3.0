import urllib, urllib2
import os
from array import *
import MySQLdb
import time

dbcontent = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = "",
                             charset = "utf8",
                             use_unicode = True,
                             db = "nncloudtv_content")

feed = open("billboardPool.txt", "rU")
i = 0
for line in feed:
  data = line.split('\t')
  cId = data[0]
  contentCursor = dbcontent.cursor() 
  contentCursor.execute("""
     update nnchannel 
        set poolType = 30
      where id = %s
      """, (cId))     
  i = i+1
 
dbcontent.commit()  
contentCursor.close ()

print "record done:" + str(i)
feed.close()

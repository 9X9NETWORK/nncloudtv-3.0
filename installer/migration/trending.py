#process billboard channels

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

feed = open("trending_zh.txt", "rU")
################################
i = 19
for line in feed:
  cId = line
  contentCursor = dbcontent.cursor() 
  contentCursor.execute("""
      update tag_map
         set channelId = %s
       where id = %s
      """, (cId, i))     
  i = i+1
dbcontent.commit()
##################################
feed = open("trending_zh.txt", "rU")
i = 28
for line in feed:
  cId = line
  contentCursor = dbcontent.cursor() 
  contentCursor.execute("""
      update tag_map
         set channelId = %s
       where id = %s
      """, (cId, i))     
  i = i+1
dbcontent.commit()  
contentCursor.close ()

print "record done:" + str(i)
feed.close()

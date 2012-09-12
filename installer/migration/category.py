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

contentCursor = dbcontent.cursor()
contentCursor.execute("""truncate category """)   

feed = open("category_en.txt", "rU")
i = 1
for line in feed:  
  line = line.rstrip()
  line = line.lstrip()
  contentCursor.execute("""
     insert into category
       (createDate, isPublic, lang, seq, name, updateDate)
     values
       (now(), true, 'en', %s, %s, now())              
     """, (i, line))
  dbcontent.commit()
  i = i +1
print "record english done:" + str(i)  
########
feed = open("category_zh.txt", "rU")
i = 1
for line in feed:  
  line = line.rstrip()
  line = line.lstrip()
  contentCursor.execute("""
     insert into category
       (createDate, isPublic, lang, seq, name, updateDate)
     values
       (now(), true, 'zh', %s, %s, now())              
     """, (i, line))
  dbcontent.commit()
  i = i +1 
print "record chinese done:" + str(i)
  
contentCursor.close ()
dbcontent.close ()
print "record done:" + str(i)
feed.close()


# initialize tag information
# 1) basic value for hot and thrending
# 2) give channel tags

import urllib, urllib2
import os
from array import *
import MySQLdb
import time
import string
import codecs

dbcontent = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = "",
                             charset = "utf8",
                             use_unicode = True,
                             db = "nncloudtv_content")
cursor = dbcontent.cursor()
cursor.execute("""
  select id
    from nnepisode
   where duration = 0
   """)
rows = cursor.fetchall()
i=0
for r in rows:
   i = i + 1
   eid = r[0]
   url = "http://localhost:8080/playerAPI/episodeUpdate?epId=" + str(eid)
   print url
   urllib2.urlopen(url).read()
   print "eid:" + str(eid) 

dbcontent.close()

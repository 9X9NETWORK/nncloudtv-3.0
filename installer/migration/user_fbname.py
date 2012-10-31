# get latest youtube update from tom's feed.out

import urllib, urllib2
import os
from array import *
import MySQLdb
import time

dbuser = MySQLdb.connect (host = "localhost",
                          user = "root",
                          passwd = "",
                          charset = "utf8",
                          use_unicode = True,
                          db = "nncloudtv_nnuser1")
cursor = dbuser.cursor()
cursor.execute("""
   select id, email, fbId, token from nnuser where fbId is not null;
   """)
rows = cursor.fetchall()
i = 0
for r in rows:
   uid = r[0]
   url = "http://localhost:8080/playerAPI/resetFbName?id=" + str(uid)
   print url
   urllib2.urlopen(url).read()
   url = "http://localhost:8080/playerAPI/resetFbPic?id=" + str(uid)
   urllib2.urlopen(url).read()
   print url
   i = i + 1

dbuser.commit()
cursor.close ()

print "record done:" + str(i)


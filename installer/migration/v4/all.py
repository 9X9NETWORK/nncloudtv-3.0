#process world wide channels

import urllib, urllib2
import os
from array import *
import MySQLdb
import time
import translate

dbcontent = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = "",
                             charset = "utf8",
                             use_unicode = True,
                             db = "nncloudtv_content")

i = 0
print "--- category all ---"
cursor = dbcontent.cursor()
try:
   cursor.execute("""
          insert into systag_map (systagId, channelId, createDate, updateDate) (select 1, channelId, now(), now() from systag_map where systagId < 20 order by systagId) on duplicate key update systagId = 1;
       """)
   dbcontent.commit()
except MySQLdb.IntegrityError:
   print "duplicate key"


cursor.close ()


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
cursor.execute("""truncate nnepisode""")
cursor.execute("""
  select p.name, p.imageUrl, p.intro, c.id, p.id
    from nnprogram p, nnchannel c
   where c.contentType = 6
    and c.id = p.channelId
   """)
rows = cursor.fetchall()
for r in rows:
   cursor.execute("""
      insert into nnepisode (name, imageUrl, intro, publishDate, updateDate, channelId)
                     values (%s, %s, %s, now(), now(), %s) 
       """, (r[0], r[1], r[2], r[3]))   
   pid = r[4]
   eid = cursor.lastrowid
   print str(pid) + ";" + str(eid)   
   cursor.execute("""update nnprogram set episodeId = %s
                      where id = %s
                   """, (eid, r[4]))
dbcontent.commit()
dbcontent.close()

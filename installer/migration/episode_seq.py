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
   select distinct p.episodeId, p.seq
     from nnprogram p, nnchannel c
    where c.contentType = 6
      and c.id = p.channelId 
   """)
rows = cursor.fetchall()
i=0
for r in rows:
   i = i + 1        
   eid = r[0]
   seq = int(r[1])
   print "eid:" + str(eid) + ";seq:" + str(seq)   
   cursor.execute("""
      update nnepisode
         set seq = %s
       where id = %s 
      """, (seq, eid))
  
dbcontent.commit()
dbcontent.close()

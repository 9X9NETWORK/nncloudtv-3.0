# scheduler to publish the pisode

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
   select id, channelId, scheduleDate from  nnepisode where scheduleDate < date_add(now(), interval 10 minute);
   """)                                                                                
rows = cursor.fetchall()
i=0
for r in rows:                                               
   eid = r[0]
   cid = r[1]
   print "publish eid:" + str(eid)
   # update its property 
   cursor.execute("""
      update nnepisode                                                     
         set isPublic = true, publishDate = now(), scheduleDate = null, seq = 1    
       where id = %s                                    
      """, (eid))              
   # change the specific episode to seq = 1           
   cursor.execute("""
      select id, seq from nnepisode                                                                   
       where channelId = %s
         and id != %s 
      order by seq
      """, (cid, eid))       
   seqrows = cursor.fetchall()
   i = 2
   for seqr in seqrows:
      seid = seqr[0]
      print "modify all episode seq - eid:" + str(seid) + " with seq: " + str(i) 
      cursor.execute("""
         update nnepisode
            set seq = %s
          where id = %s
         """, (i, seid))    
      i = i + 1
   dbcontent.commit()

dbcontent.close()          

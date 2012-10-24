# scheduler to publish the episode
# cron job is scheduled to run at every hour xx:58

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
   select id, channelId, scheduleDate, isPublic 
     from  nnepisode 
     where scheduleDate > now()              
       and scheduleDate < date_add(now(), interval 10 minute);   
   """)                                                                                                                      
rows = cursor.fetchall()
i=0 #episode published                                      
k=0 #episode re-run
for r in rows:  
   eid = r[0]
   cid = r[1]                           
   scheduleDate = r[2]                                                                 
   isPublic = r[3]                     
   print 'Success' if isPublic == '\x00' else 'Fail'
   if isPublic == '\x00':           
      isPublic = True
   else:
      isPublic = False
   print "publish eid:" + str(eid) + "; its cid: " + str(cid) + ";schedule date:" + str(scheduleDate) + ";isPublic:"  
   # update its property 
   cursor.execute("""                     
      update nnepisode                                                     
         set isPublic = true, publishDate = now(), scheduleDate = null    
       where id = %s                                    
      """, (eid))       
   if isPublic: #rerun        
      print "--- rerun ---"
      cursor.execute("""  
         update nnepisode                                                     
            set seq = 1                           
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
      j = 2                                         
      for seqr in seqrows:
         seid = seqr[0]
         print "modify all episode seq - eid:" + str(seid) + " with seq: " + str(j) 
         cursor.execute("""                                                
            update nnepisode
               set seq = %s
             where id = %s
            """, (j, seid))    
         j = j + 1 
      k = k + 1
   dbcontent.commit()                         
   i = i + 1      
                                                                          
dbcontent.close()
print "total episode published:" + str(i)
print "episode re-run:" + str(k)

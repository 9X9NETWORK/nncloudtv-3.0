# scheduler to publish the episode
# cron job is scheduled to run at every hour xx:58

import urllib, urllib2
import os
from array import *
import MySQLdb
import time
import string
import codecs
import pycurl
#import pika

class GetPage:
    def __init__ (self, url):
        self.contents = ''
        self.url = url

    def read_page (self, buf):
        self.contents = self.contents + buf

    def show_page (self):
        print self.contents
        

autoshareCurl = pycurl.Curl()

#connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))

                                  
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
   if isPublic == '\x00':
   	  print 'Success'
   else:
   	  print 'Fail'
   if isPublic == '\x00':  
      isPublic = False
   else:
      isPublic = True
   print "publish eid:" + str(eid) + "; its cid: " + str(cid) + ";schedule date:" + str(scheduleDate) + ";isPublic:" + str(isPublic)  
   # update its property     
   cursor.execute("""                     
      update nnepisode                                                     
         set isPublic = true, publishDate = now(), scheduleDate = null    
       where id = %s                                    
      """, (eid))       
   if isPublic == True:  #rerun    
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
   cursor.execute("""                     
      update nnchannel                                                     
         set updateDate = now()    
       where id = %s                                    
      """, (cid))             
   dbcontent.commit()                         
   url = "http://localhost:8080/wd/channelCache?channel=" + str(cid)
   urllib2.urlopen(url).read()
   
   resultPage = GetPage("http://localhost:8080/api/episodes/" + str(eid) + "/scheduledAutosharing/facebook")
   autoshareCurl.setopt(autoshareCurl.URL, resultPage.url)
   autoshareCurl.setopt(autoshareCurl.WRITEFUNCTION, resultPage.read_page)
   autoshareCurl.perform()
   print "autosharing episode ID : " + str(eid)
   resultPage.show_page()
   
   #message = "http://localhost:8080/api/episodes/" + str(eid) + "/scheduledAutosharing/facebook"
   #message = bytearray(message)
   #channel = connection.channel()
   #channel.queue_declare(queue='/')
   #channel.basic_publish(exchange="", routing_key="QUEUE_NNCLOUDTV", body=message)
   #channel.close()
   
   i = i + 1                  
                                                                          
dbcontent.close()
    
autoshareCurl.close()
#connection.close()

#if i > 1:
#   print "flush cache" 
#   url = "http://localhost:8080/playerAPI/flush"
#   urllib2.urlopen(url).read()

print "total episode published:" + str(i)
print "episode re-run:" + str(k)

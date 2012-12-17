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
cursor = dbcontent.cursor()
cursor.execute ("""            
   select id, sourceUrl
    from nnchannel
    where contentType=3
    group by lower(sourceUrl)
    having (count(lower(sourceUrl)) > 1)
   order by id;
		 """)		           
duplicate_urls = cursor.fetchall()
for dup_url in duplicate_urls:
   sourceUrl = dup_url[1]
   print sourceUrl
   cursor.execute ("""            
      select id, sourceUrl
       from nnchannel
      where lower(sourceUrl) = lower(%s)  
     		 """, sourceUrl)      
   duplicate_rows = cursor.fetchall()
   i = 1
   base_cid = 0
   for dup_row in duplicate_rows:
      if i == 1:
         base_cid = dup_row[0]         
      if i > 1:
         cid = dup_row[0]
         print str(base_cid) + ";" + str(cid)
         cursor.execute ("""
            update tag_map set channelId = %s where channelId = %s 
          """, (base_cid, cid))        
         cursor.execute ("""
            update nncloudtv_nnuser1.nnuser_subscribe set channelId = %s where channelId= %s 
          """, (base_cid, cid))        
         cursor.execute ("""
            delete from nnchannel where id= %s; 
          """, (cid))
         cursor.execute ("""
            delete from ytprogram where channelId = %s; 
          """, (cid))         
      i = i + 1   
   dbcontent.commit()
   
dbcontent.close()       

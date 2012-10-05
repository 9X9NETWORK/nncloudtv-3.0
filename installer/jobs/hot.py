# process hot channels
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

cursor.execute("""
	 select max(id) from nnchannel      
	 """)
row = cursor.fetchone()
startIdx = row[0] + 1
endIdx = startIdx + 18                                  
i = 1
for x in range(startIdx, endIdx):
   #reset channels
   cursor.execute("""
   	  update tag_map set channelId=%s where id = %s      
   	  """, (x, i))
   print "cid:" + str(x) + ";" + str(i)   
   i = i + 1

lang = ['en', 'zh']
langId = [1, 10]

i=0             
for l in lang:
   cursor.execute("""
      select distinct c.id 
        from nnchannel c, counter_shard s 
       where (c.sphere = %s or c.sphere='other')
         and c.poolType>=30
         and s.counterName = concat('ch',c.id)      
       order by count desc 
       limit 9;     
       """, l)
   hotRow = cursor.fetchall()
   hotList = list()
   for h in hotRow:                                            
     cid = h[0]
     hotList.append(cid)   
   hotList.reverse()
   print "hotlist:" + str(len(hotList))
   
   j=langId[i]              
   for h in hotList:
      print "channelid: " + str(h) + ";id:" + str(j) 
      cursor.execute("""
         update tag_map
            set channelId = %s
          where id = %s
         """, (h, j))
      j = j+1

   i = i + 1   

#########################################################
dbcontent.commit()  
cursor.close ()


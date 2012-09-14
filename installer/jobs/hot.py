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
contentCursor = dbcontent.cursor()
contentCursor.execute("""
   select c.id 
     from nnchannel c, counter_shard s 
    where (c.lang = 'en' or c.lang='other')
      and s.counterName = concat('ch',c.id) 
    order by count desc 
    limit 9;     
   """)
hotRow = contentCursor.fetchall()
hotList = list()
for h in hotRow: 
  cid = h[0]
  hotList.append(cid)

hotList.reverse()
print "hotlist:" + str(len(hotList))
#select m.id, m.tagId, m.channelId 
#	from tag t, tag_map m 
# where t.name  = 'hot(9x9en)' and t.id = m.tagId;   

i=1
for h in hotList:
   contentCursor.execute("""
      update tag_map
         set channelId = %s
       where id = %s
      """, (h, i))
   i = i+1
#########################################################ˇ
contentCursor.execute("""
   select c.id 
     from nnchannel c, counter_shard s 
    where (c.lang = 'zh' or c.lang='other')
      and s.counterName = concat('ch',c.id) 
    order by count desc 
    limit 9;     
   """)
hotRow = contentCursor.fetchall()
hotList = list()
for h in hotRow: 
  cid = h[0]
  hotList.append(cid)

hotList.reverse()
print "hotlist:" + str(len(hotList))
#select m.id, m.tagId, m.channelId 
#	from tag t, tag_map m 
# where t.name  = 'hot(9x9en)' and t.id = m.tagId;   

i=1
for h in hotList:
   contentCursor.execute("""
      update tag_map
         set channelId = %s
       where id = %s
      """, (h, i))
   i = i+1

#########################################################ˇ
dbcontent.commit()  
contentCursor.close ()


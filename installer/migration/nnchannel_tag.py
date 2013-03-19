# create categories
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
i = 1
  
cursor.execute("""
     select id, tag from nnchannel where contentType = 6 and tag is not null
     """)

rows = cursor.fetchall()
for r in rows:
  cid = r[0]
  tags = r[1]  
  print "cid:" + str(cid) + ";old tag:" + tags  
  tagstr = tags.split(",")
  tagDic = {}
  #put tag string in dictionary to avoid duplication
  for s in tagstr:
    s = s.rstrip().lstrip()  
    tagDic[s] = s 
  
  newTagStr = ""           
  for key in tagDic.keys():
     newTagStr += "," + key     
     cursor.execute("""
       select id 
         from tag
        where name = %s
        """, (key))   
     count = cursor.rowcount
     tid = 0   
     if count == 0:   
        cursor.execute("""
           insert into tag (name, updateDate)
                    values (%s, now()) 
           """, (key))
         
        tid = cursor.lastrowid
        #print "insert tid:" + str(tid)       
     else:
        tid = cursor.fetchone()[0]
        #print "old tid:" + str(tid)
        
     cursor.execute("""
       select id
         from tag_map                          
        where tagId = %s and channelId = %s
        """, (tid, cid))
     count = cursor.rowcount
     if count == 0:           
        cursor.execute("""
           insert into tag_map (tagId, channelId, updateDate)
                        values (%s, %s, now()) 
           """, (tid, cid))                       
             
  newTagStr = newTagStr.lstrip(",")   
  print "cid:" + str(cid) + ";new tag:" + newTagStr    
  cursor.execute("""update nnchannel set tag = %s where id = %s""", (newTagStr, cid))
     
  i = i +1 
  
dbcontent.commit()                  
print "record done:" + str(i)
cursor.close ()
dbcontent.close ()


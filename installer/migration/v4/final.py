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

cursor = dbcontent.cursor()
# category channel count
for n in range(1, 20):
   print "n:" + str(n)
   cursor.execute("""
      update systag_display set cntChannel = (
        select count(*) from nnchannel where (lang = 'en' or lang='other') and id in (select channelId from systag_map where systagId=%s))
      where systagId = %s and lang='en';
   """, (n, n))
   cursor.execute("""
      update systag_display set cntChannel = (
        select count(*) from nnchannel where (lang = 'zh' or lang='other') and id in (select channelId from systag_map where systagId=%s))
      where systagId = %s and lang='zh';
   """, (n, n))
dbcontent.commit()

# dashboard channel count
for n in range(38, 46):
   print "n:" + str(n)
   cursor.execute("""
      update systag_display set cntChannel = 9
      where systagId = %s
   """, (n))

for n in range(48, 56):
   print "n:" + str(n)
   cursor.execute("""
      update systag_display set cntChannel = 9
      where systagId = %s
   """, (n))

dbcontent.commit()

# dashboard image url
systagIds = [38, 39, 40,41,42,43,44, 48, 49,50,51,52,54]
for systagId in systagIds:
   lang = ['zh', 'en']
   for l in lang:
      cursor.execute("""
         select id, contentType from nnchannel where (lang=%s or lang='other') and id in (select channelId from systag_map where systagId = %s) order by updateDate desc limit 1;
      """, (l, systagId))  
      rows = cursor.fetchall()
      for r in rows:
         cId = r[0]
         contentType = r[1]
         print "systagId:" + str(systagId) + ";cId:" + str(cId) + ";contentType:" + str(contentType)
         if contentType == 6:
            cursor.execute("""
              select imageUrl from nnepisode 
               where channelId = %s and isPublic=true and publishDate is not null order by seq desc limit 1;
              """, (cId))
            episode_rows = cursor.fetchall()
            for e in episode_rows:
               imageUrl = e[0] 
               print "systagId:" + str(systagId) + ";lang:" + l + ";imageUrl:" + imageUrl + ";cId:" + str(cId)
               cursor.execute("""
                  update systag_display set imageUrl = %s where systagId = %s and lang = %s
                  """, (systagId, l))
         else: 
            cursor.execute("""
              select imageUrl from ytprogram 
               where channelId = %s order by updateDate desc limit 1;
              """, (cId))
            ytprogram_rows = cursor.fetchall()
            for y in ytprogram_rows:
               imageUrl = y[0]
               print "systagId:" + str(systagId) + ";lang:" + l + ";imageUrl:" + imageUrl + ";cId:" + str(cId)
               cursor.execute("""
                   update systag_display set imageUrl = %s where systagId = %s and lang=%s
                   """, (imageUrl, systagId, l))
         print "============================"
previousIds = [45, 55]
for previousId in previousIds:
   lang = ['zh', 'en']
   for l in lang:
      cursor.execute("""
         update systag_display set imageUrl='http://dev1.9x9.tv/en/img/previously.jpg' where systagId = %s and lang = %s
         """, (previousId, l))   

dbcontent.commit()
cursor.close ()


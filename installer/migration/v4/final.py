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

for n in range(38, 58):
   print "n:" + str(n)
   cursor.execute("""
      update systag_display set cntChannel = 9
      where systagId = %s
   """, (n))

dbcontent.commit()
cursor.close ()


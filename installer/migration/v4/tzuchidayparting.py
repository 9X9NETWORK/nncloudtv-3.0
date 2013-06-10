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

files = ['morning', 'daytime', 'slack', 'evening', 'primetime', 'latenight', 'nightowl']
#files = ['morning']
for daypart in files:
  filename = "data/" + "tzuchi_" + daypart + ".csv"
  print filename
  feed = open(filename, "rU")
  i = 0
  cntNnChannel=0
  cursor = dbcontent.cursor()
  for line in feed:
    # -- get data
    data = line.split(',')
    url = data[1]                                                           
    print "url:" + url
    # -- translate data            
    url = url.strip()
    posturl = "http://localhost:8080/wd/urlSubmit?url=" + url + "&lang=zh&sphere=zh"
    print "lookup cid posturl:" + posturl
    cId = urllib2.urlopen(posturl).read()
    print "lookup id:" + cId 
    if not "error" in cId:
       daypartSystagId = translate.get_tzuchi_daypartingSystagId(daypart)    
       print "daypartSystagId is:" + str(daypartSystagId)
       try:
          cursor.execute("""
             insert into systag_map (systagId, channelId, createDate, updateDate) values (%s, %s, now(), now());
             """, (daypartSystagId, cId)) 
       except MySQLdb.IntegrityError:
          print "duplicate key"
       cursor.execute("""
          update nnchannel set status = 0, isPublic = true where id = %s
           """, (cId))
    i = i+1
    print "----------"
    #if i > 2:
    #   break
  print "================================"    
  dbcontent.commit()  
cursor.close ()

print "record done:" + str(i)
print "nnchannel count:" + str(cntNnChannel)
feed.close()

#process world wide channels

import urllib, urllib2
import os
from array import *
import MySQLdb
import time
import translate

import myconfig
import sys

env = "dev"
pwd = ""
urlroot = ""
for arg in sys.argv:
   myconfig.getSqlconfig(arg)
   urlroot = myconfig.getUrlRoot(arg)   

print "pwd:" + pwd
print "urlroot:" + urlroot

dbcontent = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = pwd,
                             charset = "utf8",
                             use_unicode = True,
                             db = "nncloudtv_content")

files = ['morning', 'daytime', 'slack', 'evening', 'primetime', 'latenight', 'nightowl']
#files = ['morning']
for daypart in files:
  filename = "data/" + daypart + ".csv"
  print filename
  feed = open(filename, "rU")
  i = 0
  cntNnChannel=0
  cursor = dbcontent.cursor()
  for line in feed:
    # -- get data
    data = line.split(',')
    cId = data[0]
    name = data[1]                                                           
    url = data[2]
    category = data[3]
    lang = data[5]
    sphere = data[6]
    print "cid: " + cId + "; name:" + name + "; url:" + url + "; category:" + category + "; lang:" + lang + "; sphere:" + sphere
    # -- translate data            
    sphere = translate.get_sphere(sphere)
    lang = translate.get_lang(lang)
    if cId == "":
      url = url.strip()
      posturl = urlroot + "/wd/urlSubmit?url=" + url + "&lang=" + lang + "&sphere=" + sphere
      print "lookup cid posturl:" + posturl
      cId = urllib2.urlopen(posturl).read()
      print "lookup id:" + cId
    if url == "":
      cntNnChannel = cntNnChannel+1
      cursor.execute("""
         update nnchannel set isPublic=true, status=0 where id= %s
             """, (cId))
    else:
      if not "error" in cId:
         systagId = translate.get_systagId(category)
         print "systagId:" + str(systagId)
         if systagId == -1:
            break
         print "updated lang:" + lang + ";updated sphere:" + sphere
         cursor.execute("""
            update nnchannel set lang=%s, sphere=%s, isPublic=true, status=0 where id=%s
            """, (lang, sphere, cId))
         try:
            cursor.execute("""
               insert into systag_map (systagId, channelId, createDate, updateDate) values (%s, %s, now(), now())
             """, (systagId, cId))
         except MySQLdb.IntegrityError:
            print "duplicate key"
    if not "error" in cId:
       daypartSystagId = translate.get_daypartingSystagId(daypart)    
       print "daypartSystagId is:" + str(daypartSystagId)
       try:
          cursor.execute("""
             insert into systag_map (systagId, channelId, createDate, updateDate) values (%s, %s, now(), now());
            """, (daypartSystagId, cId))
       except MySQLdb.IntegrityError:
          print "duplicate key"
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

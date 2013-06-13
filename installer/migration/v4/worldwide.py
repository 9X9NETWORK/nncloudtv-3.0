#process world wide n US channels

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
   pwd = myconfig.getSqlconfig(arg)
   urlroot = myconfig.getUrlRoot(arg) 

print "pwd:" + pwd
print "urlroot:" + urlroot

dbcontent = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = pwd,
                             charset = "utf8",
                             use_unicode = True,
                             db = "nncloudtv_content")

files = ['worldwide', 'us']
for myfile in files:
  filename = "data/" + myfile + ".csv"
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
    else:
      if not "error" in cId:
         systagId = translate.get_systagId(category)
         print "systagId:" + str(systagId)
         if systagId == -1:
            break
         print "updated lang:" + lang + ";updated sphere:" + sphere
         #update channel property    
         cursor.execute("""
            update nnchannel set lang=%s, sphere=%s, isPublic=true, status=0 where id=%s
            """, (lang, sphere, cId))
         # insert into systag_map    
         #try:
         cursor.execute("""
               insert into systag_map (systagId, channelId, createDate, updateDate) values (%s, %s, now(), now()) on duplicate key update systagId = %s, channelId = %s
             """, (systagId, cId, systagId, cId))
         #except MySQLdb.IntegrityError:
         #   print "duplicate key"
    i = i+1
    print "----------"
  print "record done:" + str(i)
  print "nnchannel count:" + str(cntNnChannel)
  print "===================================="
  i = 0
 
dbcontent.commit()  
cursor.close ()

print "record done:" + str(i)
print "nnchannel count:" + str(cntNnChannel)
feed.close()

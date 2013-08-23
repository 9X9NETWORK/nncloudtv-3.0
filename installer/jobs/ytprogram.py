# for android virtual channels

import urllib, urllib2
import os
from array import *
import MySQLdb
import time, datetime

dbcontent = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = "",
                             charset = "utf8",
                             use_unicode = True,
                             db = "nncloudtv_content")

pwd = os.path.dirname(os.path.realpath(__file__))
md5_file = pwd + '/ponderosa.feed.txt.md5'
md5_url = 'http://channelwatch.9x9.tv/dan/ponderosa.feed.txt.md5'
f = open(md5_file, 'r')    
md5 = f.read()
f.close()                
user_agent = 'Mozilla/4.0 (compatible; MSIE 5.5; Windows NT)'
values = {'language' : 'Python' }           
headers = { 'User-Agent' : user_agent }              
data = urllib.urlencode(values)
req_md5 = urllib2.Request(md5_url, data, headers)
response = urllib2.urlopen(req_md5)
md5_new = response.readline()

if md5_new == md5:
  print "same\n"
  quit()                 
print datetime.datetime.now()
print "\n"
f = open(md5_file, 'w')
f.write(md5_new)
f.close()
              
url = 'http://channelwatch.9x9.tv/dan/ponderosa.feed.txt'
user_agent = 'Mozilla/4.0 (compatible; MSIE 5.5; Windows NT)'
values = {'language' : 'Python' }
headers = { 'User-Agent' : user_agent }
data = urllib.urlencode(values)            
req = urllib2.Request(url, data, headers)
response = urllib2.urlopen(req)
feed = response.readlines()                  

i = 0                                           
for line in feed:
  data = line.split('\t')
  cid = data[0]
  username = data[1]
  crawldate = data[2]
  videoid = data[3]
  name = data[4]        
  timestamp = data[5]
  duration = data[6]
  thumbnail = data[7]
  description = data[8]
  print "cid:" + cid
  print "username:" + username
  print "crawdate:" + crawldate
  print "name:" + name 
  print "timestamp:" + timestamp 
  print "duration:" + duration 
  print "thumbnail:" + thumbnail
  print "description:" + description
  
  cursor = dbcontent.cursor()   
  cursor.execute("""                 
     insert into ytprogram
       (channelId, ytUserName, ytVideoId, name, intro, imageUrl, duration, updateDate, crawlDate)
     values                                             
       (%s, %s, %s, %s, %s, %s, %s, from_unixtime(%s), from_unixtime(%s))                     
     ON DUPLICATE KEY UPDATE updateDate = from_unixtime(%s), crawldate = from_unixtime(%s), name = %s
     """, (cid, username, videoid, name, description, thumbnail, duration, timestamp, crawldate, timestamp, crawldate, name))

  # ch updateDate using video
  cursor.execute("""
     select unix_timestamp(updateDate) from nnchannel
      where id = %s
        """, (cid))
  ch_row = cursor.fetchone()
  ch_updateDate = ch_row[0]
  print "original channel time: " + str(ch_updateDate) + "; time from youtube video:" + timestamp
  if (ch_updateDate < long(timestamp)):
     print "ch updateDate is older, update with yt video"
     cursor.execute("""
          update nnchannel set updateDate = from_unixtime(%s) 
           where id = %s             
               """, (timestamp, cid))  

  i = i+1
dbcontent.commit()  
cursor.close ()

print "record done:" + str(i)


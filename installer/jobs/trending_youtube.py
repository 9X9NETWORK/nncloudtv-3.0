# update trending youtube channels based on tom's output

import urllib, urllib2
import os
from array import *
import MySQLdb
import time, datetime

dbcontent = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = "letlet",
                             charset = "utf8",
                             use_unicode = True,
                             db = "nncloudtv_content")
cursor = dbcontent.cursor()

pwd = os.path.dirname(os.path.realpath(__file__))
md5_file = pwd + '/trending.feed.txt.md5'
md5_url = 'http://channelwatch.9x9.tv/dan/trending.feed.txt.md5'
url = 'http://channelwatch.9x9.tv/dan/trending.feed.txt'
user_agent = 'Mozilla/4.0 (compatible; MSIE 5.5; Windows NT)'
values = {'language' : 'Python' }
headers = { 'User-Agent' : user_agent }
data = urllib.urlencode(values)

f = open(md5_file, 'r')
md5 = f.read()
f.close()

req_md5 = urllib2.Request(md5_url, data, headers)
response = urllib2.urlopen(req_md5)
md5_new = response.readline()

if md5_new == md5:
  #print "same\n"
  quit()

#print "new\n"
print datetime.datetime.now()
print "\n"
f = open(md5_file, 'w')
f.write(md5_new)
f.close()

req = urllib2.Request(url, data, headers)
response = urllib2.urlopen(req)
feed = response.readlines()
i = 0
j = 0
for line in feed:
  i = i+1
  data = line.split('\t')
  cId = data[0]
  username = data[1]
  userEmail = username + "@9x9.tv"
  chTitle = data[2]
  chTitle = chTitle.replace("|", ";")
  thumbnail = data[3]
  url1 = data[4]
  url2 = data[5]
  url3 = data[6]
  updateDate = data[9]
  programCnt = data[10]
  lastEpisodeTitle = data[11]
  lastEpisodeTitle = lastEpisodeTitle.replace("|", ";")
  lastEpisodeTitle = lastEpisodeTitle.replace("\n", "")
  finalChName = str(chTitle) + "|" + str(lastEpisodeTitle)  
  #print "finalChName:" + finalChName
  imageUrl = thumbnail + "|" + url1 + "|" + url2 + "|" + url3
  cursor.execute("""                                                    
     select distinct c.id
       from tag_map map, nnchannel c
      where (map.tagId=3 or map.tagId=4)
        and map.channelId = c.id    
        and (c.contentType=3 or c.contentType=4)
        and c.id = %s
      """, (long(cId)))  
  
  rows = cursor.fetchall()
  for r in rows:
     j = j+1    
     cursor.execute("""                                                    
       select id 
         from nncloudtv_nnuser1.nnuser
        where email = %s
        """, (userEmail))
     count = cursor.rowcount  
     if count == 0:
        epoch = time.mktime(time.gmtime())
        print "create new user, userEmail:" + userEmail
        cursor.execute("""
           insert into nncloudtv_nnuser1.nnuser
             (email, msoId, name, imageUrl, token, shard, type, createDate, updateDate, gender, isTemp, profileUrl)
           values
             (%s, 1, %s, %s, %s, 1, 8, now(), now(), 0, false, %s)              
           """, (userEmail, username, thumbnail, epoch, username))
        dbcontent.commit()                                     
     cursor.execute("""
       select id 
         from nncloudtv_nnuser1.nnuser
        where email = %s
        """, (userEmail))
     user = cursor.fetchone()
     userId = user[0]
     userIdStr = "1-" + str(userId)
     cursor = dbcontent.cursor()  
     
     cursor.execute("""
        select userIdStr  
          from nnchannel 
         where id = %s
         """, cId)      
     oriUserIdStr = cursor.fetchone()[0]  

     if oriUserIdStr == None:
        print "ch: " + cId + " oriUserId is null, added new user:" + userIdStr 
        cursor.execute("""    
           update nnchannel 
              set imageUrl = %s, userIdStr = %s, updateDate = from_unixtime(%s), cntEpisode = %s, name = %s
            where id = %s                                 
            """, (imageUrl, userIdStr, updateDate, programCnt, finalChName, cId))
     else:
        cursor.execute("""    
           update nnchannel 
              set imageUrl = %s, updateDate = from_unixtime(%s), cntEpisode = %s, name = %s
            where id = %s                                 
            """, (imageUrl, updateDate, programCnt, finalChName, cId))         

dbcontent.commit()     
cursor.close ()
print "record read:" + str(i) + "; record update:" + str(j)


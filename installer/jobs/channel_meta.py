# get latest youtube update from tom's feed.out

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

dbuser = MySQLdb.connect (host = "localhost",
                          user = "root",
                          passwd = "",
                          charset = "utf8",
                          use_unicode = True,
                          db = "nncloudtv_nnuser1")

pwd = os.path.dirname(os.path.realpath(__file__))                                  
md5_file = pwd + '/basic.feed.txt.md5'
md5_url = 'http://channelwatch.9x9.tv/dan/basic.feed.txt.md5'
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

url = 'http://channelwatch.9x9.tv/dan/basic.feed.txt'
user_agent = 'Mozilla/4.0 (compatible; MSIE 5.5; Windows NT)'
values = {'language' : 'Python' }
headers = { 'User-Agent' : user_agent }
data = urllib.urlencode(values)
req = urllib2.Request(url, data, headers)
response = urllib2.urlopen(req)
feed = response.readlines()

#print feed
#feed = open("feed.out", "rU")

i = 0
for line in feed:
  i = i+1
  data = line.split('\t')
  cId = data[0]
  username = data[1]
  name = data[2]
  userEmail = username + "@9x9.tv"
  thumbnail = data[3]
  url1 = data[4]
  url2 = data[5]
  url3 = data[6]
  updateDate = data[9]
  programCnt = data[10]
  print "username:" + username
  print "thumbnail:" + thumbnail
  print "url1" + url1
  print "url2" + url2
  print "url3" + url3
  print "updateDate:" + str(updateDate)
  print "programCnt:" + programCnt
  #if (i > 0):
  #   break
  #imageUrl = url1 + "|" + url2 + "|" + url3
  imageUrl = thumbnail + "|" + url1 + "|" + url2 + "|" + url3
  #if len(url1) > 0:
  #   imageUrl = imageUrl + "|" + url1
  #if len(url2) > 0:
  #   imageUrl = imageUrl + "|" + url2
  #if len(url3) > 0:
  #   imageUrl = imageUrl + "|" + url3 
  userCursor = dbuser.cursor()
  userCursor.execute("""                                                    
    select id                     
      from nncloudtv_nnuser1.nnuser
     where email = %s
     """, (userEmail))
  count = userCursor.rowcount              
  if count == 0:
     epoch = time.mktime(time.gmtime()) 
     userCursor.execute("""
        insert into nncloudtv_nnuser1.nnuser
          (email, msoId, name, imageUrl, token, shard, type, createDate, updateDate, gender, isTemp, profileUrl)
        values
          (%s, 1, %s, %s, %s, 1, 8, now(), now(), 0, false, %s)              
        """, (userEmail, username, thumbnail, epoch, username))
     dbuser.commit()                                     
  userCursor.execute("""
    select id                  
      from nncloudtv_nnuser1.nnuser
     where email = %s
     """, (userEmail))
  user = userCursor.fetchone()
  userId = user[0]                         
  userIdStr = "1-" + str(userId)
  print "user Id str going to use: " + userIdStr
  contentCursor = dbcontent.cursor()            

  contentCursor.execute("""
     select userIdStr                             
       from nnchannel 
      where id = %s
      """, cId)      
  user_row = contentCursor.fetchone()
  if user_row == None:              
     #oriUserIdStr = contentCursor.fetchone()[0]  
     #if oriUserIdStr == None:                   
     print "ch: " + cId + " oriUserId is null, add new user:" + userIdStr 
     contentCursor.execute("""     
        update nnchannel 
           set imageUrl = %s, userIdStr = %s, updateDate = from_unixtime(%s), cntEpisode = %s
         where id = %s                                 
         """, (imageUrl, userIdStr, updateDate, programCnt, cId))
  #else:
  #   oriUserIdStr = user_row[0]        
  #   print "!!! ori user id str !!! "
  #   print oriUserIdStr     
  #   contentCursor.execute("""    
  #      update nnchannel 
  #         set imageUrl = %s, updateDate = from_unixtime(%s), cntEpisode = %s
  #       where id = %s                                 
  #       """, (imageUrl, updateDate, programCnt, cId))

  contentCursor.execute("""
     update nnchannel
        set name= %s, imageUrl = %s, updateDate = from_unixtime(%s), cntEpisode = %s
      where id = %s
     """, (name, imageUrl, updateDate, programCnt, cId))
 
  dbcontent.commit()  
  userCursor.close ()
  contentCursor.close ()

url = "http://localhost:8080/playerAPI/flush"
urllib2.urlopen(url).read()

print "record done:" + str(i)
#feed.close()


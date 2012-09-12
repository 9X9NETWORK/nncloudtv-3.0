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

dbuser = MySQLdb.connect (host = "localhost",
                          user = "root",
                          passwd = "",
                          charset = "utf8",
                          use_unicode = True,
                          db = "nncloudtv_nnuser1")


#url = 'http://channelwatch.9x9.tv/dan/feed.out'
#user_agent = 'Mozilla/4.0 (compatible; MSIE 5.5; Windows NT)'
#values = {'language' : 'Python' }
#headers = { 'User-Agent' : user_agent }
#
#data = urllib.urlencode(values)
#req = urllib2.Request(url, data, headers)
#response = urllib2.urlopen(req)
#the_page = response.read()

feed = open("feed.out", "rU")
i = 0
for line in feed:
  i = i+1
  data = line.split('\t')
  cId = data[0]
  username = data[1]
  userEmail = username + "@9x9.tv"
  thumbnail = data[2]
  url1 = data[3]
  url2 = data[4]
  url3 = data[5]                                           
  imageUrl = url1 + "|" + url2 + "|" + url3  
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
          (email, msoId, name, imageUrl, token, shard, type, createDate, updateDate, gender, isTemp)
        values
          (%s, %s, %s, %s, %s, %s, %s, now(), now(), 0, false)              
        """, (userEmail, 1, username, thumbnail, epoch, 1, 8))
     dbuser.commit()
  userCursor.execute("""
    select id 
      from nncloudtv_nnuser1.nnuser
     where email = %s
     """, (userEmail))
  user = userCursor.fetchone()
  userId = user[0]
  userIdStr = "1-" + str(userId)
  contentCursor = dbcontent.cursor()     
  contentCursor.execute("""
     update nnchannel 
        set imageUrl = %s, userIdStr = %s
      where id = %s
      """, (imageUrl, userIdStr, cId))     
  rows = userCursor.fetchall()
    
  dbcontent.commit()  
  userCursor.close ()
  contentCursor.close ()

print "record done:" + str(i)
feed.close()


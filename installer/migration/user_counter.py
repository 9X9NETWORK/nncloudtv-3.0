#process billboard channels

import urllib, urllib2
import os
from array import *
import MySQLdb
import time

db = MySQLdb.connect (host = "localhost",
                      user = "root",
                      passwd = "",
                      charset = "utf8",
                      use_unicode = True,
                      db = "nncloudtv_nnuser1")
   
cursor = db.cursor()
###cntSubscribe: how many channels a user subscribers to
cursor.execute("""
   select userId, count(*)
     from nnuser_subscribe
    group by userId
		""")     
rows = cursor.fetchall()
for r in rows:
   uId = r[0]
   cnt = r[1]
   print "uId: " + str(uId) + ";subscribe channel:" + str(cnt)
   cursor.execute("""
			update nnuser
			   set cntSubscribe = %s
       where id = %s
        """, (cnt, uId))     
	 
###cntChannel: how many channels a user creates
cursor.execute("""
    select c.id, c.userIdStr, count(*) 
      from nncloudtv_content.nnchannel c, nncloudtv_nnuser1.nnuser u
     where c.userIdStr = concat('1-', u.id)
     group by c.userIdStr
     order by c.id 
		""")     
rows = cursor.fetchall()
for r in rows:
  uId = r[1]
  cnt = r[2]
  uId = uId.lstrip("1-")
  print "uId: " + str(uId) + "; create channels cnt:" + str(cnt)
  cursor.execute("""
			update nnuser
			   set cntChannel = %s
			 where id = %s
			 """, (cnt, uId))     

###cntFollower: how many subscribers a user have for all channels
cursor.execute("""
    select id from nnuser 
		""")     
userrows = cursor.fetchall()
for ur in userrows:
  uId = ur[0]
  uIdStr = "1-" + str(uId)
  cursor.execute("""
     select id 
       from nncloudtv_content.nnchannel 
      where userIdStr = %s
			 """, (uIdStr))     
  chrows = cursor.fetchall()
  cursor.execute("""
     select count(*) 
       from nnuser_subscribe 
      where channelId in (select id from nncloudtv_content.nnchannel where userIdStr = %s)
			 """, (uIdStr))
  cnt = cursor.fetchone()[0]
  print "channels: " + chlist + ";cnt:" + str(cnt) + ";uId:" + str(uId)
  if cnt > 0:
     cursor.execute("""
        update nnuser 
          set cntFollower = %s
        where id = %s
	   		 """, (cnt, uId))

db.commit()  
cursor.close ()


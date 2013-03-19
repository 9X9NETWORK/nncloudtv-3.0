import MySQLdb

conn = MySQLdb.connect (host = "localhost",
                        user = "root",
                        passwd = "",
                        charset = "utf8", 
                        use_unicode = True,                                               
                        db = "nncloudtv_content")


cursor = conn.cursor()
cursor.execute ("""
   select c.id, count(*)
     from nnchannel c, nnepisode e      
    where c.contentType = 6
      and c.id = e.channelId
      and e.isPublic=true
      and publishDate < now()
    group by c.id
   """)
data = cursor.fetchall ()
i = 1
for d in data:       
  cId = d[0]    
  cnt = d[1]
  cursor.execute ("""            
			 update nnchannel set cntEpisode = %s
			  where id = %s
			 """, (cnt, cId))		          
  i = i+ 1

conn.commit()
cursor.close()
conn.close()
print "record done:" + str(i)
  

# count channel in the category

import MySQLdb

conn = MySQLdb.connect (host = "localhost",
                        user = "root",
                        passwd = "letlet",
                        charset = "utf8", 
                        use_unicode = True,                                               
                        db = "nncloudtv_content")

try:
  cursor = conn.cursor()
  cursor.execute ("""  
        select map.categoryId, count(*)
         from category_map map, nnchannel c
        where map.channelId = c.id
          and c.isPublic=true
          and c.status=0
        group by map.categoryId  
        order by map.categoryId                    
     """)
  data = cursor.fetchall ()
  i = 1
  for d in data:       
    categoryId = d[0]    
    cnt = d[1]
    cursor.execute ("""            
				 update category set channelCnt = %s
				  where id = %s
				 """, (cnt, categoryId))		          
    i = i+ 1   
  conn.commit()
  cursor.close ()
  conn.close ()

  print "record done:" + str(i)

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)

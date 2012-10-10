# import category_channel_set
#- read through every row,
#  find in category table, the ori_id, update with new id
#  find in nnset table, the ori_id, new_id, update with new id

import MySQLdb

conn = MySQLdb.connect (host = "localhost",
                        user = "root",
                        passwd = "",
                        charset = "utf8", 
                        use_unicode = True,                                               
                        db = "nncloudtv_content")

try:
  cursor = conn.cursor ()
  cursor.execute ("""
     select channelId                 
       from tag_map
      where tagId=3 or tagId = 4           
      order by tagId, channelId               
                  """)                 
  map_rows = cursor.fetchall ()
  print "map_rows count:" + str(len(map_rows))
  for m in map_rows:     
    cid = m[0]   
    print "cid: " + str(cid)
    cursor.execute ("""
         select id, updateDate 
           from nnprogram 
          where channelId = %s order by updateDate desc limit 1; 
        """, (cid)) 
    program_rows = cursor.fetchall ()              
    for p in program_rows: 
       pid = p[0] 
       updateDate = p[1]
       print "udpate nnchannel " + str(cid) + " with " + str(pid) + " date " + str(updateDate)
       cursor.execute (""" 
           update nnchannel                 
              set updateDate = %s 
            where id = %s """, (updateDate, pid))        
    
  conn.commit()
  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)

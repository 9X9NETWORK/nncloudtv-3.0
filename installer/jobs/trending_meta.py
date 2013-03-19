# update nnchannel update time based on nnprogram

import urllib, urllib2
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
      select distinct c.id 
        from tag_map map, nnchannel c
       where (map.tagId=3 or map.tagId=4)            
         and map.channelId = c.id    
         and (c.contentType=6);              
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
            where id = %s """, (updateDate, cid))                
  conn.commit()
  for m in map_rows:
    cid = m[0]
    url = "http://localhost:8080/wd/channelCache?channel=" + str(cid)
    print url
    urllib2.urlopen(url).read()
  
  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)

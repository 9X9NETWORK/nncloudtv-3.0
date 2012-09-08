# give channel tag based on nnset
import MySQLdb                                     

conn = MySQLdb.connect (host = "localhost",
                        user = "root",    
                        passwd = "",
                        charset = "utf8",   
                        use_unicode = True,                 
                        db = "nncloudtv_content")
                                                         
try:
  cursor = conn.cursor ()
  cursor.execute ("select id, name from nnset where isPublic=true")
  set_rows = cursor.fetchall ()
  for s in set_rows:
    catOriId = 
    catId = s[0]
    tag_name = s[1]
    
    cursor.execute ("""
        UPDATE category SET tag = %s 
        WHERE id = %s
        """, (tag_name, catId))
    
         

  conn.commit()
  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)
                      

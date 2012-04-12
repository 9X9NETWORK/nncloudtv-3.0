# import category_channel_set
#- read through every row,
#  find in category table, the ori_id, update with new id
#  find in nnset table, the ori_id, new_id, update with new id

import MySQLdb

conn = MySQLdb.connect (host = "localhost",
                        user = "root",
                        passwd = "letlet",
                        charset = "utf8", 
                        use_unicode = True,                                               
                        db = "nncloudtv_content")

try:
  cursor = conn.cursor ()
  cursor.execute ("select channelId, setId, id from nnset_to_nnchannel")
  rows = cursor.fetchall ()
  for row in rows:
       print "original channel_id, set_id = %s, %s" % (row[0], row[1])
       myid = row[2]
       ori_channel_id = row[0]
       ori_set_id = row[1]
       new_channel_id = row[0]
       new_set_id = row[1]
       cursor.execute ("""
            select id from nnchannel 
            WHERE ori_id = %s
            """, (ori_channel_id))
       rows = cursor.fetchall ()
       for row in rows:
          new_channel_id = row[0] 
          print "new channel_id = %s" % (new_channel_id)
       cursor.execute ("""
            select id from nnset 
            WHERE ori_id = %s
            """, (ori_set_id))
       rows = cursor.fetchall ()
       for row in rows:
          new_set_id = row[0] 
          print "new set_id = %s" % (new_set_id)
       cursor.execute ("""
           UPDATE nnset_to_nnchannel SET channelId = %s, setId = %s 
           WHERE id = %s
           """, (new_channel_id, new_set_id, myid))
       
  conn.commit()
  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)


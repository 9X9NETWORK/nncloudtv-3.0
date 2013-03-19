# update channel subscribers user id
import MySQLdb
import sqlite3

conn = MySQLdb.connect (host = "localhost",
                        user = "root",
                        passwd = "",
                        charset = "utf8",
                        use_unicode = True,
                        db = "nncloudtv_content")

try:
  cursor = conn.cursor()
  #TODO nnuser2 table as well
  cursor.execute("""
    select id from nnchannel order by id
      """)

  rows = cursor.fetchall()
  for row in rows:
    cid = row[0]
    cursor.execute("""
      select distinct userId from nncloudtv_nnuser1.nnuser_subscribe where channelId = %s order by rand() limit 3;
        """, (cid))
    user_rows = cursor.fetchall()
    uidStr = ""
    for ur in user_rows:
       uid = ur[0]
       uidStr = uidStr + "1-" + str(uid) + ";"
    if len(uidStr) > 0:
       print "update ch " + str(cid) + " with uids: " + uidStr
       cursor.execute("""
         update nnchannel set subscribersIdStr = %s where id = %s
           """, (uidStr, cid))
       
  conn.commit()
  cursor.close ()
  conn.close()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



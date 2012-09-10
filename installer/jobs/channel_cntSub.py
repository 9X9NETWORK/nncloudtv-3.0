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
  cursor.execute("""
     select channelId, count(*) cnt 
       from nncloudtv_nnuser1.nnuser_subscribe
      group by channelId
      order by channelId
      """)

  rows = cursor.fetchall()
  for row in rows:
    cid = row[0]
    cnt = row[1]
    cursor.execute("""
        update nnchannel set cntSubscribe = %s
         where id = %s 
       """, (cnt, cid))
  conn.commit()
  cursor.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



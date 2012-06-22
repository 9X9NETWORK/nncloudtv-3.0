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
     select s.id, count(*)
       from nnset_to_nnchannel sc,
            nnchannel c,
            nnset s
      where s.id = sc.setId
        and c.id = sc.channelId
        and c.status=0
        and c.isPublic=true
      group by s.id
           """)

  rows = cursor.fetchall()
  for row in rows:
    sid = row[0]
    calibrate = row[1]
    cursor.execute("""
         select channelCnt
           from nnset
          where id = %s
       """, sid)
    rows = cursor.fetchall()
    for row in rows:
       cnt = row[0]
       if calibrate != cnt:
          print str(sid) +" changes to " + str(calibrate)
          cursor.execute("""
             update nnset
                set channelCnt = %s
              where id = %s
          """, (calibrate, sid))
          conn.commit()
  cursor.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)

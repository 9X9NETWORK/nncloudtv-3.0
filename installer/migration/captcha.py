import MySQLdb
import sqlite3
from google.appengine.datastore import entity_pb
from google.appengine.api import datastore

def AllEntities(db):
    conn = sqlite3.connect(db)
    cursor = conn.cursor()
    cursor.execute('select id, value from result order by sort_key, id')
    for unused_entity_id, entity in cursor:
        entity_proto = entity_pb.EntityProto(contents=entity)
        yield datastore.Entity._FromPb(entity_proto)

#main
conn = MySQLdb.connect (host = "localhost",
                        user = "root",
                        passwd = "letlet",
                        db = "nncloudtv_content",
                        charset = "utf8", 
                        use_unicode = True)

try:

  cursor = conn.cursor ()
  cursor.execute("truncate captcha")
  db = '/home/ubuntu/files/gae/Captcha.sql3'
  for entity in AllEntities(db):
     batch = entity['batch']
     name = entity['name']
     fileName = entity['fileName']
     random = entity['random']
     toBeExpired = entity['toBeExpired']
     lockedDate = entity['lockedDate']
     
     cursor.execute ("""
          INSERT INTO captcha (batch, name, fileName, random, toBeExpired, lockedDate, createDate)
          VALUES (%s, %s, %s, %s, %s, %s, now())
       """, (batch, name, fileName, random, toBeExpired, lockedDate))
     conn.commit()

  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



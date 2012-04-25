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
  cursor.execute("truncate sns_auth")
  db = '/home/ubuntu/files/gae/SnsAuth.sql3'
  for entity in AllEntities(db):
     msoId = entity['msoId']
     nntype = entity['type']
     token = entity['token']
     secret = entity['secrete']
     enabled = entity['enabled']
     createDate = entity['createDate']
     
     cursor.execute ("""
          INSERT INTO sns_auth (msoId, type, token, enabled, createDate, secret)
          VALUES (%s, %s, %s, %s, %s, %s)
       """, (msoId, nntype, token, enabled, createDate, secret))
     conn.commit()

  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



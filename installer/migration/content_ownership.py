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
                        passwd = "",
                        db = "nncloudtv_content",
                        charset = "utf8", 
                        use_unicode = True)

try:

  cursor = conn.cursor ()
  cursor.execute("truncate content_ownership")
  db = '/home/ubuntu/files/gae/ContentOwnership.sql3'
  for entity in AllEntities(db):
     contentId = entity['contentId']
     contentType = entity['contentType']
     createDate = entity['createDate']
     createMode = entity['createMode']
     msoId = entity['msoId']
     
     cursor.execute ("""
          INSERT INTO content_ownership (contentId, contentType, createDate, createMode, msoId)
          VALUES (%s, %s, %s, %s, %s)
       """, (contentId, contentType, createDate, createMode, msoId))
     conn.commit()

  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



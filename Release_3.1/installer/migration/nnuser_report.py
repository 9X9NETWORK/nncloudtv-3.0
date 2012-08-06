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
                        charset = "utf8", 
                        use_unicode = True,                       
                        db = "nncloudtv_analytics")

try:
  cursor = conn.cursor ()
  cursor.execute("truncate nnuser_report")
  db = '/home/ubuntu/files/gae/NnUserReport.sql3'
  for entity in AllEntities(db):
     comment     = entity['comment']
     createDate  = entity['createDate']
     deviceId    = entity['deviceId']
     deviceToken = entity['deviceToken']
     session     = entity['session']
     userId      = entity['userId']                      
     userToken   = entity['userToken']

     cursor.execute ("""
          INSERT INTO nnuser_report (comment, createDate, deviceId, deviceToken, session, userId, userToken)
          VALUES (%s, %s, %s, %s, %s, %s, %s)
       """, (comment, createDate, deviceId, deviceToken, session, userId, userToken))
          
     conn.commit()
          
  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



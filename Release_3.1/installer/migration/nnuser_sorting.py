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
                        charset = "utf8", 
                        use_unicode = True,                       
                        db = "nncloudtv_nnuser1")

try:

  cursor = conn.cursor ()
  cursor.execute("truncate nnuser_channel_sorting")
  db = '/home/ubuntu/files/gae/NnUserChannelSorting.sql3'
  for entity in AllEntities(db):
     userId      = entity['userId']                      
     channelId   = entity['channelId']
     createDate  = entity['createDate']
     updateDate  = entity['updateDate']
     sort        = entity['sort']

     cursor.execute ("""
          INSERT INTO nnuser_channel_sorting (userId, channelId, createDate, updateDate, sort)
          VALUES (%s, %s, %s, %s, %s)
       """, (userId, channelId, createDate, updateDate, sort))
                              
     conn.commit()
          
  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



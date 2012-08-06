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
  print "nnset_autosharing"
  cursor = conn.cursor ()
  cursor.execute("truncate nnset_autosharing")
  db = '/home/ubuntu/files/gae/ChannelSetAutosharing.sql3'
  cnt = 1
  for entity in AllEntities(db):
     try:   
        msoId = entity['msoId']
        setId = entity['channelSetId']
        nntype = entity['type']
        createDate = entity['createDate']
        cnt = cnt + 1                                            
        print cnt                    
        cursor.execute ("""
             INSERT INTO nnset_autosharing (msoId, setId, type, createDate)
             VALUES (%s, %s, %s, %s)
          """, (msoId, setId, nntype, createDate))
        conn.commit()
     except KeyError, e:     
        print "key error"

  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



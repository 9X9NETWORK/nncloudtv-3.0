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
                        db = "nncloudtv_content")

try:

  cursor = conn.cursor ()
  cursor.execute("truncate nnprogram")
  db = '/home/ubuntu/files/gae/MsoProgram.sql3'
  for entity in AllEntities(db):
     ori_id          = entity.key().id()
     audioFileUrl    = entity['audioFileUrl']            
     comment         = entity['comment']        
     channelId       = entity['channelId']     
     contentType     = entity['contentType']   
     createDate      = entity['createDate']    
     duration        = entity['duration']        
     errorCode       = entity['errorCode']     
     imageLargeUrl   = entity['imageLargeUrl'] 
     imageUrl        = entity['imageUrl']       
     intro           = entity['intro']          
     isPublic        = entity['isPublic']                                
     name            = entity['name']            
     fileUrl         = entity['otherFileUrl']                
     status          = entity['status']                          
     storageId       = entity['storageId']     
     nntype          = entity['type']
     updateDate      = entity['updateDate']
     seq = ""     
     subSeq          = ""
     try:
        seq = entity['seq']  
        subSeq = entity['subSeq']
     except KeyError:
        print "no seq and subseq"

     if channelId != "0":
       cursor.execute ("""
            INSERT INTO nnprogram (ori_id, audioFileUrl, comment, channelId, contentType, createDate, duration, errorCode, imageLargeUrl, imageUrl, intro, isPublic, name, fileUrl, seq, subSeq, status, storageId, type, updateDate)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) 
            """, (ori_id, audioFileUrl, comment, channelId, contentType, createDate, duration, errorCode, imageLargeUrl, imageUrl, intro, isPublic, name, fileUrl, seq, subSeq, status, storageId, nntype, updateDate))

     conn.commit()
          
  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



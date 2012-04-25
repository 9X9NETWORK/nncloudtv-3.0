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
  cursor.execute("truncate nnchannel")
  db = '/home/ubuntu/files/gae/MsoChannel.sql3'
  for entity in AllEntities(db):
     ori_id                  = entity.key().id()
     contentType             = entity['contentType']                      
     createDate              = entity['createDate']            
     errorReason             = entity['errorReason']           
     oriName                 = entity['oriName']               
     imageUrl                = entity['imageUrl']              
     intro                   = entity['intro']                  
     isPublic                = entity['isPublic']             
     name                    = entity['name']                   
     piwik                   = entity['piwik']                  
     programCnt              = entity['programCount']          
     sourceUrl               = entity['sourceUrl']                                       
     status                  = entity['status']                 
     tag                     = entity['tags']                    
     transcodingUpdateDate   = entity['transcodingUpdateDate']
     updateDate              = entity['updateDate']                             
     sorting                 = 0
     isTemp                  = 0

     cursor.execute ("""
          INSERT INTO nnchannel (contentType, createDate, errorReason, oriName, imageUrl, intro, isPublic, name, piwik, programCnt, sourceUrl, status, tag, transcodingUpdateDate, updateDate, ori_id, sorting, isTemp)
          VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
       """, (contentType, createDate, errorReason, oriName, imageUrl, intro, isPublic, name, piwik, programCnt, sourceUrl, status, tag, transcodingUpdateDate, updateDate, ori_id, sorting, isTemp))
          
     conn.commit()
          
  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



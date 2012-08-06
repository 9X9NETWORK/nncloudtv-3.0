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
  cursor.execute("truncate nnset")
  db = '/home/ubuntu/files/gae/ChannelSet.sql3'
  for entity in AllEntities(db):
     ori_id          = entity.key().id()
     beautifulUrl    = entity['beautifulUrl']    
     channelCnt      = entity['channelCount']    
     createDate      = entity['createDate']    
     featured        = entity['featured']       
     imageUrl        = entity['imageUrl']      
     intro           = entity['intro']          
     isPublic        = entity['isPublic']      
     lang            = entity['lang']           
     name            = entity['name']           
     piwik           = entity['piwik']          
     seq             = entity['seq']            
     tag             = entity['tag']            
     updateDate      = entity['updateDate']                   
     cursor.execute ("""
          INSERT INTO nnset (beautifulUrl, channelCnt, createDate, featured, imageUrl, intro, isPublic, lang, name, piwik, seq, tag, updateDate, ori_id)
          VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
       """, (beautifulUrl, channelCnt, createDate, featured, imageUrl, intro, isPublic, lang, name, piwik, seq, tag, updateDate, ori_id))
     conn.commit()
          
  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



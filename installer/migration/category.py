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
  cursor.execute("truncate category")
  db = '/home/ubuntu/files/gae/Category.sql3'
  for entity in AllEntities(db):
     ori_id = entity.key().id()
     parentId = entity['parentId']
     lang = entity['lang']
     name = entity['name']
     isPublic = entity['isPublic']
     channelCnt = entity['channelCount']
     seq = entity['seq']
     subCatCnt = entity['subCategoryCnt']
     cursor.execute ("""
          INSERT INTO category (parentId, lang, name, isPublic, channelCnt, seq, subCatCnt, createDate, updateDate, ori_id)
          VALUES (%s, %s, %s, %s, %s, %s, %s, now(), now(), %s)
       """, (parentId, lang, name, isPublic, channelCnt, seq, subCatCnt, ori_id))
     conn.commit()
     
  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



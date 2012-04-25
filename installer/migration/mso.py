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
  cursor.execute("truncate mso")
  db = '/home/ubuntu/files/gae/Mso.sql3'
  for entity in AllEntities(db):
     ori_id = entity.key().id()   
     contactEmail = entity['contactEmail']
     intro = entity['intro']
     jingleUrl = entity['jingleUrl']
     logoUrl = entity['logoUrl']
     name = entity['name']
     lang = entity['preferredLangCode']
     title = entity['title']
     createDate = entity['createDate']
     updateDate = entity['updateDate']
     nntype = int(entity['type'])
     #nntype = 2
     
     cursor.execute ("""
          INSERT INTO mso (contactEmail, createDate, intro, jingleUrl, logoUrl, name, lang, title, type, updateDate, ori_id)
          VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
       """, (contactEmail, createDate, intro, jingleUrl, logoUrl, name, lang,  title, nntype, updateDate, ori_id))
     conn.commit()

  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



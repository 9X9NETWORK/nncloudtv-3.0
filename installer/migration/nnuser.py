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
                        db = "nncloudtv_nnuser1")

try:

  cursor = conn.cursor ()
  cursor.execute("truncate nnuser")
  db = '/home/ubuntu/files/gae/NnUser.sql3'
  for entity in AllEntities(db):
     ori_id            = entity.key().id()
     token             = entity['token']                      
     email             = entity['email']
     msoId             = entity['msoId']
     name              = entity['name']
     dob               = ""
     lang              = "en"
     sphere            = "en"
     isTemp            = 0
     try:     
       dob             = entity['dob']
       lang            = entity['lang']                             
       sphere          = entity['sphere']
     except KeyError:
       print "no dob or lang or sphere"
     intro             = entity['intro']              
     imageUrl          = entity['imageUrl']                  
     cryptedPassword   = entity['cryptedPassword']                   
     salt              = entity['salt']                  
     createDate        = entity['createDate']          
     updateDate        = entity['updateDate']                                       
     nntype            = entity['type']                 
     facebookToken     = entity['facebookToken']                    
     gender            = entity['gender']

     cursor.execute ("""
          INSERT INTO nnuser (ori_id, token, email, name, dob, intro, imageUrl, cryptedPassword, salt, createDate, updateDate, type, facebookToken, gender, lang, sphere, msoId, isTemp)
          VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
       """, (ori_id, token, email, name, dob, intro, imageUrl, cryptedPassword, salt, createDate, updateDate, nntype, facebookToken, gender, lang, sphere, msoId, isTemp))
          
     conn.commit()
          
  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)



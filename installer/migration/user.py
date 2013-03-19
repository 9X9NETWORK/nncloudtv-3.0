import hashlib, uuid
import MySQLdb


dbuser = MySQLdb.connect (host = "localhost",
                          user = "root",
                          passwd = "",
                          charset = "utf8",
                          use_unicode = True,
                          db = "nncloudtv_nnuser1")

userCursor = dbuser.cursor()

userEmail = "a51@a.com"
username = "a51"
thumbnail = "http://www.google.com"
epoch = "lala"
password = "123456"
salt = uuid.uuid4().bytes
hashed_password = hashlib.sha512(password + salt).digest()

userCursor.execute("""
   update nncloudtv_nnuser1.nnuser
     set salt = %s, cryptedPassword = %s 
   where id = 2170 
     (%s, %s)              
   """, (salt, cryptedPassword))
dbuser.commit()


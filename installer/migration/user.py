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
   insert into nncloudtv_nnuser1.nnuser
     (email, msoId, name, imageUrl, token, shard, type, createDate, updateDate, gender, isTemp, salt, cryptedPassword)
   values
     (%s, %s, %s, %s, %s, %s, %s, now(), now(), 0, false, %s, %s)              
   """, (userEmail, 1, username, thumbnail, epoch, 1, 8, salt, hashed_password))
dbuser.commit()


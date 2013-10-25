# -*- coding: utf-8 -*-
import urllib, urllib2
import os, datetime, shutil
import MySQLdb

#---- get environment ----  
title = raw_input('Enter brand title: ')
email = raw_input('Enter brand email: ')
lang = raw_input('Enter brand lang: ')
if lang != "en" and lang != "zh":
   print "!!! lang has to be either lang or zh !!!"
   exit()
if not "@" in email:
   print "!!! please fill email !!!"
   exit()
   
#---- db check ----
dbcontent = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = "",
                             charset = "utf8",
                             use_unicode = True,
                             db = "nncloudtv_content")
cursor = dbcontent.cursor()

name = title.lower()
cursor.execute("""
         select id from mso where name = %s      
         """, name)
row = cursor.fetchone()
if row == None:
  # create mso	
  print "-- creating a new entry ---"     
  cursor.execute("""
     insert into mso (contactEmail, createDate, intro, jingleUrl, logoUrl, name, lang, title, type, updateDate)
              values (%s, now(), null, null, null, %s, %s, %s, 2, now());
         """, (email, name, lang, title))
  msoId = cursor.lastrowid
  # create mso config
  region = "en English"
  if lang == "zh":
     region = "zh 中文"
  cursor.execute("""
     insert into mso_config (createDate, item, msoId, updateDate, value) 
              values (now(), 'supported-region', %s, now(), %s);
         """, (msoId, region))
  storeUrl = "http://www.9x9.tv/en/products.html"  
  cursor.execute("""
     insert into mso_config (createDate, item, msoId, updateDate, value) 
              values (now(), 'store-ios', %s, now(), %s);
         """, (msoId, storeUrl))
  cursor.execute("""
     insert into mso_config (createDate, item, msoId, updateDate, value) 
              values (now(), 'store-android', %s, now(), %s);
         """, (msoId, storeUrl))
    
  dbcontent.commit() 
  # create new user
  url = "http://www.9x9.tv/playerAPI/signup?email=" + email + "&password=" + name + "9x9" + "&name=" + name + "&mso=" + name
  print url 
  print urllib2.urlopen(url).read()

  url = "http://www.9x9.tv/playerAPI/login?email=" + email + "&password=" + name + "9x9" + "&name=" + name + "&mso=" + name
  print url
  print urllib2.urlopen(url).read()

  # update super admin user profile
  cursor.execute("""
     select id from nncloudtv_nnuser1.nnuser where email= %s
        """, (email))
  userRow = cursor.fetchone()
  userId = userRow[0]
  print "userId:" + str(userId) + ";msoId:" + str(msoId)
  cursor.execute("""
     update nncloudtv_nnuser1.nnuser_profile set priv='111111' where userId=%s and msoId=%s;
        """, (userId, msoId))
  dbcontent.commit()
  
else: 
  print "!!! existing brand id:" + str(row[0])

cursor.close ()


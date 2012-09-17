#!/usr/bin/python
# -*- coding: utf-8 -*-

# initialize tag information
# 1) basic value for hot and thrending
# 2) give channel tags

import urllib, urllib2
import os
from array import *
import MySQLdb
import time
import string

dbcontent = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = "",
                             charset = "utf8",
                             use_unicode = True,
                             db = "nncloudtv_content")

contentCursor = dbcontent.cursor()

contentCursor.execute("""
    select c.id, u.email, u.id 
      from nnchannel c, content_ownership o, mso m, nncloudtv_nnuser1.nnuser u 
     where c.contentType = 6
       and c.id = o.contentId
       and o.contentType = 2
       and o.msoId = m.id
       and m.contactEmail = u.email 
    """)
i = 0
rows = contentCursor.fetchall()
for r in rows:
  i = i + 1
  cId = r[0]
  uId = r[2]
  userIdStr = "1-" + str(uId)
  print "update channel: " + str(cId) + "; with user: " + userIdStr   
  contentCursor.execute("""
    update nnchannel
       set userIdStr = %s
     where id = %s 
    """, (userIdStr, cId))

dbcontent.commit()

print "record done:" + str(i)
dbcontent.close()

# initialize tag information
# 1) basic value for hot and thrending
# 2) give channel tags

import urllib, urllib2
import os
from array import *
import MySQLdb
import time
import string
import codecs

dbcontent = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = "",
                             charset = "utf8",
                             use_unicode = True,
                             db = "nncloudtv_content")
contentCursor = dbcontent.cursor()
current = "zh"       
fileName = "contentPool_" + current + ".txt"
feed = open(fileName, "rU")
allCategoryId = 1 
if current == "zh":
   allCategoryId = 20
i = 0
for line in feed:  
  line = line.decode("utf-8-sig")
  i = i+1
  data = line.split('\t')
  cId = data[0].rstrip().lstrip()   
  cName = data[1].rstrip().lstrip()
  categoryName = data[3].rstrip().lstrip()
  tags = data[4].rstrip("\"").lstrip("\"")
  tag = tags.split(',')
  lang = data[5].rstrip().lstrip()
  sphere = data[6].rstrip().lstrip()  
  lang = lang.lower()
  sphere = sphere.lower()  
  if lang == 'tw':
     lang = 'zh'
  if lang == 'others':
     lang = 'other'
  if sphere == 'tw':
     sphere = 'zh'
  if sphere == 'worldwide':
     sphere = 'other'
  if sphere == 'us':
     sphere = 'en'
  print "i: " + str(i) + ";cid: " + str(cId) + "; name:" + cName + "; categoryName: " + categoryName + "; lang:" + lang + "; sphere:" + sphere + "; tags:" + tags   
  contentCursor.execute ("""             
     select id from nnchannel 
      where id = %s 
       """, (cId))        
  cnt = contentCursor.rowcount
  if cnt == 0:
     print "this cid does not exist:" + str(cId)

  ### update channel metadata               
  contentCursor.execute ("""             
		 update nnchannel set name = %s, lang = %s, sphere = %s, tag = %s
		  where id = %s
     """, (cName, lang, sphere, tags, cId)) 
  dbcontent.commit()
  ### update category info                
  print "categoryName: " + categoryName
  contentCursor.execute("""
    select id 
      from category
     where name = %s
     """, (categoryName))
  categoryRow = contentCursor.fetchone()
  categoryId = categoryRow[0]
  print "categoryId:" + str(categoryId)       
  print "channelId:" + str(cId)
  #contentCursor.execute("""
  #   select id
  #    from category_map
  #   where categoryId = %s and channelId = %s
  #   """, (categoryId, cId))
  #count = contentCursor.rowcount          
  #if count == 0:           
  contentCursor.execute("""
     insert into category_map (categoryId, channelId, updateDate)
                       values (%s, %s, now()) 
     """, (categoryId, cId))
  contentCursor.execute("""
     insert into category_map (categoryId, channelId, updateDate)
                       values (%s, %s, now()) 
     """, (allCategoryId, cId))
    
  if sphere == 'other':
     otherCategoryId = 0
     otherAllCategoryId = 0
     if current == "en":
        otherCategoryId = categoryId + 19
        otherAllCategoryId = 20
     if current == "zh":
        otherCategoryId = categoryId - 19
        otherAllCategoryId = 1
     contentCursor.execute("""
        insert into category_map (categoryId, channelId, updateDate)
                          values (%s, %s, now()) 
        """, (otherCategoryId, cId))     
     contentCursor.execute("""
        insert into category_map (categoryId, channelId, updateDate)
                          values (%s, %s, now()) 
        """, (otherAllCategoryId, cId))
  
  ### update tag info
  for t in tag:    
     t = t.lower()  
     t = t.rstrip().lstrip().lstrip("\t")
     print (t)
     tagId = 0
     if t != "":
        contentCursor.execute("""
          select id 
            from tag
           where name = %s
           """, (t))
        count = contentCursor.rowcount
        if count == 0:   
           contentCursor.execute("""
              insert into tag (name, updateDate)
                       values (%s, now()) 
              """, (t))                
           contentCursor.execute("""         
             select id 
               from tag
              where name = %s
              """, (t))
        tagRow = contentCursor.fetchone()
        tagId = tagRow[0]        
        contentCursor.execute("""
          select id
            from tag_map                          
           where tagId = %s and channelId = %s
           """, (tagId, cId))
        count = contentCursor.rowcount
        if count == 0:           
           contentCursor.execute("""
              insert into tag_map (tagId, channelId, updateDate)
                           values (%s, %s, now()) 
              """, (tagId, cId))               
  dbcontent.commit()      
  #if i > 2:                              
  #   break

print "record done:" + str(i)
feed.close()
dbcontent.close()

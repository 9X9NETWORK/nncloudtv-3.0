#process billboard channels

import urllib, urllib2
import os
from array import *
import time
import MySQLdb
                                          
themes = ['daypart', 'daytime', 'downtime', 'evening', 'primetime', 'latenight', 'nightowl']
                                                    
dbcontent = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = "",
                             charset = "utf8",
                             use_unicode = True,
                             db = "nncloudtv_content")

root = "http://localhost:8080"
for theme in themes:      
   # get tagId
   tagurl = root + "/wd/tag?name=" + theme + "(9x9en)"                
   tagId = urllib2.urlopen(tagurl).read()
   print theme + ";tagId:" + str(tagId)            
   # submit channels           
   filename = theme + ".txt"                                                                    
   feed = open(filename, "rU")   
   i = 0
   for line in feed:
      data = line.split('\t')                   
      name = data[0]
      name = name.replace(' ', '%20')
      url = data[1]
      #print "name:" + name                                        
      #print "url:" + url        
      posturl = root + "/wd/channelSubmit?url=" + url + "&name=" + name
      posturl = posturl.replace('\n', '')
      print posturl
      cid = urllib2.urlopen(posturl).read()
      #print cid   
      # submit tagId and channelId
      tagmapurl = root + "/wd/tagMap?tagId=" + tagId + "&chId=" + cid
      print tagmapurl
      urllib2.urlopen(tagmapurl).read()
      #print tagmapurl
      i = i+1      
 
print "record done:" + str(i)
feed.close()

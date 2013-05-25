#3. marked notified

import urllib, urllib2
import MySQLdb

dbcontent = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = "",
                             charset = "utf8",                                                         
                             use_unicode = True,
                             db = "nncloudtv_content")
cursor = dbcontent.cursor()

# find instances need to be notified
cursor.execute("""                             
   select id, userId, msoId, poiId, eventId 
     from nncloudtv_analytics.poi_pdr
     where scheduledDate is not null 
       and scheduledDate > now()              
       and scheduledDate < date_add(now(), interval 10 minute) 
       and notified = false
   """)                                                                                                                      
scheduler_rows = cursor.fetchall()

# find the corresponding devices
for sr in scheduler_rows: 
   scheduledId = sr[0] 
   userId = sr[1]
   msoId = sr[2]                           
   eventId = sr[4]
   print "scheduledId:" + str(scheduledId) + ";eventId:" + eventId
   cursor.execute("""                             
      select token, vendor  
        from nncloudtv_nnuser1.endpoint
       where userId = %s
         and msoId = %s
   """, (userId, msoId))                                                                                                                      
   device_rows = cursor.fetchall()
   for dr in device_rows:
      token = dr[0]
      vendor = dr[1]
      print "token:" + str(token) + ";vendor:" + str(vendor)
      cursor.execute("""                             
         select message
           from nncloudtv_content.poi_event
          where id = %s
         """, (eventId))
      event_rows = cursor.fetchall()
      for er in event_rows:
         msg = er[0]
	 mapmsg = {'msg': msg}
         msg = urllib.quote(msg, '')
         print "msg:" + msg 
         #url = "http://localhost:8080/notify/send?device=" + str(token) + "&msg=" + str(msg) + "&vendor=" + str(vendor);
         #print url
         #urllib2.urlopen(url).read()
         cursor.execute("""
             update nncloudtv_analytics.poi_pdr
                set notified = true
              where id = %s
             """, (scheduledId))       
         #dbcontent.commit() 
 
cursor.close()                                                                          
dbcontent.close()


# migrate from old guide to 3.2 guide
import hashlib, uuid
import MySQLdb
import urllib, urllib2
import os
from array import *
import time

def seqChange(seq):    
   newSeq = [1,2,3,10,11,12,19,20,21,4,5,6,13,14,15,22,23,24,7,8,9,16,17,18,25,26,27,28,29,30,37,38,39,46,47,48,31,32,33,40,41,42,49,50,51,34,35,36,43,44,45,52,53,54,55,56,57,64,65,66,73,74,75,58,59,60,67,68,69,76,77,78,61,62,63,70,71,72,79,80,81]    
   newValue = newSeq[seq-1]                                            
   return newValue   

dbuser = MySQLdb.connect (host = "localhost",
                          user = "root",
                          passwd = "",
                          charset = "utf8",
                          use_unicode = True,
                          db = "nncloudtv_nnuser1")

userCursor = dbuser.cursor()
userCursor.execute("""
 select id from nnuser order by id""")
userRow = userCursor.fetchall() 
i = 0
for ur in userRow:
   uid = ur[0]
   userCursor.execute("""
       select id, seq, channelId
   	     from nnuser_subscribe
   	    where userId = %s 
   	    order by userId, seq
   """, (uid))
   subscribeRow = userCursor.fetchall()
   
   sidList = list()
   newSeqList = list()
   processIdList = list()
   processSeqList = list()    
   deleteIdList = list()
   seqarr = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
   print "seqerr len:" + str(len(seqarr))
   for sr in subscribeRow:
      sid = sr[0]
      seq = sr[1]
      channelId = sr[2]
      if seq > 81:
         print "wrong data"      
      if (seq >= 61 and seq <=63):
         processIdList.append(sid)
         processSeqList.append(seq)                    
      elif (seq >= 70 and seq <=72):
         processIdList.append(sid)
         processSeqList.append(seq)     
      elif (seq >= 79 and seq <=81): 
         processIdList.append(sid) 
         processSeqList.append(seq)
      elif (seq > 81 or seq < 1):                                                  
         print "bad data"
      else:
         newSeq = seqChange(seq)     
         print "userId:" + str(uid) + ";sid:" + str(sid) + ";seq:" + str(seq) + ";newSeq:" + str(newSeq)
         seqarr[newSeq-1] = 1         
         sidList.append(sid)
         newSeqList.append(newSeq)      

   print "process id list len:" + str(len(processIdList))
   for p in processIdList:
      print "processidlist"
      j = 0
      for s in seqarr:
	       if s == 0:
	          sidList.append(p) #sid
	          newSeqList.append(j+1) #new seq
	          processIdList.remove(p)
	          seqarr[j] = 1
	          print "sidlist add:" + str(p) + ";new seq:" + str(j+1)
	          break
	       j = j+1
   k = 0
   for sid in sidList:
      print "i:" + str(i) + ";seq:" + str(newSeqList[k])
      userCursor.execute("""                      
          update nnuser_subscribe
             set seq = %s
           where id = %s
           """, (newSeqList[k], sid))
      k = k+1
   for p in processIdList:
      userCursor.execute("""                      
          delete from nnuser_subscribe
           where id = %s
           """, (p))
          
   dbuser.commit()   
   i = i+1
   #if i > 2:
   #   break

userCursor.execute("""                      
		delete from nnuser_subscribe
		 where seq > 72
		 """)
dbuser.commit()
userCursor.close ()
print "record done:" + str(i)


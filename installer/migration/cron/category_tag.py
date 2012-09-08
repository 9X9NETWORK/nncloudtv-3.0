# import category_channel_set
#- read through every row,
#  find in category table, the ori_id, update with new id
#  find in nnset table, the ori_id, new_id, update with new id

import MySQLdb

conn = MySQLdb.connect (host = "localhost",
                        user = "root",
                        passwd = "",
                        charset = "", 
                        use_unicode = True,                                               
                        db = "nncloudtv_content")

try:
  cursor = conn.cursor ()
  cursor.execute ("select c.id categoryId, t.id tagId, t.name tagName, count(*) cnt
                     from category c, tag t, category_map cm, tag_map tm
                    where c.id = cm.categoryId
                      and t.id = tm.tagId
                      and cm.channelId = tm.channelId
                    group by categoryId, tagId
                    order by categoryId, cnt desc")
  data = cursor.fetchone()
  baseCategoryId = data[0]
	i = 0  
  data = cursor.fetchall ()
  tagName = ""
  for d in data:
    categoryId = d[0]
    tagName += d[2]
    if categoryId != baseCategoryId:    
       cursor.execute ("""            
            update category set tag = "%s" where id = %s 
            """, (tagName, categoryId))    
    else:
       tagName = ""
       baseCategoryId = categoryId
       
  conn.commit()
  cursor.close ()
  conn.close ()

except MySQLdb.Error, e:
  print "Error %d: %s" % (e.args[0], e.args[1])
  sys.exit (1)


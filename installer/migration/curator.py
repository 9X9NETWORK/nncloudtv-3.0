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

dbuser = MySQLdb.connect (host = "localhost",
                             user = "root",
                             passwd = "",
                             charset = "utf8",
                             use_unicode = True,
                             db = "nncloudtv_nnuser1")

userCursor = dbuser.cursor()

userCursor.execute("""delete from nnuser where email like 'curator%@9x9.tv' """)         
dbuser.commit()
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, true, 'ichi', '陳奕棋', 'curator1@9x9.tv', '喜歡ACG，但是還是有比較少涉入的領域～請大家多多指教!', 'http://a4.sphotos.ak.fbcdn.net/hphotos-ak-snc7/487255_4230090354172_1364920231_n.jpg'); """)         
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, true, 'evonne', 'Evonne', 'curator2@9x9.tv', 'you care;I care;woman care!', 'http://a8.sphotos.ak.fbcdn.net/hphotos-ak-ash4/304426_528514960496996_185500788_n.jpg'); """)
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, true, 'janetc', 'Janet Chou', 'curator3@9x9.tv', 'Do What I Wanna Do', 'https://fbcdn-sphotos-f-a.akamaihd.net/hphotos-ak-snc7/381156_3382567293342_272921887_n.jpg'); """)
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, true, 'tanny', 'T Anny', 'curator4@9x9.tv', 'Play!Life.', 'https://fbcdn-sphotos-d-a.akamaihd.net/hphotos-ak-snc6/264510_232961963399330_8192909_n.jpg');""")
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, true, 'newsme', '時事達人', 'curator5@9x9.tv', '網羅時事一把罩', 'http://www.greekshares.com/uploads/image/latest_news_greekshares.jpg');  """)
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, false,'apha',  '阿發', 'curator6@9x9.tv', '人生就是開心就好，簡簡單單的生活足矣！', 'http://etch.s.dpool.sina.com.cn/nd/dataent/moviepic/pics/59/moviepic_a550a67604ae0c89fb734631aa4ea5d9.jpg'); """)
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, false,'mengjie',  'Mengjie', 'curator7@9x9.tv', 'Hi，這是我的頻道。', 'http://a4.sphotos.ak.fbcdn.net/hphotos-ak-ash3/39913_1406015591115_1687309_n.jpg');   """)
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, false,'grass',  '細草，有風飄飄。', 'curator8@9x9.tv', '時間，會告訴你甚麼是愛。時間，會證明一切。預言和詛咒的差別。', 'http://www.qqxoo.com/uploads/allimg/120730/9-120I01J6440-L.jpg'); """)
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, false,'amulo',  '阿姆羅', 'curator9@9x9.tv', '你能保證戰鬥完了後可以好好睡一覺嗎????', 'http://gd.766.com/h002/h30/img200902021050150.jpg'); """)                                             
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, false,'weic',  '偽˙善', 'curator10@9x9.tv', '跟一般人一樣，單純的存在著. 如果說我能為大家帶來甚麼, 或許是那微不足道但真誠的，話語', 'http://pica.nipic.com/2007-06-12/2007612211417720_2.jpg'); """)
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, false,'wulala',  '烏拉拉', 'curator11@9x9.tv', '只要一秒鐘 讓你呵呵笑出來！', 'http://pic.pimg.tw/octa1113/1685f9b90ecbfe1b48663ca6e5df3416.jpg');""")
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, false,'archer',  'archer', 'curator12@9x9.tv', 'To be yourself', 'http://www.xpcolor.com/wallpaper/UploadPic/2007-12/200712502827771.jpg');""")
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, false,'debbie',  'Debbie', 'curator13@9x9.tv', '不完美才是完美', 'http://image.tpwang.net/image/%E8%89%BE/artist-%E8%89%BE%E8%96%87%E5%85%92%C2%B7%E6%8B%89%E7%B6%AD%E5%B0%BC/%E8%89%BE%E8%96%87%E5%85%92%C2%B7%E6%8B%89%E7%B6%AD%E5%B0%BC192371.jpg');""")
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, false,'sumer',  'sumer', 'curator14@9x9.tv', 'Keep in faith', 'http://motorcyclehdwallpaper.com/?attachment_id=473'); """)
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, false,'dpd',  '大排檔', 'curator15@9x9.tv', '整理了一堆雜七雜八的節目，歡迎訂閱！', '');  """)
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, true, 'rong', 'RONG', 'curator16@9x9.tv', '來自四次原星球，喜好追求新事物，喜歡就訂閱我的頻道吧', 'http://a6.sphotos.ak.fbcdn.net/hphotos-ak-snc6/s720x720/216077_525923707423517_1297698671_n.jpg'); """)
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, true, 'sing', '思吟', 'curator17@9x9.tv', 'Europe travel', 'https://fbcdn-sphotos-f-a.akamaihd.net/hphotos-ak-ash4/488324_3816646129815_373229457_n.jpg') ; """)
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, true, 'jasonl', 'Jason Lee', 'curator18@9x9.tv', 'Sensation 電音派對', 'http://profile.ak.fbcdn.net/hprofile-ak-ash4/275985_100001674112594_2144873977_n.jpg') ; """)
userCursor.execute("""insert into nnuser (updateDate, type, shard, featured, profileUrl, name, email, intro, imageUrl) values (now(), 4, 1, true, 'kingw', '金木義則', 'curator19@9x9.tv', '嚴選音樂，就在Music Day', 'http://profile.ak.fbcdn.net/hprofile-ak-snc7/368861_100002766796163_971690919_n.jpg') ; """)
dbuser.commit()

curators = []
curators.append( [8549])
curators.append([8571])
curators.append([8486])
curators.append([8487])
curators.append([8599,8600,8601,8602])
curators.append([3380,3417,3450,3833,8430,3421,3562,3333,3451,3452,3861,3454,3339,3566,3425,3309,3863,3568,3839,3340,3576,3559,3389,3574,3338,6636,3835,3570,3841,3573,3569])
curators.append([3449,3558,3422,6522,6561,3575,3845,3560,3303,3836,3337,8427,3561,3419,3859,3304,3306,3571,3391])
curators.append([3563,3564,3334,3565,3384,3840,3418,3426,3341,6590,3455,6704,3344])
curators.append([3335,3423,6546,6560,6640,6521,3336,3429,6600,3390,3844,3860,3305])
curators.append([6588,6589,3862,3307,3308,3383,3310,3386,3339,3387,3343,7160,3864,6562,3388,3311,6741])
curators.append([7155,6781,8445,8447,7355,7165,8434,6730,7156,6684,8444,7138,7251,7194,7162,6679,8455,7532,7891,6735,6683])
curators.append([7687,8451,8457,3857,7983,6691,6672,7892,3377,7807,6543,6557,6705,3328,3548,6556,7888,8459,8453,3409,6608,3442,8001,7197,8439,8450,8446,3826,6536,6620,8432,6580,3406,6605,7998,8436,3298,7684,6691])
curators.append([6550,8437,3440,3549,6579,7889,3369,7655,6629,3322,7403,8458,3853,6552,3828,3550,3370,6621,3854,3829,7149,6512,7166,3372,6609,6538,7354,3324,8452,3295,3443,8438,7343,3373,3410,8449,3444,3374,3296,3326,3445,6596,6514,6611,3375,3830,6597,6515,3411,3299,3412,3855,6583,6541,6632,8000,3831,3552,7992,3856,3405,6614,3832,6558,3553,7139,6625,6708,7984,8460,3300,6692,6517,3447,3554,7249,6544,3330,3414,8428,3331,3555,6633,7432,7940])
curators.append([3301,7712,7588,8435,3415,6545,7511,6518,3329,6584,3413,6624,6542,7281,8443,6555,3838,7587,3325,7890,3551,7161,7999,7996,8002,3371,7321,7253,3323,3293,6745,7182,7190,3441,6606,3408,3407,6619,7128,6627,6607])
curators.append([6634,6587,3381,3858,3834,3420,8442,7995,3424,3837,3382,6626,3385,8431,8456,3342,3572,3842,3456,3843,3457,6617,6547])
curators.append([8310])
curators.append([])
curators.append([5541])
curators.append([8612])

i = 1
for curator in curators:
   print i
   email = 'curator' + str(i) + '@9x9.tv'
   for channel in curator:
      print channel
      userCursor.execute("""
        select id                              
          from nnuser                          
         where email = %s
         """, (email))
      user = userCursor.fetchone()
      userId = user[0]
      userIdStr = "1-" + str(userId)
      print userIdStr
      userCursor.execute("""
        update nncloudtv_content.nnchannel                              
          set userIdStr = %s                          
         """, (userIdStr))
      
   i = i+1
dbuser.commit()      

print "record done:" + str(i)
dbuser.close()
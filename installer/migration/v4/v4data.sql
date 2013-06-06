use nncloudtv_content;
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 5,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 6,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 7,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 8,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 9,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 10, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 11, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 12, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 13, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 14, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 15, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 16, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 17, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 18, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 19, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 1,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 2,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 3,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 4,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 5,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 6,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 7,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 8,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 9,  1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 10, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 11, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 12, 1, false, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 13, 1, false, now(), now());
# systag: nnset 9x9
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 1,   2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (1, 2,   2, true, now(), now());
# systag: nnset cts
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 1,   2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 2,   2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 3,   2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 4,   2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 5,   2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 6,   2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 7,   2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 8,   2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 9,   2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 10,  2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 11,  2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 12,  2, true, now(), now());
insert into systag(msoId, seq, type, featured, createDate, updateDate) values (3, 13,  2, true, now(), now());
# systag: dashboard 9x9
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 1, 1,  3, false, now(), now(), timeStart, timeEnd, '7'   from dashboard where id=1);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 1, 1,  3, false, now(), now(), timeStart, timeEnd, '1'   from dashboard where id=2);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 1, 1,  3, false, now(), now(), timeStart, timeEnd, '2'   from dashboard where id=3);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 1, 1,  3, false, now(), now(), timeStart, timeEnd, '3'   from dashboard where id=4);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 1, 1,  3, false, now(), now(), timeStart, timeEnd, '4'   from dashboard where id=5);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 1, 1,  3, false, now(), now(), timeStart, timeEnd, '5'   from dashboard where id=6);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 1, 1,  3, false, now(), now(), timeStart, timeEnd, '6'   from dashboard where id=7);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 1, 2,  4, false, now(), now(), timeStart, timeEnd, null  from dashboard where id=8);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 1, 3,  4, false, now(), now(), timeStart, timeEnd, null  from dashboard where id=9);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 1, 4,  4, false, now(), now(), timeStart, timeEnd, null  from dashboard where id=10);

# systag: dashboard tzuchi
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 4, 1,  3, false, now(), now(), timeStart, timeEnd,  '7'  from dashboard where id=11);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 4, 1,  3, false, now(), now(), timeStart, timeEnd,  '1'  from dashboard where id=12);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 4, 1,  3, false, now(), now(), timeStart, timeEnd,  '2'  from dashboard where id=13);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 4, 1,  3, false, now(), now(), timeStart, timeEnd,  '3'  from dashboard where id=14);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 4, 1,  3, false, now(), now(), timeStart, timeEnd,  '4'  from dashboard where id=15);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 4, 1,  3, false, now(), now(), timeStart, timeEnd,  '5'  from dashboard where id=16);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 4, 1,  3, false, now(), now(), timeStart, timeEnd,  '6'  from dashboard where id=17);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 4, 2,  4, false, now(), now(), timeStart, timeEnd,  null from dashboard where id=18);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 4, 3,  4, false, now(), now(), timeStart, timeEnd,  null from dashboard where id=19);
insert into systag(msoId, seq, type, featured, createDate, updateDate, timeStart, timeEnd, attr) (select 4, 4,  4, false, now(), now(), timeStart, timeEnd,  null from dashboard where id=20);

############################################
# systag_display: category 9x9
truncate systag_display;
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 1,  name, null, lang, null, now(), channelCnt from category where id=1);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 2,  name, null, lang, null, now(), channelCnt from category where id=2);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 3,  name, null, lang, null, now(), channelCnt from category where id=3);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 4,  name, null, lang, null, now(), channelCnt from category where id=4);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 5,  name, null, lang, null, now(), channelCnt from category where id=5);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 6,  name, null, lang, null, now(), channelCnt from category where id=6);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 7,  name, null, lang, null, now(), channelCnt from category where id=7);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 8,  name, null, lang, null, now(), channelCnt from category where id=8);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 9,  name, null, lang, null, now(), channelCnt from category where id=9);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 10, name, null, lang, null, now(), channelCnt from category where id=10);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 11, name, null, lang, null, now(), channelCnt from category where id=11);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 12, name, null, lang, null, now(), channelCnt from category where id=12);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 13, name, null, lang, null, now(), channelCnt from category where id=13);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 14, name, null, lang, null, now(), channelCnt from category where id=14);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 15, name, null, lang, null, now(), channelCnt from category where id=15);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 16, name, null, lang, null, now(), channelCnt from category where id=16);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 17, name, null, lang, null, now(), channelCnt from category where id=17);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 18, name, null, lang, null, now(), channelCnt from category where id=18);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 19, name, null, lang, null, now(), channelCnt from category where id=19);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 1,  name, null, lang, null, now(), channelCnt from category where id=20);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 2,  name, null, lang, null, now(), channelCnt from category where id=21);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 3,  name, null, lang, null, now(), channelCnt from category where id=22);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 4,  name, null, lang, null, now(), channelCnt from category where id=23);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 5,  name, null, lang, null, now(), channelCnt from category where id=24);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 6,  name, null, lang, null, now(), channelCnt from category where id=25);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 7,  name, null, lang, null, now(), channelCnt from category where id=26);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 8,  name, null, lang, null, now(), channelCnt from category where id=27);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 9,  name, null, lang, null, now(), channelCnt from category where id=28);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 10, name, null, lang, null, now(), channelCnt from category where id=29);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 11, name, null, lang, null, now(), channelCnt from category where id=30);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 12, name, null, lang, null, now(), channelCnt from category where id=31);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 13, name, null, lang, null, now(), channelCnt from category where id=32);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 14, name, null, lang, null, now(), channelCnt from category where id=33);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 15, name, null, lang, null, now(), channelCnt from category where id=34);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 16, name, null, lang, null, now(), channelCnt from category where id=35);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 17, name, null, lang, null, now(), channelCnt from category where id=36);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 18, name, null, lang, null, now(), channelCnt from category where id=37);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 19, name, null, lang, null, now(), channelCnt from category where id=38);
# systag_display: category cts
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 20, name, null, lang, null, now(), channelCnt from category where id=39);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 21, name, null, lang, null, now(), channelCnt from category where id=40);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 23, name, null, lang, null, now(), channelCnt from category where id=42);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 24, name, null, lang, null, now(), channelCnt from category where id=43);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 25, name, null, lang, null, now(), channelCnt from category where id=44);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 26, name, null, lang, null, now(), channelCnt from category where id=45);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 27, name, null, lang, null, now(), channelCnt from category where id=46);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 28, name, null, lang, null, now(), channelCnt from category where id=47);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 29, name, null, lang, null, now(), channelCnt from category where id=48);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 30, name, null, lang, null, now(), channelCnt from category where id=49);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 31, name, null, lang, null, now(), channelCnt from category where id=50);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 32, name, null, lang, null, now(), channelCnt from category where id=51);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 32, name, null, lang, null, now(), channelCnt from category where id=52);
# systag_display: nnset cts 
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 33, name, imageUrl, lang, null, now(), cntChannel from nnset where id=1);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 34, name, imageUrl, lang, null, now(), cntChannel from nnset where id=2);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 35, name, imageUrl, lang, null, now(), cntChannel from nnset where id=3);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 36, name, imageUrl, lang, null, now(), cntChannel from nnset where id=4);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 37, name, imageUrl, lang, null, now(), cntChannel from nnset where id=5);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 38, name, imageUrl, lang, null, now(), cntChannel from nnset where id=6);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 39, name, imageUrl, lang, null, now(), cntChannel from nnset where id=7);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 40, name, imageUrl, lang, null, now(), cntChannel from nnset where id=8);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 41, name, imageUrl, lang, null, now(), cntChannel from nnset where id=9);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 42, name, imageUrl, lang, null, now(), cntChannel from nnset where id=10);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 43, name, imageUrl, lang, null, now(), cntChannel from nnset where id=11);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 44, name, imageUrl, lang, null, now(), cntChannel from nnset where id=12);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 45, name, imageUrl, lang, null, now(), cntChannel from nnset where id=13);
# systag_display: nnset 9x9
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) values (46, 'Internet Daily', null, 'zh', null, now(), 11);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) values (47, 'Music Day',      null, 'zh', null, now(), 22);
# systag_display: dashboard 9x9
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 48,  name, null, "en", null, now(), 9 from dashboard where id=1);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 49,  name, null, "en", null, now(), 9 from dashboard where id=2);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 50,  name, null, "en", null, now(), 9 from dashboard where id=3);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 51,  name, null, "en", null, now(), 9 from dashboard where id=4);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 52,  name, null, "en", null, now(), 9 from dashboard where id=5);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 53,  name, null, "en", null, now(), 9 from dashboard where id=6);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 54,  name, null, "en", null, now(), 9 from dashboard where id=7);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 55,  name, null, "en", null, now(), 9 from dashboard where id=8);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 56,  name, null, "en", null, now(), 9 from dashboard where id=9);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 57,  name, null, "en", null, now(), 9 from dashboard where id=10);

insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 48,  '活力早晨',     null, "zh", null, now(), 9 from dashboard where id=1);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 49,  '日間焦點',     null, "zh", null, now(), 9 from dashboard where id=2);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 50,  '午後光影',     null, "zh", null, now(), 9 from dashboard where id=3);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 51,  '傍晚時分', null, "zh", null, now(), 9 from dashboard where id=4);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 52,  '黃金強檔',     null, "zh", null, now(), 9 from dashboard where id=5);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 53,  '夜間精選',     null, "zh", null, now(), 9 from dashboard where id=6);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 54,  '午夜小品',     null, "zh", null, now(), 9 from dashboard where id=7);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 55,  '前時段',   null, "zh", null, now(), 9 from dashboard where id=8);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 56,  '訂閱',     null, "zh", null, now(), 9 from dashboard where id=9);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 57,  '帳戶',     null, "zh", null, now(), 9 from dashboard where id=10);

# systag_display: dashboard tzuchi
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 58,  '活力早晨', null, "zh", null, now(), 9 from dashboard where id=11);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 59,  '日間焦點', null, "zh", null, now(), 9 from dashboard where id=12);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 60,  '午後光影', null, "zh", null, now(), 9 from dashboard where id=13);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 61,  '傍晚時分', null, "zh", null, now(), 9 from dashboard where id=14);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 62,  '黃金強檔', null, "zh", null, now(), 9 from dashboard where id=15);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 63,  '夜間精選', null, "zh", null, now(), 9 from dashboard where id=16);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 64,  '午夜小品', null, "zh", null, now(), 9 from dashboard where id=17);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 65,  name, null, "en", null, now(), 9 from dashboard where id=18);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 66,  name, null, "en", null, now(), 9 from dashboard where id=19);
insert into systag_display (systagId, name, imageUrl, lang, popularTag, updateDate, cntChannel) (select 67,  name, null, "en", null, now(), 9 from dashboard where id=20);
############################################
# systag_map: category channel map cts
truncate systag_map;
insert into systag_map (systagId, channelId, createDate, updateDate) (select 20, channelId, now(), now() from category_map where categoryId = 39);
insert into systag_map (systagId, channelId, createDate, updateDate) (select 21, channelId, now(), now() from category_map where categoryId = 40);
insert into systag_map (systagId, channelId, createDate, updateDate) (select 22, channelId, now(), now() from category_map where categoryId = 42);
insert into systag_map (systagId, channelId, createDate, updateDate) (select 23, channelId, now(), now() from category_map where categoryId = 43);
insert into systag_map (systagId, channelId, createDate, updateDate) (select 24, channelId, now(), now() from category_map where categoryId = 44);
insert into systag_map (systagId, channelId, createDate, updateDate) (select 25, channelId, now(), now() from category_map where categoryId = 45);
insert into systag_map (systagId, channelId, createDate, updateDate) (select 26, channelId, now(), now() from category_map where categoryId = 46);
insert into systag_map (systagId, channelId, createDate, updateDate) (select 27, channelId, now(), now() from category_map where categoryId = 47);
insert into systag_map (systagId, channelId, createDate, updateDate) (select 28, channelId, now(), now() from category_map where categoryId = 48);
insert into systag_map (systagId, channelId, createDate, updateDate) (select 39, channelId, now(), now() from category_map where categoryId = 49);
insert into systag_map (systagId, channelId, createDate, updateDate) (select 30, channelId, now(), now() from category_map where categoryId = 50);
insert into systag_map (systagId, channelId, createDate, updateDate) (select 31, channelId, now(), now() from category_map where categoryId = 51);
insert into systag_map (systagId, channelId, createDate, updateDate) (select 32, channelId, now(), now() from category_map where categoryId = 52);

# systag_map: nnset channels, cts
insert into systag_map (systagId, channelId, createDate, updateDate) (select 33, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=1)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 34, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=2)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 35, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=3)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 36, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=4)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 37, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=5)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 38, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=6)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 39, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=7)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 40, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=8)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 41, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=9)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 42, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=10)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 43, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=11)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 44, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=12)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 45, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(name,'(ctszh)') from nnset where id=13)));

#systag_map: nnset 9x9
insert into systag_map (systagId, channelId, createDate, updateDate) values (46, 26825, now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (46, 8816,  now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (46, 14381, now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (46, 14384, now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (46, 14383, now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (46, 14382, now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (46, 8599,  now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (46, 8600,  now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (46, 8601,  now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (46, 8602,  now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (46, 14385, now(), now());

insert into systag_map (systagId, channelId, createDate, updateDate) values (47, 8763, now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (47, 8784, now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (47, 2659, now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (47, 480, now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (47, 483, now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (47, 8800, now(), now());
insert into systag_map (systagId, channelId, createDate, updateDate) values (47, 8761, now(), now());

# systag_map: dashboard channels 9x9
insert into systag_map (systagId, channelId, createDate, updateDate) (select 48, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(stackName,'(9x9en)') from dashboard where id=1)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 49, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(stackName,'(9x9en)') from dashboard where id=2)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 50, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(stackName,'(9x9en)') from dashboard where id=3)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 51, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(stackName,'(9x9en)') from dashboard where id=4)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 52, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(stackName,'(9x9en)') from dashboard where id=5)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 53, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(stackName,'(9x9en)') from dashboard where id=6)));
insert into systag_map (systagId, channelId, createDate, updateDate) (select 54, channelId, now(), now() from tag_map where tagId in (select id from tag where name = (select concat(stackName,'(9x9en)') from dashboard where id=7)));

# systag_display imageUrl for all dashboard and nnset

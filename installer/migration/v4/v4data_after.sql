use nncloudtv_content;

# set channel count
update systag_display set cntChannel = (select count(*) from systag_map where systagId=23) where systagId=23;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=24) where systagId=24;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=25) where systagId=25;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=26) where systagId=26;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=27) where systagId=27;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=28) where systagId=28;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=29) where systagId=29;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=30) where systagId=30;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=31) where systagId=31;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=32) where systagId=32;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=33) where systagId=33;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=34) where systagId=34;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=35) where systagId=35;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=36) where systagId=36;
update systag_display set cntChannel = (select count(*) from systag_map where systagId=37) where systagId=37;

# systag_display imageUrl for all dashboard and nnset


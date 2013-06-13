# mysql < v4base.sql -u root -p --verbose > v4.out

########################
use nncloudtv_content;
########################
alter table poi add column campaignId  bigint(20) DEFAULT '0';
alter table poi add column eventId     bigint(20) DEFAULT '0';
alter table poi add column pointId     bigint(20) DEFAULT '0';
alter table poi add column startDate   datetime DEFAULT NULL;
alter table poi add column endDate     datetime DEFAULT NULL;
alter table poi add column hoursOfWeek  varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL;
update poi set updateDate = now();
#####alter table poi modify updateDate timestamp not null DEFAULT CURRENT_TIMESTAMP;

alter table poi_event add column userId             bigint(20) DEFAULT '1';
alter table poi_event add column msoId              bigint(20) DEFAULT '1';
alter table poi_event add column name               varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table poi_event add column notifyMsg          varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL;
alter table poi_event add column notifyScheduler    varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL;

create index counter_shard_name on counter_shard (counterName);


CREATE TABLE `poi_point` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `targetId` bigint(20) DEFAULT '0',
  `type` smallint(6) NOT NULL,
  `startTime` varchar(255) DEFAULT '0',
  `endTime` varchar(255) DEFAULT '0',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime DEFAULT NULL,
  `tag` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `poi_campaign` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) DEFAULT '0',
  `msoId` bigint(20) DEFAULT '0',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `startdate` datetime DEFAULT NULL,
  `createdate` datetime DEFAULT NULL,
  `updatedate` datetime DEFAULT NULL,
  `enddate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `systag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `msoId` bigint(20) DEFAULT '1',
  `type` smallint(6) NOT NULL,
  `seq` smallint(6) NOT NULL DEFAULT '0',
  `featured` bit(1) NOT NULL,
  `timeStart` smallint(6) DEFAULT '0',
  `timeEnd` smallint(6) DEFAULT '0',
  `attr` varchar(10) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `updateDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `sorting` smallint(6) DEFAULT '1',
  KEY `mso_id` (`msoId`),
  KEY `type` (`type`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `systag_map` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `systagId` bigint(20) NOT NULL,
  `channelId` bigint(20) NOT NULL,
  `seq` smallint(6) NOT NULL DEFAULT '0',
  `createDate` datetime DEFAULT NULL,
  `updateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `timeStart` smallint(6) DEFAULT '0',
  `timeEnd` smallint(6) DEFAULT '0',
  `attr` varchar(10) DEFAULT NULL,
  `alwaysOnTop` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `channel_id` (`channelId`),
  UNIQUE KEY `systagMap` (`systagId`,`channelId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `systag_display` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `systagId` bigint(20) DEFAULT '1',
  `cntChannel` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  `imageUrl` varchar(255) DEFAULT NULL,
  `lang` varchar(5) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `popularTag` varchar(500) DEFAULT NULL,
  `updateDate` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  KEY `lang` (`lang`),
  PRIMARY KEY (`id`),
  KEY `systag_id` (`systagId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `store_listing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `channelid` bigint(20) NOT NULL,
  `msoid` bigint(20) NOT NULL,
  `updatedate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

######################
use nncloudtv_nnuser1;
######################
alter table nnuser_profile add column phoneNumber varchar(15);
alter table nnuser_profile add column priv varchar(6);
alter table nnuser_subscribe drop index userSubscribe;
alter table nnuser_watched add msoId bigint(20) default 1;

CREATE TABLE `endpoint` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `msoId` bigint(20) DEFAULT '1',
  `token` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `vendor` smallint(6) DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `updateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `endpoint_msoId` (`msoId`),
  KEY `endpoint_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#########################
use nncloudtv_analytics;
#########################
#alter table pdr drop column createDate;

CREATE TABLE `poi_pdr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `msoId` bigint(20) DEFAULT '1',
  `eventId` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `poiId` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `select` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `scheduledDate` datetime DEFAULT NULL,
  `updateDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `endpoint_msoId` (`msoId`),
  KEY `endpoint_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

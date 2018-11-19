/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.sampler;

import com.exedio.cope.Revision;
import com.exedio.cope.Revisions;

final class SamplerRevisions implements Revisions.Factory
{
	@Override
	public Revisions create(final Context ctx)
	{
		final String db = ctx.getEnvironment().getDatabaseProductName();

		if("mysql".equalsIgnoreCase(db))
			return getMysql();
		else
			return getOthers();
	}

	private static Revisions getMysql()
	{
		return new Revisions(
			new Revision(18, "drop columns for non-LRU ItemCache, rename invalidateLast to stamps",
				"ALTER TABLE `DiffModel` " +
					"DROP COLUMN `itemCacheReplacementRuns`," +
					"CHANGE `itemCacheInvalidaLastSize` `itemCacheStampsSize` "  +"int not null," +
					"CHANGE `itemCacheInvalidaLastHits` `itemCacheStampsHits` "  +"int not null," +
					"CHANGE `itemCacheInvaliLastPurged` `itemCacheStampsPurged` "+"int not null",
				"ALTER TABLE `DiffItemCache` " +
					"DROP COLUMN `limit`, " +
					"DROP COLUMN `replacementRuns`, " +
					"DROP COLUMN `lastReplacementRun`, " +
					"DROP COLUMN `ageAverageMillis`, " +
					"DROP COLUMN `ageMinimumMillis`, " +
					"DROP COLUMN `ageMaximumMillis`," +
					"CHANGE `invalidateLastSize` "  +"`stampsSize` "  +"int not null," +
					"CHANGE `invalidateLastHits` "  +"`stampsHits` "  +"int not null," +
					"CHANGE `invalidateLastPurged` "+"`stampsPurged` "+"int not null"
			),
			new Revision(17, "add QueryCacheStamps",
				"ALTER TABLE `DiffModel` " +
					"ADD COLUMN `queryCacheStampsSize` "  +"int not null AFTER `queryCacheConcurrentLoads`, " +
					"ADD COLUMN `queryCacheStampsHits` "  +"int not null AFTER `queryCacheStampsSize`, " +
					"ADD COLUMN `queryCacheStampsPurged` "+"int not null AFTER `queryCacheStampsHits`"
			),
			new Revision(16, "add SamplerTransaction invalidationSize",
				"ALTER TABLE `DiffTransaction` " +
					"ADD COLUMN `invalidationSize` int not null " +
						"AFTER `thread_stackTrace`"
			),
			new Revision(15, "add SamplerTransaction commit-hooks",
				"ALTER TABLE `DiffTransaction` " +
					"ADD COLUMN `preCommitHookCount` int not null, " +
					"ADD COLUMN `preCommitHookDuplicates` int not null, " +
					"ADD COLUMN `postCommitHookCount` int not null, " +
					"ADD COLUMN `postCommitHookDuplicates` int not null"
			),
			new Revision(14, "add SamplerModel#itemCacheLimit, #itemCacheLevel",
				"ALTER TABLE `DiffModel` " +
					"ADD COLUMN `itemCacheLimit` int not null, " +
					"ADD COLUMN `itemCacheLevel` int not null"
			),
			new Revision(13, "add SamplerModel#queryCacheConcurrentLoads",
				"ALTER TABLE `DiffModel` " +
					"ADD COLUMN `queryCacheConcurrentLoads` int not null"
			),
			new Revision(12, "add SamplerEnvironment#buildTag",
				"ALTER TABLE `SamplerEnvironment` " +
					"ADD COLUMN `buildTag` text CHARACTER SET utf8 COLLATE utf8_bin"
			),
			new Revision(11, "remove tables for absolute values, not used anymore since revision 6",
				"DROP TABLE `SamplerClusterNode`",
				"DROP TABLE `SamplerItemCache`",
				"DROP TABLE `SamplerMedia`",
				"DROP TABLE `SamplerTransaction`",
				"DROP TABLE `SamplerModel`"
			),
			new Revision(10, "add SamplerEnvironment",
				"CREATE TABLE `SamplerEnvironment`(" +
					"`this` int," +
					"`connectDate` bigint not null," +
					"`initializeDate` bigint not null," +
					"`sampleDate` bigint not null," +
					"`hostname` text CHARACTER SET utf8 COLLATE utf8_bin," +
					"`connectionUrl` text CHARACTER SET utf8 COLLATE utf8_bin not null," +
					"`connectionUsername` text CHARACTER SET utf8 COLLATE utf8_bin not null," +
					"`databaseProductName` text CHARACTER SET utf8 COLLATE utf8_bin not null," +
					"`databaseProductVersion` text CHARACTER SET utf8 COLLATE utf8_bin not null," +
					"`databaseVersionMajor` int not null," +
					"`databaseVersionMinor` int not null," +
					"`driverName` text CHARACTER SET utf8 COLLATE utf8_bin not null," +
					"`driverVersion` text CHARACTER SET utf8 COLLATE utf8_bin not null," +
					"`driverVersionMajor` int not null," +
					"`driverVersionMinor` int not null," +
					"CONSTRAINT `SamplerEnvironment_Pk` PRIMARY KEY(`this`)," +
					"CONSTRAINT `SampleEnviro_connDate_Unq` UNIQUE(`connectDate`)" +
				") ENGINE=innodb"
			),
			new Revision(9, "Revision table gets primary key instead of unique constraint",
				"ALTER TABLE `SamplerRevision` DROP INDEX `SamplerRevisionUnique`",
				"ALTER TABLE `SamplerRevision` MODIFY `v` int PRIMARY KEY"),
			new Revision(8, "add MediaInfo.getInvalidSpecial",
				"alter table `DiffModel` add column `mediasInvalidSpecial` int not null",
				"alter table `DiffMedia` add column `invalidSpecial` int not null"
			),
			new Revision(7, "sample ChangeListenerInfo#getSize()",
				"alter table `DiffModel` " +
					"add column `changeListenerSize` int not null after `queryCacheInvalidations`"
			),
			new Revision(6, "store differences instead of absolute values",
				"create table `SamplerMediaId`(" +
					"`this` int," +
					"`id` varchar(80) character set utf8 collate utf8_bin not null," +
					"constraint `SamplerMediaId_Pk` primary key(`this`)," +
					"constraint `SamplerMediaId_id_Unq` unique(`id`)" +
					") engine=innodb", // 29ms, 0 rows
				"create table `DiffModel`(" +
					"`this` int,`from` bigint not null," +
					"`date` bigint not null," +
					"`duration` bigint not null," +
					"`initialized` bigint not null," +
					"`connected` bigint not null," +
					"`connectionPoolIdle` int not null," +
					"`connectionPoolGet` int not null," +
					"`connectionPoolPut` int not null," +
					"`connectioPoolInvalidOnGet` int not null," +
					"`connectioPoolInvalidOnPut` int not null," +
					"`nextTransactionId` int not null," +
					"`commitOutConnection` int not null," +
					"`commitWithConnection` int not null," +
					"`rollbackOutConnection` int not null," +
					"`rollbackWithConnection` int not null," +
					"`itemCacheHits` int not null," +
					"`itemCacheMisses` int not null," +
					"`itemCacheConcurrentLoads` int not null," +
					"`itemCacheReplacementRuns` int not null," +
					"`itemCacheReplacements` int not null," +
					"`itemCacheInvalidatOrdered` int not null," +
					"`itemCacheInvalidationDone` int not null," +
					"`itemCacheInvalidaLastSize` int not null," +
					"`itemCacheInvalidaLastHits` int not null," +
					"`itemCacheInvaliLastPurged` int not null," +
					"`queryCacheHits` int not null," +
					"`queryCacheMisses` int not null," +
					"`queryCacheReplacements` int not null," +
					"`queryCacheInvalidations` int not null," +
					"`changeListenerCleared` int not null," +
					"`changeListenerRemoved` int not null," +
					"`changeListenerFailed` int not null," +
					"`changeListenerOverflow` int not null," +
					"`changeListenerException` int not null," +
					"`changeListenerPending` int not null," +
					"`mediasNoSuchPath` int not null," +
					"`mediasRedirectFrom` int not null," +
					"`mediasException` int not null," +
					"`mediasGuessedUrl` int not null," +
					"`mediasNotAnItem` int not null," +
					"`mediasNoSuchItem` int not null," +
					"`mediasMoved` int not null," +
					"`mediasIsNull` int not null," +
					"`mediasNotComputable` int not null," +
					"`mediasNotModified` int not null," +
					"`mediasDelivered` int not null," +
					"`clusterSender_invaliSplit` int," +
					"`clusterListener_exception` int," +
					"`clusterListene_missiMagic` int," +
					"`clusterListen_wrongSecret` int," +
					"`clusterListene_fromMyself` int," +
					"constraint `DiffModel_Pk` primary key(`this`)," +
					"constraint `DiffModel_from_Unq` unique(`from`)," +
					"constraint `DiffModel_date_Unq` unique(`date`)" +
					") engine=innodb", // 3ms, 0 rows
				"create table `DiffTransaction`(" +
					"`this` int," +
					"`model` int not null," +
					"`date` bigint not null," +
					"`id` bigint not null," +
					"`name` text character set utf8 collate utf8_bin," +
					"`startDate` bigint not null," +
					"`thread_id` bigint," +
					"`thread_name` varchar(80) character set utf8 collate utf8_bin," +
					"`thread_priority` int," +
					"`thread_state` int," +
					"`thread_stackTrace` mediumtext character set utf8 collate utf8_bin," +
					"constraint `DiffTransaction_Pk` primary key(`this`)," +
					"constraint `DiffTransaction_model_Fk` foreign key (`model`) references `DiffModel`(`this`)" +
					") engine=innodb", // 3ms, 0 rows
				"create table `DiffItemCache`(" +
					"`this` int,`model` int not null," +
					"`type` int not null," +
					"`date` bigint not null," +
					"`limit` int not null," +
					"`level` int not null," +
					"`hits` int not null," +
					"`misses` int not null," +
					"`concurrentLoads` int not null," +
					"`replacementRuns` int not null," +
					"`replacements` int not null," +
					"`lastReplacementRun` bigint," +
					"`ageAverageMillis` bigint not null," +
					"`ageMinimumMillis` bigint not null," +
					"`ageMaximumMillis` bigint not null," +
					"`invalidationsOrdered` int not null," +
					"`invalidationsDone` int not null," +
					"`invalidateLastSize` int not null," +
					"`invalidateLastHits` int not null," +
					"`invalidateLastPurged` int not null," +
					"constraint `DiffItemCache_Pk` primary key(`this`)," +
					"constraint `DiffItemCache_model_Fk` foreign key (`model`) references `DiffModel`(`this`)," +
					"constraint `DiffItemCache_type_Fk` foreign key (`type`) references `SamplerTypeId`(`this`)," +
					"constraint `DiffItemCach_daAndTyp_Unq` unique(`date`,`type`)" +
					") engine=innodb", // 3ms, 0 rows
				"create table `DiffClusterNode`(" +
					"`this` int,`model` int not null," +
					"`id` int not null," +
					"`date` bigint not null," +
					"`firstEncounter` bigint not null," +
					"`fromAddress` varchar(80) character set utf8 collate utf8_bin not null," +
					"`fromPort` int not null," +
					"`invalidate_inOrder` int not null," +
					"`invalidate_outOfOrder` int not null," +
					"`invalidate_duplicate` int not null," +
					"`invalidate_lost` int not null," +
					"`invalidate_late` int not null," +
					"`invalidate_pending` int not null," +
					"`ping_inOrder` int not null," +
					"`ping_outOfOrder` int not null," +
					"`ping_duplicate` int not null," +
					"`ping_lost` int not null," +
					"`ping_late` int not null," +
					"`ping_pending` int not null," +
					"`pong_inOrder` int not null," +
					"`pong_outOfOrder` int not null," +
					"`pong_duplicate` int not null," +
					"`pong_lost` int not null," +
					"`pong_late` int not null," +
					"`pong_pending` int not null," +
					"constraint `DiffClusterNode_Pk` primary key(`this`)," +
					"constraint `DiffClusterNode_model_Fk` foreign key (`model`) references `DiffModel`(`this`)," +
					"constraint `DiffClusNode_datAndId_Unq` unique(`date`,`id`)" +
					") engine=innodb", // 3ms, 0 rows
				"create table `DiffMedia`(" +
					"`this` int,`model` int not null," +
					"`media` int not null," +
					"`date` bigint not null," +
					"`redirectFrom` int not null," +
					"`exception` int not null," +
					"`guessedUrl` int not null," +
					"`notAnItem` int not null," +
					"`noSuchItem` int not null," +
					"`moved` int not null," +
					"`isNull` int not null," +
					"`notComputable` int not null," +
					"`notModified` int not null," +
					"`delivered` int not null," +
					"constraint `DiffMedia_Pk` primary key(`this`)," +
					"constraint `DiffMedia_model_Fk` foreign key (`model`) references `DiffModel`(`this`)," +
					"constraint `DiffMedia_media_Fk` foreign key (`media`) references `SamplerMediaId`(`this`)," +
					"constraint `DiffMedia_dateAndMedi_Unq` unique(`date`,`media`)" +
					") engine=innodb" // 3ms, 0 rows
			),
			new Revision(5, "more length for SamplerTransaction#name",
				"alter table `SamplerTransaction` modify `name` text character set utf8 collate utf8_bin"),
			new Revision(4, "bugfix: SamplerTransaction#date must not be unique",
				"alter table `SamplerTransaction` drop index `SamplerTransacti_date_Unq`"),
			new Revision(3, "sample transactions",
				"create table `SamplerTransaction`(" +
					"`this` int," +
					"`model` int not null," +
					"`date` bigint not null," +
					"`initializeDate` bigint not null," +
					"`connectDate` bigint not null," +
					"`thread` int not null," +
					"`running` int not null," +
					"`id` bigint not null," +
					"`name` varchar(80) character set utf8 collate utf8_bin," +
					"`startDate` bigint not null," +
					"`thread_id` bigint," +
					"`thread_name` varchar(80) character set utf8 collate utf8_bin," +
					"`thread_priority` int," +
					"`thread_state` int," +
					"`thread_stackTrace` mediumtext character set utf8 collate utf8_bin," +
					"constraint `SamplerTransaction_Pk` primary key(`this`)," +
					"constraint `SamplerTransact_this_CkPk` check((`this`>=0) AND (`this`<=2147483647))," +
					"constraint `SamplerTransacti_model_Ck` check((`model`>=0) AND (`model`<=2147483647))," +
					"constraint `SamplerTransacti_model_Fk` foreign key (`model`) references `SamplerModel`(`this`)," +
					"constraint `SamplerTransactio_date_Ck` check((`date`>=-9223372036854775808) AND (`date`<=9223372036854775807))," +
					"constraint `SampleTransa_initiDate_Ck` check((`initializeDate`>=-9223372036854775808) AND (`initializeDate`<=9223372036854775807))," +
					"constraint `SampleTransa_conneDate_Ck` check((`connectDate`>=-9223372036854775808) AND (`connectDate`<=9223372036854775807))," +
					"constraint `SamplerTransact_thread_Ck` check((`thread`>=-2147483648) AND (`thread`<=2147483647))," +
					"constraint `SamplerTransact_runnin_Ck` check((`running`>=0) AND (`running`<=2147483647))," +
					"constraint `SamplerTransaction_id_Ck` check((`id`>=0) AND (`id`<=9223372036854775807))," +
					"constraint `SamplerTransactio_name_Ck` check(((`name` IS NOT NULL) AND ((CHAR_LENGTH(`name`)>=1) AND (CHAR_LENGTH(`name`)<=80))) OR (`name` IS NULL))," +
					"constraint `SampleTransa_startDate_Ck` check((`startDate`>=-9223372036854775808) AND (`startDate`<=9223372036854775807))," +
					"constraint `SamplerTransa_threa_id_Ck` check(((`thread_id` IS NOT NULL) AND ((`thread_id`>=0) AND (`thread_id`<=9223372036854775807))) OR (`thread_id` IS NULL))," +
					"constraint `SampleTransa_thre_name_Ck` check(((`thread_name` IS NOT NULL) AND ((CHAR_LENGTH(`thread_name`)>=1) AND (CHAR_LENGTH(`thread_name`)<=80))) OR (`thread_name` IS NULL))," +
					"constraint `SampleTransa_thre_prio_Ck` check(((`thread_priority` IS NOT NULL) AND ((`thread_priority`>=1) AND (`thread_priority`<=10))) OR (`thread_priority` IS NULL))," +
					"constraint `SampleTransa_thre_stat_Ck` check(((`thread_state` IS NOT NULL) AND (`thread_state` IN (10,20,30,40,50,60))) OR (`thread_state` IS NULL))," +
					"constraint `SamplTrans_thr_staTrac_Ck` check(((`thread_stackTrace` IS NOT NULL) AND ((CHAR_LENGTH(`thread_stackTrace`)>=1) AND (CHAR_LENGTH(`thread_stackTrace`)<=100000))) OR (`thread_stackTrace` IS NULL))," +
					"constraint `SamplerTransacti_date_Unq` unique(`date`)," +
					"constraint `SamplerTransa_threa_uniso` check(((`thread_id` is null and `thread_name` is null and `thread_priority` is null and `thread_state` is null and `thread_stackTrace` is null) or (`thread_id` is not null and `thread_priority` is not null and `thread_state` is not null)))" +
				") engine=innodb"
			),
			new Revision(2, "use SamplerTypeId",
				"create table `SamplerTypeId`(" +
					"`this` int," +
					"`id` varchar(80) character set utf8 collate utf8_bin not null," +
					"constraint `SamplerTypeId_Pk` primary key(`this`)," +
					"constraint `SamplerTypeId_this_CkPk` check((`this`>=0) AND (`this`<=2147483647))," +
					"constraint `SamplerTypeId_id_Ck` check((CHAR_LENGTH(`id`)>=1) AND (CHAR_LENGTH(`id`)<=80))," +
					"constraint `SamplerTypeId_id_Unq` unique(`id`)" +
				") engine=innodb",
				"set @a=0",
				"insert into `SamplerTypeId` (`this`,`id`) " +
					"select distinct if(@a, @a:=@a+1, @a:=1) as `this`, type as id from SamplerItemCache",
				"alter table `SamplerItemCache` " +
					"drop index `SampItemCach_daAndTyp_Unq`," +
					"change `type` `typeId` varchar(80) character set utf8 collate utf8_bin," +
					"add column `type` int," +
					"add constraint `SamplerItemCache_type_Fk` foreign key (`type`) references `SamplerTypeId`(`this`)",
				"update `SamplerItemCache` v join `SamplerTypeId` i on v.`typeId`=i.`id` set v.`type`=i.`this`",
				"alter table `SamplerItemCache` " +
					"modify `type` int not null," +
					"add constraint `SampItemCach_daAndTyp_Unq` unique(`date`,`type`)"
			),
			new Revision(1, "use composite SamplerClusterListener",
				"alter table `SamplerModel` " +
					"change `clusterListenerException`  `clusterListener_exception` bigint, " +
					"change `clusterListeneWrongSecret` `clusterListen_wrongSecret` bigint, " +
					"change `clusterListeneMissinMagic` `clusterListene_missiMagic` bigint, " +
					"change `clusterListenerFromMyself` `clusterListene_fromMyself` bigint"
			)
		);
	}

	private static Revisions getOthers()
	{
		return new Revisions(0);
	}
}

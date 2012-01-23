/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
	public Revisions create(final Context ctx)
	{
		final String db = ctx.getEnvironment().getDatabaseProductName();

		if("mysql".equalsIgnoreCase(db))
			return getMysql();
		else
			return getOthers();
	}

	private Revisions getMysql()
	{
		return new Revisions(
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

	private Revisions getOthers()
	{
		return new Revisions(0);
	}
}

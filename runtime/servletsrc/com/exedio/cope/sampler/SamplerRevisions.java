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
			)
		);
	}

	private static Revisions getOthers()
	{
		return new Revisions(0);
	}
}

/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.console;

import com.exedio.cope.Revision;
import com.exedio.cope.Revisions;

/**
 * Currently works for MySQL only.
 */
final class HistoryRevisions
{
	static final Revisions REVISIONS =
		new Revisions(
			new Revision(1, "item cache renames \"cleanup\" to \"replacement\"",
				"alter table `HistoryModel`" +
					" change `connectPoolInvaliFromIdle` `connectioPoolInvalidOnGet` integer," +
					" change `connectPoolInvaliIntoIdle` `connectioPoolInvalidOnPut` integer," +
					" change `itemCacheNumberOfCleanups` `itemCacheReplacementRuns` integer," +
					" change `itemCacheItemsCleanedUp` `itemCacheReplacements` integer",
				"alter table `HistoryItemCache`" +
					" change `numberOfCleanups` `replacementRuns` integer," +
					" change `itemsCleanedUp` `replacements` integer," +
					" change `lastCleanup` `lastReplacementRun` bigint")
		);
	
	private HistoryRevisions()
	{
		// prevent instantiation
	}
}

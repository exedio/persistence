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

package com.exedio.cope.misc;

import static com.exedio.cope.util.Check.requireGreaterZero;
import static com.exedio.cope.util.JobContext.deferOrStopIfRequested;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobStop;
import java.util.List;

public final class Delete
{
	public static void delete(
			final Query<? extends Item> query,
			final int itemsPerTransaction,
			final String transactionName,
			final JobContext ctx)
	{
		requireNonNull(query, "query");
		requireGreaterZero(itemsPerTransaction, "itemsPerTransaction"); // prevents infinite loop
		requireNonNull(ctx, "ctx");

		if(query.getPageOffset()!=0)
			throw new IllegalArgumentException(
					"query with page offset (" + query.getPageOffset() + ") not supported: " + query);
		if(query.getPageLimitOrMinusOne()!=-1)
			throw new IllegalArgumentException(
					"query with page limit (" + query.getPageLimitOrMinusOne() + ") not supported: " + query);

		query.setPage(0, itemsPerTransaction);
		final Model model = query.getType().getModel();
		for(int transaction = 0; ; transaction++)
		{
			deferOrStopIfRequested(ctx);
			try
			{
				model.startTransaction(transactionName + '#' + transaction);

				final List<? extends Item> items = query.search();
				final int itemsSize = items.size();
				if(itemsSize==0)
					return;
				for(final Item item : items)
				{
					ctx.stopIfRequested();
					item.deleteCopeItem();
					ctx.incrementProgress();
				}

				model.commit();

				if(itemsSize<itemsPerTransaction)
					return;
			}
			catch(final JobStop ignored)
			{
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
	}

	private Delete()
	{
		// prevent instantiation
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated
	 * Use {@link #delete(Query, int, String, JobContext)} instead.
	 * Choose {@code limit} wisely and allow customization,
	 * {@code 100} is probably too small.
	 */
	@Deprecated
	public static void delete(
			final Query<? extends Item> query,
			final String transactionName,
			final JobContext ctx)
	{
		delete(query, 100, transactionName, ctx);
	}
}

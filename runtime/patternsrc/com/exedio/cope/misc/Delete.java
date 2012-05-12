/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import java.util.List;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.JobStop;

public final class Delete
{
	public static void delete(
			final Query<? extends Item> query,
			final String transactionName,
			final JobContext ctx)
	{
		if(ctx==null)
			throw new NullPointerException("ctx");

		final int LIMIT = 100;
		query.setLimit(0, LIMIT);
		final Model model = query.getType().getModel();
		for(int transaction = 0; ; transaction++)
		{
			ctx.stopIfRequested();
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

				if(itemsSize<LIMIT)
					return;
			}
			catch(final JobStop js)
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
	 * @deprecated Use {@link #delete(Query,String,JobContext)} instead.
	 */
	@Deprecated
	public static int delete(
			final Query<? extends Item> query,
			final String transactionName,
			final com.exedio.cope.util.Interrupter interrupter)
	{
		return com.exedio.cope.util.InterrupterJobContextAdapter.run(
			interrupter,
			new com.exedio.cope.util.InterrupterJobContextAdapter.Body(){public void run(final JobContext ctx)
			{
				delete(query, transactionName, ctx);
			}}
		);
	}
}

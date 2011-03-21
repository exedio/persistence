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

package com.exedio.cope.misc;

import static com.exedio.cope.util.InterrupterJobContextAdapter.run;

import java.util.List;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.util.Interrupter;
import com.exedio.cope.util.JobContext;
import com.exedio.cope.util.InterrupterJobContextAdapter.Body;

public final class Delete
{
	public static int delete(
			final Query<? extends Item> query,
			final String transactionName,
			final Interrupter interrupter)
	{
		return run(
			interrupter,
			new Body(){public void run(final JobContext ctx)
			{
				delete(query, transactionName, ctx);
			}}
		);
	}

	public static void delete(
			final Query<? extends Item> query,
			final String transactionName,
			final JobContext ctx)
	{
		if(ctx==null)
			throw new NullPointerException("ctx");

		final int LIMIT = 100;
		final Model model = query.getType().getModel();
		for(int transaction = 0; !ctx.requestedToStop(); transaction++)
		{
			try
			{
				model.startTransaction(transactionName + '#' + transaction);

				query.setLimit(0, LIMIT);
				final List<? extends Item> items = query.search();
				final int itemsSize = items.size();
				if(itemsSize==0)
					return;
				for(final Item item : items)
				{
					if(ctx.requestedToStop())
					{
						model.commit();
						return;
					}

					item.deleteCopeItem();
					ctx.incrementProgress();
				}

				model.commit();

				if(itemsSize<LIMIT)
					return;
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}

		System.out.println("Aborting " + transactionName);
	}

	private Delete()
	{
		// prevent instantiation
	}
}

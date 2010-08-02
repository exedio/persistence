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

package com.exedio.cope.misc;

import java.util.List;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.util.Interrupter;

public final class Delete
{
	public static int delete(
			final Query<? extends Item> query,
			final String transactionName,
			final Interrupter interrupter)
	{
		final int LIMIT = 100;
		final Model model = query.getType().getModel();
		int result = 0;
		for(int transaction = 0; transaction<30; transaction++)
		{
			if(interrupter!=null && interrupter.isRequested())
				return result;

			try
			{
				model.startTransaction(transactionName + '#' + transaction);

				query.setLimit(0, LIMIT);
				final List<? extends Item> tokens = query.search();
				final int tokensSize = tokens.size();
				if(tokensSize==0)
					return result;
				for(final Item token : tokens)
					token.deleteCopeItem();
				result += tokensSize;
				if(tokensSize<LIMIT)
				{
					model.commit();
					return result;
				}

				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}

		System.out.println("Aborting " + transactionName + " after " + result);
		return result;
	}

	private Delete()
	{
		// prevent instantiation
	}
}

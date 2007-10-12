/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import java.util.Collection;
import java.util.Date;

import com.exedio.cope.Item;
import com.exedio.cope.Transaction;

final class Commit
{
	final String modifiedItems;
	final long id;
	final String name;
	private final long startDate;
	final long elapsed;
	
	Commit(final Collection<Item> modifiedItems, final Transaction transaction)
	{
		final long now = System.currentTimeMillis();
		
		final StringBuilder bf = new StringBuilder();
		boolean first = true;
		for(final Item item : modifiedItems)
		{
			if(first)
				first = false;
			else
				bf.append(',').append(' ');
			
			bf.append(item.getCopeID());
		}
		this.modifiedItems = bf.toString();
		this.id = transaction.getID();
		this.name = transaction.getName();
		this.startDate = transaction.getStartDate().getTime();
		this.elapsed = now - startDate;
	}
	
	Date getStartDate()
	{
		return new Date(startDate);
	}
}

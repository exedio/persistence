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

package com.exedio.cope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

final class TracedItems
{
	static final Logger logger = Logger.getLogger(TracedItems.class.getName());

	private volatile boolean virgin = true;
	private final ArrayList<Item> list = new ArrayList<Item>();

	List<Item> get()
	{
		if(virgin)
			return Collections.<Item>emptyList();

		synchronized(list)
		{
			// copy to avoid ConcurrentModificationExceptions
			return Collections.unmodifiableList(
					Arrays.asList(list.toArray(new Item[list.size()])));
		}
	}

	void add(final Item item)
	{
		if(item==null)
			throw new NullPointerException("item");

		synchronized(list)
		{
			virgin = false;
			list.add(item);
		}
	}

	boolean remove(final Item item)
	{
		if(item==null)
			throw new NullPointerException("item");
		if(virgin)
			return false;

		synchronized(list)
		{
			return list.remove(item);
		}
	}

	void clear()
	{
		if(virgin)
			return;

		synchronized(list)
		{
			list.clear();
		}

		virgin = true;
	}
}

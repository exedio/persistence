/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.Item;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.NotNullViolationException;
import com.exedio.cope.ObjectAttribute;
import com.exedio.cope.ReadOnlyViolationException;
import com.exedio.cope.TypeComponent;
import com.exedio.cope.UniqueViolationException;

public final class Vector extends TypeComponent
{
	final ObjectAttribute[] sources;

	public Vector(final ObjectAttribute[] sources)
	{
		this.sources = sources;
	}
	
	public final List getSources()
	{
		return Collections.unmodifiableList(Arrays.asList(sources));
	}
	
	public List get(final Item item)
	{
		final ArrayList result = new ArrayList(sources.length);

		for(int i = 0; i<sources.length; i++)
		{
			final Object value = item.get(sources[i]);
			if(value!=null)
				result.add(value);
		}
		return result;
	}
	
	public void set(final Item item, final Collection values)
		throws
			UniqueViolationException,
			NotNullViolationException,
			LengthViolationException,
			ReadOnlyViolationException,
			ClassCastException
	{
		int i = 0;

		for(Iterator it = values.iterator(); it.hasNext(); i++)
			item.setAttribute(sources[i], it.next());

		for(; i<sources.length; i++)
			item.setAttribute(sources[i], null);
	}
	
}

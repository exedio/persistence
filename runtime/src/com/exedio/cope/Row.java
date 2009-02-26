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

import java.util.HashMap;

/**
 * This is essentially a typed map from {@link Column}s to {@link Object}s.
 * @author Ralf Wiebicke
 */
final class Row
{
	
	// TODO: use arrays for String/int/double instead of the HashMap
	private final HashMap<Column, Object> impl;
	
	Row()
	{
		impl = new HashMap<Column, Object>();
	}
	
	Row(final Row row)
	{
		impl = new HashMap<Column, Object>(row.impl);
	}
	
	Object get(final Column column)
	{
		return impl.get(column);
	}
	
	void put(final Column column, final Object value)
	{
		if(value!=null)
			impl.put(column, value);
		else
			impl.remove(column);
	}
	
	@Override
	public String toString()
	{
		return impl.toString();
	}
	
}

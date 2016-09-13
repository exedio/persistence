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

package com.exedio.cope;

import java.util.LinkedHashMap;

public final class FieldValues
{
	private final LinkedHashMap<Field<?>, Object> sources;
	private final Item item;

	FieldValues(
			final LinkedHashMap<Field<?>, Object> sources,
			final Item item)
	{
		this.sources = sources;
		this.item = item;
	}

	public <E> E get(final Field<E> field)
	{
		if(sources.containsKey(field))
			return getX(field);

		if(item!=null)
			return field.get(item);

		return null;
	}

	@SuppressWarnings("unchecked")
	private <E> E getX(final Field<E> field)
	{
		return (E)sources.get(field);
	}

	public Item getItem()
	{
		return item;
	}
}

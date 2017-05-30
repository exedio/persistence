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

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import javax.annotation.Nonnull;

public final class FieldValues
{
	private final LinkedHashMap<Field<?>, Object> sources;
	private final Item backingItem;
	private final Type<?> backingType;

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	FieldValues(
			final LinkedHashMap<Field<?>, Object> sources,
			final Item backingItem,
			final Type<?> backingType)
	{
		this.sources = sources;
		this.backingItem = backingItem;
		this.backingType = backingType;
	}

	public <E> E get(@Nonnull final Field<E> field)
	{
		requireNonNull(field, "field");
		backingType.assertBelongs(field);

		if(sources.containsKey(field))
			return getX(field);

		if(backingItem!=null)
			return field.get(backingItem);

		return null;
	}

	@SuppressWarnings("unchecked")
	private <E> E getX(final Field<E> field)
	{
		return (E)sources.get(field);
	}

	public Item getBackingItem()
	{
		return backingItem;
	}

	@Override
	public String toString()
	{
		return
				backingItem!=null
				? sources.toString() + '(' + backingItem + ')'
				: sources.toString();
	}
}

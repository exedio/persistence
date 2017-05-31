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

import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public final class FieldValues
{
	private final LinkedHashMap<Field<?>, Object> sources = new LinkedHashMap<>();
	private final Set<Map.Entry<Field<?>, Object>> sourcesEntrySet = unmodifiableSet(sources.entrySet());
	private final Item backingItem;
	private final Type<?> backingType;

	FieldValues(final Type<?> backingType, final SetValue<?>[] sources)
	{
		this.backingItem = null;
		this.backingType = backingType;
		put(sources);
	}

	FieldValues(final Item backingItem, final SetValue<?>[] sources)
	{
		this.backingItem = backingItem;
		this.backingType = backingItem.type;
		put(sources);
	}

	private void put(final SetValue<?>[] sources)
	{
		for(final SetValue<?> source : sources)
		{
			if(source.settable instanceof Field<?>)
			{
				put(source);
			}
			else
			{
				for(final SetValue<?> part : execute(source))
					put(part);
			}
		}
	}

	private void put(final SetValue<?> setValue)
	{
		put((Field<?>)setValue.settable, setValue.value);
	}

	private <X> SetValue<?>[] execute(final SetValue<X> sv)
	{
		return sv.settable.execute(sv.value, backingItem);
	}


	boolean containsKey(@Nonnull final Field<?> field) // TODO rename to isModified
	{
		requireNonNull(field, "field");
		backingType.assertBelongs(field);

		return sources.containsKey(field);
	}

	Set<Map.Entry<Field<?>, Object>> entrySet() // TODO rename to getModified
	{
		return sourcesEntrySet;
	}

	void put(final Field<?> field, final Object value) // TODO rename to setModified
	{
		backingType.assertBelongs(field);
		if(sources.putIfAbsent(field, value)!=null)
			throw new IllegalArgumentException("SetValues contain duplicate settable " + field);
	}

	HashMap<BlobColumn, byte[]> toBlobs()
	{
		final HashMap<BlobColumn, byte[]> result = new HashMap<>();

		for(final Map.Entry<Field<?>, Object> e : sources.entrySet())
		{
			final Field<?> field = e.getKey();
			if(!(field instanceof DataField))
				continue;

			final DataField.Value value = (DataField.Value)e.getValue();
			final DataField df = (DataField)field;
			result.put((BlobColumn)df.getColumn(), value!=null ? value.asArray(df, backingItem) : null);
		}
		return result;
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

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

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public final class FieldValues
{
	private final LinkedHashMap<Field<?>, Object> dirt = new LinkedHashMap<>();
	private final Set<Map.Entry<Field<?>, Object>> dirtEntrySet = unmodifiableSet(dirt.entrySet());
	private final Item backingItem;
	final Type<?> backingType;

	FieldValues(final Type<?> backingType, final SetValue<?>[] initialDirt)
	{
		this.backingItem = null;
		this.backingType = backingType;
		setDirty(initialDirt);
	}

	FieldValues(final Item backingItem, final SetValue<?>[] initialDirt)
	{
		this.backingItem = backingItem;
		this.backingType = backingItem.type;
		setDirty(initialDirt);
	}

	FieldValues(final Item backingItem)
	{
		this.backingItem = backingItem;
		this.backingType = backingItem.type;
	}

	FieldValues(final Map<FunctionField<?>, Object> initialDirt)
	{
		dirt.putAll(initialDirt);
		this.backingItem = null;
		this.backingType = null;
	}

	private void setDirty(final SetValue<?>[] initialDirt)
	{
		for(final SetValue<?> dirt : initialDirt)
		{
			if(dirt.settable instanceof Field<?>)
			{
				setDirty(dirt);
			}
			else
			{
				for(final SetValue<?> part : execute(dirt))
					setDirty(part);
			}
		}
	}

	private void setDirty(final SetValue<?> setValue)
	{
		setDirty((Field<?>)setValue.settable, setValue.value);
	}

	private <X> SetValue<?>[] execute(final SetValue<X> sv)
	{
		return sv.settable.execute(sv.value, backingItem);
	}


	boolean isDirty(@Nonnull final Field<?> field)
	{
		requireNonNull(field, "field");
		backingType.assertBelongs(field);

		return dirt.containsKey(field);
	}

	Set<Map.Entry<Field<?>, Object>> dirtySet()
	{
		return dirtEntrySet;
	}

	void setDirty(final Field<?> field, final Object value)
	{
		backingType.assertBelongs(field);

		if(backingItem!=null)
			FinalViolationException.check(field, backingItem);

		field.check(value, backingItem);

		if(dirt.putIfAbsent(field, value)!=null)
			throw new IllegalArgumentException("SetValues contain duplicate settable " + field);
	}

	void checkNonDirtyMandatoryOnCreate()
	{
		assert backingItem==null;

		for(final Field<?> field : backingType.getFields())
			if(field.isMandatory() && !dirt.containsKey(field))
				throw MandatoryViolationException.create(field, null);
	}

	// IdentityHashMap is more efficient than HashMap because it uses linear-probe instead of chaining
	IdentityHashMap<BlobColumn, byte[]> toBlobs()
	{
		final IdentityHashMap<BlobColumn, byte[]> result = new IdentityHashMap<>(); // TODO set expectedMaxSize

		for(final Map.Entry<Field<?>, Object> e : dirt.entrySet())
		{
			final Field<?> field = e.getKey();
			if(!(field instanceof DataField))
				continue;
			final Column column = field.getColumn();
			if(!(column instanceof BlobColumn)) // just for DataVault
				continue;

			final DataField.Value value = (DataField.Value)e.getValue();
			final DataField df = (DataField)field;
			result.put(df.getBlobColumnIfSupported("toBlobs"), value!=null ? value.asArray(df, backingItem) : null);
		}
		return result;
	}


	public <E> E get(@Nonnull final Field<E> field)
	{
		requireNonNull(field, "field");
		if(backingType!=null) // is null for CheckConstraint in Composite
			backingType.assertBelongs(field);

		if(dirt.containsKey(field))
			return getX(field);

		if(backingItem!=null)
			return field.get(backingItem);

		return null;
	}

	@SuppressWarnings("unchecked")
	private <E> E getX(final Field<E> field)
	{
		return (E)dirt.get(field);
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
				? dirt.toString() + '(' + backingItem + ')'
				: dirt.toString();
	}
}

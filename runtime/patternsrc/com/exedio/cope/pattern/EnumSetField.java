/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.instrument.Wrapper;

public final class EnumSetField<E extends Enum<E>> extends Pattern
{
	private final Class<E> elementClass;
	private final EnumMap<E, BooleanField> fields;

	private EnumSetField(final Class<E> keyClass)
	{
		this.elementClass = keyClass;
		this.fields = new EnumMap<E, BooleanField>(keyClass);

		for(E key : keyClass.getEnumConstants())
		{
			final BooleanField value = new BooleanField().defaultTo(false);
			registerSource(value, key.name());
			fields.put(key, value);
		}
	}
	
	public static final <K extends Enum<K>> EnumSetField<K> newSet(final Class<K> keyClass)
	{
		return new EnumSetField<K>(keyClass);
	}
	
	/**
	 * @deprecated Use {@link #getElementClass()} instead
	 */
	@Deprecated
	public Class<E> getKeyClass()
	{
		return getElementClass();
	}

	public Class<E> getElementClass()
	{
		return elementClass;
	}

	public BooleanField getField(final E element)
	{
		return fields.get(element);
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		final char KEY = 'k';
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(
			new Wrapper("contains").
			setReturn(boolean.class).
			addParameter(Wrapper.TypeVariable0.class, String.valueOf(KEY)));
		
		result.add(
			new Wrapper("add").
			addParameter(Wrapper.TypeVariable0.class));
		
		result.add(
			new Wrapper("remove").
			addParameter(Wrapper.TypeVariable0.class));

		final java.lang.reflect.Type valueType =
			Wrapper.generic(EnumSet.class, Wrapper.TypeVariable0.class);
		result.add(
			new Wrapper("get").
			setReturn(valueType));
		result.add(
			new Wrapper("set").
			addParameter(valueType));
			
		return Collections.unmodifiableList(result);
	}
	
	private void assertElement(final E element)
	{
		if(element==null)
			throw new NullPointerException("element must not be null");
		if(elementClass!=element.getClass())
			throw new ClassCastException("expected a " + elementClass.getName() + ", but was a " + element.getClass().getName());
	}

	public boolean contains(final Item item, final E element)
	{
		assertElement(element);
		return fields.get(element).get(item);
	}
	
	public void add(final Item item, final E element)
	{
		assertElement(element);
		fields.get(element).set(item, true);
	}
	
	public void remove(final Item item, final E element)
	{
		assertElement(element);
		fields.get(element).set(item, false);
	}
	
	public EnumSet<E> get(final Item item)
	{
		final EnumSet<E> result = EnumSet.<E>noneOf(elementClass);
		for(final E key : fields.keySet())
		{
			if(fields.get(key).getMandatory(item))
				result.add(key);
		}
		return result;
	}
	
	public void set(final Item item, final EnumSet<E> value)
	{
		final SetValue[] setValues = new SetValue[fields.size()];
		int i = 0;
		for(final E key : fields.keySet())
			setValues[i++] = fields.get(key).map(value.contains(key));
		item.set(setValues);
	}
}

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

package com.exedio.cope.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.Wrapper;

public final class EnumMapField<K extends Enum<K>,V> extends Pattern
{
	private final Class<K> keyClass;
	private final EnumMap<K, FunctionField<V>> fields;

	private EnumMapField(final Class<K> keyClass, final FunctionField<V> valueTemplate)
	{
		this.keyClass = keyClass;
		this.fields = new EnumMap<K, FunctionField<V>>(keyClass);

		for(K key : keyClass.getEnumConstants())
		{
			final FunctionField<V> value = valueTemplate.copy();
			registerSource(value);
			fields.put(key, value);
		}
	}
	
	public static final <K extends Enum<K>,V> EnumMapField<K,V> newMap(final Class<K> keyClass, final FunctionField<V> value)
	{
		return new EnumMapField<K,V>(keyClass, value);
	}
	
	@Override
	public void initialize()
	{
		final String name = getName();
		
		for(K key : keyClass.getEnumConstants())
		{
			final FunctionField<V> value = fields.get(key);
			initialize(value, name+key.name());
		}
	}
	
	public Class<K> getKeyClass()
	{
		return keyClass;
	}

	public FunctionField<V> getField(final K key)
	{
		return fields.get(key);
	}

	/**
	 * @deprecated renamed to {@link #getField(Enum)}.
	 */
	@Deprecated
	public FunctionField<V> getAttribute(final K key)
	{
		return getField(key);
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final char KEY = 'k';
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		result.add(new Wrapper(
			Wrapper.TypeVariable1.class, "get",
			"Returns the value mapped to <tt>" + KEY + "</tt> by the field map {0}.",
			"getter").
			addParameter(Wrapper.TypeVariable0.class, String.valueOf(KEY)));
		
		result.add(new Wrapper(
			void.class, "set",
			"Associates <tt>" + KEY + "</tt> to a new value in the field map {0}.",
			"setter").
			addParameter(Wrapper.TypeVariable0.class, String.valueOf(KEY)).
			addParameter(Wrapper.TypeVariable1.class));
		
		return Collections.unmodifiableList(result);
	}

	public V get(final Item item, final K key)
	{
		return fields.get(key).get(item);
	}
	
	public void set(final Item item, final K key, final V value)
	{
		fields.get(key).set(item, value);
	}
}

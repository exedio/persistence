/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.EnumMap;

import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;

public final class FieldMapLimited<K extends Enum<K>,V> extends Pattern
{
	private final Class<K> keyClass;
	private final EnumMap<K, FunctionField<V>> fields;

	private FieldMapLimited(final Class<K> keyClass, final FunctionField<V> valueTemplate)
	{
		this.keyClass = keyClass;
		this.fields = new EnumMap<K, FunctionField<V>>(keyClass);

		for(K key : keyClass.getEnumConstants())
		{
			final FunctionField<V> value = valueTemplate.copyFunctionField();
			registerSource(value);
			fields.put(key, value);
		}
	}
	
	public static final <K extends Enum<K>,V> FieldMapLimited<K,V> newMap(final Class<K> keyClass, final FunctionField<V> value)
	{
		return new FieldMapLimited<K,V>(keyClass, value);
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

	public V get(final Item item, final K key)
	{
		return fields.get(key).get(item);
	}
	
	public void set(final Item item, final K key, final V value)
	{
		fields.get(key).set(item, value);
	}
}

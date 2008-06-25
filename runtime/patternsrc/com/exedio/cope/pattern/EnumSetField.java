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
import java.util.List;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.instrument.Wrapper;

public final class EnumSetField<K extends Enum<K>> extends Pattern
{
	private final Class<K> keyClass;
	private final EnumMap<K, BooleanField> fields;

	private EnumSetField(final Class<K> keyClass)
	{
		this.keyClass = keyClass;
		this.fields = new EnumMap<K, BooleanField>(keyClass);

		for(K key : keyClass.getEnumConstants())
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
	
	public Class<K> getKeyClass()
	{
		return keyClass;
	}

	public BooleanField getField(final K key)
	{
		return fields.get(key);
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
			
		return Collections.unmodifiableList(result);
	}
	
	private void assertKey(final K key)
	{
		if(keyClass!=key.getClass())
			throw new ClassCastException("expected a " + keyClass.getName() + ", but was a " + key.getClass().getName());
	}

	public boolean contains(final Item item, final K key)
	{
		assertKey(key);
		return fields.get(key).get(item);
	}
	
	public void add(final Item item, final K key)
	{
		assertKey(key);
		fields.get(key).set(item, true);
	}
	
	public void remove(final Item item, final K key)
	{
		assertKey(key);
		fields.get(key).set(item, false);
	}
}

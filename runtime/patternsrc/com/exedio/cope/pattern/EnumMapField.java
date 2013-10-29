/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.CopyMapper;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.EnumAnnotatedElement;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

public final class EnumMapField<K extends Enum<K>,V> extends Pattern implements Settable<EnumMap<K,V>>
{
	private static final long serialVersionUID = 1l;

	private final Class<K> keyClass;
	private final FunctionField<V> valueTemplate;
	private final EnumMap<K, FunctionField<V>> fields;
	private final EnumMap<K, V> defaultConstant;

	private EnumMapField(
			final Class<K> keyClass,
			final FunctionField<V> valueTemplate,
			final EnumMap<K, V> defaultConstant)
	{
		this.keyClass = keyClass;
		this.valueTemplate = valueTemplate;
		this.fields = new EnumMap<K, FunctionField<V>>(keyClass);
		this.defaultConstant = defaultConstant;

		for(final K key : keyClass.getEnumConstants())
		{
			final FunctionField<V> value = valueTemplate.defaultTo(defaultConstant.get(key));
			addSource(value, key.name(), EnumAnnotatedElement.get(key));
			fields.put(key, value);
		}
	}

	public static final <K extends Enum<K>,V> EnumMapField<K,V> create(
			final Class<K> keyClass,
			final FunctionField<V> value)
	{
		return new EnumMapField<K,V>(keyClass, value, new EnumMap<K, V>(keyClass));
	}

	@Override
	public EnumMapField<K,V> copy(final CopyMapper mapper)
	{
		return new EnumMapField<K,V>(keyClass, mapper.copy(valueTemplate), defaultConstant);
	}

	public EnumMapField<K,V> defaultTo(final K key, final V value)
	{
		final EnumMap<K, V> defaultConstant = new EnumMap<K, V>(this.defaultConstant);
		defaultConstant.put(key, value);
		return new EnumMapField<K,V>(keyClass, valueTemplate, defaultConstant);
	}

	public Class<K> getKeyClass()
	{
		return keyClass;
	}

	public FunctionField<V> getField(final K key)
	{
		return fields.get(key);
	}

	private static final String KEY = "k";

	private void assertKey(final K key)
	{
		if(key==null)
			throw new NullPointerException("key");
		if(keyClass!=key.getClass())
			throw new ClassCastException("expected a " + keyClass.getName() + ", but was a " + key.getClass().getName());
	}

	@Wrap(order=10, doc="Returns the value mapped to <tt>" + KEY + "</tt> by the field map {0}.")
	public V get(
			final Item item,
			@Parameter(KEY) final K key)
	{
		assertKey(key);
		return fields.get(key).get(item);
	}

	@Wrap(order=20, doc="Associates <tt>" + KEY + "</tt> to a new value in the field map {0}.")
	public void set(
			final Item item,
			@Parameter(KEY) final K key,
			final V value)
	{
		assertKey(key);
		fields.get(key).set(item, value);
	}

	@Override
	public SetValue<EnumMap<K, V>> map(final EnumMap<K, V> value)
	{
		return SetValue.map(this, value);
	}

	@Override
	public SetValue<?>[] execute(final EnumMap<K, V> value, final Item exceptionItem)
	{
		if(value==null)
			throw MandatoryViolationException.create(this, exceptionItem);

		final K[] keys = keyClass.getEnumConstants();
		final SetValue<?>[] result = new SetValue<?>[keys.length];
		for(final K key : keys)
			result[key.ordinal()] = fields.get(key).map(value.get(key));
		return result;
	}

	@Override
	public boolean isFinal()
	{
		return valueTemplate.isFinal();
	}

	@Override
	public boolean isMandatory()
	{
		return true; // map is never null
	}

	@Override
	public boolean isInitial()
	{
		for(final FunctionField<V> field : fields.values())
			if(field.isInitial())
				return true;
		return false;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final HashSet<Class<? extends Throwable>> result = new HashSet<Class<? extends Throwable>>();
		for(final FunctionField<V> field : fields.values())
			result.addAll(field.getInitialExceptions());
		return result;
	}

	// ------------------- deprecated stuff -------------------

	@Override
	@Deprecated
	public Type getInitialType()
	{
		throw new RuntimeException("not implemented");
	}

	/**
	 * @deprecated Use {@link #create(Class,FunctionField)} instead
	 */
	@Deprecated
	public static final <K extends Enum<K>,V> EnumMapField<K,V> newMap(
			final Class<K> keyClass,
			final FunctionField<V> value)
	{
		return create(keyClass, value);
	}

	/**
	 * @deprecated renamed to {@link #getField(Enum)}.
	 */
	@Deprecated
	public FunctionField<V> getAttribute(final K key)
	{
		return getField(key);
	}
}

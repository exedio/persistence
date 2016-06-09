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

package com.exedio.cope.pattern;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Nullability;
import com.exedio.cope.instrument.NullabilityGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.EnumAnnotatedElement;
import com.exedio.cope.misc.ReflectionTypes;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public final class EnumMapField<K extends Enum<K>,V> extends Pattern implements Settable<EnumMap<K,V>>, MapFieldInterface<K,V>
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
		this.fields = new EnumMap<>(keyClass);
		this.defaultConstant = defaultConstant;

		for(final K key : keyClass.getEnumConstants())
		{
			final FunctionField<V> value = valueTemplate.defaultTo(defaultConstant.get(key));
			addSource(value, stripUnderline(key.name()), EnumAnnotatedElement.get(key));
			fields.put(key, value);
		}
	}

	public static final <K extends Enum<K>,V> EnumMapField<K,V> create(
			final Class<K> keyClass,
			final FunctionField<V> value)
	{
		return new EnumMapField<>(keyClass, value, new EnumMap<K, V>(keyClass));
	}

	public EnumMapField<K,V> defaultTo(final K key, final V value)
	{
		final EnumMap<K, V> defaultConstant = new EnumMap<>(this.defaultConstant);
		defaultConstant.put(key, value);
		return new EnumMapField<>(keyClass, valueTemplate, defaultConstant);
	}

	@Override
	public Class<K> getKeyClass()
	{
		return keyClass;
	}

	public FunctionField<V> getField(final K key)
	{
		return fields.get(key);
	}

	@Override
	public Class<V> getValueClass()
	{
		return valueTemplate.getValueClass();
	}

	private static final String KEY = "k";

	private FunctionField<V> field(final K key)
	{
		assertEnum("key", keyClass, key);
		return fields.get(key);
	}

	@Override
	@Wrap(order=10, doc="Returns the value mapped to <tt>" + KEY + "</tt> by the field map {0}.", nullability=MapValueNullable.class)
	public V get(
			@Nonnull final Item item,
			@Nonnull @Parameter(KEY) final K key)
	{
		return field(key).get(item);
	}

	@Override
	@Wrap(order=20, doc="Associates <tt>" + KEY + "</tt> to a new value in the field map {0}.")
	public void set(
			@Nonnull final Item item,
			@Nonnull @Parameter(KEY) final K key,
			@Parameter(nullability=MapValueNullable.class) final V value)
	{
		field(key).set(item, value);
	}

	@Override
	@Wrap(order=110)
	@Nonnull
	public Map<K,V> getMap(@Nonnull final Item item)
	{
		final EnumMap<K,V> result = new EnumMap<>(keyClass);
		for(final K key : keyClass.getEnumConstants())
		{
			final V value = fields.get(key).get(item);
			if(value!=null) // null value not allowed
				result.put(key, value);
		}
		return Collections.unmodifiableMap(result);
	}

	@Override
	@Wrap(order=120)
	public void setMap(@Nonnull final Item item, @Nonnull final Map<? extends K,? extends V> map)
	{
		if( map==null || map.containsKey(null) )
			throw MandatoryViolationException.create(this, item);

		final K[] enums = keyClass.getEnumConstants();
		final SetValue<?>[] sv = new SetValue<?>[enums.length];

		int i = 0;
		for(final K key : enums)
		{
			final V value;
			if(map.containsKey(key))
			{
				value = map.get(key);
				if(value==null)
					throw MandatoryViolationException.create(this, item);
			}
			else
			{
				value = null;
			}
			sv[i++] = fields.get(key).map(value);
		}

		item.set(sv);
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
		return true; // can be empty but is never null
	}

	@Override
	public java.lang.reflect.Type getInitialType()
	{
		return ReflectionTypes.parameterized(EnumMap.class, keyClass, valueTemplate.getValueClass());
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
		final HashSet<Class<? extends Throwable>> result = new HashSet<>();
		for(final FunctionField<V> field : fields.values())
			result.addAll(field.getInitialExceptions());
		return result;
	}


	// helper methods for EnumMapField and EnumSetField

	static String stripUnderline(final String s)
	{
		if(s.indexOf('_')<0)
			return s;

		return s.replace("_", "");
	}

	static <E extends Enum<E>> void assertEnum(
			final String name,
			final Class<E> enumClass,
			final E enumValue)
	{
		requireNonNull(enumValue, name);
		final Class<E> valueClass = enumValue.getDeclaringClass();
		if(enumClass!=valueClass)
			throw new ClassCastException(
					"expected a " + enumClass.getName() +
					", but was a " + enumValue.name() +
					" of " + valueClass.getName());
	}

	private static final class MapValueNullable implements NullabilityGetter<EnumMapField<?,?>>
	{
		@Override
		@SuppressWarnings("synthetic-access")
		public Nullability getNullability(final EnumMapField<?,?> feature)
		{
			return Nullability.forMandatory(feature.valueTemplate.isMandatory());
		}
	}

	// ------------------- deprecated stuff -------------------

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

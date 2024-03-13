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

import static com.exedio.cope.CoalesceView.coalesce;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.CopyMapper;
import com.exedio.cope.Copyable;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Function;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.CopeNameUtil;
import com.exedio.cope.misc.EnumAnnotatedElement;
import com.exedio.cope.misc.ReflectionTypes;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

@WrapFeature
public final class EnumMapField<K extends Enum<K>,V> extends Pattern implements Settable<Map<K,V>>, MapFieldInterface<K,V>, Copyable
{
	private static final long serialVersionUID = 1l;

	private final boolean isfinal;
	private final Class<K> keyClass;
	private final K fallback;
	private final FunctionField<V> valueTemplate;
	private final EnumMap<K, FunctionField<V>> fields;
	private final EnumMap<K, V> defaultConstant;

	private EnumMapField(
			final Class<K> keyClass,
			final K fallback,
			final FunctionField<V> valueTemplate,
			final EnumMap<K, V> defaultConstant)
	{
		this.isfinal = valueTemplate.isFinal();
		this.keyClass = keyClass;
		this.fallback = fallback;
		this.valueTemplate = valueTemplate;
		this.fields = new EnumMap<>(keyClass);
		this.defaultConstant = defaultConstant;

		for(final K key : keyClass.getEnumConstants())
		{
			FunctionField<V> value = valueTemplate.copy();
			if(defaultConstant.containsKey(key))
				value = value.defaultTo(defaultConstant.get(key));
			if(fallback!=null && key!=fallback && value.isMandatory())
				value = value.optional();
			addSourceFeature(value, name(key), EnumAnnotatedElement.get(key));
			fields.put(key, value);
		}
	}

	public static <K extends Enum<K>,V> EnumMapField<K,V> create(
			final Class<K> keyClass,
			final FunctionField<V> value)
	{
		return new EnumMapField<>(keyClass, null, value, new EnumMap<>(keyClass));
	}

	@Override
	public EnumMapField<K,V> copy(final CopyMapper mapper)
	{
		return new EnumMapField<>(keyClass, fallback, mapper.copy(valueTemplate), defaultConstant);
	}

	public EnumMapField<K,V> fallbackTo(final K key)
	{
		return new EnumMapField<>(keyClass, requireNonNull(key, "key"), valueTemplate, defaultConstant);
	}

	/**
	 * @see CopeEnumFallback
	 * @see #fallbackTo(Enum)
	 */
	public EnumMapField<K,V> fallback()
	{
		return fallbackTo(getFallbackByAnnotation());
	}

	public EnumMapField<K,V> defaultTo(final K key, final V value)
	{
		final EnumMap<K, V> defaultConstant = new EnumMap<>(this.defaultConstant);
		defaultConstant.put(key, value);
		return new EnumMapField<>(keyClass, fallback, valueTemplate, defaultConstant);
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

	public FunctionField<V> getValueTemplate()
	{
		return valueTemplate;
	}

	@Override
	public Class<V> getValueClass()
	{
		return valueTemplate.getValueClass();
	}

	private FunctionField<V> field(final K key)
	{
		assertEnum("key", keyClass, key);
		return fields.get(key);
	}

	@Override
	@Wrap(order=10, doc=Wrap.MAP_GET_DOC, nullability=MapValueNullableIgnoringFallbacks.class)
	public V get(
			@Nonnull final Item item,
			@Nonnull @Parameter(Wrap.MAP_KEY) final K key)
	{
		return field(key).get(item);
	}

	@Override
	@Wrap(order=20,
			doc=Wrap.MAP_SET_DOC,
			hide=FinalSettableGetter.class)
	public void set(
			@Nonnull final Item item,
			@Nonnull @Parameter(Wrap.MAP_KEY) final K key,
			@Parameter(nullability=MapValueNullableIgnoringFallbacks.class) final V value)
	{
		FinalViolationException.check(this, item);

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

	static boolean containsKeyNull(@Nonnull final Map<?,?> map)
	{
		try
		{
			return map.containsKey(null);
		}
		catch(final NullPointerException e)
		{
			return false; // because map does not support null
		}
	}

	@Override
	@Wrap(order=120, hide=FinalSettableGetter.class)
	public void setMap(@Nonnull final Item item, @Nonnull final Map<? extends K,? extends V> map)
	{
		FinalViolationException.check(this, item);
		MandatoryViolationException.requireNonNull(map, this, item);
		if(containsKeyNull(map))
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
			sv[i++] = SetValue.map(fields.get(key), value);
		}

		item.set(sv);
	}

	@Override
	public SetValue<?>[] execute(final Map<K, V> value, final Item exceptionItem)
	{
		if(value==null)
			throw MandatoryViolationException.create(this, exceptionItem);

		final K[] keys = keyClass.getEnumConstants();
		final SetValue<?>[] result = new SetValue<?>[keys.length];
		for(final K key : keys)
			result[key.ordinal()] = SetValue.map(fields.get(key), value.get(key));
		return result;
	}

	@Override
	public boolean isFinal()
	{
		return isfinal;
	}

	/**
	 * Does always return {@code true}.
	 * An {@code EnumMapField} may store an empty map,
	 * but never null.
	 */
	@Override
	public boolean isMandatory()
	{
		return true;
	}

	@Override
	public java.lang.reflect.Type getInitialType()
	{
		return ReflectionTypes.parameterized(Map.class, keyClass, valueTemplate.getValueClass());
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

	public Condition isEmpty()
	{
		final Condition[] c = new Condition[fields.size()];
		int i = 0;
		for(final Enum<K> e : keyClass.getEnumConstants())
			//noinspection SuspiciousMethodCalls OK: bug in idea
			c[i++] = fields.get(e).isNull();
		return Cope.and(c);
	}

	public Condition isNotEmpty()
	{
		final Condition[] c = new Condition[fields.size()];
		int i = 0;
		for(final Enum<K> e : keyClass.getEnumConstants())
			//noinspection SuspiciousMethodCalls OK: bug in idea
			c[i++] = fields.get(e).isNotNull();
		return Cope.or(c);
	}


	// helper methods for EnumMapField and EnumSetField

	static String name(final Enum<?> value)
	{
		final String s = CopeNameUtil.getAndFallbackToName(value);
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


	// fallbacks

	public boolean hasFallbacks()
	{
		return fallback!=null;
	}

	public K getFallback()
	{
		return fallback;
	}

	@Wrap(order=11,
			doc=Wrap.MAP_GET_DOC,
			hide=NoFallbacksGetter.class,
			nullability=MapValueNullable.class)
	public V getWithFallback(
			@Nonnull final Item item,
			@Nonnull @Parameter(Wrap.MAP_KEY) final K key)
	{
		assertFallbacks();

		final V withoutFallback = field(key).get(item);
		if(withoutFallback!=null)
			return withoutFallback;

		return field(fallback).get(item);
	}

	@Wrap(order=111, hide=NoFallbacksGetter.class)
	@Nonnull
	public Map<K,V> getMapWithFallback(@Nonnull final Item item)
	{
		assertFallbacks();

		final EnumMap<K,V> result = new EnumMap<>(keyClass);
		V fallbackValue = null;
		for(final K key : keyClass.getEnumConstants())
		{
			final V value = fields.get(key).get(item);
			if(value!=null) // null value not allowed
			{
				result.put(key, value);

				if(key==fallback)
					fallbackValue = value;
			}
		}

		if(fallbackValue!=null)
		{
			for(final K key : keyClass.getEnumConstants())
				if(!result.containsKey(key))
					result.put(key, fallbackValue);
		}

		return Collections.unmodifiableMap(result);
	}

	public Function<V> getFunctionWithFallback(final K key)
	{
		assertFallbacks();

		final Function<V> forKey = fields.get(requireNonNull(key, "key"));

		if(fallback==key)
			return forKey;
		else
			return coalesce(forKey, fields.get(fallback));
	}

	private void assertFallbacks()
	{
		if(fallback==null)
			throw new IllegalArgumentException("field " + this + " has no fallbacks");
	}

	private K getFallbackByAnnotation()
	{
		K result = null;
		for(final K key : keyClass.getEnumConstants())
		{
			if(EnumAnnotatedElement.get(key).isAnnotationPresent(CopeEnumFallback.class))
			{
				if(result!=null)
					throw new IllegalArgumentException(
							"duplicate @" + CopeEnumFallback.class.getSimpleName() + " in " + keyClass.getName() +
							" at " + result.name() + " and " + key.name());

				result = key;
			}
		}

		if(result==null)
			throw new IllegalArgumentException(
					"missing @" + CopeEnumFallback.class.getSimpleName() + " in " + keyClass.getName());

		return result;
	}
}

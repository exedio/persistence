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

package com.exedio.cope.tojunit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.opentest4j.AssertionFailedError;

/**
 * An implementation of {@link Map} that is as sensitive as possible while still
 * fulfilling the contract.
 * <ul>
 * <li>It does not allow null, similar to Map.of available in JDK 11.
 * <li>It does not allow classes other that allowed yb type variables.
 * </ul>
 */
public class SensitiveMap<K, V> implements Map<K, V>
{
	protected final Map<K, V> backing;
	private final Class<K> keyClass;
	private final Class<V> valueClass;

	public SensitiveMap(
			final Map<K, V> backing,
			final Class<K> keyClass,
			final Class<V> valueClass)
	{
		assertNotNull(backing, "backing");
		assertNotNull(keyClass, "keyClass");
		assertNotNull(valueClass, "valueClass");
		//noinspection AssignmentOrReturnOfFieldWithMutableType
		this.backing = backing;
		this.keyClass = keyClass;
		this.valueClass = valueClass;
	}

	private <X> X checkKey(final X o)
	{
		return check(o, keyClass, "key");
	}

	private <X> X checkValue(final X o)
	{
		return check(o, valueClass, "value");
	}

	private static <X> X check(final X o, final Class<?> clazz, final String subject)
	{
		if(o==null)
			throw new AssertionFailedError("null forbidden in " + subject);
		if(!clazz.isInstance(o))
			throw new AssertionFailedError("class forbidden in " + subject + ": " + o.getClass().getName());
		return o;
	}

	@Override
	public final int size()
	{
		return backing.size();
	}

	@Override
	public final boolean isEmpty()
	{
		return backing.isEmpty();
	}

	@Override
	public boolean containsKey(final Object key)
	{
		return backing.containsKey(checkKey(key));
	}

	@Override
	public final boolean containsValue(final Object val)
	{
		return backing.containsValue(checkValue(val));
	}

	@Override
	public final V get(final Object key)
	{
		return backing.get(checkKey(key));
	}

	@Override
	public final V put(final K key, final V value)
	{
		return backing.put(checkKey(key), checkValue(value));
	}

	@Override
	public final V remove(final Object key)
	{
		return backing.remove(checkKey(key));
	}

	@Override
	public final void putAll(final Map<? extends K, ? extends V> m)
	{
		throw new AssertionFailedError(); // TODO
	}

	@Override
	public final void clear()
	{
		backing.clear();
	}

	@Override
	public final Set<K> keySet()
	{
		return backing.keySet();
	}

	@Override
	public final Set<Entry<K, V>> entrySet()
	{
		return backing.entrySet();
	}

	@Override
	public final Collection<V> values()
	{
		return backing.values();
	}

	@Override
	public final boolean equals(final Object o)
	{
		throw new AssertionFailedError();
	}

	@Override
	public final int hashCode()
	{
		return backing.hashCode();
	}

	@Override
	public final String toString()
	{
		return backing.toString();
	}
}

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
import java.util.Iterator;
import org.opentest4j.AssertionFailedError;

class SensitiveCollection<E> implements Collection<E>
{
	private final Collection<E> backing;
	private final Class<E> elementClass;

	protected SensitiveCollection(
			final Collection<E> backing,
			final Class<E> elementClass)
	{
		assertNotNull(backing, "backing");
		assertNotNull(elementClass, "elementClass");
		//noinspection AssignmentOrReturnOfFieldWithMutableType
		this.backing = backing;
		this.elementClass = elementClass;
	}

	protected <X> X checkElement(final X o)
	{
		if(o==null)
			throw new AssertionFailedError("null forbidden in element");
		if(!elementClass.isInstance(o))
			throw new AssertionFailedError("class forbidden in element: " + o.getClass().getName());
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
	public final boolean contains(final Object o)
	{
		return backing.contains(checkElement(o));
	}

	@Override
	public final Object[] toArray()
	{
		return backing.toArray();
	}

	@Override
	public final <T> T[] toArray(final T[] a)
	{
		throw new AssertionFailedError(); // TODO
	}

	@Override
	public final Iterator<E> iterator()
	{
		return backing.iterator();
	}

	@Override
	public final boolean add(final E e)
	{
		return backing.add(checkElement(e));
	}

	@Override
	public final boolean remove(final Object o)
	{
		return backing.remove(checkElement(o));
	}

	@Override
	public final boolean containsAll(final Collection<?> coll)
	{
		throw new AssertionFailedError(); // TODO
	}

	@Override
	public final boolean addAll(final Collection<? extends E> coll)
	{
		throw new AssertionFailedError(); // TODO
	}

	@Override
	public final boolean removeAll(final Collection<?> coll)
	{
		throw new AssertionFailedError(); // TODO
	}

	@Override
	public final boolean retainAll(final Collection<?> coll)
	{
		throw new AssertionFailedError(); // TODO
	}

	@Override
	public final void clear()
	{
		backing.clear();
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

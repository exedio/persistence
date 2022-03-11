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

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.opentest4j.AssertionFailedError;

final class SensitiveList<E> extends SensitiveCollection<E> implements List<E>
{
	private final List<E> backing;

	SensitiveList(
			final List<E> backing,
			final Class<E> elementClass)
	{
		super(backing, elementClass);
		//noinspection AssignmentOrReturnOfFieldWithMutableType
		this.backing = backing;
	}

	@Override
	public E get(final int index)
	{
		return backing.get(index);
	}

	@Override
	public E set(final int index, final E element)
	{
		return backing.set(index, checkElement(element));
	}

	@Override
	public void add(final int index, final E element)
	{
		backing.add(index, checkElement(element));
	}

	@Override
	public E remove(final int index)
	{
		return backing.remove(index);
	}

	@Override
	public int indexOf(final Object o)
	{
		return backing.indexOf(checkElement(o));
	}

	@Override
	public int lastIndexOf(final Object o)
	{
		return backing.lastIndexOf(checkElement(o));
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends E> c)
	{
		throw new AssertionFailedError(); // TODO
	}

	@Override
	public ListIterator<E> listIterator()
	{
		return backing.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(final int index)
	{
		return backing.listIterator(index);
	}

	@Override
	public List<E> subList(final int fromIndex, final int toIndex)
	{
		return backing.subList(fromIndex, toIndex);
	}
}

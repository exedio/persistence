/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.editor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

final class GetterSet<E> implements Set<E>, Serializable // for session persistence
{
	private static final long serialVersionUID = 1l;
	
	private final LinkedHashMap<E,E> map = new LinkedHashMap<E,E>();
	
	@SuppressWarnings("unchecked") <X extends E> X get(final X e) // OK map consistency is maintained by GetterSet
	{
		return (X)map.get(e);
	}

	public boolean add(final E e)
	{
		return map.put(e, e)==null;
	}

	@Deprecated
	public boolean addAll(Collection<? extends E> c)
	{
		throw new RuntimeException();
	}

	public void clear()
	{
		map.clear();
	}

	public boolean contains(Object o)
	{
		return map.containsKey(o);
	}

	@Deprecated
	public boolean containsAll(Collection<?> c)
	{
		throw new RuntimeException();
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Iterator<E> iterator()
	{
		return map.keySet().iterator();
	}

	public boolean remove(final Object o)
	{
		return map.remove(o)!=null;
	}

	@Deprecated
	public boolean removeAll(Collection<?> c)
	{
		throw new RuntimeException();
	}

	@Deprecated
	public boolean retainAll(Collection<?> c)
	{
		throw new RuntimeException();
	}

	public int size()
	{
		return map.size();
	}

	@Deprecated
	public Object[] toArray()
	{
		throw new RuntimeException();
	}

	@Deprecated
	public <T> T[] toArray(T[] a)
	{
		throw new RuntimeException();
	}
	
	@Override
	public int hashCode()
	{
		return map.hashCode();
	}
	
	@Override
	public boolean equals(final Object other)
	{
		return (other instanceof GetterSet) && map.equals(((GetterSet)other).map);
	}
	
	@Override
	public String toString()
	{
		return map.keySet().toString();
	}
}

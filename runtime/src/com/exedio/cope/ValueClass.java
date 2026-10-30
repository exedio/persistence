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

package com.exedio.cope;

import static java.util.Objects.requireNonNull;

import org.jspecify.annotations.NullMarked;

final class ValueClass<T>
{
	@NullMarked
	static <T> ValueClass<T> of(final Class<T> value)
	{
		return new ValueClass<>(requireNonNull(value, "valueClass"), true);
	}

	@NullMarked
	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: preliminaryItem is a hack
	static <T> ValueClass<T> preliminaryItem(final Class<?> superClass)
	{
		if(!Item.class.isAssignableFrom(superClass))
			throw new IllegalStateException("not an item " + superClass);

		return new ValueClass<T>((Class)superClass, false);
	}

	private Class<T> value;
	private boolean isFinal;

	@NullMarked
	private ValueClass(final Class<T> value, final boolean isFinal)
	{
		this.value = value;
		this.isFinal = isFinal;
	}

	@NullMarked
	boolean isFinal()
	{
		return isFinal;
	}

	@NullMarked
	void setFinally(final Class<T> value)
	{
		if(isFinal)
			throw new IllegalStateException("already final");
		if(!this.value.isAssignableFrom(value))
			throw new IllegalStateException("not assignable " + this.value + " from " + value);

		this.value = value;
		this.isFinal = true;
	}

	@NullMarked
	Class<T> get()
	{
		return value;
	}

	@NullMarked
	String getName()
	{
		return value.getName();
	}

	@NullMarked
	boolean isAssignableFrom(final Class<?> cls)
	{
		return value.isAssignableFrom(cls);
	}

	@NullMarked
	boolean isInstance(final Object obj)
	{
		return value.isInstance(obj);
	}

	@NullMarked
	T cast(final Object obj)
	{
		return value.cast(obj);
	}
}

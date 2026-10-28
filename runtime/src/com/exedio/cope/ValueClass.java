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

import java.io.Serial;
import org.jspecify.annotations.NullMarked;

final class ValueClass<T>
{
	@NullMarked
	static <T> ValueClass<T> of(final Class<T> value)
	{
		return new ValueClass<>(requireNonNull(value, "valueClass"));
	}

	@NullMarked
	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: PreliminaryItem is a hack
	static <T> ValueClass<T> preliminaryItem()
	{
		return new ValueClass<T>((Class)PreliminaryItem.class);
	}

	private static final class PreliminaryItem extends Item
	{
		@Serial private static final long serialVersionUID = 1l;
	}

	private Class<T> value;

	@NullMarked
	private ValueClass(final Class<T> value)
	{
		this.value = value;
	}

	@NullMarked
	boolean isFinal()
	{
		return value!=PreliminaryItem.class;
	}

	@NullMarked
	void setFinally(final Class<T> value)
	{
		if(this.value!=PreliminaryItem.class)
			throw new IllegalStateException("already final");

		this.value = value;
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

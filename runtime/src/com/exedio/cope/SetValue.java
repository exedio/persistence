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

import java.util.Objects;

public final class SetValue<E>
{
	public static final SetValue<?>[] EMPTY_ARRAY = new SetValue<?>[0];

	public final Settable<E> settable;
	public final E value;

	public static <E, F extends Feature & Settable<E>> SetValue<E> map(final F settable, final E value)
	{
		return new SetValue<>(settable, value);
	}

	/**
	 * @throws ClassCastException if {@code settable} is not an instance of class {@link Feature}.
	 */
	@SuppressWarnings("unchecked")
	public static <E, F extends Feature & Settable<E>> SetValue<E> mapAndCastToFeature(final Settable<E> settable, final E value)
	{
		return map((F)settable, value);
	}

	SetValue(final Settable<E> settable, final E value)
	{
		this.settable = requireNonNull(settable, "settable");
		this.value = value;
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof final SetValue<?> o))
			return false;

		return settable.equals(o.settable) && Objects.equals(value, o.value);
	}

	@Override
	public int hashCode()
	{
		return settable.hashCode() ^ Objects.hashCode(value);
	}

	@Override
	public String toString()
	{
		return settable.toString() + '=' + value;
	}
}

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

import java.io.Serializable;
import java.util.Objects;

/**
 * If the value for {@link #getFrom() from} is null this means, that the range contains all values less    or equal to {@code to}.
 * If the value for {@link #getTo  () to  } is null this means, that the range contains all values greater or equal to {@code from}.
 * If both the value for {@link #getFrom() from} and {@link #getTo() to} is null this means,
 * that the range contains all values.
 */
public final class Range<E extends Comparable<E>> implements Serializable
{
	public static <E extends Comparable<E>> Range<E> valueOf(final E from, final E to)
	{
		if(from==null && to==null)
			return all();

		return new Range<>(from, to, true);
	}

	@SuppressWarnings("unchecked") // OK
	private static <E extends Comparable<E>> Range<E> all()
	{
		return all;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static final Range all = new Range(null, null, true);


	private static final long serialVersionUID = 1l;

	@SuppressWarnings("NonSerializableFieldInSerializableClass") // OK: container is serializable if part is serializable
	private final E from;
	@SuppressWarnings("NonSerializableFieldInSerializableClass") // OK: container is serializable if part is serializable
	private final E to;

	/**
	 * @deprecated Use {@link #valueOf(Comparable, Comparable)} instead.
	 */
	@Deprecated
	public Range(final E from, final E to)
	{
		this(from, to, true);
	}

	private Range(final E from, final E to, @SuppressWarnings("unused") final boolean dummy)
	{
		if(from!=null && to!=null && from.compareTo(to)>0)
			throw new IllegalArgumentException("from " + from + " greater than to " + to);

		this.from = from;
		this.to = to;
	}

	public E getFrom()
	{
		return from;
	}

	public E getTo()
	{
		return to;
	}

	public boolean contains(final E value)
	{
		requireNonNull(value, "value");

		return
			(from==null || from.compareTo(value)<=0) &&
			(to  ==null || to  .compareTo(value)>=0) ;
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof Range<?>))
			return false;

		final Range<?> o = (Range<?>)other;
		return Objects.equals(from, o.from) && Objects.equals(to, o.to);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(from) ^ (Objects.hashCode(to) << 2);
	}

	@Override
	public String toString()
	{
		return
				"[" + Objects.toString(from, "") +
				'-' + Objects.toString(to,   "") + ']';
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #valueOf(Comparable,Comparable)} instead
	 */
	@Deprecated
	public static <E extends Comparable<E>> Range<E> newRange(final E from, final E to)
	{
		return valueOf(from, to);
	}
}

/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

/**
 * If the value for {@link #getFrom() from} is null this means, that the range contains all values less    or equal to <code>to</code>.
 * If the value for {@link #getTo  () to  } is null this means, that the range contains all values greater or equal to <code>from</code>.
 * If both the value for {@link #getFrom() from} and {@link #getTo() to} is null this means,
 * that the range contains all values.
 */
public final class Range<E extends Comparable<E>>
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


	private final E from;
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
		if(value==null)
			throw new NullPointerException("value");

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
		return equals(from, o.from) && equals(to, o.to);
	}

	private static boolean equals(final Object e1, final Object e2)
	{
		return e1!=null ? e1.equals(e2) : (e2==null);
	}

	@Override
	public int hashCode()
	{
		return hashCode(from) ^ (hashCode(to) << 2);
	}

	private static int hashCode(final Object e1)
	{
		return e1!=null ? e1.hashCode() : 0;
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

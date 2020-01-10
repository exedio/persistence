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

import static java.lang.Math.toIntExact;

public final class SequenceInfo
{
	private final Feature feature;
	private final long start;
	private final long minimum;
	private final long maximum;

	private final boolean known;
	private final long count;
	private final long first;
	private final long last;

	SequenceInfo(
			final Feature feature,
			final long start,
			final long minimum,
			final long maximum,
			final long count,
			final long first,
			final double last)
	{
		assert feature!=null;

		this.feature = feature;
		this.start = start;
		this.minimum = minimum;
		this.maximum = maximum;

		this.known = true;
		this.count = count;
		this.first = first;
		this.last = Math.round(last);
	}

	SequenceInfo(
			final Feature feature,
			final long start,
			final long minimum,
			final long maximum)
	{
		assert feature!=null;

		this.feature = feature;
		this.start = start;
		this.minimum = minimum;
		this.maximum = maximum;

		this.known = false;
		this.count = 0;
		this.first = 0;
		this.last = 0;
	}

	public Feature getFeature()
	{
		return feature;
	}

	/**
	 * @deprecated Use {@link #getStartL()} instead.
	 */
	@Deprecated
	public int getStart()
	{
		return toIntExact(getStartL());
	}

	public long getStartL()
	{
		return start;
	}

	/**
	 * @deprecated Use {@link #getMinimumL()} instead.
	 */
	@Deprecated
	public int getMinimum()
	{
		return toIntExact(getMinimumL());
	}

	public long getMinimumL()
	{
		return minimum;
	}

	/**
	 * @deprecated Use {@link #getMaximumL()} instead.
	 */
	@Deprecated
	public int getMaximum()
	{
		return toIntExact(getMaximumL());
	}

	public long getMaximumL()
	{
		return maximum;
	}

	/**
	 * @deprecated Use {@link #getCountL()} instead.
	 */
	@Deprecated
	public int getCount()
	{
		return toIntExact(getCountL());
	}

	public long getCountL()
	{
		return count;
	}

	public boolean isKnown()
	{
		return known;
	}

	/**
	 * Returns the first primary key number generated for the type since the startup of the application.
	 * @deprecated Use {@link #getFirstL()} instead.
	 */
	@Deprecated
	public int getFirst()
	{
		return toIntExact(getFirstL());
	}

	/**
	 * Returns the first primary key number generated for the type since the startup of the application.
	 */
	public long getFirstL()
	{
		if(!known)
			throw new IllegalStateException("not known");

		return first;
	}

	/**
	 * Returns the last primary key number generated for the type.
	 * @deprecated Use {@link #getLastL()} instead.
	 */
	@Deprecated
	public int getLast()
	{
		return toIntExact(getLastL());
	}

	/**
	 * Returns the last primary key number generated for the type.
	 */
	public long getLastL()
	{
		if(!known)
			throw new IllegalStateException("not known");

		return last;
	}

	@Override
	public String toString()
	{
		return feature.toString();
	}
}

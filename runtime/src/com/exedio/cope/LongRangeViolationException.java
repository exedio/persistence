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

package com.exedio.cope;

import com.exedio.cope.instrument.ConstructorComment;

/**
 * Signals, that an attempt to write an {@link IntegerField} has been failed,
 * because the value to be written violated the range constraint on that field.
 *
 * This exception will be thrown by {@link FunctionField#set(Item,Object)}
 * and item constructors.
 *
 * @author Ralf Wiebicke
 */
@ConstructorComment("if {0} violates its range constraint.")
public final class LongRangeViolationException extends RangeViolationException
{
	private static final long serialVersionUID = 1l;

	private final LongField feature;
	private final long value;

	/**
	 * Creates a new LongRangeViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 * @param value initializes, what is returned by {@link #getValue()}.
	 */
	LongRangeViolationException(
			final LongField feature,
			final Item item,
			final long value,
			final boolean isTooSmall,
			final long border)
	{
		super(feature, item, value, isTooSmall, border);
		this.feature = feature;
		this.value = value;
	}

	/**
	 * Returns the field, that was attempted to be written.
	 */
	@Override
	public LongField getFeature()
	{
		return feature;
	}

	/**
	 * Returns the value, that was attempted to be written.
	 */
	public long getValue()
	{
		return value;
	}
}

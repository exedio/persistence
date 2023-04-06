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

import com.exedio.cope.instrument.ConstructorComment;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Signals, that an attempt to write an {@link DateField} has been failed,
 * because the value to be written violated the range constraint on that field.
 * <p>
 * This exception will be thrown by {@link FunctionField#set(Item,Object)}
 * and item constructors.
 *
 * @author Ralf Wiebicke
 */
@ConstructorComment("if {0} violates its range constraint.")
public final class DateRangeViolationException extends ConstraintViolationException
{
	private static final long serialVersionUID = 1l;

	private final DateField feature;
	private final long value;
	private final boolean isTooSmall;
	private final long border;

	/**
	 * Creates a new DateRangeViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 * @param value initializes, what is returned by {@link #getValue()}.
	 */
	DateRangeViolationException(
			final DateField feature,
			final Item item,
			final long value,
			final boolean isTooSmall,
			final long border)
	{
		super(item, null);
		this.feature = feature;
		this.value = value;
		this.isTooSmall = isTooSmall;
		this.border = border;
	}

	/**
	 * Returns the field, that was attempted to be written.
	 */
	@Override
	public DateField getFeature()
	{
		return feature;
	}

	/**
	 * Returns the value, that was attempted to be written.
	 */
	public Date getValue()
	{
		return new Date(value);
	}

	public boolean isTooSmall()
	{
		return isTooSmall;
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		final SimpleDateFormat format = DateField.format();
		return
				"range violation" + getItemPhrase() +
				", " + format.format(getValue()) + ' ' + format.getTimeZone().getID() + " is too " +
				(isTooSmall?"small":"big") +
				(withFeature ? (" for " + feature) : "") +
				", must be at " + (isTooSmall?"least":"most") +
				' ' + format.format(new Date(border));
	}
}

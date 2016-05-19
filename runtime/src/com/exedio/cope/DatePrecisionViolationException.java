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

import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;

import com.exedio.cope.instrument.ConstructorComment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Signals, that an attempt to write a {@link DateField date field} has been failed,
 * because the value to be written violated the precision constraint on that field.
 *
 * This exception will be thrown by {@link FunctionField#set(Item,Object)}
 * and item constructors.
 *
 * @author Ralf Wiebicke
 */
@ConstructorComment("if {0} violates its precision constraint.")
public final class DatePrecisionViolationException extends ConstraintViolationException
{
	private static final long serialVersionUID = 1l;

	private final DateField feature;
	private final long value;
	private final int violation;

	/**
	 * Creates a new DatePrecisionViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 * @param value initializes, what is returned by {@link #getValue()}.
	 */
	DatePrecisionViolationException(
			final DateField feature,
			final Item item,
			final Date value,
			final int violation)
	{
		super(item, null);
		this.feature = feature;
		this.value = value.getTime();
		this.violation = violation;
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

	@Override
	public String getMessage(final boolean withFeature)
	{
		final StringBuilder bf = new StringBuilder();

		bf.append("precision violation").
			append(getItemPhrase()).
			append(", ").
			append(df().format(new Date(value))).
			append(" (").
			append(violation).
			append(") is too precise ");

		if(withFeature)
			bf.append(" for ").
				append(feature);
		bf.append(", must be ").
			append(feature.getPrecision().name());

		return bf.toString();
	}

	private static SimpleDateFormat df()
	{
		final SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		result.setTimeZone(ZONE);
		result.setLenient(false);
		return result;
	}

	private static final TimeZone ZONE = getTimeZone("GMT");
}

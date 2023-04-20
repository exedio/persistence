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

import com.exedio.cope.DateField.Precision;
import com.exedio.cope.DateField.RoundingMode;
import com.exedio.cope.instrument.ConstructorComment;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Signals, that an attempt to write a {@link DateField date field} has been failed,
 * because the value to be written violated the precision constraint on that field.
 * <p>
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
	private final Precision precision;
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
			final Precision precision,
			final Item item,
			final Date value,
			final int violation)
	{
		super(item, null);
		this.feature = feature;
		this.precision = requireNonNull(precision);
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
	 * Returns the precision the value was too precise for.
	 */
	public Precision getPrecision()
	{
		return precision;
	}

	/**
	 * Returns the value, that was attempted to be written.
	 */
	public Date getValue()
	{
		return new Date(value);
	}

	/**
	 * Returns a value allowed for the field
	 * and as close as possible to
	 * {@link #getValue() the value attempted to be written} the past.
	 */
	public Date getValueAllowedInPast()
	{
		return precision.round(getValue(), RoundingMode.PAST, null, null);
	}

	/**
	 * Returns a value allowed for the field
	 * and as close as possible to
	 * {@link #getValue() the value attempted to be written} the future.
	 */
	public Date getValueAllowedInFuture()
	{
		return precision.round(getValue(), RoundingMode.FUTURE, null, null);
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		final SimpleDateFormat df = DateField.format();
		final StringBuilder bf = new StringBuilder();

		bf.append("precision violation").
			append(getItemPhrase()).
			append(", ").
			append(df.format(getValue())).
			append(' ').
			append(df.getTimeZone().getID()).
			append(" (").
			append(violation).
			append(") is too precise for ").
			append(precision.name());

		if(withFeature)
			bf.append(" of ").
				append(feature);
		bf.append(", round either to ").
			append(df.format(getValueAllowedInPast())).
			append(" in the past or ").
			append(df.format(getValueAllowedInFuture())).
			append(" in the future");

		return bf.toString();
	}
}

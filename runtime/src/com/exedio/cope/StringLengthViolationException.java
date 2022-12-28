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

import static com.exedio.cope.StringField.truncateValue;

import com.exedio.cope.instrument.ConstructorComment;

/**
 * Signals, that an attempt to write a {@link StringField string field} has been failed,
 * because value to be written violated the length constraint on that field.
 *
 * This exception will be thrown by {@link FunctionField#set(Item,Object)}
 * and item constructors.
 *
 * @author Ralf Wiebicke
 */
@ConstructorComment("if {0} violates its length constraint.")
public final class StringLengthViolationException extends ConstraintViolationException
{
	private static final long serialVersionUID = 1l;

	private final StringField feature;
	private final String value;

	/**
	 * Creates a new StringLengthViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 * @param value initializes, what is returned by {@link #getValue()}.
	 */
	StringLengthViolationException(
			final StringField feature,
			final Item item,
			final String value)
	{
		super(item, null);
		this.feature = feature;
		this.value = value;
	}

	/**
	 * Returns the field, that was attempted to be written.
	 */
	@Override
	public StringField getFeature()
	{
		return feature;
	}

	/**
	 * Returns the value, that was attempted to be written.
	 */
	public String getValue()
	{
		return value;
	}

	public boolean isTooShort()
	{
		return value.length()<feature.getMinimumLength();
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		final int len = value.length();
		final int min = feature.getMinimumLength();
		final int max = feature.getMaximumLength();
		final StringBuilder bf = new StringBuilder();

		bf.append("length violation").
			append(getItemPhrase()).
			append(", ").
			append(truncateValue(value)).
			append(" is too ").
			append((len<min)?"short":"long");

		if(withFeature)
			bf.append(" for ").
				append(feature);
		bf.append(", must be ");

		if(min==max)
			bf.append("exactly");
		else
			bf.append("at ").
				append((len<min)?"least":"most");

		bf.append(' ').
			append((len<min)?min:max).
			append(" characters, but was ").
			append(len);

		return bf.toString();
	}
}

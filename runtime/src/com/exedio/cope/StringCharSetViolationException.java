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
import java.io.Serial;

/**
 * Signals, that an attempt to write a {@link StringField string field} has been failed,
 * because value to be written violated the character set constraint on that field.
 * <p>
 * This exception will be thrown by {@link FunctionField#set(Item,Object)}
 * and item constructors.
 *
 * @author Ralf Wiebicke
 */
@ConstructorComment("if {0} violates its character set constraint.")
public final class StringCharSetViolationException extends ConstraintViolationException
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final StringField feature;
	private final String value;
	private final char character;
	private final int position;

	/**
	 * Creates a new StringCharSetViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 * @param value initializes, what is returned by {@link #getValue()}.
	 */
	StringCharSetViolationException(
			final StringField feature,
			final Item item,
			final String value,
			final char character,
			final int position)
	{
		super(item, null);
		this.feature = feature;
		this.value = value;
		this.character = character;
		this.position = position;
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

	public char getCharacter()
	{
		return character;
	}

	public int getPosition()
	{
		return position;
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		final StringBuilder bf = new StringBuilder();

		bf.append("character set violation").
			append(getItemPhrase()).
			append(", ").append(truncateValue(value));

		if(withFeature)
			bf.append(" for ").
				append(feature);

		bf.append(", contains forbidden character '").append(character).
			append("' (U+").append(String.format("%04x", (int)character)).
			append(") on position ").append(position);

		return bf.toString();
	}
}

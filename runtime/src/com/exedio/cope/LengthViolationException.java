/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

/**
 * Signals, that an attempt to write a {@link StringField string attribute} has been failed,
 * because value to be written violated the length constraint on that attribute.
 *
 * This exception will be thrown by {@link FunctionField#set(Item,Object)}
 * and item constructors.
 *
 * @author Ralf Wiebicke
 */
public final class LengthViolationException extends ConstraintViolationException
{
	private static final long serialVersionUID = 21864323501623l;
	
	private final StringField stringAttribute;
	private final String value;
	private final boolean isTooShort;
	private final int border;
	
	/**
	 * Creates a new LengthViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param stringAttribute initializes, what is returned by {@link #getStringAttribute()}.
	 * @param value initializes, what is returned by {@link #getValue()}.
	 */
	public LengthViolationException(final StringField stringAttribute, final Item item, final String value, final boolean isTooShort, final int border)
	{
		super(stringAttribute, item, null);
		this.stringAttribute = stringAttribute;
		this.value = value;
		this.isTooShort = isTooShort;
		this.border = border;
	}
	
	/**
	 * Returns the attribute, that was attempted to be written.
	 */
	public StringField getStringAttribute()
	{
		return stringAttribute;
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
		return isTooShort;
	}

	@Override
	public String getMessage()
	{
		return
			"length violation on " + getItemID() +
			", '" + value + "' is too " +
			(isTooShort?"short":"long") +
			" for "+ stringAttribute +
			", must be at " + (isTooShort?"least":"most") +
			' ' + border + " characters.";
	}
	
	@Override
	public String getMessageWithoutFeature()
	{
		return
			"length violation on " + getItemID() +
			", '" + value + "' is too " +
			(isTooShort?"short":"long") +
			", must be at " + (isTooShort?"least":"most") +
			' ' + border + " characters.";
	}
	
}

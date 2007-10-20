/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
 * Signals, that an attempt to write a {@link StringField string field} has been failed,
 * because value to be written violated the length constraint on that field.
 *
 * This exception will be thrown by {@link FunctionField#set(Item,Object)}
 * and item constructors.
 *
 * @author Ralf Wiebicke
 */
public final class LengthViolationException extends ConstraintViolationException
{
	private static final long serialVersionUID = 1l;
	
	private final StringField feature;
	private final String value;
	private final boolean isTooShort;
	private final int border;
	
	/**
	 * Creates a new LengthViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 * @param value initializes, what is returned by {@link #getValue()}.
	 */
	public LengthViolationException(final StringField feature, final Item item, final String value, final boolean isTooShort, final int border)
	{
		super(item, null);
		this.feature = feature;
		this.value = value;
		this.isTooShort = isTooShort;
		this.border = border;
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
	 * @deprecated Renamed to {@link #getFeature()}.
	 */
	@Deprecated
	public StringField getStringAttribute()
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
		return isTooShort;
	}

	@Override
	public String getMessage()
	{
		return
			"length violation on " + getItemID() +
			", '" + value + "' is too " +
			(isTooShort?"short":"long") +
			" for "+ feature +
			", must be at " + (isTooShort?"least":"most") +
			' ' + border + " characters, but was " + value.length() + '.';
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

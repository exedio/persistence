/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
 * Signals, that an attempt to write a {@link StringAttribute string attribute} has been failed,
 * because value to be written violated the length constraint on that attribute.
 *
 * This exception will be thrown by {@link StringAttribute#set(Item,String)}
 * and item constructors
 * if for that attribute is
 * {@link StringAttribute#hasLengthConstraintCheckedException()}==false.
 * 
 * @see LengthViolationException
 * @author Ralf Wiebicke
 */
public final class LengthViolationRuntimeException extends ConstraintViolationRuntimeException
{
	private final Item item;
	private final StringAttribute stringAttribute;
	private final String value;
	
	/**
	 * Creates a new LengthViolationRuntimeException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param stringAttribute initializes, what is returned by {@link #getStringAttribute()}.
	 * @param value initializes, what is returned by {@link #getValue()}.
	 */
	public LengthViolationRuntimeException(final Item item, final StringAttribute stringAttribute, final String value)
	{
		super(stringAttribute);
		this.item = item;
		this.stringAttribute = stringAttribute;
		this.value = value;
	}
	
	/**
	 * Returns the item that was attempted to be modified.
	 * Returns null, if the violation occured on the creation of an item.
	 */
	public final Item getItem()
	{
		return item;
	}

	/**
	 * Returns the attribute, that was attempted to be written.
	 */
	public StringAttribute getStringAttribute()
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
	
	public String getMessage()
	{
		return
			"length violation on " + Cope.getCopeID(item) +
			", '" + value + "' is too long" +
			" for "+ stringAttribute;
	}
	
}

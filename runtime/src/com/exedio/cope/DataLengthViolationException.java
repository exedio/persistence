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
 * Signals, that an attempt to write a {@link DataAttribute data attribute} has been failed,
 * because value to be written violated the length constraint on that attribute.
 *
 * This exception will be thrown by {@link DataAttribute#set(Item,byte[])} etc.
 * and item constructors.
 * 
 * @author Ralf Wiebicke
 */
public final class DataLengthViolationException extends ConstraintViolationRuntimeException
{
	private final DataAttribute dataAttribute;
	private final long length;
	private final boolean lengthExact;
	
	/**
	 * Creates a new LengthViolationRuntimeException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param dataAttribute initializes, what is returned by {@link #getDataAttribute()}.
	 * @param length initializes, what is returned by {@link #getLength()}.
	 */
	public DataLengthViolationException(final Item item, final DataAttribute dataAttribute, final long length, final boolean lengthExact)
	{
		super(dataAttribute, item);
		
		if(length<dataAttribute.getMaximumLength())
			throw new RuntimeException(dataAttribute.toString()+'/'+length+'/'+dataAttribute.getMaximumLength());
		
		this.dataAttribute = dataAttribute;
		this.length = length;
		this.lengthExact = lengthExact;
	}
	
	/**
	 * Returns the attribute, that was attempted to be written.
	 */
	public DataAttribute getDataAttribute()
	{
		return dataAttribute;
	}

	/**
	 * Returns the length of the data, that was attempted to be written.
	 * Returns -1, if that length is unknown.
	 */	
	public long getLength()
	{
		return length;
	}
	
	public boolean isLengthExact()
	{
		return lengthExact;
	}
	
	public String getMessage()
	{
		return
			"length violation on " + Cope.getCopeID(getItem()) +
			", " + length + " bytes " +
			(lengthExact ? "" : "or more ") +
			"is too long for " + dataAttribute;
	}
	
}

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
 * Signals, that an attempt to write an attribute has been failed,
 * because it cannot be written with a null value.
 *
 * This exception will be thrown by {@link Item#set(ObjectAttribute,Object) Item.set}
 * and item constructors
 * if that attribute is {@link Attribute#isMandatory() mandatory}.
 * <p>
 * This exception is also thrown for empty strings if
 * {@link Model#supportsEmptyStrings()} is false.
 * 
 * @author Ralf Wiebicke
 */
public final class MandatoryViolationException extends ConstraintViolationException
{
	private final Item item;
	private final Attribute mandatoryAttribute;
	
	/**
	 * Creates a new MandatoryViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param mandatoryAttribute initializes, what is returned by {@link #getMandatoryAttribute()}.
	 */
	MandatoryViolationException(final Item item, final Attribute mandatoryAttribute)
	{
		super(null);
		this.item = item;
		this.mandatoryAttribute = mandatoryAttribute;
	}
	
	/**
	 * Returns the item that was attempted to be modified.
	 * Returns null, if the mandatory violation occured on the creation of an item.
	 */
	public final Item getItem()
	{
		return item;
	}

	/**
	 * Returns the attribute, that was attempted to be written.
	 */
	public Attribute getMandatoryAttribute()
	{
		return mandatoryAttribute;
	}

	public String getMessage()
	{
		return "mandatory violation on " + (item!=null ? item.getCopeID() : "a newly created item") + " for "+ mandatoryAttribute;
	}
	
}

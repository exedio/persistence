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
 * Signals, that an attempt to write an attribute has been failed,
 * because it cannot be written with any value.
 *
 * This exception will be thrown by {@link Item#set(FunctionField,Object) Item.set}
 * if that attribute is {@link Attribute#isFinal() final}.
 *
 * @author Ralf Wiebicke
 */
public final class FinalViolationException extends ConstraintViolationException
{
	private static final long serialVersionUID = 19862582163486l;
	
	private final Attribute finalAttribute;
	
	/**
	 * Creates a new FinalViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param finalAttribute initializes, what is returned by {@link #getFinalAttribute()}.
	 */
	public FinalViolationException(final Attribute finalAttribute, final Item item)
	{
		super(finalAttribute, item, null);
		
		if(item==null)
			throw new NullPointerException();
		
		this.finalAttribute = finalAttribute;
	}
	
	/**
	 * Returns the attribute, that was attempted to be written.
	 */
	public Attribute getFinalAttribute()
	{
		return finalAttribute;
	}

	@Override
	public String getMessage()
	{
		return "final violation on " + getItemID() + " for " + finalAttribute;
	}
	
	@Override
	public String getMessageWithoutFeature()
	{
		return "final violation on " + getItemID();
	}
	
}

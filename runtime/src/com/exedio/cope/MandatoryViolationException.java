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
 * Signals, that an attempt to write an field has been failed,
 * because it cannot be written with a null value.
 *
 * This exception will be thrown by {@link Item#set(FunctionField,Object) Item.set}
 * and item constructors
 * if that field is {@link Field#isMandatory() mandatory}.
 * <p>
 * This exception is also thrown for empty strings if
 * {@link Model#supportsEmptyStrings()} is false.
 *
 * @author Ralf Wiebicke
 */
public final class MandatoryViolationException extends ConstraintViolationException
{
	private static final long serialVersionUID = 37264512124982l;
	
	private final Field feature;
	
	/**
	 * Creates a new MandatoryViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 */
	MandatoryViolationException(final Field feature, final Item item)
	{
		super(item, null);
		this.feature = feature;
	}
	
	/**
	 * Returns the field, that was attempted to be written.
	 */
	@Override
	public Field getFeature()
	{
		return feature;
	}
	
	/**
	 * @deprecated Renamed to {@link #getFeature()}.
	 */
	@Deprecated
	public Field getMandatoryAttribute()
	{
		return feature;
	}

	@Override
	public String getMessage()
	{
		return "mandatory violation on " + getItemID() + " for " + feature;
	}
	
	@Override
	public String getMessageWithoutFeature()
	{
		return "mandatory violation on " + getItemID();
	}
	
}

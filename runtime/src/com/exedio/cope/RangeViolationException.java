/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
 * Signals, that an attempt to write an {@link IntegerField} has been failed,
 * because the value to be written violated the range constraint on that field.
 *
 * This exception will be thrown by {@link FunctionField#set(Item,Object)}
 * and item constructors.
 *
 * @author Ralf Wiebicke
 */
public final class RangeViolationException extends ConstraintViolationException
{
	private static final long serialVersionUID = 1l;
	
	private final IntegerField feature;
	private final int value;
	private final boolean isTooSmall;
	private final int border;
	
	/**
	 * Creates a new RangeViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 * @param value initializes, what is returned by {@link #getValue()}.
	 */
	public RangeViolationException(final IntegerField feature, final Item item, final int value, final boolean isTooSmall, final int border)
	{
		super(item, null);
		this.feature = feature;
		this.value = value;
		this.isTooSmall = isTooSmall;
		this.border = border;
	}
	
	/**
	 * Returns the field, that was attempted to be written.
	 */
	@Override
	public IntegerField getFeature()
	{
		return feature;
	}
	
	/**
	 * Returns the value, that was attempted to be written.
	 */
	public int getValue()
	{
		return value;
	}
	
	public boolean isTooSmall()
	{
		return isTooSmall;
	}

	@Override
	public String getMessage()
	{
		return
			"range violation on " + getItemID() +
			", " + value + " is too " +
			(isTooSmall?"small":"big") +
			" for "+ feature +
			", must be at " + (isTooSmall?"least":"most") +
			' ' + border + '.';
	}
	
	@Override
	public String getMessageWithoutFeature()
	{
		return
			"range violation on " + getItemID() +
			", " + value + " is too " +
			(isTooSmall?"small":"big") +
			" for "+ feature +
			", must be at " + (isTooSmall?"least":"most") +
			' ' + border + '.';
	}
}

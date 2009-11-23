/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.ConstructorComment;

/**
 * Signals, that an attempt to write a {@link AbstractListField list field} has been failed,
 * because value to be written violated the size constraint on that field.
 *
 * This exception will be thrown by {@link FunctionField#set(Item,Object)}
 * and item constructors.
 *
 * @author Ralf Wiebicke
 */
@ConstructorComment("if {0} violates its length constraint.")
public final class ListSizeViolationException extends ConstraintViolationException
{
	private static final long serialVersionUID = 1l;
	
	private final AbstractListField feature;
	private final int size;
	private final int border;
	
	/**
	 * Creates a new ListSizeViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 * @param value initializes, what is returned by {@link #getValue()}.
	 */
	public ListSizeViolationException(final AbstractListField feature, final Item item, final int size, final int border)
	{
		super(item, null);
		this.feature = feature;
		this.size = size;
		this.border = border;
	}
	
	/**
	 * Returns the field, that was attempted to be written.
	 */
	@Override
	public AbstractListField getFeature()
	{
		return feature;
	}

	/**
	 * Returns the size of the value, that was attempted to be written.
	 */
	public int getSize()
	{
		return size;
	}
	
	@Override
	public String getMessage(final boolean withFeature)
	{
		return
			"size violation on " + getItemText() +
			", value is too long" +
			(withFeature ? (" for "+ feature) : "") +
			", must be at most" +
			' ' + border + " elements, " +
			"but was " + size + '.';
	}
}

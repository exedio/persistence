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

public final class CopyViolationException extends ConstraintViolationException
{
	private static final long serialVersionUID = 1l;
	
	private final CopyField feature;
	private final Object expectedValue;
	private final Object actualValue;
	private final Item targetItem;
	
	public CopyViolationException(final Item targetItem, final CopyField feature, final Object expectedValue, final Object actualValue)
	{
		super(null, null);
		this.feature = feature;
		this.expectedValue = expectedValue;
		this.actualValue = actualValue;
		this.targetItem = targetItem;
	}
	
	/**
	 * Returns the field, that was attempted to be written.
	 */
	@Override
	public CopyField getFeature()
	{
		return feature;
	}

	public Object getExpectedValue()
	{
		return expectedValue;
	}

	public Object getActualValue()
	{
		return actualValue;
	}

	public Item getTargetItem()
	{
		return targetItem;
	}
	
	private static final String toString(final Object s)
	{
		return s!=null ? ('\'' + s.toString() + '\'') : "null";
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		return
			"mismatch on copy field " + feature +
			", expected " + toString(expectedValue) +
			" from target " + targetItem +
			", but was " +	toString(actualValue);
	}
}

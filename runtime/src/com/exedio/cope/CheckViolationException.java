/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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
 * and the value to be set violated a check constraint.
 * <p>
 * This exception will be thrown by {@link Item#set(FunctionField,Object) Item.set}
 * and item constructors
 * if that field is covered by a {@link CheckConstraint check constraint}
 * and the value to be set violated the constraint.
 *
 * @author Ralf Wiebicke
 */
public final class CheckViolationException extends ConstraintViolationException
{
	private static final long serialVersionUID = 1l;

	private final CheckConstraint feature;

	/**
	 * Creates a new CheckViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 * @throws NullPointerException if {@code constraint} is null.
	 */
	CheckViolationException(
			final FieldValues item,
			final CheckConstraint feature)
	{
		super(item.getBackingItem(), null);
		this.feature = feature;
	}

	/**
	 * Returns the violated constraint.
	 */
	@Override
	public CheckConstraint getFeature()
	{
		return feature;
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		return "check violation" + getItemPhrase() + (withFeature ? (" for " + feature) : "");
	}
}

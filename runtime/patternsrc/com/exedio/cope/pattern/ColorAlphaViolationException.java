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

package com.exedio.cope.pattern;

import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.ConstructorComment;
import java.awt.Color;

/**
 * Signals, that an attempt to write an {@link ColorField} has been failed,
 * because the value to be written had a alpha component.
 * <p>
 * This exception will be thrown by {@link FunctionField#set(Item,Object)}
 * and item constructors.
 *
 * @author Ralf Wiebicke
 */
@ConstructorComment("if {0} violates its alpha constraint.")
public final class ColorAlphaViolationException extends ConstraintViolationException
{
	private static final long serialVersionUID = 1l;

	private final ColorField feature;
	private final Color value;

	/**
	 * Creates a new ColorAlphaViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 * @param value initializes, what is returned by {@link #getValue()}.
	 */
	ColorAlphaViolationException(
			final ColorField feature,
			final Item item,
			final Color value)
	{
		super(item, null);
		this.feature = feature;
		this.value = value;
	}

	/**
	 * Returns the field, that was attempted to be written.
	 */
	@Override
	public ColorField getFeature()
	{
		return feature;
	}

	/**
	 * Returns the value, that was attempted to be written.
	 */
	public Color getValue()
	{
		return value;
	}

	@Override
	protected String getMessage(final boolean withFeature)
	{
		return
			"alpha violation" + getItemPhrase() +
			", " + value + " has alpha of " + value.getAlpha() +
			(withFeature ? (" for " + feature) : "");
	}
}

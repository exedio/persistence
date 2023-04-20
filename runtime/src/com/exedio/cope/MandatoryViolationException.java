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

import com.exedio.cope.instrument.ConstructorComment;
import javax.annotation.Nonnull;

/**
 * Signals, that an attempt to write an field has been failed,
 * because it cannot be written with a null value.
 * <p>
 * This exception will be thrown by {@link Item#set(FunctionField,Object) Item.set}
 * and item constructors
 * if that field is {@link Field#isMandatory() mandatory}.
 *
 * @author Ralf Wiebicke
 */
@ConstructorComment("if {0} is null.")
public final class MandatoryViolationException extends ConstraintViolationException
{
	public static void requireNonNull(
			@Nonnull final Object value,
			final Feature feature,
			final Item item)
	{
		//noinspection ConstantConditions OK: this is the check
		if(value==null)
			throw create(feature, item);
	}

	private static final long serialVersionUID = 1l;

	private final Feature feature;

	/**
	 * Creates a new MandatoryViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 */
	public static MandatoryViolationException create(
			final Feature feature,
			final Item item)
	{
		return new MandatoryViolationException(feature, item);
	}

	private MandatoryViolationException(final Feature feature, final Item item)
	{
		super(item, null);
		this.feature = feature;
	}

	/**
	 * Returns the feature, that was attempted to be written.
	 */
	@Override
	public Feature getFeature()
	{
		return feature;
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		return "mandatory violation" + getItemPhrase() + (withFeature ? (" for " + feature) : "");
	}
}

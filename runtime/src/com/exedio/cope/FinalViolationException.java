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

import static java.util.Objects.requireNonNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.annotation.Nonnull;

/**
 * Signals, that an attempt to write an field has been failed,
 * because it cannot be written with any value.
 *
 * This exception will be thrown by {@link Item#set(FunctionField,Object) Item.set}
 * if that field is {@link Field#isFinal() final}.
 *
 * @author Ralf Wiebicke
 */
public final class FinalViolationException extends ConstraintViolationException
{
	@SuppressFBWarnings("BC_UNCONFIRMED_CAST")
	public static <F extends Feature & Settable<?>> void check(
			@Nonnull final F feature,
			@Nonnull final Item item)
	{
		if(feature.isFinal())
			throw create(feature, item);
	}

	private static final long serialVersionUID = 1l;

	private final Feature feature;

	/**
	 * Creates a new FinalViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 */
	@SuppressWarnings("deprecation") // needed for idea
	@SuppressFBWarnings("BC_UNCONFIRMED_CAST")
	public static <F extends Feature & Settable<?>> FinalViolationException create(final F feature, final Item item)
	{
		return new FinalViolationException(feature, feature, item);
	}

	/**
	 * @deprecated Use {@link #create(Feature, Item)} instead.
	 */
	@Deprecated
	public FinalViolationException(final Feature feature, final Settable<?> settable, final Item item)
	{
		super(requireNonNull(item, "item"), null);

		if(feature!=settable)
			throw new IllegalArgumentException("feature and settable must be the same object, but was " + feature + " and " + settable);
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
		return "final violation" + getItemPhrase() + (withFeature ? (" for " + feature) : "");
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Renamed to {@link #getFeature()}.
	 */
	@Deprecated
	public Feature getFinalAttribute()
	{
		return feature;
	}
}
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

import java.io.Serial;
import javax.annotation.Nonnull;

/**
 * Signals, that an attempt to write an field has been failed,
 * because it cannot be written with any value.
 * <p>
 * This exception will be thrown by {@link Item#set(FunctionField,Object) Item.set}
 * if that field is {@link Field#isFinal() final}.
 *
 * @author Ralf Wiebicke
 */
public final class FinalViolationException extends ConstraintViolationException
{
	public static <F extends Feature & Settable<?>> void check(
			@Nonnull final F feature,
			@Nonnull final Item item)
	{
		if(feature.isFinal())
			throw create(feature, item);
	}

	@Serial
	private static final long serialVersionUID = 1l;

	private final Settable<?> feature;

	/**
	 * Creates a new FinalViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()} and {@link #getFeatureSettable()}.
	 * @deprecated Use {@link #create(Settable, Item)} instead.
	 */
	@Deprecated
	@SuppressWarnings({"ClassEscapesDefinedScope", "unchecked"})
	//The "PreventUsage" generic bound is required so that the compiler no longer resolves create(Feature, Item) to
	//this method and instead to create(Settable, Item). The interface is not supposed to be available outside this class.
	public static <F extends Feature & Settable<?> & PreventUsage> FinalViolationException create(final F feature, final Item item)
	{
		return create((Settable<? super Object>)feature, item);
	}

	@SuppressWarnings({"InterfaceNeverImplemented", "MarkerInterface", "unused"})
	private interface PreventUsage
	{}

	/**
	 * Creates a new FinalViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()} and {@link #getFeatureSettable()}.
	 */
	public static FinalViolationException create(final Settable<?> feature, final Item item)
	{
		return new FinalViolationException(feature, item);
	}

	private FinalViolationException(final Settable<?> feature, final Item item)
	{
		super(requireNonNull(item, "item"), null);

		this.feature = feature;
	}

	/**
	 * Returns the feature, that was attempted to be written.
	 * <p>
	 * Always returns implementations of {@link Settable}.
	 * If you need the result casted to {@link Settable},
	 * use {@link #getFeatureSettable()} instead.
	 */
	@Override
	public Feature getFeature()
	{
		return feature;
	}

	/**
	 * Returns the feature, that was attempted to be written.
	 * Is equivalent to {@link #getFeature()}.
	 */
	public Settable<?> getFeatureSettable()
	{
		return feature;
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		return "final violation" + getItemPhrase() + (withFeature ? (" for " + feature) : "");
	}
}

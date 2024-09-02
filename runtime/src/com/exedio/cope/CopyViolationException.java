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
import javax.annotation.Nullable;

public final class CopyViolationException extends ConstraintViolationException
{
	@Serial
	private static final long serialVersionUID = 1l;

	@Nonnull  private final CopyConstraint feature;
	@Nullable private final CopyConstraint additionalFeature;
	private final Object expectedValue;
	private final Object actualValue;
	private final Type<?> actualValueType;
	@Nonnull  private final Item targetItem;
	@Nullable private final Item additionalTargetItem;

	CopyViolationException(
			final FieldValues item,
			final Item targetItem,
			final CopyConstraint feature,
			final Object expectedValue,
			final Object actualValue)
	{
		this(item, targetItem, null, feature, null, expectedValue, actualValue);
	}

	CopyViolationException(
			final FieldValues item,
			final Item targetItem,
			@Nullable final Item additionalTargetItem,
			final CopyConstraint feature,
			@Nullable final CopyConstraint additionalFeature,
			final Object expectedValue,
			final Object actualValue)
	{
		super(item.getBackingItem(), null);
		assert (additionalFeature==null) == (additionalTargetItem==null);
		this.feature = requireNonNull(feature);
		this.additionalFeature = additionalFeature;
		this.expectedValue = expectedValue;
		this.actualValue = actualValue;
		this.actualValueType = null;
		this.targetItem = requireNonNull(targetItem);
		this.additionalTargetItem = additionalTargetItem;
	}

	CopyViolationException(
			final Item targetItem,
			final CopyConstraint feature,
			final Object expectedValue,
			final Type<?> actualValueType)
	{
		super(null, null);
		this.feature = requireNonNull(feature);
		this.additionalFeature = null;
		this.expectedValue = expectedValue;
		this.actualValue = null;
		this.actualValueType = actualValueType;
		this.targetItem = requireNonNull(targetItem);
		this.additionalTargetItem = null;
	}

	/**
	 * Returns the field, that was attempted to be written.
	 */
	@Nonnull
	@Override
	public CopyConstraint getFeature()
	{
		return feature;
	}

	@Nullable
	public CopyConstraint getAdditionalFeature()
	{
		return additionalFeature;
	}

	public Object getExpectedValue()
	{
		return expectedValue;
	}

	public Object getActualValue()
	{
		return actualValue;
	}

	@Nonnull
	public Item getTargetItem()
	{
		return targetItem;
	}

	@Nullable
	public Item getAdditionalTargetItem()
	{
		return additionalTargetItem;
	}

	private static String toString(final Object s)
	{
		return s!=null ? ('\'' + (s instanceof Item ? ((Item)s).getCopeID() : s.toString()) + '\'') : "null";
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		if (additionalFeature==null)
		{
			return
				"copy violation" + getItemPhrase() +
				" for " + feature +
				", expected " + toString(expectedValue) +
				" from target " + targetItem.getCopeID() +
				", but was " + (actualValueType!=null ? ("newly created instance of type " + actualValueType) : toString(actualValue));
		}
		else
		{
			return
				"copy violation" + getItemPhrase() +
				" for " + feature + " and " + additionalFeature +
				", expected " + toString(expectedValue) +
				" from target " + targetItem.getCopeID() +
				" but also " + toString(actualValue) +
				" from target " + requireNonNull(additionalTargetItem).getCopeID();
		}
	}
}

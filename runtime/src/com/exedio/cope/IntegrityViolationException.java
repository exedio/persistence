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


/**
 * Signals, that an attempt to delete an item has been failed,
 * because some other item point to that item with some
 * {@link ItemField item field}.
 * <p>
 * Also knows as foreign key constraint violation.
 * <p>
 * This exception is thrown by {@link Item#deleteCopeItem()}.
 *
 * @author Ralf Wiebicke
 */
public final class IntegrityViolationException extends ConstraintViolationException
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final ItemField<?> feature;
	private final int referrers;

	/**
	 * Creates a new IntegrityViolationException with the necessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param feature initializes, what is returned by {@link #getFeature()}.
	 * @throws NullPointerException if {@code item} or {@code feature} is null.
	 */
	IntegrityViolationException(
			final ItemField<?> feature,
			final Item item,
			final int referrers)
	{
		super(requireNonNull(item), null);

		this.feature = feature;
		this.referrers = referrers;
	}

	/**
	 * Returns the item field, for which the integrity (foreign key) constraint has been violated.
	 * Returns null, if the violated constraint is unknown.
	 */
	@Override
	public ItemField<?> getFeature()
	{
		return feature;
	}

	@Override
	public String getMessage(final boolean withFeature)
	{
		return
			"integrity violation" +
			" on deletion of " + getItem().getCopeID() +
			(withFeature ? (" because of " + feature) : "") +
			" referring to " + referrers + " item(s)";
	}
}

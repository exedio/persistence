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

import java.io.Serial;

/**
 * Is thrown, when a persistent modification violates a constraint.
 *
 * @author Ralf Wiebicke
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass") // OK: abstract exception classes have no choice
public abstract class ConstraintViolationException extends RuntimeException
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final Item item;

	protected ConstraintViolationException(final Item item, final Throwable cause)
	{
		// Specifying message==null avoids detailMessage computed from cause in
		// Throwable constructor, not needed because getMessage is overridden anyway.
		super(null, cause);
		this.item = item;
	}

	public abstract Feature getFeature();

	/**
	 * Returns the item that was attempted to be modified.
	 * Returns null, if the constraint violation occurred on the creation of an item.
	 */
	public final Item getItem()
	{
		return item;
	}

	protected final String getItemPhrase()
	{
		return item!=null ? (" on " + item.getCopeID()) : "";
	}

	@Override
	public final String getMessage()
	{
		return getMessage(true);
	}

	public final String getMessageWithoutFeature()
	{
		return getMessage(false);
	}

	protected abstract String getMessage(boolean withFeature);
}

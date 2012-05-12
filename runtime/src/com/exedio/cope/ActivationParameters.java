/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
 * A parameter class for activation constructors.
 *
 * @author Ralf Wiebicke
 */
public final class ActivationParameters
{
	final Type<?> type;
	final int pk;

	ActivationParameters(final Type type, final int pk)
	{
		this.type = type;
		this.pk = pk;
	}

	/**
	 * This method is equivalent to {@link Item#toString()}.
	 */
	@Override
	public String toString()
	{
		return type.id + Item.ID_SEPARATOR + pk;
	}
}

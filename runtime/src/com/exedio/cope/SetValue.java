/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

public final class SetValue<E>
{
	public final Settable<E> settable;
	public final E value;
	
	/**
	 * Creates a new SetValue.
	 * Instead of using this constructor directly,
	 * you may want to use the convenience functions.
	 * @see Settable#map(Object)
	 */
	public SetValue(final Settable<E> settable, final E value)
	{
		this.settable = settable;
		this.value = value;
	}
	
}

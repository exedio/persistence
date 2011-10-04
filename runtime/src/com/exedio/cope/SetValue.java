/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
	 */
	@SuppressWarnings("deprecation") // OK
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("BC_UNCONFIRMED_CAST")
	public static <E, F extends Feature & Settable<E>> SetValue<E> map(final F settable, final E value)
	{
		return new SetValue<E>(settable, value);
	}

	/**
	 * @deprecated Use {@link #map(Feature, Item)} instead.
	 */
	@Deprecated
	public SetValue(final Settable<E> settable, final E value)
	{
		if(settable==null)
			throw new NullPointerException("settable");

		this.settable = settable;
		this.value = value;
	}

	@Override
	public String toString()
	{
		return settable.toString() + '=' + value;
	}
}

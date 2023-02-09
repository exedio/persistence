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

public final class BindStringFunction extends BindFunction<String>
	implements StringFunction
{
	private static final long serialVersionUID = 1l;

	/**
	 * @deprecated
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see NumberFunction#bind(Join)
	 */
	@Deprecated
	public BindStringFunction(final StringFunction function, final Join join)
	{
		super(function, join);
	}

	/**
	 * Return this.
	 * It makes no sense wrapping a BindFunction into another BindFunction,
	 * because the inner BindFunction &quot;wins&quot;.
	 */
	@Override
	public BindStringFunction bind(final Join join)
	{
		return this;
	}
}

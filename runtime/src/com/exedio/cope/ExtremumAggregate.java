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

public final class ExtremumAggregate<E> extends Aggregate<E,E>
{
	@Serial
	private static final long serialVersionUID = 1l;

	final boolean minimum;

	/**
	 * Creates a new ExtremumAggregate.
	 * @deprecated
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see Function#min()
	 * @see Function#max()
	 */
	@Deprecated
	public ExtremumAggregate(final Function<E> source, final boolean minimum)
	{
		super(source, minimum?"min":"max", minimum?"MIN":"MAX", source.getValueType());
		this.minimum = minimum;
	}

	public boolean isMinimum()
	{
		return minimum;
	}

	public boolean isMaximum()
	{
		return !minimum;
	}

	@Override
	public Function<E> bind(final Join join)
	{
		return new ExtremumAggregate<>(source.bind(join), minimum);
	}
}

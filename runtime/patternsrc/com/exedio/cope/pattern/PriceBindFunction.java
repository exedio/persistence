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

package com.exedio.cope.pattern;

import com.exedio.cope.Condition;
import com.exedio.cope.Join;
import com.exedio.cope.NumberFunction;

final class PriceBindFunction implements PriceFunction
{
	final NumberFunction<Integer> integer;

	PriceBindFunction(final PriceField function, final Join join)
	{
		integer = function.getInt().bind(join);
	}

	public Condition isNull()
	{
		return integer.isNull();
	}

	public Condition isNotNull()
	{
		return integer.isNotNull();
	}

	public Condition equal(final Price value)
	{
		return integer.equal(store(value));
	}

	public Condition notEqual(final Price value)
	{
		return integer.notEqual(store(value));
	}

	public Condition less(final Price value)
	{
		return integer.less(value.store());
	}

	public Condition lessOrEqual(final Price value)
	{
		return integer.lessOrEqual(value.store());
	}

	public Condition greater(final Price value)
	{
		return integer.greater(value.store());
	}

	public Condition greaterOrEqual(final Price value)
	{
		return integer.greaterOrEqual(value.store());
	}

	public Condition between(final Price lowerBound, final Price upperBound)
	{
		return integer.between(lowerBound.store(), upperBound.store());
	}

	private static Integer store(final Price p)
	{
		return p!=null ? p.store() : null;
	}

	@Override
	public final String toString()
	{
		return integer.toString();
	}
}

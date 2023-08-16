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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Condition;
import com.exedio.cope.Join;

final class PriceBindFunction implements PriceFunction
{
	private final PriceField field;
	private final Join join;

	PriceBindFunction(final PriceField field, final Join join)
	{
		this.field = field;
		this.join = requireNonNull(join, "join");
	}

	@Override
	public Condition isNull()
	{
		return field.isNull().bind(join);
	}

	@Override
	public Condition isNotNull()
	{
		return field.isNotNull().bind(join);
	}

	@Override
	public Condition equal(final Price value)
	{
		return field.equal(value).bind(join);
	}

	@Override
	public Condition notEqual(final Price value)
	{
		return field.notEqual(value).bind(join);
	}

	@Override
	public Condition less(final Price value)
	{
		return field.less(value).bind(join);
	}

	@Override
	public Condition lessOrEqual(final Price value)
	{
		return field.lessOrEqual(value).bind(join);
	}

	@Override
	public Condition greater(final Price value)
	{
		return field.greater(value).bind(join);
	}

	@Override
	public Condition greaterOrEqual(final Price value)
	{
		return field.greaterOrEqual(value).bind(join);
	}

	@Override
	public Condition between(final Price lowerBound, final Price upperBound)
	{
		return field.between(lowerBound, upperBound).bind(join);
	}

	@Override
	public String toString()
	{
		return field.getInt().bind(join).toString();
	}
}

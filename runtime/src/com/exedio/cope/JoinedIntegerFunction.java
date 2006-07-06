/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.function.PlusView;
import com.exedio.cope.search.SumAggregate;

public final class JoinedIntegerFunction extends JoinedFunction<Integer> implements IntegerFunction // TODO SOON rename to BindIntegerFunction
{
	final IntegerFunction integerFunction;
	
	/**
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see IntegerFunction#bind(Join)
	 */
	public JoinedIntegerFunction(final IntegerFunction function, final Join join)
	{
		super(function, join);
		this.integerFunction = function;
	}
	
	// convenience methods for conditions and views ---------------------------------

	/**
	 * Return this.
	 * It makes no sense wrapping a JoinedFunction into another JoinedFunction,
	 * because the inner JoinedFunction &quot;wins&quot;.
	 * @see JoinedFunction#bind(Join)
	 */
	@Override
	public JoinedIntegerFunction bind(final Join join)
	{
		return this;
	}
	
	public final PlusView plus(final IntegerFunction other)
	{
		return new PlusView(new IntegerFunction[]{this, other});
	}

	/**
	 * @deprecated renamed to {@link #plus(IntegerFunction)}.
	 */
	@Deprecated
	public final PlusView sum(final IntegerFunction other)
	{
		return plus(other);
	}

	public final SumAggregate<Integer> sum()
	{
		return new SumAggregate<Integer>(this);
	}
}

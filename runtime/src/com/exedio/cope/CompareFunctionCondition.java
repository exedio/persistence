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


public final class CompareFunctionCondition<E> extends Condition
{
	private final CompareCondition.Operator operator;
	private final Function<E> left;
	private final Function<E> right;

	/**
	 * Creates a new CompareFunctionCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see com.exedio.cope.Function#equal(Function)
	 * @see com.exedio.cope.Function#less(Function)
	 * @see com.exedio.cope.Function#lessOrEqual(Function)
	 * @see com.exedio.cope.Function#greater(Function)
	 * @see com.exedio.cope.Function#greaterOrEqual(Function)
	 */
	public CompareFunctionCondition(final CompareCondition.Operator operator, final Function<E> left, final Function<E> right)
	{
		if(operator==null)
			throw new NullPointerException();
		if(left==null)
			throw new NullPointerException("left function must not be null");
		if(right==null)
			throw new NullPointerException("right function must not be null");

		this.operator = operator;
		this.left = left;
		this.right = right;
	}
	
	@Override
	void append(final Statement bf)
	{
		bf.append(left, (Join)null).
			append(operator.sql).
			append(right, (Join)null);
	}

	@SuppressWarnings("deprecation") // OK: For internal use within COPE only
	@Override
	void check(final TC tc)
	{
		left.check(tc, null);
		right.check(tc, null);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof CompareFunctionCondition))
			return false;
		
		final CompareFunctionCondition o = (CompareFunctionCondition)other;
		
		return operator.equals(o.operator) && left.equals(o.left) && right.equals(o.right);
	}
	
	@Override
	public int hashCode()
	{
		return operator.hashCode() ^ left.hashCode() ^ right.hashCode() ^ 286438162;
	}

	@Override
	public String toString()
	{
		return left.toString() + operator.sql + right.toString();
	}

	@Override
	String toStringForQueryKey()
	{
		return toString();
	}
}

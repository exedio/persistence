/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

public final class CompareCondition<E> extends Condition
{
	private final CompareFunctionCondition.Operator operator;
	private final Function<E> left;
	private final E right;

	/**
	 * Creates a new CompareCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see com.exedio.cope.Function#equal(Object)
	 * @see com.exedio.cope.Function#notEqual(Object)
	 * @see com.exedio.cope.Function#less(Object)
	 * @see com.exedio.cope.Function#lessOrEqual(Object)
	 * @see com.exedio.cope.Function#greater(Object)
	 * @see com.exedio.cope.Function#greaterOrEqual(Object)
	 */
	public CompareCondition(final CompareFunctionCondition.Operator operator, final Function<E> left, final E right)
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
			appendParameter(left, right);
	}

	@Override
	void check(final TC tc)
	{
		Cope.check(left, tc, null);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof CompareCondition))
			return false;
		
		final CompareCondition o = (CompareCondition)other;
		
		return operator.equals(o.operator) && left.equals(o.left) && right.equals(o.right);
	}
	
	@Override
	public int hashCode()
	{
		return operator.hashCode() ^ left.hashCode() ^ right.hashCode() ^ 918276;
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type defaultType)
	{
		left.toString(bf, defaultType);
		bf.append(operator.sql).
			append('\'').
			append(toStringForValue(right, key)).
			append('\'');
	}
}

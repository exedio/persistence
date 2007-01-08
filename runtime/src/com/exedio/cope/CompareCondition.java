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


public final class CompareCondition<E> extends Condition
{
	private final Operator operator;
	private final Function<E> function;
	private final E value;

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
	public CompareCondition(final Operator operator, final Function<E> function, final E value)
	{
		this.operator = operator;
		this.function = function;
		this.value = value;

		if(operator==null)
			throw new NullPointerException();
		if(function==null)
			throw new NullPointerException();
		if(value==null)
			throw new NullPointerException();
	}
	
	@Override
	void append(final Statement bf)
	{
		bf.append(function, (Join)null).
			append(operator.sql).
			appendParameter(function, value);
	}

	@Override
	void check(final TC tc)
	{
		Cope.check(function, tc, null);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof CompareCondition))
			return false;
		
		final CompareCondition o = (CompareCondition)other;
		
		return operator.equals(o.operator) && function.equals(o.function) && value.equals(o.value);
	}
	
	@Override
	public int hashCode()
	{
		return operator.hashCode() ^ function.hashCode() ^ value.hashCode() ^ 918276;
	}

	@Override
	public String toString()
	{
		return function.toString() + operator.sql + '\'' + value + '\'';
	}

	@Override
	String toStringForQueryKey()
	{
		return function.toString() + operator.sql + '\'' + toStringForQueryKey(value) + '\'';
	}

	public static enum Operator
	{
		Equal("="),
		NotEqual("<>"),
		Less("<"),
		LessEqual("<="),
		Greater(">"),
		GreaterEqual(">=");
		
		final String sql;
		
		Operator(final String sql)
		{
			this.sql = sql;
		}
	}
}

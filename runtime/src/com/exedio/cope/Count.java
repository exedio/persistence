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

import java.util.function.Consumer;

/**
 * Use only as select in query using groupBy
 * <p>
 * Grouping functionality is 'beta' - API may change
 */
public final class Count implements Selectable<Integer>
{
	private static final long serialVersionUID = 1l;

	@Override
	public Class<Integer> getValueClass()
	{
		return Integer.class;
	}

	@Override
	public SelectType<Integer> getValueType()
	{
		return SimpleSelectType.INTEGER;
	}

	@Override
	public Type<?> getType()
	{
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public void toString( final StringBuilder bf, final Type<?> defaultType )
	{
		bf.append( "count(*)" );
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	@Override
	public void check( final TC tc, final Join join )
	{
		// TODO
		// probably: nothing to do here, since there are no sources
	}

	@Override
	public void acceptFieldsCovered(final Consumer<Field<?>> consumer)
	{
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	@Override
	public void append( final Statement bf, final Join join )
	{
		bf.append("COUNT(*)");
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	@Override
	public void appendSelect( final Statement bf, final Join join )
	{
		bf.append("COUNT(*)");
	}

	// convenience methods for conditions and views ---------------------------------

	public Condition equal(final Integer value)
	{
		return value!=null ? new CompareCondition<>(CompareFunctionCondition.Operator.Equal, this, value) : new IsNullCondition<>(this, false);
	}

	public Condition notEqual(final Integer value)
	{
		return value!=null ? new CompareCondition<>(CompareFunctionCondition.Operator.NotEqual, this, value) : new IsNullCondition<>(this, true);
	}

	public CompareCondition<Integer> less(final Integer value)
	{
		return new CompareCondition<>(CompareFunctionCondition.Operator.Less, this, value);
	}

	public CompareCondition<Integer> lessOrEqual(final Integer value)
	{
		return new CompareCondition<>(CompareFunctionCondition.Operator.LessEqual, this, value);
	}

	public CompareCondition<Integer> greater(final Integer value)
	{
		return new CompareCondition<>(CompareFunctionCondition.Operator.Greater, this, value);
	}

	public CompareCondition<Integer> greaterOrEqual(final Integer value)
	{
		return new CompareCondition<>(CompareFunctionCondition.Operator.GreaterEqual, this, value);
	}

	public Condition between(final Integer lowerBound, final Integer upperBound)
	{
		return greaterOrEqual(lowerBound).and(lessOrEqual(upperBound));
	}
}

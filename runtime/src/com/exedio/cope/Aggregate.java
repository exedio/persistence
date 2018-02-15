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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.CompareFunctionCondition.Operator;

public abstract class Aggregate<E> implements Selectable<E>
{
	private static final long serialVersionUID = 1l;

	final Function<?> source;
	private final String name;
	private final String sqlPrefix;
	private final SelectType<E> valueType;

	protected Aggregate(
			final Function<?> source,
			final String name, final String sqlName,
			final SelectType<E> valueType)
	{
		this.source = requireNonNull(source, "source");
		this.name = requireNonNull(name, "name");
		this.sqlPrefix = requireNonNull(sqlName, "sqlName") + '(';
		this.valueType = requireNonNull(valueType);
	}

	public final Function<?> getSource()
	{
		return source;
	}

	public final String getName()
	{
		return name;
	}

	@Override
	public final Class<E> getValueClass()
	{
		return valueType.getJavaClass();
	}

	@Override
	public final SelectType<E> getValueType()
	{
		return valueType;
	}

	@Override
	public final Type<?> getType()
	{
		return source.getType();
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public final void check(final TC tc, final Join join)
	{
		source.check(tc, join);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public void append(final Statement bf, final Join join)
	{
		bf.append(sqlPrefix).
			append(source, join).
			append(')');
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public void appendSelect(final Statement bf, final Join join)
	{
		bf.append(sqlPrefix).
			appendSelect(source, join).
			append(')');
	}

	@Override
	public final boolean equals(final Object other)
	{
		if(!(other instanceof Aggregate<?>))
			return false;

		final Aggregate<?> a = (Aggregate<?>)other;

		return name.equals(a.name) && source.equals(a.source);
	}

	@Override
	public final int hashCode()
	{
		return name.hashCode() ^ source.hashCode();
	}

	@Override
	public final String toString()
	{
		return name + '(' + source + ')';
	}

	@Override
	public final void toString(final StringBuilder bf, final Type<?> defaultType)
	{
		bf.append(name).
			append('(');
		source.toString(bf, defaultType);
		bf.append(')');
	}

	// convenience methods for conditions and views ---------------------------------

	public final Condition equal(final E value)
	{
		return value!=null ? new CompareCondition<>(Operator.Equal, this, value) : new IsNullCondition<>(this, false);
	}

	public final Condition notEqual(final E value)
	{
		return value!=null ? new CompareCondition<>(Operator.NotEqual, this, value) : new IsNullCondition<>(this, true);
	}

	public final CompareCondition<E> less(final E value)
	{
		return new CompareCondition<>(Operator.Less, this, value);
	}

	public final CompareCondition<E> lessOrEqual(final E value)
	{
		return new CompareCondition<>(Operator.LessEqual, this, value);
	}

	public final CompareCondition<E> greater(final E value)
	{
		return new CompareCondition<>(Operator.Greater, this, value);
	}

	public final CompareCondition<E> greaterOrEqual(final E value)
	{
		return new CompareCondition<>(Operator.GreaterEqual, this, value);
	}

	public Condition between(final E lowerBound, final E upperBound)
	{
		return greaterOrEqual(lowerBound).and(lessOrEqual(upperBound));
	}
}

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

import static com.exedio.cope.CompareCondition.isComparableCheckEnabled;
import static com.exedio.cope.CompareCondition.topItemClass;
import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

public final class CompareFunctionCondition<E> extends Condition
{
	private static final long serialVersionUID = 1l;

	private final Operator operator;
	private final Function<E> left;
	private final Function<? extends E> right;

	static <E> CompareFunctionCondition<E> create(
			final Operator operator,
			final Function<E> left,
			final Function<? extends E> right)
	{
		return new CompareFunctionCondition<>(operator, left, right);
	}

	/**
	 * Creates a new CompareFunctionCondition.
	 * @deprecated
	 * Instead of using this constructor directly,
	 * use the convenience methods.
	 * @see com.exedio.cope.Function#equal(Function)
	 * @see com.exedio.cope.Function#notEqual(Function)
	 * @see com.exedio.cope.Function#less(Function)
	 * @see com.exedio.cope.Function#lessOrEqual(Function)
	 * @see com.exedio.cope.Function#greater(Function)
	 * @see com.exedio.cope.Function#greaterOrEqual(Function)
	 */
	@Deprecated
	public CompareFunctionCondition(
			final Operator operator,
			final Function<E> left,
			final Function<? extends E> right)
	{
		this.operator = requireNonNull(operator, "operator");
		this.left = requireNonNull(left, "left");
		this.right = requireNonNull(right, "right");

		if(!isComparable(left.getValueClass(), right.getValueClass()) &&
			isComparableCheckEnabled(left))
			throw new IllegalArgumentException(
					left + " not comparable to " + right);
	}

	@SuppressWarnings("RedundantIfStatement")
	private static boolean isComparable(
			final Class<?> left,
			final Class<?> right)
	{
		if(left==right)
			return true;

		if(Item.class.isAssignableFrom(left) &&
			Item.class.isAssignableFrom(right) &&
			topItemClass(left)==topItemClass(right))
			return true;

		return false;
	}

	@Override
	void append(final Statement bf)
	{
		bf.append(left).
			append(operator.sql).
			append(right);
	}

	@Override
	void requireSupportForGetTri() throws UnsupportedGetException
	{
		left.requireSupportForGet();
		right.requireSupportForGet();
	}

	@Override
	Trilean getTri(final FieldValues item) throws UnsupportedGetException
	{
		return operator.evaluate(left.get(item), right.get(item));
	}

	@Override
	void check(final TC tc)
	{
		Cope.check(left,  tc, null);
		Cope.check(right, tc, null);
	}

	@Override
	public void acceptFieldsCovered(final Consumer<Field<?>> consumer)
	{
		left .acceptFieldsCovered(consumer);
		right.acceptFieldsCovered(consumer);
	}

	@Override
	CompareFunctionCondition<E> copy(final CopyMapper mapper)
	{
		return new CompareFunctionCondition<>(operator, mapper.getS(left), mapper.getS(right));
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof CompareFunctionCondition<?>))
			return false;

		final CompareFunctionCondition<?> o = (CompareFunctionCondition<?>)other;

		return operator==o.operator && left.equals(o.left) && right.equals(o.right);
	}

	@Override
	public int hashCode()
	{
		return
				operator.hashCode() ^
				left.hashCode() ^
				(right.hashCode()<<2) ^ // not commutative
				286438162;
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		left.toString(bf, defaultType);
		bf.append(operator.sql);
		right.toString(bf, defaultType);
	}

	public enum Operator
	{
		Equal("=")
		{
			@Override
			boolean evaluateNotNull(final Comparable<Comparable<?>> left, final Comparable<Comparable<?>> right)
			{
				return left.equals(right);
			}
		},
		NotEqual("<>")
		{
			@Override
			boolean evaluateNotNull(final Comparable<Comparable<?>> left, final Comparable<Comparable<?>> right)
			{
				return !left.equals(right);
			}
		},
		Less("<")
		{
			@Override
			boolean evaluateNotNull(final Comparable<Comparable<?>> left, final Comparable<Comparable<?>> right)
			{
				return left.compareTo(right)<0;
			}
		},
		LessEqual("<=")
		{
			@Override
			boolean evaluateNotNull(final Comparable<Comparable<?>> left, final Comparable<Comparable<?>> right)
			{
				return left.compareTo(right)<=0;
			}
		},
		Greater(">")
		{
			@Override
			boolean evaluateNotNull(final Comparable<Comparable<?>> left, final Comparable<Comparable<?>> right)
			{
				return left.compareTo(right)>0;
			}
		},
		GreaterEqual(">=")
		{
			@Override
			boolean evaluateNotNull(final Comparable<Comparable<?>> left, final Comparable<Comparable<?>> right)
			{
				return left.compareTo(right)>=0;
			}
		};

		final String sql;

		Operator(final String sql)
		{
			this.sql = sql;
		}

		@SuppressWarnings("unchecked")
		final Trilean evaluate(final Object left, final Object right)
		{
			if(left==null || right==null)
				return Trilean.Null;

			return
					Trilean.valueOf(evaluateNotNull(
						(Comparable<Comparable<?>>)left, // TODO make casts to Comparable redundant
						(Comparable<Comparable<?>>)right));
		}

		abstract boolean evaluateNotNull(Comparable<Comparable<?>> left, Comparable<Comparable<?>> right);
	}
}

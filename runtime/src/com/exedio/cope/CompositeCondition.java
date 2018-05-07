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

import static com.exedio.cope.misc.Check.requireNonEmptyAndCopy;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;

public final class CompositeCondition extends Condition
{
	private static final long serialVersionUID = 1l;
	private static final Condition[] EMPTY_CONDITION_ARRAY = new Condition[0];

	public final Operator operator;
	final Condition[] conditions;

	/**
	 * @deprecated Use {@link Cope#and(List)} or {@link Cope#or(List)} instead
	 * @throws NullPointerException if <tt>conditions==null</tt>
	 * @throws IllegalArgumentException if <tt>conditions.size()==0</tt>
	 */
	@Deprecated
	public CompositeCondition(
			final Operator operator,
			final List<? extends Condition> conditions)
	{
		this(operator, conditions.toArray(EMPTY_CONDITION_ARRAY));
	}

	/**
	 * @deprecated Use {@link Cope#and(Condition[])} or {@link Cope#or(Condition[])} instead
	 * @throws NullPointerException if <tt>conditions==null</tt>
	 * @throws IllegalArgumentException if <tt>conditions.length==0</tt>
	 */
	@Deprecated
	public CompositeCondition(
			final Operator operator,
			final Condition... conditions)
	{
		this.operator = requireNonNull(operator, "operator");
		this.conditions = requireNonEmptyAndCopy(conditions, "conditions");

		for(int i = 0; i<conditions.length; i++)
		{
			final Condition c = conditions[i];
			if(c instanceof Literal)
				throw new IllegalArgumentException("conditions" + '[' + i + ']' + " must not be a literal, but was " + c);
		}
	}

	@Override
	void append(final Statement bf)
	{
		bf.append('(');
		conditions[0].append(bf);
		for(int i = 1; i<conditions.length; i++)
		{
			bf.append(')');
			bf.append(operator.sql);
			bf.append('(');
			conditions[i].append(bf);
		}
		bf.append(')');
	}

	@Override
	Trilean getTri(final Item item)
	{
		final Trilean absorber = operator.absorber.valueTri;
		Trilean resultWithoutAbsorber = operator.identity.valueTri;
		for(final Condition condition : conditions)
		{
			final Trilean value = condition.getTri(item);
			if(value==absorber)
				return absorber;
			if(value==Trilean.Null)
				resultWithoutAbsorber = Trilean.Null;
		}
		return resultWithoutAbsorber;
	}

	@Override
	void check(final TC tc)
	{
		for(final Condition condition : conditions)
			condition.check(tc);
	}

	@Override
	@SuppressWarnings("deprecation") // needed for idea
	CompositeCondition copy(final CopyMapper mapper)
	{
		final Condition[] c = new Condition[conditions.length];
		//noinspection Java8ArraySetAll OK: performance
		for(int i = 0; i<c.length; i++)
			c[i] = conditions[i].copy(mapper);

		return new CompositeCondition(operator, c);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof CompositeCondition))
			return false;

		final CompositeCondition o = (CompositeCondition)other;

		if(operator!=o.operator || conditions.length!=o.conditions.length)
			return false;

		for(int i = 0; i<conditions.length; i++)
		{
			if(!conditions[i].equals(o.conditions[i]))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = operator.hashCode();

		for(final Condition condition : conditions)
			result = (31*result) + condition.hashCode(); // may not be commutative

		return result;
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		bf.append('(');
		conditions[0].toString(bf, key, defaultType);
		for(int i = 1; i<conditions.length; i++)
		{
			bf.append(' ').
				append(operator).
				append(' ');
			conditions[i].toString(bf, key, defaultType);
		}
		bf.append(')');
	}

	@SafeVarargs
	@SuppressWarnings("deprecation") // needed for idea
	public static <E> Condition in(final Function<E> function, final E... values)
	{
		switch(values.length)
		{
			case 0:
				return FALSE;
			case 1:
				return function.equal(values[0]);
			default:
				final Condition[] result = new Condition[values.length];

				int i = 0;
				for(final E value : values)
					result[i++] = function.equal(value);

				return new CompositeCondition(Operator.OR, result);
		}
	}

	@SuppressWarnings("deprecation") // needed for idea
	public static <E> Condition in(final Function<E> function, final Collection<? extends E> values)
	{
		switch(values.size())
		{
			case 0:
				return FALSE;
			case 1:
				return function.equal(values.iterator().next());
			default:
				final Condition[] result = new Condition[values.size()];

				int i = 0;
				for(final E value : values)
					result[i++] = function.equal(value);

				return new CompositeCondition(Operator.OR, result);
		}
	}

	public enum Operator
	{
		AND(" AND ", FALSE, TRUE),
		OR (" OR ",  TRUE, FALSE);

		final String sql;
		final Literal absorber;
		final Literal identity;

		Operator(
				final String sql,
				final Literal absorber,
				final Literal identity)
		{
			this.sql = sql;
			this.absorber = absorber;
			this.identity = identity;
		}
	}


	static Condition composite(
			final Operator operator,
			final Condition left,
			final Condition right)
	{
		if(left instanceof Literal)
			if(right instanceof Literal)
				return valueOf(
					(operator==Operator.AND)
					? ( ((Literal)left).value && ((Literal)right).value )
					: ( ((Literal)left).value || ((Literal)right).value ));
			else
				return compositeLiteral(operator, (Literal)left, right);
		else
			if(right instanceof Literal)
				return compositeLiteral(operator, (Literal)right, left);
			else
				return compositeFlattening(operator, left, right);
	}

	private static Condition compositeLiteral(
			final Operator operator,
			final Literal literal,
			final Condition other)
	{
		requireNonNull(other, "other");
		return operator.absorber==literal ? literal : other;
	}

	@SuppressWarnings("deprecation") // needed for idea
	private static Condition compositeFlattening(
			final Operator operator,
			final Condition leftCondition,
			final Condition rightCondition)
	{
		if(leftCondition instanceof CompositeCondition && ((CompositeCondition)leftCondition).operator==operator)
		{
			final CompositeCondition left = (CompositeCondition)leftCondition;

			if(rightCondition instanceof CompositeCondition && ((CompositeCondition)rightCondition).operator==operator)
			{
				final CompositeCondition right = (CompositeCondition)rightCondition;

				final Condition[] c = new Condition[left.conditions.length + right.conditions.length];
				System.arraycopy(left.conditions, 0, c, 0, left.conditions.length);
				System.arraycopy(right.conditions, 0, c, left.conditions.length, right.conditions.length);
				return new CompositeCondition(operator, c);
			}
			else
			{
				final Condition[] c = new Condition[left.conditions.length + 1];
				System.arraycopy(left.conditions, 0, c, 0, left.conditions.length);
				c[left.conditions.length] = rightCondition;
				return new CompositeCondition(operator, c);
			}
		}
		else
		{
			if(rightCondition instanceof CompositeCondition && ((CompositeCondition)rightCondition).operator==operator)
			{
				final CompositeCondition right = (CompositeCondition)rightCondition;

				final Condition[] c = new Condition[1 + right.conditions.length];
				c[0] = leftCondition;
				System.arraycopy(right.conditions, 0, c, 1, right.conditions.length);
				return new CompositeCondition(operator, c);
			}
			else
			{
				return new CompositeCondition(operator, leftCondition, rightCondition);
			}
		}
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	static Condition composite(final Operator operator, final List<? extends Condition> conditions)
	{
		return composite(operator,
				requireNonNull(conditions, "conditions").toArray(new Condition[conditions.size()]));
	}

	@SuppressWarnings("deprecation") // needed for idea
	static Condition composite(final Operator operator, final Condition[] conditions)
	{
		requireNonNull(conditions, "conditions");

		int filtered = 0;

		for(int i = 0; i<conditions.length; i++)
		{
			final Condition c = conditions[i];
			if(c==null)
				throw new NullPointerException("conditions" + '[' + i + ']');

			if(c instanceof Literal)
			{
				if(operator.absorber==c)
					return c;
				else
					filtered++;
			}
		}

		final Condition[] filteredConditions;
		if(filtered==0)
		{
			filteredConditions = conditions;
		}
		else
		{
			filteredConditions = new Condition[conditions.length-filtered];

			int j = 0;
			for(final Condition c : conditions)
				if(operator.identity!=c)
					filteredConditions[j++] = c;

			assert j==filteredConditions.length;
		}

		switch(filteredConditions.length)
		{
			case 0:
				return operator.identity;
			case 1:
				return filteredConditions[0];
			default:
				return new CompositeCondition(operator, filteredConditions);
		}
	}
}

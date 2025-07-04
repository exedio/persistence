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

import static com.exedio.cope.util.Check.requireNonEmptyAndCopy;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.misc.Arrays;
import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CompositeCondition extends Condition
{
	@Serial
	private static final long serialVersionUID = 1l;
	private static final Condition[] EMPTY_CONDITION_ARRAY = new Condition[0];

	public final Operator operator;
	final Condition[] conditions;

	/**
	 * @deprecated Use {@link Cope#and(List)} or {@link Cope#or(List)} instead
	 * @throws NullPointerException if {@code conditions==null}
	 * @throws IllegalArgumentException if {@code conditions.size()==0}
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
	 * @throws NullPointerException if {@code conditions==null}
	 * @throws IllegalArgumentException if {@code conditions.length==0}
	 */
	@Deprecated
	public CompositeCondition(
			final Operator operator,
			final Condition... conditions)
	{
		this.operator = requireNonNull(operator, "operator");
		this.conditions = requireNonEmptyAndCopy(conditions, "conditions");
		if(conditions.length<2)
			throw new IllegalArgumentException(
					"conditions must contain more than one element");

		for(int i = 0; i<conditions.length; i++)
		{
			final Condition c = conditions[i];
			if(c instanceof Literal)
				throw new IllegalArgumentException("conditions" + '[' + i + ']' + " must not be a literal, but was " + c);
			if(c instanceof CompositeCondition &&
				((CompositeCondition)c).operator==operator)
				throw new IllegalArgumentException(
						"conditions[" + i + ']' + " must not be an " + operator + ", " +
						"but was " + c);
		}
	}

	@Override
	void appendAfterFrom(final Statement statement)
	{
		for(final Condition condition : conditions)
			condition.appendAfterFrom(statement);
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
	void requireSupportForGetTri() throws UnsupportedGetException
	{
		for(final Condition condition : conditions)
			condition.requireSupportForGetTri();
	}

	@Override
	Trilean getTri(final FieldValues item) throws UnsupportedGetException
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
	public void forEachFieldCovered(final Consumer<Field<?>> action)
	{
		for(final Condition condition : conditions)
			condition.forEachFieldCovered(action);
	}

	@Override
	CompositeCondition copy(final CopyMapper mapper)
	{
		final Condition[] c = new Condition[conditions.length];
		//noinspection Java8ArraySetAll OK: performance
		for(int i = 0; i<c.length; i++)
			c[i] = conditions[i].copy(mapper);

		return new CompositeCondition(operator, c);
	}

	@Override
	public Condition bind(final Join join)
	{
		final Condition[] c = new Condition[conditions.length];
		//noinspection Java8ArraySetAll OK: performance
		for(int i = 0; i<c.length; i++)
			c[i] = conditions[i].bind(join);

		return new CompositeCondition(operator, c);
	}

	@Override
	public Condition not()
	{
		// De Morgan's laws https://en.wikipedia.org/wiki/De_Morgan%27s_laws
		final Condition[] c = new Condition[conditions.length];
		//noinspection Java8ArraySetAll OK: performance
		for(int i = 0; i<c.length; i++)
			c[i] = conditions[i].not();

		return new CompositeCondition(operator.not(), c);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof final CompositeCondition o))
			return false;

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
			bf.append(operator.forToString);
			conditions[i].toString(bf, key, defaultType);
		}
		bf.append(')');
	}

	@SafeVarargs
	@SuppressWarnings( "varargs" )
	public static <E> Condition in(final Function<E> function, final E... values)
	{
		switch(values.length)
		{
			case 0 -> {
				return ofFalse();
			} case 1 -> {
				return function.is(values[0]);
			} default -> {
				if (Stream.of(values).anyMatch(Objects::isNull))
				{
					return getConditionForInWithNull(function, Stream.of(values).filter(Objects::nonNull).distinct().toList());
				}
				else
				{
					return new InCondition<>(function, false, List.of(values));
				}
			}
		}
	}

	public static <E> Condition in(final Function<E> function, final Collection<? extends E> values)
	{
		switch(values.size())
		{
			case 0 -> {
				return ofFalse();
			} case 1 -> {
				return function.is(values.iterator().next());
			} default -> {
				boolean containsNull;
				try
				{
					containsNull = values.contains(null);
				}
				catch(final NullPointerException e)
				{
					containsNull = false;
				}
				if (containsNull)
				{
					return getConditionForInWithNull(function, values.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList()));
				}
				else
				{
					return new InCondition<>(function, false, List.copyOf(values));
				}
			}
		}
	}

	private static <E> Condition getConditionForInWithNull(final Function<E> function, final List<E> nonNullValues)
	{
		return switch(nonNullValues.size())
		{
			case 0 ->
				function.isNull();
			case 1 ->
				composite(Operator.OR, function.isNull(), function.is(nonNullValues.get(0)));
			default ->
				composite(Operator.OR, function.isNull(), new InCondition<>(function, false, nonNullValues));
		};
	}

	public enum Operator
	{
		@SuppressWarnings("deprecation") // OK: deprecation will be dropped when no longer public
		AND(" and ", " AND ", FALSE, TRUE),
		@SuppressWarnings("deprecation") // OK: deprecation will be dropped when no longer public
		OR (" or ",  " OR ",  TRUE, FALSE);

		final String forToString;
		final String sql;
		final Literal absorber;
		final Literal identity;

		Operator(
				final String forToString,
				final String sql,
				final Literal absorber,
				final Literal identity)
		{
			this.forToString = forToString;
			this.sql = sql;
			this.absorber = absorber;
			this.identity = identity;
		}

		Operator not()
		{
			return this==AND ? OR : AND;
		}
	}


	static Condition composite(
			final Operator operator,
			final Condition left,
			final Condition right)
	{
		if(left instanceof Literal)
			if(right instanceof Literal)
				return of(
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

	private static Condition compositeFlattening(
			final Operator operator,
			final Condition left,
			final Condition right)
	{
		if(left instanceof CompositeCondition && ((CompositeCondition)left).operator==operator)
		{
			final Condition[] leftConditions = ((CompositeCondition)left).conditions;

			if(right instanceof CompositeCondition && ((CompositeCondition)right).operator==operator)
			{
				final Condition[] rightConditions = ((CompositeCondition)right).conditions;

				return new CompositeCondition(operator, Arrays.append(leftConditions, rightConditions));
			}
			else
			{
				return new CompositeCondition(operator, Arrays.append(leftConditions, right));
			}
		}
		else
		{
			if(right instanceof CompositeCondition && ((CompositeCondition)right).operator==operator)
			{
				final Condition[] rightConditions = ((CompositeCondition)right).conditions;

				return new CompositeCondition(operator, Arrays.prepend(left, rightConditions));
			}
			else
			{
				return new CompositeCondition(operator, left, right);
			}
		}
	}

	@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
	static Condition composite(final Operator operator, final List<? extends Condition> conditions)
	{
		return composite(operator,
				requireNonNull(conditions, "conditions").toArray(new Condition[conditions.size()]));
	}

	static Condition composite(final Operator operator, final Condition[] conditions)
	{
		requireNonNull(conditions, "conditions");

		int filtered = 0, flattened = 0;

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
			else if(c instanceof final CompositeCondition cc &&
					cc.operator==operator)
			{
				flattened += cc.conditions.length - 1; // the "- 1" is the CompositeCondition dropped for its nested conditions
			}
		}

		final Condition[] filteredConditions;
		if(filtered==0 && flattened==0)
		{
			filteredConditions = conditions;
		}
		else
		{
			filteredConditions = new Condition[conditions.length-filtered+flattened];

			int j = 0;
			for(final Condition c : conditions)
				if(operator.identity!=c)
				{
					if(c instanceof final CompositeCondition cc &&
						cc.operator==operator)
					{
						for(final Condition ccc : cc.conditions)
							filteredConditions[j++] = ccc;
						continue;
					}
					filteredConditions[j++] = c;
				}

			assert j==filteredConditions.length;
		}

		return switch(filteredConditions.length)
		{
			case 0 ->
				operator.identity;
			case 1 ->
				filteredConditions[0];
			default ->
				new CompositeCondition(operator, filteredConditions);
		};
	}
}

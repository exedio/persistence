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

import java.util.Date;
import java.util.function.Consumer;

public final class CompareCondition<E> extends Condition
{
	private static final long serialVersionUID = 1l;

	private final CompareFunctionCondition.Operator operator;
	private final Selectable<E> left;
	@SuppressWarnings("NonSerializableFieldInSerializableClass") // OK: container is serializable if part is serializable
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
	public CompareCondition(
			final CompareFunctionCondition.Operator operator,
			final Selectable<E> left,
			final E right)
	{
		this.operator = requireNonNull(operator, "operator");
		this.left = requireNonNull(left, "left");
		this.right = requireNonNull(right, "right");

		if(!isComparable(left.getValueClass(), right.getClass(), right) &&
			isComparableCheckEnabled(left))
		{
			final StringBuilder bf = new StringBuilder();
			bf.append(left).
				append(" not comparable to '");
			toStringForValue(bf, right, false);
			bf.append("' (").
				append(right.getClass().getName()).
				append(')');
			throw new IllegalArgumentException(bf.toString());
		}
	}

	@SuppressWarnings("RedundantIfStatement")
	private static boolean isComparable(
			final Class<?> left,
			final Class<?> right,
			final Object rightValue)
	{
		if(left==right)
			return true;

		if(rightValue instanceof Enum &&
			left==((Enum<?>)rightValue).getDeclaringClass())
			return true;

		if(Item.class.isAssignableFrom(left) &&
			Item.class.isAssignableFrom(right) &&
			topItemClass(left)==topItemClass(right))
			return true;

		return false;
	}

	static Class<?> topItemClass(Class<?> valueClass)
	{
		do
		{
			final Class<?> superclass = valueClass.getSuperclass();
			if(superclass==Item.class)
				return valueClass;
			valueClass = superclass;
		}
		while(true);
	}

	static boolean isComparableCheckEnabled(final Selectable<?> left)
	{
		final Type<?> type;
		try
		{
			type = left.getType();
		}
		catch(final IllegalStateException e)
		{
			return true;
		}
		final Model model;
		try
		{
			model = type.getModel();
		}
		catch(final IllegalStateException e)
		{
			return true;
		}
		return
				!model.isConnected() ||
				model.getConnectProperties().comparableCheck;
	}

	@Override
	void append(final Statement bf)
	{
		bf.append(left).
			append(operator.sql).
			appendParameterAny(right);
	}

	@Override
	void supportsGetTri()
	{
		// TODO do something nicer
		if(!(left instanceof Function))
			throw new IllegalArgumentException("not supported for non-function: " + left);
	}

	@Override
	Trilean getTri(final FieldValues item)
	{
		return operator.evaluate(((Function<E>)left).get(item), right);
	}

	@Override
	void check(final TC tc)
	{
		Cope.check(left, tc, null);
	}

	@Override
	void acceptFieldsCovered(final Consumer<Field<?>> consumer)
	{
		left.acceptFieldsCovered(consumer);
	}

	@Override
	CompareCondition<E> copy(final CopyMapper mapper)
	{
		return new CompareCondition<>(operator, mapper.getS(left), right);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof CompareCondition<?>))
			return false;

		final CompareCondition<?> o = (CompareCondition<?>)other;

		return operator==o.operator && left.equals(o.left) && right.equals(o.right);
	}

	@Override
	public int hashCode()
	{
		return operator.hashCode() ^ left.hashCode() ^ right.hashCode() ^ 918276;
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		left.toString(bf, defaultType);
		bf.append(operator.sql).
			append('\'');
		toStringForValue(bf, right, key);
		bf.append('\'');
	}

	private static void toStringForValue(final StringBuilder bf, final Object o, final boolean key)
	{
		if(o instanceof Item)
		{
			if(key)
				((Item)o).appendCopeID(bf);
			else
				bf.append(o);
		}
		else if(o instanceof Date)
			bf.append(key ? String.valueOf(((Date)o).getTime()) : DateField.format().format((Date)o));
		else
			bf.append(o);
	}
}

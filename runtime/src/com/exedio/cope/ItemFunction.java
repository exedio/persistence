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

public interface ItemFunction<E extends Item> extends Function<E>
{
	@Override
	Type<E> getValueType();

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	void appendType(Statement bf, Join join);

	/**
	 * @see #checkTypeColumn()
	 */
	boolean needsCheckTypeColumn();

	/**
	 * @see #needsCheckTypeColumn()
	 */
	long checkTypeColumnL();

	// convenience methods for conditions and views ---------------------------------

	@Override
	default BindItemFunction<E> bind(final Join join)
	{
		return new BindItemFunction<>(this, join);
	}

	default CompareFunctionCondition<?> equalTarget()
	{
		return equal(getValueType().thisFunction);
	}

	default CompareFunctionCondition<?> equalTarget(final Join targetJoin)
	{
		return equal(getValueType().castTypeExtends(targetJoin.getType()).thisFunction.bind(targetJoin));
	}

	// TODO allow Class<? extends Item> as well to be safely used in CheckConstraints
	default InstanceOfCondition<E> instanceOf(final Type<? extends E> type1)
	{
		return new InstanceOfCondition<>(this, false, type1);
	}

	default InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return new InstanceOfCondition<>(this, false, type1, type2);
	}

	default InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return new InstanceOfCondition<>(this, false, type1, type2, type3);
	}

	default InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<? extends E> type4)
	{
		return new InstanceOfCondition<>(this, false, type1, type2, type3, type4);
	}

	@SuppressWarnings("rawtypes")
	default InstanceOfCondition<E> instanceOf(final Type[] types)
	{
		return new InstanceOfCondition<>(this, false, types);
	}

	default InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1)
	{
		return new InstanceOfCondition<>(this, true, type1);
	}

	default InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return new InstanceOfCondition<>(this, true, type1, type2);
	}

	default InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return new InstanceOfCondition<>(this, true, type1, type2, type3);
	}

	default InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<? extends E> type4)
	{
		return new InstanceOfCondition<>(this, true, type1, type2, type3, type4);
	}

	@SuppressWarnings("rawtypes")
	default InstanceOfCondition<E> notInstanceOf(final Type[] types)
	{
		return new InstanceOfCondition<>(this, true, types);
	}

	long serialVersionUID = -3944156504239779975L;

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #checkTypeColumnL()} instead
	 */
	@Deprecated
	int checkTypeColumn();

	/**
	 * @deprecated Has been renamed to {@link #instanceOf(Type)}.
	 */
	@Deprecated
	default InstanceOfCondition<E> typeIn(final Type<? extends E> type1)
	{
		return instanceOf(type1);
	}

	/**
	 * @deprecated Has been renamed to {@link #instanceOf(Type,Type)}.
	 */
	@Deprecated
	default InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return instanceOf(type1, type2);
	}

	/**
	 * @deprecated Has been renamed to {@link #instanceOf(Type,Type,Type)}.
	 */
	@Deprecated
	default InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return instanceOf(type1, type2, type3);
	}

	/**
	 * @deprecated Has been renamed to {@link #instanceOf(Type,Type,Type,Type)}.
	 */
	@Deprecated
	default InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return instanceOf(type1, type2, type3, type4);
	}

	/**
	 * @deprecated Has been renamed to {@link #instanceOf(Type[])}.
	 */
	@Deprecated
	@SuppressWarnings("rawtypes")
	default InstanceOfCondition<E> typeIn(final Type[] types)
	{
		return instanceOf(types);
	}

	/**
	 * @deprecated Has been renamed to {@link #notInstanceOf(Type)}.
	 */
	@Deprecated
	default InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1)
	{
		return notInstanceOf(type1);
	}

	/**
	 * @deprecated Has been renamed to {@link #notInstanceOf(Type,Type)}.
	 */
	@Deprecated
	default InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return notInstanceOf(type1, type2);
	}

	/**
	 * @deprecated Has been renamed to {@link #notInstanceOf(Type,Type,Type)}.
	 */
	@Deprecated
	default InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return notInstanceOf(type1, type2, type3);
	}

	/**
	 * @deprecated Has been renamed to {@link #notInstanceOf(Type,Type,Type,Type)}.
	 */
	@Deprecated
	default InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return notInstanceOf(type1, type2, type3, type4);
	}

	/**
	 * @deprecated Has been renamed to {@link #notInstanceOf(Type[])}.
	 */
	@Deprecated
	@SuppressWarnings("rawtypes")
	default InstanceOfCondition<E> typeNotIn(final Type[] types)
	{
		return notInstanceOf(types);
	}
}

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

public interface ItemFunction<E extends Item> extends Function<E>
{
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
	int checkTypeColumn();
	
	// convenience methods for conditions and views ---------------------------------

	BindItemFunction<E> bind(Join join);
	
	CompareFunctionCondition equalTarget();
	CompareFunctionCondition equalTarget(Join targetJoin);
	
	InstanceOfCondition<E> instanceOf(final Type<? extends E> type1);
	InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2);
	InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3);
	InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4);
	InstanceOfCondition<E> instanceOf(final Type[] types);
	
	InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1);
	InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2);
	InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3);
	InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4);
	InstanceOfCondition<E> notInstanceOf(final Type[] types);

	/**
	 * @deprecated Has been renamed to {@link #instanceOf(Type)}.
	 */
	@Deprecated
	InstanceOfCondition<E> typeIn(final Type<? extends E> type1);
	/**
	 * @deprecated Has been renamed to {@link #instanceOf(Type,Type)}.
	 */
	@Deprecated
	InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2);
	/**
	 * @deprecated Has been renamed to {@link #instanceOf(Type,Type,Type)}.
	 */
	@Deprecated
	InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3);
	/**
	 * @deprecated Has been renamed to {@link #instanceOf(Type,Type,Type,Type)}.
	 */
	@Deprecated
	InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4);
	/**
	 * @deprecated Has been renamed to {@link #instanceOf(Type[])}.
	 */
	@Deprecated
	InstanceOfCondition<E> typeIn(final Type[] types);
	
	/**
	 * @deprecated Has been renamed to {@link #notInstanceOf(Type)}.
	 */
	@Deprecated
	InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1);
	/**
	 * @deprecated Has been renamed to {@link #notInstanceOf(Type,Type)}.
	 */
	@Deprecated
	InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2);
	/**
	 * @deprecated Has been renamed to {@link #notInstanceOf(Type,Type,Type)}.
	 */
	@Deprecated
	InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3);
	/**
	 * @deprecated Has been renamed to {@link #notInstanceOf(Type,Type,Type,Type)}.
	 */
	@Deprecated
	InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4);
	/**
	 * @deprecated Has been renamed to {@link #notInstanceOf(Type[])}.
	 */
	@Deprecated
	InstanceOfCondition<E> typeNotIn(final Type[] types);
}

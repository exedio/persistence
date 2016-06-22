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

import java.lang.reflect.AnnotatedElement;

public final class This<E extends Item> extends Feature
	implements ItemFunction<E>
{
	private static final long serialVersionUID = 1l;

	static final String NAME = "this";

	final Type<E> type;

	This(final Type<E> type)
	{
		assert type!=null;
		this.type = type;
	}

	@Override
	void mount(final Type<?> type, final String name, final AnnotatedElement annotationSource)
	{
		super.mount(type, name, annotationSource);
		assert this.type == type;
		assert NAME.equals(name);
	}

	@Override
	public E get(final Item item)
	{
		return type.cast(item);
	}

	@Override
	public Class<E> getValueClass()
	{
		return type.getJavaClass();
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void check(final TC tc, final Join join)
	{
		tc.check(this, join);
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void append(final Statement bf, final Join join)
	{
		bf.appendPK(type, join);
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void appendSelect(final Statement bf, final Join join)
	{
		final Type<?> selectType = getType();
		bf.appendPK(selectType, join);

		final IntegerColumn column = selectType.getTable().primaryKey;
		assert column.primaryKey;

		final StringColumn typeColumn = column.table.typeColumn;
		if(typeColumn!=null)
		{
			bf.append(',').
				append(typeColumn, join);
		}
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void appendType(final Statement bf, final Join join)
	{
		bf.append(Statement.assertTypeColumn(type.getTable().typeColumn, type), join);
	}

	@Deprecated // OK: for internal use within COPE only
	public void appendParameter(final Statement bf, final E value)
	{
		bf.appendParameter(value.pk);
	}

	@Override
	public Type<E> getValueType()
	{
		return type;
	}

	@Override
	public boolean needsCheckTypeColumn()
	{
		return type.needsCheckTypeColumn();
	}

	@Override
	public int checkTypeColumn()
	{
		ItemFunctionUtil.checkTypeColumnNeeded(this);
		return type.checkTypeColumn();
	}

	// convenience methods for conditions and views ---------------------------------

	// Note about isNull/isNotNull: a primary key can become null in queries using outer joins.

	@Override
	public InstanceOfCondition<E> instanceOf(final Type<? extends E> type1)
	{
		return new InstanceOfCondition<>(this, false, type1);
	}

	@Override
	public InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return new InstanceOfCondition<>(this, false, type1, type2);
	}

	@Override
	public InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return new InstanceOfCondition<>(this, false, type1, type2, type3);
	}

	@Override
	public InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return new InstanceOfCondition<>(this, false, type1, type2, type3, type4);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public InstanceOfCondition<E> instanceOf(final Type[] types)
	{
		return new InstanceOfCondition<>(this, false, types);
	}

	@Override
	public InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1)
	{
		return new InstanceOfCondition<>(this, true, type1);
	}

	@Override
	public InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return new InstanceOfCondition<>(this, true, type1, type2);
	}

	@Override
	public InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return new InstanceOfCondition<>(this, true, type1, type2, type3);
	}

	@Override
	public InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return new InstanceOfCondition<>(this, true, type1, type2, type3, type4);
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public InstanceOfCondition<E> notInstanceOf(final Type[] types)
	{
		return new InstanceOfCondition<>(this, true, types);
	}

	// ------------------- deprecated stuff -------------------

	@Override
	@Deprecated
	public InstanceOfCondition<E> typeIn(final Type<? extends E> type1)
	{
		return instanceOf(type1);
	}

	@Override
	@Deprecated
	public InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return instanceOf(type1, type2);
	}

	@Override
	@Deprecated
	public InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return instanceOf(type1, type2, type3);
	}

	@Override
	@Deprecated
	public InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return instanceOf(type1, type2, type3, type4);
	}

	@Override
	@Deprecated
	@SuppressWarnings({"unchecked", "rawtypes"})
	public InstanceOfCondition<E> typeIn(final Type[] types)
	{
		return instanceOf(types);
	}

	@Override
	@Deprecated
	public InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1)
	{
		return notInstanceOf(type1);
	}

	@Override
	@Deprecated
	public InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return notInstanceOf(type1, type2);
	}

	@Override
	@Deprecated
	public InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return notInstanceOf(type1, type2, type3);
	}

	@Override
	@Deprecated
	public InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return notInstanceOf(type1, type2, type3, type4);
	}

	@Override
	@Deprecated
	@SuppressWarnings({"unchecked", "rawtypes"})
	public InstanceOfCondition<E> typeNotIn(final Type[] types)
	{
		return notInstanceOf(types);
	}
}

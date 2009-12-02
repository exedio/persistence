/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.Collection;

import com.exedio.cope.CompareFunctionCondition.Operator;
import com.exedio.cope.search.ExtremumAggregate;

public final class This<E extends Item> extends Feature implements Function<E>, ItemFunction<E>
{
	static final String NAME = "this";
	
	final Type<E> type;
	
	This(final Type<E> type)
	{
		assert type!=null;
		this.type = type;
	}
	
	@Override
	void mount(final Type<? extends Item> type, final String name, final java.lang.reflect.Field annotationSource)
	{
		super.mount(type, name, annotationSource);
		assert this.type == type;
		assert NAME.equals(name);
	}
	
	public E get(final Item item)
	{
		return type.cast(item);
	}
	
	public Class<E> getValueClass()
	{
		return type.getJavaClass();
	}
	
	@Deprecated // OK: for internal use within COPE only
	public void check(final TC tc, final Join join)
	{
		tc.check(this, join);
	}
	
	@Deprecated // OK: for internal use within COPE only
	public void append(final Statement bf, final Join join)
	{
		bf.appendPK(type, join);
	}
	
	@Deprecated // OK: for internal use within COPE only
	public void appendSelect(final Statement bf, final Join join, final Holder<Column> columnHolder, final Holder<Type> typeHolder)
	{
		final Type selectType = getType();
		bf.appendPK(selectType, join);

		final IntegerColumn column = selectType.getTable().primaryKey;
		assert column.primaryKey;
		columnHolder.value = column;
		
		final StringColumn typeColumn = column.table.typeColumn;
		if(typeColumn!=null)
		{
			bf.append(',').
				append(typeColumn);
		}
		else
			typeHolder.value = selectType.getOnlyPossibleTypeOfInstances();
	}
	
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

	public Type<E> getValueType()
	{
		return type;
	}
	
	public boolean needsCheckTypeColumn()
	{
		return type.supertype!=null && type.supertype.getTable().typeColumn!=null;
	}

	public int checkTypeColumn()
	{
		if(!needsCheckTypeColumn())
			throw new RuntimeException("no check for type column needed for " + this);
		
		return type.table.database.checkTypeColumn(type.getModel().getCurrentTransaction().getConnection(), type);
	}
	
	// convenience methods for conditions and views ---------------------------------

	/**
		* Note: a primary key can become null in queries using outer joins.
		*/
	public IsNullCondition<E> isNull()
	{
		return new IsNullCondition<E>(this, false);
	}
	
	/**
		* Note: a primary key can become null in queries using outer joins.
		*/
	public IsNullCondition<E> isNotNull()
	{
		return new IsNullCondition<E>(this, true);
	}
	
	public Condition equal(final E value)
	{
		return Cope.equal(this, value);
	}
	
	public Condition equal(final Join join, final E value)
	{
		return this.bind(join).equal(value);
	}
	
	public final Condition in(final E... values)
	{
		return CompositeCondition.in(this, values);
	}
	
	public Condition in(final Collection<E> values)
	{
		return CompositeCondition.in(this, values);
	}
	
	public Condition notEqual(final E value)
	{
		return Cope.notEqual(this, value);
	}
	
	public CompareCondition<E> less(final E value)
	{
		return new CompareCondition<E>(Operator.Less, this, value);
	}
	
	public CompareCondition<E> lessOrEqual(final E value)
	{
		return new CompareCondition<E>(Operator.LessEqual, this, value);
	}
	
	public CompareCondition<E> greater(final E value)
	{
		return new CompareCondition<E>(Operator.Greater, this, value);
	}
	
	public CompareCondition<E> greaterOrEqual(final E value)
	{
		return new CompareCondition<E>(Operator.GreaterEqual, this, value);
	}
	
	public Condition between(final E lowerBound, final E upperBound)
	{
		return greaterOrEqual(lowerBound).and(lessOrEqual(upperBound));
	}
	
	public CompareFunctionCondition<E> equal(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.Equal, this, right);
	}

	public CompareFunctionCondition<E> notEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.NotEqual, this, right);
	}

	public final CompareFunctionCondition<E> less(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.Less, this, right);
	}
	
	public final CompareFunctionCondition<E> lessOrEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.LessEqual, this, right);
	}
	
	public final CompareFunctionCondition<E> greater(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.Greater, this, right);
	}
	
	public final CompareFunctionCondition<E> greaterOrEqual(final Function<? extends E> right)
	{
		return new CompareFunctionCondition<E>(Operator.GreaterEqual, this, right);
	}

	public ExtremumAggregate<E> min()
	{
		return new ExtremumAggregate<E>(this, true);
	}
	
	public ExtremumAggregate<E> max()
	{
		return new ExtremumAggregate<E>(this, false);
	}

	public final BindItemFunction<E> bind(final Join join)
	{
		return new BindItemFunction<E>(this, join);
	}
	
	public CompareFunctionCondition equalTarget()
	{
		return equal(getValueType().thisFunction);
	}
	
	public CompareFunctionCondition equalTarget(final Join targetJoin)
	{
		return equal(getValueType().thisFunction.bind(targetJoin));
	}
	
	public InstanceOfCondition<E> instanceOf(final Type<? extends E> type1)
	{
		return new InstanceOfCondition<E>(this, false, type1);
	}

	public InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return new InstanceOfCondition<E>(this, false, type1, type2);
	}

	public InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return new InstanceOfCondition<E>(this, false, type1, type2, type3);
	}

	public InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return new InstanceOfCondition<E>(this, false, type1, type2, type3, type4);
	}

	public InstanceOfCondition<E> instanceOf(final Type[] types)
	{
		return new InstanceOfCondition<E>(this, false, types);
	}
	
	public InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1)
	{
		return new InstanceOfCondition<E>(this, true, type1);
	}

	public InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return new InstanceOfCondition<E>(this, true, type1, type2);
	}

	public InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return new InstanceOfCondition<E>(this, true, type1, type2, type3);
	}

	public InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return new InstanceOfCondition<E>(this, true, type1, type2, type3, type4);
	}

	public InstanceOfCondition<E> notInstanceOf(final Type[] types)
	{
		return new InstanceOfCondition<E>(this, true, types);
	}

	// ------------------- deprecated stuff -------------------
	
	@Deprecated
	public InstanceOfCondition<E> typeIn(final Type<? extends E> type1)
	{
		return instanceOf(type1);
	}

	@Deprecated
	public InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return instanceOf(type1, type2);
	}

	@Deprecated
	public InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return instanceOf(type1, type2, type3);
	}

	@Deprecated
	public InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return instanceOf(type1, type2, type3, type4);
	}

	@Deprecated
	public InstanceOfCondition<E> typeIn(final Type[] types)
	{
		return instanceOf(types);
	}
	
	@Deprecated
	public InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1)
	{
		return notInstanceOf(type1);
	}

	@Deprecated
	public InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return notInstanceOf(type1, type2);
	}

	@Deprecated
	public InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return notInstanceOf(type1, type2, type3);
	}

	@Deprecated
	public InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return notInstanceOf(type1, type2, type3, type4);
	}

	@Deprecated
	public InstanceOfCondition<E> typeNotIn(final Type[] types)
	{
		return notInstanceOf(types);
	}
}

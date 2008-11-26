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

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ItemField<E extends Item> extends FunctionField<E> implements ItemFunction<E>
{
	private final Type<E> initialValueType;
	private final DeletePolicy policy;

	private ItemField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final Class<E> valueClass,
			final Type<E> initialValueType,
			final DeletePolicy policy)
	{
		super(isfinal, optional, unique, valueClass, null/* defaultConstant makes no sense for ItemField */);
		checkValueClass(Item.class);
		if(Item.class.equals(valueClass))
			throw new IllegalArgumentException("is not a subclass of " + Item.class.getName() + " but Item itself");
		this.initialValueType = initialValueType;
		this.policy = policy;
		if(policy==null)
			throw new NullPointerException("delete policy for item field must not be null");
		if(policy==DeletePolicy.NULLIFY)
		{
			if(!optional)
				throw new IllegalArgumentException("mandatory item field cannot have delete policy nullify");
			if(isfinal)
				throw new IllegalArgumentException("final item field cannot have delete policy nullify");
		}
		checkDefaultConstant();
	}
	
	ItemField(final Class<E> valueClass)
	{
		this(false, false, false, valueClass, null, Item.FORBID);
	}
	
	ItemField(final Class<E> valueClass, final DeletePolicy policy)
	{
		this(false, policy==DeletePolicy.NULLIFY, false, valueClass, null, policy);
	}

	ItemField(final Type<E> valueType, final DeletePolicy policy)
	{
		this(false, policy==DeletePolicy.NULLIFY, false, valueType.getJavaClass(), valueType, policy);
	}

	@Override
	public ItemField<E> copy()
	{
		return new ItemField<E>(isfinal, optional, unique, valueClass, initialValueType, policy);
	}
	
	@Override
	public ItemField<E> toFinal()
	{
		return new ItemField<E>(true, optional, unique, valueClass, initialValueType, policy);
	}
	
	@Override
	public ItemField<E> optional()
	{
		return new ItemField<E>(isfinal, true, unique, valueClass, initialValueType, policy);
	}
	
	@Override
	public ItemField<E> unique()
	{
		return new ItemField<E>(isfinal, optional, true, valueClass, initialValueType, policy);
	}

	@Override
	public ItemField<E> nonUnique()
	{
		return new ItemField<E>(isfinal, optional, false, valueClass, initialValueType, policy);
	}
	
	private Type<E> valueType = null;
	private Type<? extends E> onlyPossibleValueType = null;
	private StringColumn typeColumn = null;
	
	/**
	 * Returns the type of items, this field accepts instances of.
	 */
	public Type<E> getValueType()
	{
		if(valueType==null)
			throw new RuntimeException();

		return valueType;
	}
	
	/**
	 * @see EnumField#as(Class)
	 * @see Class#asSubclass(Class)
	 */
	@SuppressWarnings("unchecked") // OK: is checked on runtime
	public <X extends Item> ItemField<X> as(final Class<X> clazz)
	{
		if(!valueClass.equals(clazz))
		{
			final String n = ItemField.class.getName();
			// exception message consistent with Cope.verboseCast(Class, Object)
			throw new ClassCastException(
					"expected a " + n + '<' + clazz.getName() +
					">, but was a " + n + '<' + valueClass.getName() + '>');
		}
		
		return (ItemField<X>)this;
	}
	
	/**
	 * Returns the delete policy of this field.
	 */
	public DeletePolicy getDeletePolicy()
	{
		return policy;
	}
	
	void postInitialize()
	{
		if(valueType!=null)
			throw new RuntimeException();
		
		valueType = initialValueType!=null ? initialValueType : Type.forClass(valueClass);
		assert valueClass.equals(valueType.getJavaClass());
		valueType.registerReference(this);
	}
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		if(valueType==null)
			throw new RuntimeException();
		if(onlyPossibleValueType!=null)
			throw new RuntimeException();
		if(typeColumn!=null)
			throw new RuntimeException();
		
		final ItemColumn result = new ItemColumn(table, this, name, optional, valueType);
		
		final String[] typeColumnValues = valueType.getTypesOfInstancesColumnValues();
		if(typeColumnValues==null)
			onlyPossibleValueType = valueType.getOnlyPossibleTypeOfInstances();
		else
			typeColumn = new TypeColumn(table, result, optional, typeColumnValues);

		return result;
	}

	@Override
	void disconnect()
	{
		if(this.onlyPossibleValueType==null && this.typeColumn==null)
			throw new RuntimeException();

		super.disconnect();
		this.onlyPossibleValueType = null;
		this.typeColumn = null;
	}
	
	StringColumn getTypeColumn()
	{
		if(valueType==null)
			throw new RuntimeException();

		return typeColumn;
	}
	
	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public final void appendSelect(final Statement bf, final Join join, final Holder<Column> columnHolder, final Holder<Type> typeHolder)
	{
		super.appendSelect(bf, join, columnHolder, typeHolder);
		final StringColumn typeColumn = getTypeColumn();
		if(typeColumn!=null)
			bf.append(',').append(typeColumn);
	}
	
	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public Object loadSearch(final ResultSet resultSet, final IntHolder columnIndex, final Row dummyRow, final Column selectColumn, final Type selectType)
	throws SQLException
	{
		final Object result = super.loadSearch(resultSet, columnIndex, dummyRow, selectColumn, selectType);
		final StringColumn typeColumn = getTypeColumn();
		if(typeColumn!=null)
			typeColumn.load(resultSet, columnIndex.value++, dummyRow);
		return result;
	}
	
	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	public void appendType(final Statement bf, final Join join)
	{
		bf.append(Statement.assertTypeColumn(typeColumn, getValueType()), join);
	}
	
	@Override
	E get(final Row row)
	{
		final Object cell = row.get(getColumn());

		if(cell==null)
		{
			if(typeColumn!=null && row.get(typeColumn)!=null)
				throw new RuntimeException("inconsistent type column on field " + toString() + ": " + row.get(typeColumn));
			
			return null;
		}
		else
		{
			final Type<? extends E> cellType;
			if(typeColumn!=null)
			{
				final String cellTypeID = (String)row.get(typeColumn);
				
				if(cellTypeID==null)
					throw new RuntimeException("inconsistent type column on field " + toString());
				
				cellType = getValueType().getTypeOfInstance(cellTypeID);
				
				if(cellType==null)
					throw new RuntimeException(cellTypeID);
			}
			else
			{
				cellType = onlyPossibleValueType;
				
				if(cellType==null)
					throw new RuntimeException();
			}
			
			return cellType.getItemObject(((Integer)cell).intValue());
		}
	}
	
	@Override
	void set(final Row row, final E surface)
	{
		if(surface==null)
		{
			row.put(getColumn(), null);
			if(typeColumn!=null)
				row.put(typeColumn, null);
		}
		else
		{
			final Item valueItem = surface;
			row.put(getColumn(), Integer.valueOf(valueItem.pk));
			if(typeColumn!=null)
				row.put(typeColumn, valueItem.type.id);
		}
	}
	
	public boolean needsCheckTypeColumn()
	{
		return typeColumn!=null;
	}
	
	public int checkTypeColumn()
	{
		if(!needsCheckTypeColumn())
			throw new RuntimeException("no check for type column needed for " + this);
		
		return getColumn().table.database.checkTypeColumn(getType().getModel().getCurrentTransaction().getConnection(), this);
	}
	
	public static enum DeletePolicy
	{
		FORBID(),
		NULLIFY(),
		CASCADE();
	}
	
	// convenience methods for conditions and views ---------------------------------

	public CompareFunctionCondition equalTarget()
	{
		return equal(getValueType().thisFunction);
	}
	
	public CompareFunctionCondition equalTarget(final Join targetJoin)
	{
		return equal(getValueType().thisFunction.bind(targetJoin));
	}
	
	@Override
	public BindItemFunction<E> bind(final Join join)
	{
		return new BindItemFunction<E>(this, join);
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
	
	/**
	 * @deprecated use {@link #toFinal()}, {@link #unique()} and {@link #optional()} instead.
	 */
	@Deprecated
	public ItemField(final Option option, final Class<E> valueClass)
	{
		this(option, valueClass, Item.FORBID);
	}
	
	/**
	 * @deprecated use {@link #toFinal()}, {@link #unique()} and {@link #optional()} instead.
	 */
	@Deprecated
	public ItemField(final Option option, final Class<E> valueClass, final DeletePolicy policy)
	{
		this(option.isFinal, option.optional, option.unique, valueClass, null, policy);
	}

	/**
	 * @deprecated use {@link #toFinal()}, {@link #unique()} and {@link #optional()} instead.
	 */
	@Deprecated
	ItemField(final Option option, final Type<E> valueType, final DeletePolicy policy)
	{
		this(option.isFinal, option.optional, option.unique, null, valueType, policy);
	}
	
	/**
	 * @deprecated Use {@link #as(Class)} instead
	 */
	@Deprecated
	public <X extends Item> ItemField<X> cast(final Class<X> clazz)
	{
		return as(clazz);
	}

	/**
	 * @deprecated Use {@link SchemaInfo#getTypeColumnName(ItemField)} instead
	 */
	@Deprecated
	public String getTypeColumnName()
	{
		return SchemaInfo.getTypeColumnName(this);
	}
	
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

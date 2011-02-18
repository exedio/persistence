/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.Executor.integerResultSetHandler;

import java.sql.Connection;

public final class ItemField<E extends Item> extends FunctionField<E>
	implements ItemFunction<E>
{
	private static final long serialVersionUID = 1l;

	private final TypeFuture<E> valueTypeFuture;
	private final DeletePolicy policy;

	private ItemField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final TypeFuture<E> valueTypeFuture,
			final DeletePolicy policy)
	{
		super(isfinal, optional, unique, valueTypeFuture.javaClass, null/* defaultConstant makes no sense for ItemField */);
		checkValueClass(Item.class);
		if(Item.class.equals(valueClass))
			throw new IllegalArgumentException("is not a subclass of " + Item.class.getName() + " but Item itself");
		this.valueTypeFuture = valueTypeFuture;
		this.policy = policy;
		if(policy==null)
			throw new NullPointerException("policy");
		if(policy==DeletePolicy.NULLIFY)
		{
			if(!optional)
				throw new IllegalArgumentException("mandatory item field cannot have delete policy nullify");
			if(isfinal)
				throw new IllegalArgumentException("final item field cannot have delete policy nullify");
		}
		checkDefaultConstant();
	}

	ItemField(final TypeFuture<E> valueTypeFuture)
	{
		this(false, false, false, valueTypeFuture, Item.FORBID);
	}

	ItemField(final TypeFuture<E> valueTypeFuture, final DeletePolicy policy)
	{
		this(false, policy==DeletePolicy.NULLIFY, false, valueTypeFuture, policy);
	}

	@Override
	public ItemField<E> copy()
	{
		return new ItemField<E>(isfinal, optional, unique, valueTypeFuture, policy);
	}

	@Override
	public ItemField<E> toFinal()
	{
		return new ItemField<E>(true, optional, unique, valueTypeFuture, policy);
	}

	@Override
	public ItemField<E> optional()
	{
		return new ItemField<E>(isfinal, true, unique, valueTypeFuture, policy);
	}

	@Override
	public ItemField<E> unique()
	{
		return new ItemField<E>(isfinal, optional, true, valueTypeFuture, policy);
	}

	@Override
	public ItemField<E> nonUnique()
	{
		return new ItemField<E>(isfinal, optional, false, valueTypeFuture, policy);
	}

	@Override
	public ItemField<E> noDefault()
	{
		return copy(); // no defaults for item fields
	}

	/**
	 * @deprecated defaults make no sense for ItemField
	 */
	@Deprecated
	@Override
	public ItemField<E> defaultTo(final E defaultConstant)
	{
		if(defaultConstant!=null)
			throw new IllegalArgumentException("no defaults for item fields");

		return copy(); // no defaults for item fields
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

	public DeletePolicy getDeletePolicy()
	{
		return policy;
	}


	private Type<E> valueType = null;

	void resolveValueType()
	{
		if(!isMounted())
			throw new RuntimeException();
		if(valueType!=null)
			throw new RuntimeException(getID());

		valueType = valueTypeFuture.get();
		assert valueClass.equals(valueType.getJavaClass());
	}

	/**
	 * Returns the type of items, this field accepts instances of.
	 */
	public Type<E> getValueType()
	{
		if(valueType==null)
			throw new IllegalStateException("valueType of " + this.toString() + " not yet resolved: " + valueTypeFuture.toString());

		return valueType;
	}


	private boolean connected = false;
	private Type<? extends E> onlyPossibleValueType = null;
	private StringColumn typeColumn = null;

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		if(valueType==null)
			throw new RuntimeException(toString());
		if(connected)
			throw new RuntimeException(toString());
		if(onlyPossibleValueType!=null)
			throw new RuntimeException(toString());
		if(typeColumn!=null)
			throw new RuntimeException(toString());

		final ItemColumn result = new ItemColumn(table, this, name, optional, valueType);

		final String[] typeColumnValues = valueType.getTypesOfInstancesColumnValues();
		if(typeColumnValues==null)
			onlyPossibleValueType = valueType.getOnlyPossibleTypeOfInstances();
		else
			typeColumn = new TypeColumn(table, result, optional, typeColumnValues);

		connected = true;

		return result;
	}

	@Override
	void disconnect()
	{
		if(!connected)
			throw new RuntimeException(toString());
		if(this.onlyPossibleValueType==null && this.typeColumn==null)
			throw new RuntimeException(toString());

		super.disconnect();
		this.connected = false;
		this.onlyPossibleValueType = null;
		this.typeColumn = null;
	}

	private Type<? extends E> getOnlyPossibleValueType()
	{
		if(!connected)
			throw new RuntimeException(toString());

		return onlyPossibleValueType;
	}

	StringColumn getTypeColumn()
	{
		if(!connected)
			throw new RuntimeException(toString());

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
			bf.append(',').append(typeColumn, join);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Deprecated // OK: for internal use within COPE only
	public void appendType(final Statement bf, final Join join)
	{
		bf.append(Statement.assertTypeColumn(getTypeColumn(), getValueType()), join);
	}

	@Override
	E get(final Row row, final Query query)
	{
		final StringColumn typeColumn = getTypeColumn();
		final Object cell = row.get(getColumn());

		if(cell==null)
		{
			if(typeColumn!=null && row.get(typeColumn)!=null)
				throw new RuntimeException("inconsistent type column on field " + toString() + ": " + row.get(typeColumn) + " --- row: " + row + " --- query: " + query);

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
				cellType = getOnlyPossibleValueType();

				if(cellType==null)
					throw new RuntimeException();
			}

			return cellType.getItemObject(((Integer)cell).intValue());
		}
	}

	@Override
	void set(final Row row, final E surface)
	{
		final StringColumn typeColumn = getTypeColumn();

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
		return getTypeColumn()!=null;
	}

	public int checkTypeColumn()
	{
		if(!needsCheckTypeColumn())
			throw new RuntimeException("no check for type column needed for " + this);

		final Type type = getType();
		final Transaction tx = type.getModel().currentTransaction();
		final Connection connection = tx.getConnection();
		final Executor executor = tx.connect.executor;
		final Table table = type.getTable();
		final Table valueTable = getValueType().getTable();
		final String alias1 = executor.dialect.dsmfDialect.quoteName(Table.SQL_ALIAS_1);
		final String alias2 = executor.dialect.dsmfDialect.quoteName(Table.SQL_ALIAS_2);

		final Statement bf = executor.newStatement(false);
		bf.append("select count(*) from ").
			append(table).append(' ').append(alias1).
			append(',').
			append(valueTable).append(' ').append(alias2).
			append(" where ").
			append(alias1).append('.').append(getColumn()).
			append('=').
			append(alias2).append('.').append(valueTable.primaryKey).
			append(" and ").
			append(alias1).append('.').append(getTypeColumn()).
			append("<>").
			append(alias2).append('.').append(valueTable.typeColumn);

		//System.out.println("CHECKA:"+bf.toString());

		return executor.query(connection, bf, null, false, integerResultSetHandler);
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

/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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


public final class ItemField<E extends Item> extends FunctionField<E> implements ItemFunction<E>
{
	private final Type<E> initialValueType;
	private final DeletePolicy policy;

	private ItemField(final boolean isfinal, final boolean optional, final boolean unique, final Class<E> valueClass, final Type<E> initialValueType, final DeletePolicy policy)
	{
		super(isfinal, optional, unique, initialValueType==null?valueClass:initialValueType.getJavaClass(), null/* defaultConstant makes no sense for ItemField */);
		checkValueClass(Item.class);
		assert (valueClass==null) != (initialValueType==null);
		this.initialValueType = initialValueType;
		this.policy = policy;
		if(policy==null)
			throw new NullPointerException("delete policy for field " + this + " must not be null");
		if(policy==DeletePolicy.NULLIFY)
		{
			if(!optional)
				throw new IllegalArgumentException("mandatory field " + this + " cannot have delete policy nullify");
			if(isfinal)
				throw new IllegalArgumentException("final field " + this + " cannot have delete policy nullify");
		}
		checkDefaultValue();
	}
	
	/**
	 * @deprecated
	 * use {@link Item#newItemField(Class)} instead,
	 * which allows ommitting the generics:
	 * instead of <tt>new ItemField&lt;Target&gt;(Target.class)</tt>
	 * one can write <tt>newItemField(Target.class)</tt>
	 */
	@Deprecated
	public ItemField(final Class<E> valueClass)
	{
		this(Item.MANDATORY, valueClass, Item.FORBID);
	}
	
	/**
	 * @deprecated
	 * use {@link Item#newItemField(com.exedio.cope.Field.Option, Class)} instead,
	 * which allows ommitting the generics:
	 * instead of <tt>new ItemField&lt;Target&gt;(OPTIONAL, Target.class)</tt>
	 * one can write <tt>newItemField(OPTIONAL, Target.class)</tt>
	 */
	@Deprecated
	public ItemField(final Option option, final Class<E> valueClass)
	{
		this(option, valueClass, Item.FORBID);
	}
	
	/**
	 * @deprecated
	 * use {@link Item#newItemField(Class, com.exedio.cope.ItemField.DeletePolicy)} instead,
	 * which allows ommitting the generics:
	 * instead of <tt>new ItemField&lt;Target&gt;(Target.class, CASCADE)</tt>
	 * one can write <tt>newItemField(Target.class, CASCADE)</tt>
	 */
	@Deprecated
	public ItemField(final Class<E> valueClass, final DeletePolicy policy)
	{
		this(Item.MANDATORY, valueClass, policy);
	}

	/**
	 * @deprecated
	 * use {@link Item#newItemField(com.exedio.cope.Field.Option, Class, com.exedio.cope.ItemField.DeletePolicy)} instead,
	 * which allows ommitting the generics:
	 * instead of <tt>new ItemField&lt;Target&gt;(OPTIONAL, Target.class, CASCADE)</tt>
	 * one can write <tt>newItemField(OPTIONAL, Target.class, CASCADE)</tt>
	 */
	@Deprecated
	public ItemField(final Option option, final Class<E> valueClass, final DeletePolicy policy)
	{
		this(option.isFinal, option.optional, option.unique, valueClass, null, policy);
	}

	ItemField(final Option option, final Type<E> valueType, final DeletePolicy policy)
	{
		this(option.isFinal, option.optional, option.unique, null, valueType, policy);
	}
	
	@Override
	public ItemField<E> copyFunctionField()
	{
		if(initialValueType==null)
			return new ItemField<E>(isfinal, optional, implicitUniqueConstraint!=null, valueClass, null, policy);
		else
			return new ItemField<E>(isfinal, optional, implicitUniqueConstraint!=null, null, initialValueType, policy);
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
		
		valueType = initialValueType!=null ? initialValueType : Type.findByJavaClass(valueClass);
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
		
		final ItemColumn result = new ItemColumn(table, name, optional, valueType);
		
		final String[] typeColumnValues = valueType.getTypesOfInstancesColumnValues();
		if(typeColumnValues==null)
			onlyPossibleValueType = valueType.getOnlyPossibleTypeOfInstances();
		else
			typeColumn = new StringColumn(table, result.id + "Type"/* not equal to "name"! */, optional, typeColumnValues);

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
	
	/**
	 * Returns the name of type column in the database for this field
	 * - <b>use with care!</b>
	 * <p>
	 * This information is needed only, if you want to access
	 * the database without cope.
	 * In this case you should really know, what you are doing.
	 * Any INSERT/UPDATE/DELETE on the database bypassing cope
	 * may lead to inconsistent caches.
	 * Please note, that this string may vary,
	 * if a cope model is configured for different databases.
	 *
	 * @throws IllegalArgumentException
	 *         if there is no type column for this ItemField,
	 *         because <code>{@link #getValueType()}.{@link Type#getTypesOfInstances() getTypesOfInstances()}</code>
	 *         contains one type only.
	 * @see Type#getTableName()
	 * @see Type#getTypeColumnName()
	 * @see Field#getColumnName()
	 */
	public String getTypeColumnName()
	{
		if(typeColumn==null)
			throw new IllegalArgumentException("no type column for " + this);

		return typeColumn.id;
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
	@Deprecated
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
				throw new RuntimeException("inconsistent type column: "+row.get(typeColumn));
			
			return null;
		}
		else
		{
			final Type<? extends E> cellType;
			if(typeColumn!=null)
			{
				final String cellTypeID = (String)row.get(typeColumn);
				
				if(cellTypeID==null)
					throw new RuntimeException("inconsistent type column");
				
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
	
	public TypeInCondition<E> instanceOf(final Type<? extends E> type1)
	{
		return new TypeInCondition<E>(this, false, type1);
	}

	public TypeInCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return new TypeInCondition<E>(this, false, type1, type2);
	}

	public TypeInCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return new TypeInCondition<E>(this, false, type1, type2, type3);
	}

	public TypeInCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return new TypeInCondition<E>(this, false, type1, type2, type3, type4);
	}

	public TypeInCondition<E> instanceOf(final Type[] types)
	{
		return new TypeInCondition<E>(this, false, types);
	}
	
	public TypeInCondition<E> notInstanceOf(final Type<? extends E> type1)
	{
		return new TypeInCondition<E>(this, true, type1);
	}

	public TypeInCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return new TypeInCondition<E>(this, true, type1, type2);
	}

	public TypeInCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return new TypeInCondition<E>(this, true, type1, type2, type3);
	}

	public TypeInCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return new TypeInCondition<E>(this, true, type1, type2, type3, type4);
	}

	public TypeInCondition<E> notInstanceOf(final Type[] types)
	{
		return new TypeInCondition<E>(this, true, types);
	}

	@Deprecated
	public TypeInCondition<E> typeIn(final Type<? extends E> type1)
	{
		return instanceOf(type1);
	}

	@Deprecated
	public TypeInCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return instanceOf(type1, type2);
	}

	@Deprecated
	public TypeInCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return instanceOf(type1, type2, type3);
	}

	@Deprecated
	public TypeInCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return instanceOf(type1, type2, type3, type4);
	}

	@Deprecated
	public TypeInCondition<E> typeIn(final Type[] types)
	{
		return instanceOf(types);
	}
	
	@Deprecated
	public TypeInCondition<E> typeNotIn(final Type<? extends E> type1)
	{
		return notInstanceOf(type1);
	}

	@Deprecated
	public TypeInCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2)
	{
		return notInstanceOf(type1, type2);
	}

	@Deprecated
	public TypeInCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
	{
		return notInstanceOf(type1, type2, type3);
	}

	@Deprecated
	public TypeInCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
	{
		return notInstanceOf(type1, type2, type3, type4);
	}

	@Deprecated
	public TypeInCondition<E> typeNotIn(final Type[] types)
	{
		return notInstanceOf(types);
	}
}

/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.search.EqualCondition;
import com.exedio.cope.search.EqualTargetCondition;
import com.exedio.cope.search.NotEqualCondition;

public final class ItemAttribute extends ObjectAttribute
{

	private final Class targetTypeClass;
	private final DeletePolicy policy;

	public ItemAttribute(final Option option, final Class targetTypeClass)
	{
		this(option, targetTypeClass, Item.FORBID);
	}
	
	public ItemAttribute(final Option option, final Class targetTypeClass, final DeletePolicy policy)
	{
		super(option, targetTypeClass, targetTypeClass.getName());
		this.targetTypeClass = targetTypeClass;
		this.policy = policy;
		if(targetTypeClass==null)
			throw new RuntimeException("target type class for attribute "+this+" must not be null");
		if(!Item.class.isAssignableFrom(targetTypeClass))
			throw new RuntimeException("target type class "+targetTypeClass+" for attribute "+this+" must be a sub class of item");
		if(policy==null)
			throw new RuntimeException("delete policy for attribute "+this+" must not be null");
		if(policy.nullify)
		{
			if(option.mandatory)
				throw new RuntimeException("mandatory attribute "+this+" cannot have delete policy nullify");
			if(option.readOnly)
				throw new RuntimeException("read-only attribute "+this+" cannot have delete policy nullify");
		}
	}
	
	private Type targetType = null;
	private Type onlyPossibleTargetType = null;
	private StringColumn typeColumn = null;

	public ObjectAttribute copyAsTemplate()
	{
		return new ItemAttribute(getTemplateOption(), targetTypeClass, policy);
	}
	
	/**
	 * Returns the type of items, this attribute accepts instances of.
	 */
	public Type getTargetType()
	{
		if(targetType==null)
			throw new RuntimeException();

		return targetType;
	}
	
	/**
	 * Returns the delete policy of this attribute.
	 */
	public DeletePolicy getDeletePolicy()
	{
		return policy;
	}
	
	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		if(targetType!=null)
			throw new RuntimeException();
		if(onlyPossibleTargetType!=null)
			throw new RuntimeException();
		if(typeColumn!=null)
			throw new RuntimeException();
		
		targetType = Type.findByJavaClass(targetTypeClass);
		targetType.registerReference(this);
		
		final ItemColumn result = new ItemColumn(table, name, notNull, targetTypeClass, this);
		
		final String[] typeColumnValues = targetType.getTypesOfInstancesColumnValues();
		if(typeColumnValues==null)
			onlyPossibleTargetType = targetType.getOnlyPossibleTypeOfInstances();
		else
			typeColumn = new StringColumn(table, name+"Type", notNull, typeColumnValues);

		return result;
	}
	
	StringColumn getTypeColumn()
	{
		if(targetType==null)
			throw new RuntimeException();

		return typeColumn;
	}
	
	Object get(final Row row)
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
			final Type cellType;
			if(typeColumn!=null)
			{
				final String cellTypeID = (String)row.get(typeColumn);
				
				if(cellTypeID==null)
					throw new RuntimeException("inconsistent type column");
				
				cellType = getTargetType().getModel().findTypeByID(cellTypeID);
				
				if(cellType==null)
					throw new RuntimeException(cellTypeID);
			}
			else
			{
				cellType = onlyPossibleTargetType;
				
				if(cellType==null)
					throw new RuntimeException();
			}
			
			return cellType.getItemObject(((Integer)cell).intValue());
		}
	}
		
	void set(final Row row, final Object surface)
	{
		if(surface==null)
		{
			row.put(getColumn(), null);
			if(typeColumn!=null)
				row.put(typeColumn, null);
		}
		else
		{
			final Item valueItem = (Item)surface;
			row.put(getColumn(), new Integer(valueItem.pk));
			if(typeColumn!=null)
				row.put(typeColumn, valueItem.type.id);
		}
	}
	
	public final Item get(final Item item)
	{
		return (Item)item.get(this);
	}
	
	public final void set(final Item item, final Item value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			ReadOnlyViolationException
	{
		try
		{
			item.set(this, value);
		}
		catch(LengthViolationException e)
		{
			throw new RuntimeException(e);
		}
	}

	public final EqualCondition equal(final Item value)
	{
		return new EqualCondition(null, this, value);
	}
	
	public final EqualCondition equal(final Item value, final Join join)
	{
		return new EqualCondition(join, this, value);
	}
	
	public final EqualTargetCondition equalTarget()
	{
		return new EqualTargetCondition(this, null);
	}
	
	public final EqualTargetCondition equalTarget(final Join targetJoin)
	{
		return new EqualTargetCondition(this, targetJoin);
	}
	
	public final NotEqualCondition notEqual(final Item value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public static final class DeletePolicy
	{
		public final boolean forbid;
		public final boolean nullify;
		public final boolean cascade;

		DeletePolicy(final int policy)
		{
			switch(policy)
			{
				case 0:
					this.forbid = true;
					this.nullify = false;
					this.cascade = false;
					break;
				case 1:
					this.forbid = false;
					this.nullify = true;
					this.cascade = false;
					break;
				case 2:
					this.forbid = false;
					this.nullify = false;
					this.cascade = true;
					break;
				default:
					throw new RuntimeException(String.valueOf(policy));
			}
		}
		
		public final String toString()
		{
			return forbid ? "FORBID" : (nullify ? "NULLIFY" : "CASCADE");
		}
	}
	
}

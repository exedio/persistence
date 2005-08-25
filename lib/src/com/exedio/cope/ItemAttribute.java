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

	/**
	 * @see Item#itemAttribute(Option, Class)
	 */
	ItemAttribute(final Option option, final Class targetTypeClass, final DeletePolicy policy)
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
	
	Type targetType = null;

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
	
	protected Column createColumn(final Table table, final String name, final boolean notNull)
	{
		if(targetType!=null)
			throw new RuntimeException();
		
		targetType = Type.findByJavaClass(targetTypeClass);
		targetType.registerReference(this);

		return new ItemColumn(table, name, notNull, targetTypeClass, this);
	}
	
	Object cacheToSurface(final Object cache)
	{
		return 
			cache==null ? 
				null : 
				getTargetType().createItemObject(((Integer)cache).intValue());
	}
		
	Object surfaceToCache(final Object surface)
	{
		return
			surface==null ? 
				null : 
				new Integer(((Item)surface).pk);
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

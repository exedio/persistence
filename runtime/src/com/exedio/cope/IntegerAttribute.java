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

import com.exedio.cope.function.SumFunction;
import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;

public final class IntegerAttribute extends ObjectAttribute implements IntegerFunction
{

	public IntegerAttribute(final Option option)
	{
		super(option, Integer.class, "integer");
	}
	
	public ObjectAttribute copyAsTemplate()
	{
		return new IntegerAttribute(getTemplateOption());
	}
	
	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		return new IntegerColumn(table, name, notNull, 10, false, null);
	}
	
	Object get(final Row row)
	{
		return (Integer)row.get(getColumn());
	}
		
	void set(final Row row, final Object surface)
	{
		row.put(getColumn(), (Integer)surface);
	}
	
	public final Integer get(final Item item)
	{
		return (Integer)item.get(this);
	}
	
	/**
	 * @throws RuntimeException if this attribute is not {@link #isMandatory() mandatory}.
	 */
	public final int getMandatory(final Item item)
	{
		if(!mandatory)
			throw new RuntimeException("attribute " + toString() + " is not mandatory");
		
		return get(item).intValue();
	}
	
	public final void set(final Item item, final Integer value)
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

	public final void set(final Item item, final int value)
		throws
			UniqueViolationException,
			ReadOnlyViolationException
	{
		try
		{
			set(item, new Integer(value));
		}
		catch(MandatoryViolationException e)
		{
			throw new RuntimeException(e);
		}
	}

	public final AttributeValue map(final Integer value)
	{
		return new AttributeValue(this, value);
	}
	
	public final AttributeValue map(final int value)
	{
		return new AttributeValue(this, new Integer(value));
	}
	
	public final EqualCondition equal(final Integer value)
	{
		return new EqualCondition(null, this, value);
	}
	
	public final EqualCondition equal(final int value)
	{
		return new EqualCondition(null, this, new Integer(value));
	}
	
	public final NotEqualCondition notEqual(final Integer value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public final NotEqualCondition notEqual(final int value)
	{
		return new NotEqualCondition(this, new Integer(value));
	}
	
	public final LessCondition less(final int value)
	{
		return new LessCondition(this, new Integer(value));
	}
	
	public final LessEqualCondition lessOrEqual(final int value)
	{
		return new LessEqualCondition(this, new Integer(value));
	}
	
	public final GreaterCondition greater(final int value)
	{
		return new GreaterCondition(this, new Integer(value));
	}
	
	public final GreaterEqualCondition greaterOrEqual(final int value)
	{
		return new GreaterEqualCondition(this, new Integer(value));
	}
	
	public final SumFunction sum(final IntegerFunction other)
	{
		return new SumFunction(new IntegerFunction[]{this, other});
	}

}

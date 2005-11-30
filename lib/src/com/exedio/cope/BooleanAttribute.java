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
import com.exedio.cope.search.NotEqualCondition;

public final class BooleanAttribute extends ObjectAttribute
{
	static final int[] ALLOWED_VALUES = new int[]{0, 1};

	public BooleanAttribute(final Option option)
	{
		super(option, Boolean.class, "boolean");
	}
	
	public ObjectAttribute copyAsTemplate()
	{
		return new BooleanAttribute(getTemplateOption());
	}
	
	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		return new IntegerColumn(table, name, notNull, 1, false, ALLOWED_VALUES);
	}
	
	Object cacheToSurface(final Row row)
	{
		final Object cell = row.get(getColumn());
		if(cell==null)
			return null;
		else
		{
			switch(((Integer)cell).intValue())
			{
				case 0:
					return Boolean.FALSE;
				case 1:
					return Boolean.TRUE;
				default:
					throw new RuntimeException("cacheToSurface:"+cell);
			}
		}
	}
	
	static final Integer FALSE = new Integer(0);
	static final Integer TRUE = new Integer(1);
		
	void surfaceToCache(final Row row, final Object surface)
	{
		row.put(getColumn(), 	surface==null ? 	null : 	((Boolean)surface).booleanValue() ? TRUE : FALSE);
	}
	
	public final Boolean get(final Item item)
	{
		return (Boolean)item.get(this);
	}
	
	public final void set(final Item item, final Boolean value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException, // TODO remove
			ReadOnlyViolationException
	{
		item.set(this, value);
	}

	public final EqualCondition equal(final Boolean value)
	{
		return new EqualCondition(null, this, value);
	}
	
	public final EqualCondition equal(final boolean value)
	{
		return new EqualCondition(null, this, value ? Boolean.TRUE : Boolean.FALSE);
	}
	
	public final NotEqualCondition notEqual(final Boolean value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public final NotEqualCondition notEqual(final boolean value)
	{
		return new NotEqualCondition(this, value ? Boolean.TRUE : Boolean.FALSE);
	}
	
}

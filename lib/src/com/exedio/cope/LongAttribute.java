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
import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;
import com.exedio.cope.search.NotEqualCondition;

public final class LongAttribute extends ObjectAttribute
{

	public LongAttribute(final Option option)
	{
		super(option, Long.class, "long");
	}
	
	public ObjectAttribute copyAsTemplate()
	{
		return new LongAttribute(getTemplateOption());
	}
	
	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		return new IntegerColumn(table, name, notNull, 20, true, null);
	}
	
	Object cacheToSurface(final Row row)
	{
		return (Long)row.get(getColumn());
	}
		
	void surfaceToCache(final Row row, final Object surface)
	{
		row.put(getColumn(), (Long)surface);
	}
	
	public final Long get(final Item item)
	{
		return (Long)item.get(this);
	}
	
	public final void set(final Item item, final Long value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException, // TODO remove
			ReadOnlyViolationException
	{
		item.set(this, value);
	}

	public final EqualCondition equal(final Long value)
	{
		return new EqualCondition(null, this, value);
	}
	
	public final EqualCondition equal(final long value)
	{
		return new EqualCondition(null, this, new Long(value));
	}
	
	public final NotEqualCondition notEqual(final Long value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public final NotEqualCondition notEqual(final long value)
	{
		return new NotEqualCondition(this, new Long(value));
	}
	
	public final LessCondition less(final long value)
	{
		return new LessCondition(this, new Long(value));
	}
	
	public final LessEqualCondition lessOrEqual(final long value)
	{
		return new LessEqualCondition(this, new Long(value));
	}
	
	public final GreaterCondition greater(final long value)
	{
		return new GreaterCondition(this, new Long(value));
	}
	
	public final GreaterEqualCondition greaterOrEqual(final long value)
	{
		return new GreaterEqualCondition(this, new Long(value));
	}
	
}

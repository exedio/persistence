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

import java.util.Collections;
import java.util.List;

import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;

public final class LongAttribute extends ObjectAttribute
{
	/**
	 * @see Item#longAttribute(Option)
	 */
	LongAttribute(final Option option)
	{
		super(option, Long.class, "long");
	}
	
	public ObjectAttribute copyAsTemplate()
	{
		return new LongAttribute(getTemplateOption());
	}
	
	protected List createColumns(final Table table, final String name, final boolean notNull)
	{
		return Collections.singletonList(new IntegerColumn(table, name, notNull, 20, true, null));
	}
	
	Object cacheToSurface(final Object cache)
	{
		return (Long)cache;
	}
		
	Object surfaceToCache(final Object surface)
	{
		return (Long)surface;
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
	
}

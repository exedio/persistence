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
import com.exedio.cope.util.Day;

public final class DayAttribute extends ObjectAttribute
{
	public DayAttribute(final Option option)
	{
		super(option, Day.class, "day");
	}
	
	public ObjectAttribute copyAsTemplate()
	{
		return new DayAttribute(getTemplateOption());
	}
	
	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		return new DayColumn(table, name, notNull);
	}
	
	Object cacheToSurface(final Object cache)
	{
		return cache==null ? null : DayColumn.getDay(((Integer)cache).intValue());
	}
		
	Object surfaceToCache(final Object surface)
	{
		return surface==null ? null : new Integer(DayColumn.getTransientNumber((Day)surface));
	}
	
	public final EqualCondition equal(final Day value)
	{
		return new EqualCondition(null, this, value);
	}
	
	public final NotEqualCondition notEqual(final Day value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public final LessCondition less(final Day value)
	{
		return new LessCondition(this, value);
	}
	
	public final LessEqualCondition lessOrEqual(final Day value)
	{
		return new LessEqualCondition(this, value);
	}
	
	public final GreaterCondition greater(final Day value)
	{
		return new GreaterCondition(this, value);
	}
	
	public final GreaterEqualCondition greaterOrEqual(final Day value)
	{
		return new GreaterEqualCondition(this, value);
	}
	
}

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
import java.util.Date;
import java.util.List;

import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;

public final class DateAttribute extends ObjectAttribute
{
	final boolean forbidTimestampColumn;

	/**
	 * @see Item#dateAttribute(Option)
	 */
	DateAttribute(final Option option)
	{
		this(option, false);
	}
	
	/**
	 * @see Item#dateAttribute(Option, boolean)
	 */
	DateAttribute(final Option option, final boolean forbidTimestampColumn)
	{
		super(option, Date.class, "date");
		this.forbidTimestampColumn = forbidTimestampColumn;
	}
	
	public ObjectAttribute copyAsTemplate()
	{
		return new DateAttribute(getTemplateOption());
	}
	
	protected List createColumns(final Table table, final String name, final boolean notNull)
	{
		final boolean useLong =
			forbidTimestampColumn ||
			!(getType().getModel().getDatabase() instanceof DatabaseTimestampCapable);
		
		return
			Collections.singletonList(
				useLong
				? (Column)new IntegerColumn(table, name, notNull, 20, true, null)
				: (Column)new TimestampColumn(table, name, notNull)
			);
	}
	
	Object cacheToSurface(final Object cache)
	{
		return cache==null ? null : new Date(((Long)cache).longValue());
	}
		
	Object surfaceToCache(final Object surface)
	{
		return surface==null ? null : new Long(((Date)surface).getTime());
	}
	
	public final LessCondition less(final Date value)
	{
		return new LessCondition(this, value);
	}
	
	public final LessEqualCondition lessOrEqual(final Date value)
	{
		return new LessEqualCondition(this, value);
	}
	
	public final GreaterCondition greater(final Date value)
	{
		return new GreaterCondition(this, value);
	}
	
	public final GreaterEqualCondition greaterOrEqual(final Date value)
	{
		return new GreaterEqualCondition(this, value);
	}
	
}

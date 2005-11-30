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

import java.util.Date;

import com.exedio.cope.search.EqualCondition;
import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;
import com.exedio.cope.search.NotEqualCondition;

public final class DateAttribute extends ObjectAttribute
{

	public DateAttribute(final Option option)
	{
		super(option, Date.class, "date");
	}
	
	public ObjectAttribute copyAsTemplate()
	{
		return new DateAttribute(getTemplateOption());
	}
	
	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		final boolean useLong =
			getType().getModel().getProperties().getDatabaseDontSupportNativeDate() ||
			!(getType().getModel().getDatabase() instanceof DatabaseTimestampCapable);
		
		return
				useLong
				? (Column)new IntegerColumn(table, name, notNull, 20, true, null)
				: (Column)new TimestampColumn(table, name, notNull);
	}
	
	Object cacheToSurface(final Row row)
	{
		final Object cell = row.get(getColumn());
		return cell==null ? null : new Date(((Long)cell).longValue());
	}
		
	void surfaceToCache(final Row row, final Object surface)
	{
		row.put(getColumn(), surface==null ? null : new Long(((Date)surface).getTime()));
	}
	
	public final Date get(final Item item)
	{
		return (Date)item.get(this);
	}
	
	public final void set(final Item item, final Date value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException, // TODO remove
			ReadOnlyViolationException
	{
		item.set(this, value);
	}

	public final EqualCondition equal(final Date value)
	{
		return new EqualCondition(null, this, value);
	}
	
	public final NotEqualCondition notEqual(final Date value)
	{
		return new NotEqualCondition(this, value);
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

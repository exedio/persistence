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

import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;

public final class DateAttribute extends FunctionAttribute
{

	private DateAttribute(final boolean readOnly, final boolean mandatory, final boolean unique)
	{
		super(readOnly, mandatory, unique, Date.class);
	}
	
	public DateAttribute(final Option option)
	{
		this(option.isFinal, option.mandatory, option.unique);
	}
	
	public FunctionAttribute copyFunctionAttribute()
	{
		return new DateAttribute(isfinal, mandatory, implicitUniqueConstraint!=null);
	}
	
	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		final boolean useLong =
			getType().getModel().getProperties().getDatabaseDontSupportNativeDate() ||
			!(getType().getModel().getDatabase().getDateTimestampType()!=null);
		
		return
				useLong
				? (Column)new IntegerColumn(table, name, notNull, 20, true, null)
				: (Column)new TimestampColumn(table, name, notNull);
	}
	
	Object get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return cell==null ? null : new Date(((Long)cell).longValue());
	}
		
	void set(final Row row, final Object surface)
	{
		row.put(getColumn(), surface==null ? null : new Long(((Date)surface).getTime()));
	}
	
	public final Date get(final Item item)
	{
		return (Date)getObject(item);
	}
	
	public final void set(final Item item, final Date value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			FinalViolationException
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

	/**
	 * @throws FinalViolationException
	 *         if this attribute is {@link #isFinal() final}.
	 */
	public final void touch(final Item item)
		throws
			UniqueViolationException,
			FinalViolationException
	{
		try
		{
			set(item, new Date()); // TODO: make a more efficient implementation
		}
		catch(MandatoryViolationException e)
		{
			throw new RuntimeException(e);
		}
	}

	public final AttributeValue map(final Date value)
	{
		return new AttributeValue(this, value);
	}
	
	public final EqualCondition equal(final Date value)
	{
		return new EqualCondition(this, value);
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

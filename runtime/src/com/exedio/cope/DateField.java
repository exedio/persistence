/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

public final class DateField extends FunctionField<Date>
{
	final boolean defaultNow;

	private DateField(
			final boolean isfinal, final boolean optional, final boolean unique,
			final Date defaultConstant, final boolean defaultNow)
	{
		super(isfinal, optional, unique, Date.class, defaultConstant);
		this.defaultNow = defaultNow;

		assert !(defaultConstant!=null && defaultNow);
		checkDefaultValue();
	}
	
	public DateField()
	{
		this(Item.MANDATORY);
	}
	
	public DateField(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null, false);
	}
	
	@Override
	public DateField copyFunctionField()
	{
		return new DateField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant, defaultNow);
	}
	
	@Override
	public DateField toFinal()
	{
		return new DateField(true, optional, implicitUniqueConstraint!=null, defaultConstant, defaultNow);
	}
	
	@Override
	public DateField unique()
	{
		return new DateField(isfinal, optional, true, defaultConstant, defaultNow);
	}
	
	public DateField defaultTo(final Date defaultConstant)
	{
		return new DateField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant, false);
	}
	
	public DateField defaultToNow()
	{
		return new DateField(isfinal, optional, implicitUniqueConstraint!=null, null, true);
	}
	
	public boolean isDefaultNow()
	{
		return defaultNow;
	}
	
	/**
	 * Returns true, if a value for the field should be specified
	 * on the creation of an item.
	 * This implementation returns
	 * <tt>{@link #isFinal() isFinal()} || ({@link #isMandatory() isMandatory()} && {@link #getDefaultConstant() getDefaultConstant()}==null && ! {@link #isDefaultNow()})</tt>.
	 */
	@Override
	public final boolean isInitial()
	{
		return isfinal || (!optional && (defaultConstant==null && !defaultNow));
	}
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		final Model model = getType().getModel();
		final boolean useLong =
			model.getProperties().getDatabaseDontSupportNativeDate() ||
			!(model.getDatabase().dialect.getDateTimestampType()!=null);
		
		return
				useLong
				? (Column)new IntegerColumn(table, name, optional, Long.MIN_VALUE, Long.MAX_VALUE, true)
				: (Column)new TimestampColumn(table, name, optional);
	}
	
	@Override
	Date get(final Row row)
	{
		final Object cell = row.get(getColumn());
		return cell==null ? null : new Date(((Long)cell).longValue());
	}
		
	@Override
	void set(final Row row, final Date surface)
	{
		row.put(getColumn(), surface==null ? null : Long.valueOf(surface.getTime()));
	}
	
	/**
	 * @throws FinalViolationException
	 *         if this field is {@link #isFinal() final}.
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

}

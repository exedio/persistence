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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.exedio.cope.instrument.Wrapper;

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
		this(false, false, false, null, false);
	}
	
	/**
	 * @deprecated use {@link #toFinal()}, {@link #unique()} and {@link #optional()} instead.
	 */
	@Deprecated
	public DateField(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null, false);
	}
	
	@Override
	public DateField copy()
	{
		return new DateField(isfinal, optional, unique, defaultConstant, defaultNow);
	}
	
	@Override
	public DateField toFinal()
	{
		return new DateField(true, optional, unique, defaultConstant, defaultNow);
	}
	
	@Override
	public DateField optional()
	{
		return new DateField(isfinal, true, unique, defaultConstant, defaultNow);
	}
	
	@Override
	public DateField unique()
	{
		return new DateField(isfinal, optional, true, defaultConstant, defaultNow);
	}
	
	public DateField defaultTo(final Date defaultConstant)
	{
		return new DateField(isfinal, optional, unique, defaultConstant, false);
	}
	
	public DateField defaultToNow()
	{
		return new DateField(isfinal, optional, unique, null, true);
	}
	
	public boolean isDefaultNow()
	{
		return defaultNow;
	}
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());
		
		if(!isfinal)
		{
			final Set<Class> exceptions = getSetterExceptions();
			exceptions.remove(MandatoryViolationException.class); // cannot set null
			
			result.add(
				new Wrapper(
					void.class, "touch",
					"Sets the current date for the date field {0}.", // TODO better text
					"toucher").
				addThrows(exceptions));
		}
			
		return Collections.unmodifiableList(result);
	}
	
	/**
	 * Returns true, if a value for the field should be specified
	 * on the creation of an item.
	 * This implementation returns
	 * <tt>({@link #isFinal() isFinal()} || {@link #isMandatory() isMandatory()}) && {@link #getDefaultConstant() getDefaultConstant()}==null && ! {@link #isDefaultNow()}</tt>.
	 */
	@Override
	public boolean isInitial()
	{
		return !defaultNow && super.isInitial();
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
				? (Column)new IntegerColumn(table, this, name, optional, Long.MIN_VALUE, Long.MAX_VALUE, true)
				: (Column)new TimestampColumn(table, this, name, optional);
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
	public void touch(final Item item)
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

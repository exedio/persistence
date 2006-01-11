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

import com.exedio.cope.function.LengthFunction;
import com.exedio.cope.function.UppercaseFunction;
import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;

public final class StringAttribute extends FunctionAttribute implements StringFunction
{
	private final int minimumLength;
	private final int maximumLength;

	private StringAttribute(final boolean readOnly, final boolean mandatory, final boolean unique, final int minimumLength, final int maximumLength)
	{
		super(readOnly, mandatory, unique, String.class, "string");
		this.minimumLength = minimumLength;
		this.maximumLength = maximumLength;
		if(minimumLength<0)
			throw new RuntimeException("mimimum length must be positive.");
		if(minimumLength>maximumLength)
			throw new RuntimeException("maximum length must be greater or equal mimimum length.");
	}
	
	public StringAttribute(final Option option)
	{
		this(option.readOnly, option.mandatory, option.unique, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * @deprecated use {@link #lengthMin(int)} instead.
	 */
	public StringAttribute(final Option option, final int minimumLength)
	{
		this(option.readOnly, option.mandatory, option.unique, minimumLength, Integer.MAX_VALUE);
	}
	
	/**
	 * @deprecated use {@link #lengthRange(int, int)} or {@link #lengthMax(int)} or {@link #lengthExact(int)} instead.
	 */
	public StringAttribute(final Option option, final int minimumLength, final int maximumLength)
	{
		this(option.readOnly, option.mandatory, option.unique, minimumLength, maximumLength);
	}
	
	public FunctionAttribute copyAsTemplate()
	{
		return new StringAttribute(readOnly, mandatory, implicitUniqueConstraint!=null, minimumLength, maximumLength);
	}
	
	public StringAttribute lengthRange(final int minimumLength, final int maximumLength)
	{
		return new StringAttribute(readOnly, mandatory, implicitUniqueConstraint!=null, minimumLength, maximumLength);
	}
	
	public StringAttribute lengthMin(final int minimumLength)
	{
		return new StringAttribute(readOnly, mandatory, implicitUniqueConstraint!=null, minimumLength, Integer.MAX_VALUE);
	}
	
	public StringAttribute lengthMax(final int maximumLength)
	{
		return new StringAttribute(readOnly, mandatory, implicitUniqueConstraint!=null, 0, maximumLength);
	}
	
	public StringAttribute lengthExact(final int exactLength)
	{
		return new StringAttribute(readOnly, mandatory, implicitUniqueConstraint!=null, exactLength, exactLength);
	}
	
	public final int getMinimumLength()
	{
		return minimumLength;
	}
	
	public final int getMaximumLength()
	{
		return maximumLength;
	}
	
	public final boolean isLengthConstrained()
	{
		return minimumLength!=0 || maximumLength!=Integer.MAX_VALUE;
	}
	
	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		return new StringColumn(table, name, notNull, minimumLength, maximumLength);
	}
	
	Object get(final Row row)
	{
		return (String)row.get(getColumn());
	}
		
	void set(final Row row, final Object surface)
	{
		final String cell;
		if(getType().getModel().supportsEmptyStrings()) // TODO dont fetch this that often
			cell = (String)surface;
		else
		{
			if(surface!=null && ((String)surface).length()==0)
				cell = null;
			else
				cell = (String)surface;
		}
		row.put(getColumn(), cell);
	}
	
	void checkNotNullValue(final Object value, final Item item)
		throws
			LengthViolationException
	{
		final String stringValue = (String)value;
		if(stringValue.length()<minimumLength)
			throw new LengthViolationException(item, this, stringValue, true);
		if(stringValue.length()>maximumLength)
			throw new LengthViolationException(item, this, stringValue, false);
	}
	
	public final String get(final Item item)
	{
		return (String)getObject(item);
	}
	
	public final void set(final Item item, final String value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			ReadOnlyViolationException
	{
		item.set(this, value);
	}

	public final AttributeValue map(final String value)
	{
		return new AttributeValue(this, value);
	}
	
	public final EqualCondition equal(final String value)
	{
		return new EqualCondition(this, value);
	}
	
	public final EqualCondition equal(final Join join, final String value)
	{
		return new EqualCondition(new JoinedFunction(this, join), value);
	}
	
	public final EqualAttributeCondition equal(final StringAttribute other)
	{
		return new EqualAttributeCondition(this, other);
	}
	
	public final NotEqualCondition notEqual(final String value)
	{
		return new NotEqualCondition(this, value);
	}
	
	public final LikeCondition like(final String value)
	{
		return new LikeCondition(this, value);
	}
	
	public final LessCondition less(final String value)
	{
		return new LessCondition(this, value);
	}
	
	public final LessEqualCondition lessOrEqual(final String value)
	{
		return new LessEqualCondition(this, value);
	}
	
	public final GreaterCondition greater(final String value)
	{
		return new GreaterCondition(this, value);
	}
	
	public final GreaterEqualCondition greaterOrEqual(final String value)
	{
		return new GreaterEqualCondition(this, value);
	}
	
	public final LengthFunction length()
	{
		return new LengthFunction(this);
	}
	
	public final UppercaseFunction uppercase()
	{
		return new UppercaseFunction(this);
	}
	
}

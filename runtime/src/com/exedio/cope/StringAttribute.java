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

import com.exedio.cope.function.LengthView;
import com.exedio.cope.function.UppercaseView;
import com.exedio.cope.search.GreaterCondition;
import com.exedio.cope.search.GreaterEqualCondition;
import com.exedio.cope.search.LessCondition;
import com.exedio.cope.search.LessEqualCondition;

public final class StringAttribute extends FunctionAttribute implements StringFunction
{
	private final int minimumLength;
	private final int maximumLength;
	private final boolean checkedLengthException;
	
	public static final int DEFAULT_LENGTH = 100;

	private StringAttribute(
			final boolean readOnly, final boolean mandatory, final boolean unique,
			final int minimumLength, final int maximumLength, final boolean checkedLengthException)
	{
		super(readOnly, mandatory, unique, String.class);
		this.minimumLength = minimumLength;
		this.maximumLength = maximumLength;
		this.checkedLengthException = checkedLengthException;

		if(minimumLength<0)
			throw new RuntimeException("mimimum length must be positive, but was " + minimumLength + '.');
		if(maximumLength<=0)
			throw new RuntimeException("maximum length must be greater zero, but was " + maximumLength + '.');
		if(minimumLength>maximumLength)
			throw new RuntimeException("maximum length must be greater or equal mimimum length, but was " + maximumLength + " and " + minimumLength + '.');
		
		assert checkedLengthException ? true : minimumLength==0;
	}
	
	public StringAttribute(final Option option)
	{
		this(option.isFinal, option.mandatory, option.unique, 0, DEFAULT_LENGTH, false);
	}
	
	/**
	 * @deprecated use {@link #lengthMin(int)} instead.
	 */
	public StringAttribute(final Option option, final int minimumLength)
	{
		this(option.isFinal, option.mandatory, option.unique, minimumLength, DEFAULT_LENGTH, true);
	}
	
	/**
	 * @deprecated use {@link #lengthRange(int, int)} or {@link #lengthMax(int)} or {@link #lengthExact(int)} instead.
	 */
	public StringAttribute(final Option option, final int minimumLength, final int maximumLength)
	{
		this(option.isFinal, option.mandatory, option.unique, minimumLength, maximumLength, true);
	}
	
	public FunctionAttribute copyFunctionAttribute()
	{
		return new StringAttribute(readOnly, mandatory, implicitUniqueConstraint!=null, minimumLength, maximumLength, checkedLengthException);
	}
	
	public StringAttribute lengthRange(final int minimumLength, final int maximumLength)
	{
		return new StringAttribute(readOnly, mandatory, implicitUniqueConstraint!=null, minimumLength, maximumLength, true);
	}
	
	public StringAttribute lengthMin(final int minimumLength)
	{
		return new StringAttribute(readOnly, mandatory, implicitUniqueConstraint!=null, minimumLength, DEFAULT_LENGTH, true);
	}
	
	/**
	 * @see #lengthMaxUnchecked(int)
	 */
	public StringAttribute lengthMax(final int maximumLength)
	{
		return new StringAttribute(readOnly, mandatory, implicitUniqueConstraint!=null, 0, maximumLength, true);
	}
	
	/**
	 * @see #lengthMax(int)
	 */
	public StringAttribute lengthMaxUnchecked(final int maximumLength)
	{
		return new StringAttribute(readOnly, mandatory, implicitUniqueConstraint!=null, 0, maximumLength, false);
	}
	
	public StringAttribute lengthExact(final int exactLength)
	{
		return new StringAttribute(readOnly, mandatory, implicitUniqueConstraint!=null, exactLength, exactLength, true);
	}
	
	public final int getMinimumLength()
	{
		return minimumLength;
	}
	
	public final int getMaximumLength()
	{
		return maximumLength;
	}
	
	public final boolean hasLengthConstraintCheckedException()
	{
		return checkedLengthException;
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
			LengthViolationException, LengthViolationRuntimeException
	{
		final String stringValue = (String)value;
		if(stringValue.length()<minimumLength)
		{
			assert checkedLengthException; // see equivalent assertion in constructor
			throw new LengthViolationException(item, this, stringValue, true);
		}
		if(stringValue.length()>maximumLength)
			if(checkedLengthException)
				throw new LengthViolationException(item, this, stringValue, false);
			else
				throw new LengthViolationRuntimeException(item, this, stringValue);
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
			FinalViolationException
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
	
	public final LengthView length()
	{
		return new LengthView(this);
	}
	
	public final UppercaseView uppercase()
	{
		return new UppercaseView(this);
	}
	
}

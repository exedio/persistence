/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.util.SortedSet;

import com.exedio.cope.function.LengthView;
import com.exedio.cope.function.UppercaseView;

/**
 * Represents an attribute within a {@link Type type},
 * that enables instances of that type to store a string.
 *
 * @author Ralf Wiebicke
 */
public final class StringField extends FunctionField<String> implements StringFunction
{
	private final int minimumLength;
	private final int maximumLength;
	
	public static final int DEFAULT_LENGTH = 100;

	private StringField(
			final boolean isfinal, final boolean optional, final boolean unique, final String defaultConstant,
			final int minimumLength, final int maximumLength)
	{
		super(isfinal, optional, unique, String.class, defaultConstant);
		this.minimumLength = minimumLength;
		this.maximumLength = maximumLength;

		if(minimumLength<0)
			throw new RuntimeException("mimimum length must be positive, but was " + minimumLength + '.');
		if(maximumLength<=0)
			throw new RuntimeException("maximum length must be greater zero, but was " + maximumLength + '.');
		if(minimumLength>maximumLength)
			throw new RuntimeException("maximum length must be greater or equal mimimum length, but was " + maximumLength + " and " + minimumLength + '.');
		
		checkDefaultValue();
	}
	
	/**
	 * Creates a new mandatory <tt>StringAttribute</tt>.
	 */
	public StringField()
	{
		this(Item.MANDATORY);
	}
	
	public StringField(final Option option)
	{
		this(option.isFinal, option.optional, option.unique, null, 0, DEFAULT_LENGTH);
	}
	
	/**
	 * @deprecated use {@link #lengthMin(int)} instead.
	 */
	@Deprecated
	public StringField(final Option option, final int minimumLength)
	{
		this(option.isFinal, option.optional, option.unique, null, minimumLength, DEFAULT_LENGTH);
	}
	
	/**
	 * @deprecated use {@link #lengthRange(int, int)} or {@link #lengthMax(int)} or {@link #lengthExact(int)} instead.
	 */
	@Deprecated
	public StringField(final Option option, final int minimumLength, final int maximumLength)
	{
		this(option.isFinal, option.optional, option.unique, null, minimumLength, maximumLength);
	}
	
	@Override
	public StringField copyFunctionField()
	{
		return new StringField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant, minimumLength, maximumLength);
	}
	
	public StringField defaultTo(final String defaultConstant)
	{
		return new StringField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant, minimumLength, maximumLength);
	}
	
	public StringField lengthRange(final int minimumLength, final int maximumLength)
	{
		return new StringField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant, minimumLength, maximumLength);
	}
	
	public StringField lengthMin(final int minimumLength)
	{
		return new StringField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant, minimumLength, DEFAULT_LENGTH);
	}
	
	public StringField lengthMax(final int maximumLength)
	{
		return new StringField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant, 0, maximumLength);
	}
	
	/**
	 * @deprecated use {@link #lengthMax(int)}.
	 */
	@Deprecated
	public StringField lengthMaxUnchecked(final int maximumLength)
	{
		return lengthMax(maximumLength);
	}
	
	public StringField lengthExact(final int exactLength)
	{
		return new StringField(isfinal, optional, implicitUniqueConstraint!=null, defaultConstant, exactLength, exactLength);
	}
	
	public final int getMinimumLength()
	{
		return minimumLength;
	}
	
	public final int getMaximumLength()
	{
		return maximumLength;
	}
	
	@Override
	public SortedSet<Class> getSetterExceptions()
	{
		final SortedSet<Class> result = super.getSetterExceptions();
		result.add(LengthViolationException.class);
		return result;
	}
	
	private boolean convertEmptyStrings = false;
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		this.convertEmptyStrings = !getType().getModel().supportsEmptyStrings();
		return new StringColumn(table, name, optional, minimumLength, maximumLength);
	}
	
	@Override
	String get(final Row row)
	{
		return (String)row.get(getColumn());
	}
		
	@Override
	void set(final Row row, final String surface)
	{
		final String cell;
		if(!convertEmptyStrings)
			cell = surface;
		else
		{
			if(surface!=null && surface.length()==0)
				cell = null;
			else
				cell = surface;
		}
		row.put(getColumn(), cell);
	}
	
	@Override
	void checkNotNullValue(final String value, final Item exceptionItem)
		throws
			LengthViolationException
	{
		if(convertEmptyStrings && value.length()==0 && !optional)
			throw new MandatoryViolationException(this, exceptionItem);
		
		final int length = value.length();
		if(length<minimumLength)
			throw new LengthViolationException(this, exceptionItem, value, true, minimumLength);
		if(length>maximumLength)
			throw new LengthViolationException(this, exceptionItem, value, false, maximumLength);
	}
	
	// convenience methods for conditions and views ---------------------------------

	public final LikeCondition like(final String value)
	{
		return new LikeCondition(this, value);
	}
	
	public final LikeCondition startsWith(final String value)
	{
		return LikeCondition.startsWith(this, value);
	}
	
	public final LikeCondition endsWith(final String value)
	{
		return LikeCondition.endsWith(this, value);
	}
	
	public final LikeCondition contains(final String value)
	{
		return LikeCondition.contains(this, value);
	}
	
	public final LengthView length()
	{
		return new LengthView(this);
	}
	
	public final UppercaseView toUpperCase()
	{
		return new UppercaseView(this);
	}
	
	/**
	 * @deprecated renamed to {@link #toUpperCase()}
	 */
	@Deprecated
	public final UppercaseView uppercase()
	{
		return toUpperCase();
	}
	
	public final EqualCondition equalIgnoreCase(final String value)
	{
		return toUpperCase().equal(value.toUpperCase());
	}
	
	public final LikeCondition likeIgnoreCase(final String value)
	{
		return toUpperCase().like(value.toUpperCase());
	}
	
	public final LikeCondition startsWithIgnoreCase(final String value)
	{
		return LikeCondition.startsWith(toUpperCase(), value.toUpperCase());
	}
	
	public final LikeCondition endsWithIgnoreCase(final String value)
	{
		return LikeCondition.endsWith(toUpperCase(), value.toUpperCase());
	}
	
	public final LikeCondition containsIgnoreCase(final String value)
	{
		return LikeCondition.contains(toUpperCase(), value.toUpperCase());
	}
	
	@Override
	public BindStringFunction bind(final Join join)
	{
		return new BindStringFunction(this, join);
	}
}

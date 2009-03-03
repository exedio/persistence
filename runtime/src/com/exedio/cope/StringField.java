/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.Set;

import com.exedio.cope.util.CharSet;

/**
 * Represents a field within a {@link Type type},
 * that enables instances of that type to store a string.
 *
 * @author Ralf Wiebicke
 */
public final class StringField extends FunctionField<String> implements StringFunction
{
	private final int minimumLength;
	private final int maximumLength;
	private final CharSet charSet;
	
	public static final int DEFAULT_LENGTH = 80; // length still fits into byte with utf8 encoding (3*80=240<255)

	private StringField(
			final boolean isfinal, final boolean optional, final boolean unique, final String defaultConstant,
			final int minimumLength, final int maximumLength, final CharSet charSet)
	{
		super(isfinal, optional, unique, String.class, defaultConstant);
		this.minimumLength = minimumLength;
		this.maximumLength = maximumLength;
		this.charSet = charSet;

		if(minimumLength<0)
			throw new IllegalArgumentException("mimimum length must be positive, but was " + minimumLength + '.');
		if(maximumLength<=0)
			throw new IllegalArgumentException("maximum length must be greater zero, but was " + maximumLength + '.');
		if(minimumLength>maximumLength)
			throw new IllegalArgumentException("maximum length must be greater or equal mimimum length, but was " + maximumLength + " and " + minimumLength + '.');
		
		checkDefaultConstant();
	}
	
	/**
	 * Creates a new mandatory <tt>StringField</tt>.
	 */
	public StringField()
	{
		this(false, false, false, null, 0, DEFAULT_LENGTH, null);
	}
	
	@Override
	public StringField copy()
	{
		return new StringField(isfinal, optional, unique, defaultConstant, minimumLength, maximumLength, charSet);
	}
	
	@Override
	public StringField toFinal()
	{
		return new StringField(true, optional, unique, defaultConstant, minimumLength, maximumLength, charSet);
	}
	
	@Override
	public StringField optional()
	{
		return new StringField(isfinal, true, unique, defaultConstant, minimumLength, maximumLength, charSet);
	}
	
	@Override
	public StringField unique()
	{
		return new StringField(isfinal, optional, true, defaultConstant, minimumLength, maximumLength, charSet);
	}

	@Override
	public StringField nonUnique()
	{
		return new StringField(isfinal, optional, false, defaultConstant, minimumLength, maximumLength, charSet);
	}
	
	public StringField defaultTo(final String defaultConstant)
	{
		return new StringField(isfinal, optional, unique, defaultConstant, minimumLength, maximumLength, charSet);
	}
	
	public StringField lengthRange(final int minimumLength, final int maximumLength)
	{
		return new StringField(isfinal, optional, unique, defaultConstant, minimumLength, maximumLength, charSet);
	}
	
	public StringField lengthMin(final int minimumLength)
	{
		return new StringField(isfinal, optional, unique, defaultConstant, minimumLength, DEFAULT_LENGTH, charSet);
	}
	
	public StringField lengthMax(final int maximumLength)
	{
		return new StringField(isfinal, optional, unique, defaultConstant, 0, maximumLength, charSet);
	}
	
	public StringField lengthExact(final int exactLength)
	{
		return new StringField(isfinal, optional, unique, defaultConstant, exactLength, exactLength, charSet);
	}
	
	public StringField charSet(final CharSet charSet)
	{
		return new StringField(isfinal, optional, unique, defaultConstant, minimumLength, maximumLength, charSet);
	}
	
	public final int getMinimumLength()
	{
		return minimumLength;
	}
	
	public final int getMaximumLength()
	{
		return maximumLength;
	}
	
	public final CharSet getCharSet()
	{
		return charSet;
	}
	
	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = super.getInitialExceptions();
		result.add(StringLengthViolationException.class);
		if(charSet!=null)
			result.add(StringCharSetViolationException.class);
		return result;
	}
	
	private boolean convertEmptyStrings = false;
	
	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		this.convertEmptyStrings = !getType().getModel().supportsEmptyStrings();
		return new StringColumn(table, this, name, optional, minimumLength, maximumLength, charSet);
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
	void checkNotNull(final String value, final Item exceptionItem)
		throws
			StringLengthViolationException, StringCharSetViolationException
	{
		if(convertEmptyStrings && value.length()==0 && !optional)
			throw new MandatoryViolationException(this, exceptionItem);
		
		final int length = value.length();
		if(length<minimumLength)
			throw new StringLengthViolationException(this, exceptionItem, value, true, minimumLength);
		if(length>maximumLength)
			throw new StringLengthViolationException(this, exceptionItem, value, false, maximumLength);
		if(charSet!=null)
		{
			for(int i = 0; i<length; i++)
				if(!charSet.contains(value.charAt(i)))
					throw new StringCharSetViolationException(this, exceptionItem, value, value.charAt(i), i);
		}
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
	
	public final Condition equalIgnoreCase(final String value)
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
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated use {@link #lengthMax(int)}.
	 */
	@Deprecated
	public StringField lengthMaxUnchecked(final int maximumLength)
	{
		return lengthMax(maximumLength);
	}
	
	/**
	 * @deprecated renamed to {@link #toUpperCase()}
	 */
	@Deprecated
	public final UppercaseView uppercase()
	{
		return toUpperCase();
	}
	
	/**
	 * @deprecated Use {@link #charSet(CharSet)} instead
	 */
	@Deprecated
	public StringField characterSet(final com.exedio.cope.util.CharacterSet characterSet)
	{
		return charSet(characterSet.getCharSet());
	}
	
	/**
	 * @deprecated Use {@link #getCharSet()} instead
	 */
	@Deprecated
	public com.exedio.cope.util.CharacterSet getCharacterSet()
	{
		return new com.exedio.cope.util.CharacterSet(getCharSet());
	}
}

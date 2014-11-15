/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.misc.Check.requireGreaterZero;
import static com.exedio.cope.misc.Check.requireNonNegative;

import com.exedio.cope.util.CharSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;

/**
 * Represents a field within a {@link Type type},
 * that enables instances of that type to store a string.
 *
 * @author Ralf Wiebicke
 */
public final class StringField extends FunctionField<String>
	implements StringFunction
{
	public static final int DEFAULT_MINIMUM_LENGTH = 1;
	public static final int DEFAULT_MAXIMUM_LENGTH = 80; // length still fits into byte with utf8 encoding (3*80=240<255)

	private static final long serialVersionUID = 1l;


	private final int minimumLength;
	private final int maximumLength;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final CharSet charSet;

	private StringField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final ItemField<?>[] copyFrom,
			final DefaultSource<String> defaultSource,
			final int minimumLength,
			final int maximumLength,
			final CharSet charSet)
	{
		super(isfinal, optional, unique, copyFrom, String.class, defaultSource);
		this.minimumLength = minimumLength;
		this.maximumLength = maximumLength;
		this.charSet = charSet;

		requireNonNegative(minimumLength, "minimumLength");
		requireGreaterZero(maximumLength, "maximumLength");
		if(minimumLength>maximumLength)
			throw new IllegalArgumentException("maximumLength must be greater or equal minimumLength, but was " + maximumLength + " and " + minimumLength);

		mountDefaultSource();
	}

	public StringField(final StringFieldMinimumLength minimumLength)
	{
		this(false, false, false, null, null, minimumLength.value, DEFAULT_MAXIMUM_LENGTH, null);
	}

	/**
	 * @deprecated
	 * Check carefully, if empty string should really be allowed.
	 * If yes, use {@link #lengthMin(int) lengthMin(0)} instead.
	 */
	@Deprecated
	public static final StringFieldMinimumLength EMPTY = new StringFieldMinimumLength(0);

	/**
	 * Creates a new mandatory <tt>StringField</tt>.
	 */
	public StringField()
	{
		this(false, false, false, null, null, DEFAULT_MINIMUM_LENGTH, DEFAULT_MAXIMUM_LENGTH, null);
	}

	@Override
	public StringField copy()
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultSource, minimumLength, maximumLength, charSet);
	}

	@Override
	public StringField toFinal()
	{
		return new StringField(true, optional, unique, copyFrom, defaultSource, minimumLength, maximumLength, charSet);
	}

	@Override
	public StringField optional()
	{
		return new StringField(isfinal, true, unique, copyFrom, defaultSource, minimumLength, maximumLength, charSet);
	}

	@Override
	public StringField unique()
	{
		return new StringField(isfinal, optional, true, copyFrom, defaultSource, minimumLength, maximumLength, charSet);
	}

	@Override
	public StringField nonUnique()
	{
		return new StringField(isfinal, optional, false, copyFrom, defaultSource, minimumLength, maximumLength, charSet);
	}

	@Override
	public StringField copyFrom(final ItemField<?> copyFrom)
	{
		return new StringField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultSource, minimumLength, maximumLength, charSet);
	}

	@Override
	public StringField noDefault()
	{
		return new StringField(isfinal, optional, unique, copyFrom, null, minimumLength, maximumLength, charSet);
	}

	@Override
	public StringField defaultTo(final String defaultConstant)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultConstant(defaultConstant), minimumLength, maximumLength, charSet);
	}

	public StringField lengthRange(final int minimumLength, final int maximumLength)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultSource, minimumLength, maximumLength, charSet);
	}

	public StringField lengthMin(final int minimumLength)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultSource, minimumLength, maximumLength, charSet);
	}

	public StringField lengthMax(final int maximumLength)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultSource, minimumLength, maximumLength, charSet);
	}

	public StringField lengthExact(final int exactLength)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultSource, exactLength, exactLength, charSet);
	}

	public StringField charSet(final CharSet charSet)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultSource, minimumLength, maximumLength, charSet);
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

	public SelectType<String> getValueType()
	{
		return SimpleSelectType.STRING;
	}

	private boolean convertEmptyStrings = false;

	@Override
	Column createColumn(final Table table, final String name, final boolean optional)
	{
		this.convertEmptyStrings = !getType().getModel().supportsEmptyStrings();
		return new StringColumn(table, name, optional, minimumLength, maximumLength, charSet, getAnnotation(MysqlExtendedVarchar.class));
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
			if(surface!=null && surface.isEmpty())
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
		if(convertEmptyStrings && value.isEmpty() && !optional)
			throw MandatoryViolationException.create(this, exceptionItem);

		final int length = value.length();
		if(length<minimumLength||length>maximumLength)
			throw new StringLengthViolationException(this, exceptionItem, value);
		if(charSet!=null)
		{
			final int i = charSet.indexOfNotContains(value);
			if(i>=0)
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
		return UppercaseView.equalIgnoreCase(this, value);
	}

	public final LikeCondition likeIgnoreCase(final String value)
	{
		return UppercaseView.likeIgnoreCase(this, value);
	}

	public final LikeCondition startsWithIgnoreCase(final String value)
	{
		return UppercaseView.startsWithIgnoreCase(this, value);
	}

	public final LikeCondition endsWithIgnoreCase(final String value)
	{
		return UppercaseView.endsWithIgnoreCase(this, value);
	}

	public final LikeCondition containsIgnoreCase(final String value)
	{
		return UppercaseView.containsIgnoreCase(this, value);
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

	/**
	 * @deprecated Use {@link #DEFAULT_MAXIMUM_LENGTH} instead
	 */
	@Deprecated
	public static final int DEFAULT_LENGTH = DEFAULT_MAXIMUM_LENGTH;
}

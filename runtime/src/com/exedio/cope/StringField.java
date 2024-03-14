/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.util.Check.requireGreaterZero;
import static com.exedio.cope.util.Check.requireNonNegative;

import com.exedio.cope.util.CharSet;
import com.exedio.cope.vault.VaultProperties;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

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
	private final CharSet charSet;

	private final String regexp;
	private final Pattern icuPattern;

	private StringField(
			final boolean isfinal,
			final boolean optional,
			final boolean unique,
			final CopyFrom[] copyFrom,
			final DefaultSupplier<String> defaultS,
			final int minimumLength,
			final int maximumLength,
			final CharSet charSet,
			final String regexp)
	{
		super(isfinal, optional, String.class, unique, copyFrom, defaultS);
		this.minimumLength = minimumLength;
		this.maximumLength = maximumLength;
		this.charSet = charSet;
		this.regexp = regexp;

		requireNonNegative(minimumLength, "minimumLength");
		requireGreaterZero(maximumLength, "maximumLength");
		if(minimumLength>maximumLength)
			throw new IllegalArgumentException("maximumLength must be greater or equal minimumLength, but was " + maximumLength + " and " + minimumLength);

		if (regexp != null)
		{
			if (regexp.isEmpty())
			{
				throw new IllegalArgumentException("pattern must be null or non-empty");
			}
		}

		this.icuPattern = regexp == null ? null : Pattern.compile(RegexpLikeCondition.getIcuRegexp(regexp));

		mountDefault();
	}

	/**
	 * Creates a new mandatory {@code StringField}.
	 */
	public StringField()
	{
		this(false, false, false, null, null, DEFAULT_MINIMUM_LENGTH, DEFAULT_MAXIMUM_LENGTH, null, null);
	}

	@Override
	public StringField copy()
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	@Override
	public StringField toFinal()
	{
		return new StringField(true, optional, unique, copyFrom, defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	@Override
	public StringField optional()
	{
		return new StringField(isfinal, true, unique, copyFrom, defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	@Override
	public StringField mandatory()
	{
		return new StringField(isfinal, false, unique, copyFrom, defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	@Override
	public StringField unique()
	{
		return new StringField(isfinal, optional, true, copyFrom, defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	@Override
	public StringField nonUnique()
	{
		return new StringField(isfinal, optional, false, copyFrom, defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	@Override
	public StringField copyFrom(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.RESOLVE_TEMPLATE));
	}

	@Override
	public StringField copyFrom(final ItemField<?> target, final Supplier<? extends FunctionField<String>> template)
	{
		return copyFrom(new CopyFrom(target, template));
	}

	@Override
	public StringField copyFromSelf(final ItemField<?> target)
	{
		return copyFrom(new CopyFrom(target, CopyConstraint.SELF_TEMPLATE));
	}

	private StringField copyFrom(final CopyFrom copyFrom)
	{
		return new StringField(isfinal, optional, unique, addCopyFrom(copyFrom), defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	@Override
	public StringField noCopyFrom()
	{
		return new StringField(isfinal, optional, unique, null, defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	@Override
	public StringField noDefault()
	{
		return new StringField(isfinal, optional, unique, copyFrom, null, minimumLength, maximumLength, charSet, regexp);
	}

	@Override
	public StringField defaultTo(final String defaultConstant)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultConstant(defaultConstant), minimumLength, maximumLength, charSet, regexp);
	}

	public StringField lengthRange(final int minimumLength, final int maximumLength)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	public StringField lengthMin(final int minimumLength)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	public StringField lengthMax(final int maximumLength)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	public StringField lengthExact(final int exactLength)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultS, exactLength, exactLength, charSet, regexp);
	}

	public StringField charSet(final CharSet charSet)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	/**
	 * Will constrain this field to the given pattern. Please note that this pattern will be expanded to
	 * force a (possibly multi-line) match against the whole value and to enable the dot to match newlines.
	 * The necessary tokens will be appended depending on the underlying database functionality e.G. for ICU engines your
	 * pattern will be expanded to (?s)\Apattern\z.
	 */
	public StringField regexp(final String regexp)
	{
		return new StringField(isfinal, optional, unique, copyFrom, defaultS, minimumLength, maximumLength, charSet, regexp);
	}

	public int getMinimumLength()
	{
		return minimumLength;
	}

	public int getMaximumLength()
	{
		return maximumLength;
	}

	public CharSet getCharSet()
	{
		return charSet;
	}

	public String getRegexp()
	{
		return regexp;
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

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<String> getValueType()
	{
		return SimpleSelectType.STRING;
	}

	@Override
	public StringFunction bind(final Join join)
	{
		return new BindStringFunction(this, join);
	}

	/**
	 * The result causes an {@link UnsupportedQueryException} when used,
	 * <ul>
	 * <li>if {@code data} is stored in a {@link Vault vault} and
	 *     {@code algorithm} does not match {@link VaultProperties#getAlgorithm()}
	 *     or
	 * <li>if {@code data} is <em>not</em> stored in a vault and
	 *     {@code algorithm} is not contained in {@link Model#getSupportedDataHashAlgorithms()}
	 * </ul>
	 * @see #hashDoesNotMatchIfSupported(String, DataField)
	 */
	@Nonnull
	public Condition hashMatchesIfSupported(
			@Nonnull final String algorithm,
			@Nonnull final DataField data)
	{
		return new HashCondition(this, algorithm, data);
	}

	/**
	 * The result causes an {@link UnsupportedQueryException} when used,
	 * <ul>
	 * <li>if {@code data} is stored in a {@link Vault vault} and
	 *     {@code algorithm} does not match {@link VaultProperties#getAlgorithm()}
	 *     or
	 * <li>if {@code data} is <em>not</em> stored in a vault and
	 *     {@code algorithm} is not contained in {@link Model#getSupportedDataHashAlgorithms()}
	 * </ul>
	 * @see #hashMatchesIfSupported(String, DataField)
	 */
	@Nonnull
	public Condition hashDoesNotMatchIfSupported(
			@Nonnull final String algorithm,
			@Nonnull final DataField data)
	{
		return hashMatchesIfSupported(algorithm, data).not();
	}


	@Override
	Column createColumn(
			final Table table,
			final String name,
			final boolean optional,
			final Connect connect,
			final ModelMetrics metrics)
	{
		return new StringColumn(table, name, optional, minimumLength, maximumLength, charSet, regexp, getAnnotation(MysqlExtendedVarchar.class));
	}

	@Override
	String get(final Row row)
	{
		return (String)row.get(getColumn());
	}

	@Override
	void set(final Row row, final String surface)
	{
		row.put(getColumn(), surface);
	}

	@Override
	void checkNotNull(final String value, final Item exceptionItem)
		throws
			StringLengthViolationException, StringCharSetViolationException
	{
		final int length = value.length();
		if(length<minimumLength||length>maximumLength)
			throw new StringLengthViolationException(this, exceptionItem, value);
		if(charSet!=null)
		{
			final int i = charSet.indexOfNotContains(value);
			if(i>=0)
				throw new StringCharSetViolationException(this, exceptionItem, value, value.charAt(i), i);
		}
		if(icuPattern != null && ! icuPattern.matcher(value).matches())
		{
			throw new StringRegexpPatternViolationException(this, exceptionItem, value);
		}
	}

	static String truncateValue(final String value)
	{
		final StringBuilder bf = new StringBuilder();
		final int length = value.length();
		final boolean truncate = length>200;
		bf.append('\'');
		if(truncate)
		{
			bf.append(value, 0, 100).
				append(" ... ").
				append(value, length-20, length);
		}
		else
		{
			bf.append(value);
		}
		bf.append('\'');

		if(truncate)
			bf.append(" (truncated, was ").
				append(length).
				append(" characters)");

		return bf.toString();
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #hashMatchesIfSupported(String, DataField)} instead.
	 */
	@Deprecated
	@Nonnull
	public Condition hashMatches(
			@Nonnull final String algorithm,
			@Nonnull final DataField data)
	{
		return hashMatchesIfSupported(algorithm, data);
	}

	/**
	 * @deprecated Use {@link #hashDoesNotMatchIfSupported(String, DataField)} instead.
	 */
	@Deprecated
	@Nonnull
	public Condition hashDoesNotMatch(
			@Nonnull final String algorithm,
			@Nonnull final DataField data)
	{
		return hashDoesNotMatchIfSupported(algorithm, data);
	}
}

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

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.util.function.Consumer;

public final class LikeCondition extends Condition
{
	@Serial
	private static final long serialVersionUID = 1l;

	/**
	 * @deprecated Use {@link #getFunction()} instead
	 */
	@Deprecated
	public final StringFunction function;

	/**
	 * @deprecated Use {@link #getValue()} instead
	 */
	@Deprecated
	public final String value;

	/**
	 * Creates a new LikeCondition.
	 * @deprecated
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper method
	 * {@link StringFunction#like(String)}.
	 */
	@Deprecated
	public LikeCondition(
			final StringFunction function,
			final String value)
	{
		this.function = requireNonNull(function, "function");
		this.value = requireNonNull(value, "value");
	}

	public StringFunction getFunction()
	{
		return function;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	void append(final Statement st)
	{
		final Dialect dialect = st.dialect;
		st.append(function).
			append(" LIKE ").
			appendParameterAny(dialect.maskLikePattern(value));

		if(dialect.likeRequiresEscapeBackslash())
			st.append(" ESCAPE '\\'");
	}

	@Override
	void requireSupportForGetTri()
	{
		throw new IllegalArgumentException("not yet implemented: " + this); // TODO
	}

	@Override
	Trilean getTri(final FieldValues item)
	{
		throw new IllegalArgumentException("not yet implemented: " + this); // TODO
		// once this method is implemented, implementation of #copy(CopyMapper) is needed to support blocks
	}

	@Override
	void check(final TC tc)
	{
		Cope.check(function, tc, null);
	}

	@Override
	public void forEachFieldCovered(final Consumer<Field<?>> action)
	{
		function.forEachFieldCovered(action);
	}

	@Override
	LikeCondition copy(final CopyMapper mapper)
	{
		// This is ok as long as getTri is not implemented as well.
		// Then we cannot use it in CheckConstraint, which is what copy is for.
		throw new RuntimeException("not yet implemented");
	}

	@Override
	public Condition bind(final Join join)
	{
		return new LikeCondition(function.bind(join), value);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof final LikeCondition o))
			return false;

		return function.equals(o.function) && value.equals(o.value);
	}

	@Override
	public int hashCode()
	{
		return function.hashCode() ^ value.hashCode() ^ 1872643;
	}

	@Override
	void toString(final StringBuilder sb, final boolean key, final Type<?> defaultType)
	{
		function.toString(sb, defaultType);
		sb.append(" like '").
			append(value).
			append('\'');
	}

	public static final char WILDCARD = '%';

	public static Condition startsWith(final StringFunction function, final String value)
	{
		return new LikeCondition(function, escapeSpecials(value) + WILDCARD);
	}

	public static Condition endsWith(final StringFunction function, final String value)
	{
		return new LikeCondition(function, WILDCARD + escapeSpecials(value));
	}

	public static Condition contains(final StringFunction function, final String value)
	{
		return new LikeCondition(function, WILDCARD + escapeSpecials(value) + WILDCARD);
	}

	private static String escapeSpecials(final String value)
	{
		return value.replaceAll("([\\\\%_])", "\\\\$1");
	}
}

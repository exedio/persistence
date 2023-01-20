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

import java.util.function.Consumer;

public final class LikeCondition extends Condition
{
	private static final long serialVersionUID = 1l;

	public final StringFunction function;
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

	@Override
	void append(final Statement bf)
	{
		bf.append(function).
			append(" like ").
			appendParameterAny(value);
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
	public void acceptFieldsCovered(final Consumer<Field<?>> consumer)
	{
		function.acceptFieldsCovered(consumer);
	}

	@Override
	LikeCondition copy(final CopyMapper mapper)
	{
		// This is ok as long as getTri is not implemented as well.
		// Then we cannot use it in CheckConstraint, which is what copy is for.
		throw new RuntimeException("not yet implemented");
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof LikeCondition))
			return false;

		final LikeCondition o = (LikeCondition)other;

		return function.equals(o.function) && value.equals(o.value);
	}

	@Override
	public int hashCode()
	{
		return function.hashCode() ^ value.hashCode() ^ 1872643;
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		function.toString(bf, defaultType);
		bf.append(" like '").
			append(value).
			append('\'');
	}

	public static final char WILDCARD = '%';

	public static LikeCondition startsWith(final StringFunction function, final String value)
	{
		return new LikeCondition(function, value + WILDCARD);
	}

	public static LikeCondition endsWith(final StringFunction function, final String value)
	{
		return new LikeCondition(function, WILDCARD + value);
	}

	public static LikeCondition contains(final StringFunction function, final String value)
	{
		return new LikeCondition(function, WILDCARD + value + WILDCARD);
	}
}

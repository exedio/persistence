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

public final class IsNullCondition<E> extends Condition
{
	@Serial
	private static final long serialVersionUID = 2l;

	private final Selectable<E> function;
	private final boolean not;

	/**
	 * Creates a new IsNullCondition.
	 * @deprecated
	 * Instead of using this constructor directly,
	 * you may want to use the more type-safe wrapper methods.
	 * @see com.exedio.cope.Function#isNull()
	 * @see com.exedio.cope.Function#isNotNull()
	 */
	@Deprecated
	public IsNullCondition(
			final Selectable<E> function,
			final boolean not)
	{
		this.function = requireNonNull(function, "function");
		this.not = not;
	}

	/**
	 * @deprecated
	 * Instead of using this constructor directly,
	 * you may want to use the more type-safe wrapper methods.
	 * @see com.exedio.cope.Function#isNull()
	 * @see com.exedio.cope.Function#isNotNull()
	 */
	@Deprecated
	public IsNullCondition(
			final Function<E> function,
			final boolean not)
	{
		this((Selectable<E>)function, not);
	}

	@Override
	void append(final Statement bf)
	{
		bf.append(function).
			append(not ? " IS NOT NULL" : " IS NULL");
	}

	@Override
	void requireSupportForGetTri()
	{
		// TODO do something nicer
		if(!(function instanceof Function))
			throw new IllegalArgumentException("not supported for non-function: " + function);
	}

	@Override
	Trilean getTri(final FieldValues item) throws UnsupportedGetException
	{
		requireSupportForGetTri();
		return Trilean.valueOf( (((Function<E>)function).get(item)==null) ^ not );
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
	IsNullCondition<E> copy(final CopyMapper mapper)
	{
		return new IsNullCondition<>(mapper.getS(function), not);
	}

	@Override
	public Condition bind(final Join join)
	{
		return new IsNullCondition<>(((Function<?>)function).bind(join), not);
	}

	@Override
	public Condition not()
	{
		return new IsNullCondition<>(function, !not);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof final IsNullCondition<?> o))
			return false;

		return function.equals(o.function) && not==o.not;
	}

	@Override
	public int hashCode()
	{
		return function.hashCode() ^ (not ? 934658732 : 546637842);
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		function.toString(bf, defaultType);
		bf.append(not ? " is not null" : " is null");
	}
}

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

class BindFunction<E> implements Function<E>
{
	private static final long serialVersionUID = 1l;

	final Function<E> function;
	final Join join;

	BindFunction(final Function<E> function, final Join join)
	{
		this.function = requireNonNull(function, "function");
		this.join = requireNonNull(join, "join");
	}

	@Override
	public final void requireSupportForGet() throws UnsupportedGetException
	{
		function.requireSupportForGet();
	}

	@Override
	public final E get(final Item item) throws UnsupportedGetException
	{
		return function.get(item);
	}

	@Override
	public final Class<E> getValueClass()
	{
		return function.getValueClass();
	}

	@Override
	public SelectType<E> getValueType()
	{
		return function.getValueType();
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public final void check(final TC tc, final Join join)
	{
		function.check(tc, this.join);
	}

	@Override
	public final void acceptFieldsCovered(final Consumer<Field<?>> consumer)
	{
		function.acceptFieldsCovered(consumer);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public final void append(final Statement bf, final Join join)
	{
		function.append(bf, this.join);
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	public final void appendSelect(final Statement bf, final Join join)
	{
		function.appendSelect(bf, this.join);
	}

	@Override
	public final Type<?> getType()
	{
		return function.getType();
	}

	@Override
	public final boolean equals(final Object other)
	{
		if(!(other instanceof BindFunction<?>))
			return false;

		final BindFunction<?> o = (BindFunction<?>)other;

		return function.equals(o.function) && join.index==o.join.index; // using Join#equals(Object) causes infinite recursion
	}

	@Override
	public final int hashCode()
	{
		return function.hashCode() ^ join.index; // using Join#hashCode() causes infinite recursion
	}

	@Override
	public final String toString()
	{
		return join.getToStringAlias() + '.' + function;
	}

	@Override
	public final void toString(final StringBuilder bf, final Type<?> defaultType)
	{
		bf.append(join.getToStringAlias()).
			append('.');
		function.toString(bf, defaultType);
	}

	/**
	 * Return this.
	 * It makes no sense wrapping a BindFunction into another BindFunction,
	 * because the inner BindFunction &quot;wins&quot;.
	 */
	@Override
	public Function<E> bind(final Join join)
	{
		return this;
	}
}

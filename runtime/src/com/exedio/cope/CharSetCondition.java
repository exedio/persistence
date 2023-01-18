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

import com.exedio.cope.util.CharSet;
import java.util.function.Consumer;

public final class CharSetCondition extends Condition
{
	private static final long serialVersionUID = 1l;

	public final StringFunction function;
	public final CharSet value;

	/**
	 * Creates a new CharSetCondition.
	 * @deprecated
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper method.
	 * @see StringFunction#conformsTo(CharSet)
	 */
	@Deprecated
	public CharSetCondition(
			final StringFunction function,
			final CharSet value)
	{
		this.function = requireNonNull(function, "function");
		this.value = requireNonNull(value, "value");
	}

	@Override
	void append(final Statement bf)
	{
		bf.dialect.append(bf, function, null, value);
	}

	@Override
	void requireSupportForGetTri() throws UnsupportedGetException
	{
		function.requireSupportForGet();
	}

	@Override
	Trilean getTri(final FieldValues item) throws UnsupportedGetException
	{
		final String s = function.get(item);
		if(s==null)
			return Trilean.Null;

		final int i = value.indexOfNotContains(s);
		return Trilean.valueOf( i<0 );
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
	CharSetCondition copy(final CopyMapper mapper)
	{
		throw new RuntimeException("not yet implemented"); // TODO
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof CharSetCondition))
			return false;

		final CharSetCondition o = (CharSetCondition)other;

		return function.equals(o.function) && value.equals(o.value);
	}

	@Override
	public int hashCode()
	{
		return function.hashCode() ^ value.hashCode() ^ 125345;
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		function.toString(bf, defaultType);
		bf.append(" conformsTo ").
			append(value);
	}
}

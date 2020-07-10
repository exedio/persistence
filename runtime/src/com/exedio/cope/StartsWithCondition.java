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

import com.exedio.cope.util.Hex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.function.Consumer;

public final class StartsWithCondition extends Condition
{
	private static final long serialVersionUID = 1l;

	public final DataField field;
	public final byte[] value;

	/**
	 * Creates a new StartsWithCondition.
	 * @deprecated Use {@link DataField#startsWithIfSupported(byte[])} instead.
	 */
	@Deprecated
	@SuppressFBWarnings("EI_EXPOSE_REP2") // May expose internal representation by incorporating reference to mutable object
	public StartsWithCondition(
			final DataField field,
			final byte[] value)
	{
		this.field = requireNonNull(field, "field");
		this.value = requireNonNull(value, "value");

		if(value.length==0)
			throw new IllegalArgumentException("value must not be empty");
	}

	@Override
	void append(final Statement bf)
	{
		bf.dialect.appendStartsWith(bf, field.getBlobColumnIfSupported("startsWith"), value);
	}

	@Override
	void supportsGetTri()
	{
		throw new IllegalArgumentException("not yet implemented: " + this); // TODO
	}

	@Override
	Trilean getTri(final FieldValues item)
	{
		throw new RuntimeException(); // TODO
		// once this method is implemented, implementation of #copy(CopyMapper) is needed to support blocks
	}

	@Override
	void check(final TC tc)
	{
		//Cope.check(field, tc, null); TODO
	}

	@Override
	public void acceptFieldsCovered(final Consumer<Field<?>> consumer)
	{
		field.acceptFieldsCovered(consumer);
	}

	@Override
	StartsWithCondition copy(final CopyMapper mapper)
	{
		// This is ok as long as getTri is not implemented as well.
		// Then we cannot use it in CheckConstraint, which is what copy is for.
		throw new RuntimeException("not yet implemented");
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof StartsWithCondition))
			return false;

		final StartsWithCondition o = (StartsWithCondition)other;

		return field.equals(o.field) && Arrays.equals(value, o.value);
	}

	@Override
	public int hashCode()
	{
		return field.hashCode() ^ Arrays.hashCode(value) ^ 1872643;
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		field.toString(bf, defaultType);
		bf.append(" startsWith '");
		Hex.append(bf, value, value.length);
		bf.append('\'');
	}
}

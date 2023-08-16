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

import static com.exedio.cope.util.Check.requireNonNegative;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.util.Hex;
import java.util.Arrays;
import java.util.function.Consumer;

public final class StartsWithCondition extends Condition
{
	private static final long serialVersionUID = 1l;

	/**
	 * @deprecated Use {@link #getField()} instead
	 */
	@Deprecated
	public final DataField field;

	private final int offset;

	/**
	 * @deprecated Use {@link #getValue()} instead
	 */
	@Deprecated
	public final byte[] value;

	/**
	 * Creates a new StartsWithCondition.
	 * @deprecated Use {@link DataField#startsWithIfSupported(byte[])} instead.
	 */
	@Deprecated
	public StartsWithCondition(
			final DataField field,
			final byte[] value)
	{
		this(field, 0, value);
	}

	StartsWithCondition(
			final DataField field,
			final int offset,
			final byte[] value)
	{
		this.field = requireNonNull(field, "field");
		this.offset = requireNonNegative(offset, "offset");
		this.value = requireNonNull(value, "value");

		if(value.length==0)
			throw new IllegalArgumentException("value must not be empty");
	}

	public DataField getField()
	{
		return field;
	}

	public byte[] getValue()
	{
		return com.exedio.cope.misc.Arrays.copyOf(value);
	}

	@Override
	void append(final Statement bf)
	{
		bf.dialect.appendStartsWith(bf, field.getBlobColumnIfSupported("startsWith"), offset, value);
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
	public Condition bind(final Join join)
	{
		throw new RuntimeException("not yet implemented");
		// Should be implemented as
		//   return new StartsWithCondition(field.bind(join), offset, value);
		// but so far there is no DataField#bind(Join).
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof StartsWithCondition))
			return false;

		final StartsWithCondition o = (StartsWithCondition)other;

		return field.equals(o.field) && offset==o.offset && Arrays.equals(value, o.value);
	}

	@Override
	public int hashCode()
	{
		return field.hashCode() ^ offset ^ Arrays.hashCode(value) ^ 1872643;
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		field.toString(bf, defaultType);
		bf.append(" startsWith ");
		if(offset>0)
		{
			bf.append("offset ").
				append(offset).
				append(' ');
		}
		bf.append('\'');
		Hex.append(bf, value, value.length);
		bf.append('\'');
	}
}

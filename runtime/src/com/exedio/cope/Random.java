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

public final class Random implements NumberFunction<Double>
{
	private static final long serialVersionUID = 1l;

	private final Type<?> type;
	private final int seed;

	Random(final Type<?> type, final int seed)
	{
		this.type = requireNonNull(type, "type");
		this.seed = seed;
	}

	@Override
	public void requireSupportForGet() throws UnsupportedGetException
	{
		throw new UnsupportedGetException(this);
	}

	@Override
	public Double get(final Item item) throws UnsupportedGetException
	{
		throw new UnsupportedGetException(this);
	}

	@Override
	public Class<Double> getValueClass()
	{
		return Double.class;
	}

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<Double> getValueType()
	{
		return SimpleSelectType.DOUBLE;
	}

	@Override
	public Type<?> getType()
	{
		return type;
	}

	@Override
	public NumberFunction<Double> bind(final Join join)
	{
		return BindNumberFunction.create(this, join);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof Random))
			return false;

		final Random o = (Random)other;
		return type.equals(o.type) && seed==o.seed;
	}

	@Override
	public int hashCode()
	{
		return type.hashCode() ^ seed;
	}

	@Override
	public void toString(final StringBuilder bf, final Type<?> defaultType)
	{
		if(defaultType!=type)
			bf.append(type.id).
				append('.');

		bf.append("rand(").
			append(seed).
			append(')');
	}

	@Override
	public String toString()
	{
		final StringBuilder bf = new StringBuilder();
		toString(bf, null);
		return bf.toString();
	}

	@Override
	public void forEachFieldCovered(final Consumer<Field<?>> consumer)
	{
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void append(@SuppressWarnings("ClassEscapesDefinedScope") final Statement bf, final Join join)
	{
		if(!type.getModel().supportsRandom())
			throw new IllegalArgumentException("random not supported by this dialect");

		bf.append("rand(").
			appendParameter(seed).
			append(')');
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void check(@SuppressWarnings("ClassEscapesDefinedScope") final TC tc, final Join join)
	{
		// nothing to do here, since there are no sources
	}
}

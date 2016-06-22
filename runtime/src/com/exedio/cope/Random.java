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

public class Random implements NumberFunction<Double>
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
	public Double get(final Item item)
	{
		throw new RuntimeException();
	}

	@Override
	public Class<Double> getValueClass()
	{
		return Double.class;
	}

	@Override
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
	public final boolean equals(final Object other)
	{
		if(!(other instanceof Random))
			return false;

		final Random o = (Random)other;
		return type.equals(o.type) && seed==o.seed;
	}

	@Override
	public final int hashCode()
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
	public final String toString()
	{
		final StringBuilder bf = new StringBuilder();
		toString(bf, null);
		return bf.toString();
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void append(final Statement bf, final Join join)
	{
		if(!type.getModel().supportsRandom())
			throw new IllegalArgumentException("random not supported by this dialect");

		bf.append("rand(").
			appendParameter(seed).
			append(')');
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void appendSelect(final Statement bf, final Join join)
	{
		append(bf, join);
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void check(final TC tc, final Join join)
	{
		// nothing to do here, since there are no sources
	}
}

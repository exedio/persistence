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

import java.io.Serial;

public final class AverageAggregate<S extends Number> extends Aggregate<Double,S>
{
	@Serial
	private static final long serialVersionUID = 1l;

	/**
	 * Creates a new AverageAggregate.
	 * @deprecated
	 * Instead of using this constructor directly,
	 * you may want to use the convenience methods.
	 * @see com.exedio.cope.NumberFunction#average()
	 */
	@Deprecated
	public AverageAggregate(final Function<S> source)
	{
		super(source, "avg", "AVG", SimpleSelectType.DOUBLE);
	}

	@Override
	public Function<Double> bind(final Join join)
	{
		return new AverageAggregate<>(source.bind(join));
	}

	/**
	 * @deprecated For internal use within COPE only.
	 */
	@Override
	@Deprecated // OK: for internal use within COPE only
	@SuppressWarnings("deprecation") // needed for idea
	public void append(@SuppressWarnings("ClassEscapesDefinedScope") final Statement st, final Join join)
	{
		final Dialect dialect = st.dialect;
		st.append(dialect.getAveragePrefix()).
			append(getSource(), join).
			append(dialect.getAveragePostfix());
	}
}

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

public final class LengthView extends NumberView<Integer>
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final Function<String> source;

	/**
	 * Creates a new LengthView.
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper method
	 * {@link StringFunction#length()}.
	 */
	public LengthView(final Function<String> source)
	{
		super(new Function<?>[]{source}, "length", Integer.class);
		this.source = source;
	}

	@Override
	@SuppressWarnings("ClassEscapesDefinedScope")
	public SelectType<Integer> getValueType()
	{
		return SimpleSelectType.INTEGER;
	}

	@Override
	public NumberFunction<Integer> bind(final Join join)
	{
		return new LengthView(source.bind(join));
	}

	@Override
	@SuppressWarnings("OverlyStrongTypeCast")
	public Integer mapJava(final Object[] sourceValues)
	{
		assert sourceValues.length==1;
		final Object sourceValue = sourceValues[0];
		return sourceValue==null ? null : ((String)sourceValue).length();
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void append(@SuppressWarnings("ClassEscapesDefinedScope") final Statement bf, final Join join)
	{
		bf.append("CHAR_LENGTH(").
			append(source, join).
			append(')');
	}
}

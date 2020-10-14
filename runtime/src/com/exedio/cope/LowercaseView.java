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

import java.util.Locale;

public final class LowercaseView extends StringView
{
	private static final long serialVersionUID = 1l;

	private final Function<String> source;

	/**
	 * Creates a new LowercaseView.
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper method
	 * {@link StringFunction#toLowerCase()}.
	 */
	public LowercaseView(final Function<String> source)
	{
		super(new Function<?>[]{source}, "lower");
		this.source = source;
	}

	private static String toLowerCase(final String s)
	{
		return s.toLowerCase(Locale.ENGLISH); // TODO which locale ?
	}

	@Override
	public String mapJava(final Object[] sourceValues)
	{
		assert sourceValues.length==1;
		final Object sourceValue = sourceValues[0];
		return sourceValue==null ? null : toLowerCase((String)sourceValue);
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void append(final Statement bf, final Join join)
	{
		bf.append("LOWER(").
			append(source, join).
			append(')');
	}
}

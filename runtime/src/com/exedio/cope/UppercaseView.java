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

public final class UppercaseView extends StringView
{
	private static final long serialVersionUID = 1l;

	private final Function<String> source;

	/**
	 * Creates a new UppercaseView.
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper method
	 * {@link StringField#toUpperCase()}.
	 */
	public UppercaseView(final Function<String> source)
	{
		super(new Function<?>[]{source}, "upper");
		this.source = source;
	}

	private static String toUpperCase(final String s)
	{
		return s.toUpperCase(Locale.ENGLISH); // TODO which locale ?
	}

	@Override
	public String mapJava(final Object[] sourceValues)
	{
		assert sourceValues.length==1;
		final Object sourceValue = sourceValues[0];
		return sourceValue==null ? null : toUpperCase((String)sourceValue);
	}

	@Override
	@Deprecated // OK: for internal use within COPE only
	public void append(final Statement bf, final Join join)
	{
		bf.append("UPPER(").
			append(source, join).
			append(')');
	}

	// convenience methods for conditions and views ---------------------------------

	public static Condition equalIgnoreCase(final Function<String> function, final String value)
	{
		return new UppercaseView(function).equal(toUpperCase(value));
	}

	public static LikeCondition likeIgnoreCase(final Function<String> function, final String value)
	{
		return new UppercaseView(function).like(toUpperCase(value));
	}

	public static LikeCondition startsWithIgnoreCase(final Function<String> function, final String value)
	{
		return LikeCondition.startsWith(new UppercaseView(function), toUpperCase(value));
	}

	public static LikeCondition endsWithIgnoreCase(final Function<String> function, final String value)
	{
		return LikeCondition.endsWith(new UppercaseView(function), toUpperCase(value));
	}

	public static LikeCondition containsIgnoreCase(final Function<String> function, final String value)
	{
		return LikeCondition.contains(new UppercaseView(function), toUpperCase(value));
	}
}
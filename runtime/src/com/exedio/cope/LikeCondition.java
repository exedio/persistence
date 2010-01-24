/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

public final class LikeCondition extends Condition
{
	public final StringFunction function;
	public final String value;

	/**
	 * Creates a new LikeCondition.
	 * Instead of using this constructor directly,
	 * you may want to use the more convenient wrapper method
	 * {@link StringFunction#like(String)}.
	 */
	public LikeCondition(final StringFunction function, final String value)
	{
		this.function = function;
		this.value = value;

		if(function==null)
			throw new NullPointerException("function");
		if(value==null)
			throw new NullPointerException("value");
	}
	
	@Override
	void append(final Statement bf)
	{
		bf.append(function, (Join)null).
			append(" like ").
			appendParameter(function, value);
	}

	@Override
	void check(final TC tc)
	{
		Cope.check(function, tc, null);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof LikeCondition))
			return false;
		
		final LikeCondition o = (LikeCondition)other;
		
		return function.equals(o.function) && value.equals(o.value);
	}
	
	@Override
	public int hashCode()
	{
		return function.hashCode() ^ value.hashCode() ^ 1872643;
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type defaultType)
	{
		function.toString(bf, defaultType);
		bf.append(" like '").
			append(toStringForValue(value, key)).
			append('\'');
	}
	
	public static final char WILDCARD = '%';
	
	public static final LikeCondition startsWith(final StringFunction function, final String value)
	{
		return new LikeCondition(function, value + WILDCARD);
	}
	
	public static final LikeCondition endsWith(final StringFunction function, final String value)
	{
		return new LikeCondition(function, WILDCARD + value);
	}
	
	public static final LikeCondition contains(final StringFunction function, final String value)
	{
		return new LikeCondition(function, WILDCARD + value + WILDCARD);
	}
	
	public static final LikeCondition startsWithIgnoreCase(final StringFunction function, final String value)
	{
		return startsWith(function.toUpperCase(), value.toUpperCase());
	}
	
	public static final LikeCondition endsWithIgnoreCase(final StringFunction function, final String value)
	{
		return endsWith(function.toUpperCase(), value.toUpperCase());
	}
	
	public static final LikeCondition containsIgnoreCase(final StringFunction function, final String value)
	{
		return contains(function.toUpperCase(), value.toUpperCase());
	}
}

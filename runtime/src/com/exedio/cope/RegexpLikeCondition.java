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

import static com.exedio.cope.util.Check.requireNonEmpty;
import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * This condition exposes underlying database regexp functionality.
 * Please note that the pattern will be expanded to
 * force a (possibly multi-line) match against the whole value and to enable the dot to match newlines.
 */
public final class RegexpLikeCondition extends Condition
{
	private static final long serialVersionUID = 1l;

	private final StringFunction function;
	private final String regexp;
	private final Pattern icuPattern;

	RegexpLikeCondition(
			final StringFunction function,
			final String regexp)
	{
		this.function = requireNonNull(function, "function");
		this.regexp = requireNonEmpty(regexp, "regexp");
		this.icuPattern = Pattern.compile(getIcuRegexp(regexp));
	}

	/**
	 * Wraps the regexp in a ICU compliant way, so it matches the whole string, including leading and trailing newlines.
	 * Also enables the dot (.) to match newlines.
	 * See <a href="https://unicode-org.github.io/icu/userguide/strings/regexp.html">...</a> for details.
	 */
	static String getIcuRegexp(final String regexp)
	{
		return "(?s)\\A" + regexp + "\\z";
	}

	@Override
	void append(final Statement bf)
	{
		bf.dialect.appendRegexpLike(bf, function, regexp);
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
	RegexpLikeCondition copy(final CopyMapper mapper)
	{
		return new RegexpLikeCondition(mapper.getS(function), regexp);
	}

	@Override
	public boolean equals(final Object other)
	{
		if(!(other instanceof RegexpLikeCondition))
			return false;

		final RegexpLikeCondition o = (RegexpLikeCondition)other;

		return function.equals(o.function) && regexp.equals(o.regexp);
	}

	@Override
	public int hashCode()
	{
		return function.hashCode() ^ regexp.hashCode() ^ 1872643;
	}

	@Override
	void toString(final StringBuilder bf, final boolean key, final Type<?> defaultType)
	{
		function.toString(bf, defaultType);
		bf.append(" regexp '").append(icuPattern.pattern()).append("'");
	}

	@Override
	void requireSupportForGetTri() throws UnsupportedGetException
	{
		function.requireSupportForGet();
	}

	@Override
	Trilean getTri(final FieldValues item) throws UnsupportedGetException
	{
		final String value = function.get(item);
		if(value==null)
			return Trilean.Null;

		return Trilean.valueOf(icuPattern.matcher(value).matches());
	}

	public StringFunction getFunction()
	{
		return function;
	}

	public String getRegexp()
	{
		return regexp;
	}
}

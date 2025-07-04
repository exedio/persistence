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

import com.exedio.cope.util.CharSet;

/**
 * Converts a {@link CharSet} into a regular expression supported by
 * <a href="https://unicode-org.github.io/icu/userguide/strings/regexp.html">ICU</a>
 */
final class ICU
{
	static String getRegularExpression(final CharSet charSet)
	{
		final StringBuilder bf = new StringBuilder();
		bf.append("\\A[");

		final char[] set = charSet.getCharacters();
		for(int i = 0; i<set.length; i+=2)
		{
			final char a = set[i];
			final char b = set[i + 1];

			if(a==b)
			{
				append(bf, a);
			}
			else
			{
				append(bf, a);
				bf.append('-');
				append(bf, b);
			}
		}
		if(charSet.contains(Character.MIN_SURROGATE) && charSet.contains(Character.MAX_SURROGATE))
		{
			// charSet allows surrogate characters - so we allow code points in the supplementary planes:
			bf.append("\\U00010000-\\U0010ffff");
		}

		bf.append("]*\\z");
		return bf.toString();
	}

	private static void append(final StringBuilder bf, final char c)
	{
		switch(c)
		{
			case '[', ']', '\\', '-', '&' ->
				bf.append('\\').append(c);

			default -> {
				if(c<' ' || c>126)
				{
					if(c>0xff) bf.append("\\u").append(String.format("%1$04x", (int)c));
					else       bf.append("\\x").append(String.format("%1$02x", (int)c));
				}
				else
					bf.append(c);
			}
		}
	}


	private ICU()
	{
		// prevent instantiation
	}
}

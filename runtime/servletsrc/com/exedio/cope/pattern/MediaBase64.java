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

package com.exedio.cope.pattern;

final class MediaBase64
{
	static void append(final StringBuilder bf, long src)
	{
		// NOTE
		// algorithm does not work for Long.MIN_VALUE because
		// arithmetic negation does not work for Long.MIN_VALUE
		assert src!=Long.MIN_VALUE;

		if(src<0)
		{
			// use dot instead of a minus, since minus is already part of alphabet
			bf.append('.');
			src = -src;
		}

		while(src>0)
		{
			// least significant digits first, just because it's easier to implement
			bf.append(alphabet[(int)(src & 63)]);
			src >>= 6;
		}
	}

	/**
	 * RFC 4648
	 * Base 64 Encoding with URL and Filename Safe Alphabet (base64url)
	 */
	private static final char[] alphabet = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'-', '_'
	};

	private MediaBase64()
	{
		// prevent instantiation
	}
}

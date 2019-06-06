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

import static com.exedio.cope.util.Check.requireGreaterZero;
import static com.exedio.cope.util.Check.requireNonNegative;

final class IntRatio
{
	static int ratio(final int number, final int dividend, final int divisor)
	{
		requireNonNegative(number, "number");
		requireNonNegative(dividend, "dividend");
		requireGreaterZero(divisor, "divisor");

		final long result = ((long)number) * ((long)dividend) / divisor;
		if(result>Integer.MAX_VALUE)
			throw new IllegalArgumentException("result overflow " + result);
		return (int)result;
	}

	private IntRatio()
	{
		// prevent instantiation
	}
}

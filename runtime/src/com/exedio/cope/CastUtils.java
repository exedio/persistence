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

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

final class CastUtils
{
	/**
	 * Can be replaced by static Math.toIntExact available in JDK 1.8
	 */
	static int toIntExact(final long longValue)
	{
		if (longValue < MIN_VALUE || longValue > MAX_VALUE)
		{
			throw new ArithmeticException("value '"+longValue+"' out of integer range");
		}
		return (int)longValue;
	}

	static int toIntCapped(final long longValue)
	{
		if (longValue > MAX_VALUE)
		{
			return MAX_VALUE;
		}
		if (longValue < MIN_VALUE)
		{
			return MIN_VALUE;
		}
		return (int)longValue;
	}


	private CastUtils()
	{
		// prevent instantiation
	}
}

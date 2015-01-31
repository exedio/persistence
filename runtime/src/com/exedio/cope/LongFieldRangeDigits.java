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

final class LongFieldRangeDigits
{
	static LongField rangeDigits(final LongField field, final int digits)
	{
		check(1, 18, digits, "digits");

		final long pow = pow(digits);
		return field.range(pow/10, pow-1);
	}

	static LongField rangeDigits(final LongField field, final int minimumDigits, final int maximumDigits)
	{
		check(0, 18, minimumDigits, "minimumDigits");
		check(1, 18, maximumDigits, "maximumDigits");
		if(minimumDigits>maximumDigits)
			throw new IllegalArgumentException("maximumDigits must be greater or equal than minimumDigits, but was " + maximumDigits + " and " + minimumDigits + '.');

		return field.range(
				pow(minimumDigits-1),
				pow(maximumDigits  )-1);
	}

	private static final void check(
			final int minimum,
			final int maximum,
			final int actual,
			final String name)
	{
		if(!(minimum<=actual && actual<=maximum))
			throw new IllegalArgumentException(
					name + " must be between " + minimum + " and " + maximum +
					", but was " + actual);
	}

	private static final long pow(final int exponent)
	{
		return Math.round(Math.pow(10, exponent));
	}

	private LongFieldRangeDigits()
	{
		// prevent instantiation
	}
}

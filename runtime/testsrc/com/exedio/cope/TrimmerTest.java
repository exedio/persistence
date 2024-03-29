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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class TrimmerTest
{
	private static void assertTrim(final String expected, final String actualLongString, final int maxLength)
	{
		final String actual = new Trimmer(maxLength).trimString(actualLongString);
		assertEquals(expected, actual, ">"+expected+"< >"+actual + "<");
		assertEquals(Math.min(actualLongString.length(), maxLength), actual.length());
	}

	@Test void testTrim()
	{
		assertTrim("F", "FirstSecondThird", 1);
		assertTrim("FS", "FirstSecondThird", 2);
		assertTrim("FST", "FirstSecondThird", 3);
		assertTrim("FiST", "FirstSecondThird", 4);
		assertTrim("FiSeT", "FirstSecondThird", 5);
		assertTrim("FirsSecThi", "FirstSecondThird", 10);
		assertTrim("FirsSecoThi", "FirstSecondThird", 11);
		assertTrim("FirsSecoThir", "FirstSecondThird", 12);
		assertTrim("FirstSecoThir", "FirstSecondThird", 13);
		assertTrim("FirstSeconThir", "FirstSecondThird", 14);
		assertTrim("FirstSeconThird", "FirstSecondThird", 15);
		assertTrim("FirstSecondThird", "FirstSecondThird", 16);
		assertTrim("FirstSecondThird", "FirstSecondThird", 18);

		assertTrim("First_SecondThird", "First-SecondThird", 18);
		assertTrim("FirstSecond_Third", "FirstSecond-Third", 18);
		assertTrim("Fir_SecThi", "First-SecondThird", 10);
		assertTrim("FirSec_Thi", "FirstSecond-Third", 10);
		assertTrim("First.SecondThird", "First.SecondThird", 18);
		assertTrim("FirstSecond.Third", "FirstSecond.Third", 18);
		assertTrim("FirsSecThi", "First.SecondThird", 10);
		assertTrim("FirsSecThi", "FirstSecond.Third", 10);

		assertTrim("Firs1Seco2dThird", "Firs1Seco2dThird", 18);
		assertTrim("Fir1Se2dTh", "Firs1Seco2dThird", 10);

		assertTrim("1irs2ec3hi", "1irst2econd3hird", 10);
		assertTrim("_irs_ec_hi", "_irst_econd_hird", 10);

		assertTrim("ShortVeryverylongShort", "ShortVeryverylongShort", 22);
		assertTrim("ShortVeryverylonShort", "ShortVeryverylongShort", 21);
		assertTrim("ShortVeryveryloShort", "ShortVeryverylongShort", 20);
		assertTrim("ShortVeryvShort", "ShortVeryverylongShort",15);
		assertTrim("ShortVeryvShor", "ShortVeryverylongShort",14);
		assertTrim("ShortVeryShor", "ShortVeryverylongShort",13);
		assertTrim("ShorVeryShor", "ShortVeryverylongShort",12);
		assertTrim("ShorVerySho", "ShortVeryverylongShort",11);

		try
		{
			new Trimmer(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("maxLength must be greater zero, but was 0", e.getMessage());
		}
		final Trimmer nt = new Trimmer(20);
		try
		{
			nt.trimString(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("longString", e.getMessage());
		}
		try
		{
			nt.trimString("");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("longString must not be empty", e.getMessage());
		}
	}
}

/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import junit.framework.TestCase;

public class TrimTest extends TestCase
{
	private void assertTrim(final String expected, final String actualLongString, final int maxLength)
	{
		final String actual = Database.trimString(actualLongString, maxLength);
		assertEquals(">"+expected+"< >"+actual+"<", expected, actual);
		if(actualLongString.length()>maxLength)
			assertEquals(maxLength, actual.length());
		else
			assertEquals(actualLongString.length(), actual.length());
	}

	public void testTrim()
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
			Database.trimString("hallo", 0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("maxLength must be greater zero", e.getMessage());
		}
		try
		{
			Database.trimString("", 20);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("longString must not be empty", e.getMessage());
		}
	}

}

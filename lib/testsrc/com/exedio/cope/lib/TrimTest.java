
package com.exedio.cope.lib;

public class TrimTest extends AbstractLibTest
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
	}

}

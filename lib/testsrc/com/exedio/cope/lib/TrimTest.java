
package com.exedio.cope.lib;

public class TrimTest extends AbstractLibTest
{
	private void assertTrim(final String expected, final String actualLongString, final int maxLength)
	{
		final String actual = Database.trimString(actualLongString, maxLength);
		assertEquals(">"+expected+"< >"+actual+"<", expected, actual);
	}

	public void testTrim()
	{
		assertTrim("FST", "FirstSecondThird", 5);
		assertTrim("FirSecThi", "FirstSecondThird", 10);
		assertTrim("FirstSeconThird", "FirstSecondThird", 15);
		assertTrim("FirstSecondThird", "FirstSecondThird", 18);
	}

}


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

		assertTrim("1ir2ec3hi", "1irst2econd3hird", 10);
		assertTrim("_ir_ec_hi", "_irst_econd_hird", 10);
	}

}

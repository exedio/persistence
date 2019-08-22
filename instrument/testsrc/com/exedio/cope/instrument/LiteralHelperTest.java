package com.exedio.cope.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LiteralHelperTest
{
	@Test
	void floatLiteral()
	{
		assertEquals("0.0f", LiteralHelper.getLiteralFor(0f));
		assertEquals("java.lang.Float.NEGATIVE_INFINITY", LiteralHelper.getLiteralFor(Float.NEGATIVE_INFINITY));
		assertEquals("java.lang.Float.POSITIVE_INFINITY", LiteralHelper.getLiteralFor(Float.POSITIVE_INFINITY));
		assertEquals("java.lang.Float.NaN", LiteralHelper.getLiteralFor(Float.NaN));
		assertEquals("1.4E-45f", LiteralHelper.getLiteralFor(Float.MIN_VALUE));
		assertEquals(Float.MIN_VALUE, 1.4E-45f);
		assertEquals("3.4028235E38f", LiteralHelper.getLiteralFor(Float.MAX_VALUE));
		assertEquals(Float.MAX_VALUE, 3.4028235E38f);
	}

	@Test
	void doubleLiteral()
	{
		assertEquals("0.0f", LiteralHelper.getLiteralFor(0f));
		assertEquals("java.lang.Double.NEGATIVE_INFINITY", LiteralHelper.getLiteralFor(Double.NEGATIVE_INFINITY));
		assertEquals("java.lang.Double.POSITIVE_INFINITY", LiteralHelper.getLiteralFor(Double.POSITIVE_INFINITY));
		assertEquals("java.lang.Double.NaN", LiteralHelper.getLiteralFor(Double.NaN));
		assertEquals("4.9E-324", LiteralHelper.getLiteralFor(Double.MIN_VALUE));
		assertEquals(Double.MIN_VALUE, 4.9E-324);
		assertEquals("1.7976931348623157E308", LiteralHelper.getLiteralFor(Double.MAX_VALUE));
		assertEquals(Double.MAX_VALUE, 1.7976931348623157E308);
	}

	@Test
	void intLiteral()
	{
		assertEquals("0", LiteralHelper.getLiteralFor(0));
		assertEquals("-42", LiteralHelper.getLiteralFor(-42));
		assertEquals("-2147483648", LiteralHelper.getLiteralFor(Integer.MIN_VALUE));
		assertEquals("2147483647", LiteralHelper.getLiteralFor(Integer.MAX_VALUE));
	}

	@Test
	void longLiteral()
	{
		assertEquals("0L", LiteralHelper.getLiteralFor(0L));
		assertEquals("-42L", LiteralHelper.getLiteralFor(-42L));
		assertEquals("-9223372036854775808L", LiteralHelper.getLiteralFor(Long.MIN_VALUE));
		assertEquals("9223372036854775807L", LiteralHelper.getLiteralFor(Long.MAX_VALUE));
	}

	@Test
	void shortLiteral()
	{
		assertEquals("(short)0", LiteralHelper.getLiteralFor((short)0));
		assertEquals("(short)-42", LiteralHelper.getLiteralFor((short)-42));
		assertEquals("(short)-32768", LiteralHelper.getLiteralFor(Short.MIN_VALUE));
		assertEquals("(short)32767", LiteralHelper.getLiteralFor(Short.MAX_VALUE));
	}

	@Test
	void byteLiteral()
	{
		assertEquals("(byte)0", LiteralHelper.getLiteralFor((byte)0));
		assertEquals("(byte)-42", LiteralHelper.getLiteralFor((byte)-42));
		assertEquals("(byte)-128", LiteralHelper.getLiteralFor(Byte.MIN_VALUE));
		assertEquals("(byte)127", LiteralHelper.getLiteralFor(Byte.MAX_VALUE));
	}

	@Test
	void booleanLiteral()
	{
		assertEquals("false", LiteralHelper.getLiteralFor(false));
		assertEquals("true", LiteralHelper.getLiteralFor(true));
	}

	@SuppressWarnings("HardcodedLineSeparator")
	@Test
	void stringLiteral()
	{
		assertEquals("\"\\\"\"", LiteralHelper.getLiteralFor("\""));
		assertEquals("\"'\"", LiteralHelper.getLiteralFor("'"));
		assertEquals("\"\\r\\n\"", LiteralHelper.getLiteralFor("\r\n"));
		assertEquals("\"\\u0007\"", LiteralHelper.getLiteralFor("\u0007"));
		assertEquals("\"\\u0018\"", LiteralHelper.getLiteralFor("\u0018"));
		assertEquals("\"\\u00E4\\u00F6\\u00FC\\u00C4\\u00D6\\u00DC\\u00DF\"", LiteralHelper.getLiteralFor("\u00e4\u00f6\u00fc\u00c4\u00d6\u00dc\u00df")); // german characters
		assertEquals("\"\\u0100\"", LiteralHelper.getLiteralFor("\u0100"));
		assertEquals("\"\\u9009\\u586B\"", LiteralHelper.getLiteralFor("\u9009\u586b")); // chinese characters
	}

	@SuppressWarnings("HardcodedLineSeparator")
	@Test
	void charLiteral()
	{
		assertEquals("'\"'", LiteralHelper.getLiteralFor('"'));
		assertEquals("'''", LiteralHelper.getLiteralFor('\'')); // todo
		assertEquals("'\n'", LiteralHelper.getLiteralFor('\n')); // todo
		assertEquals("'\r'", LiteralHelper.getLiteralFor('\r')); // todo
		assertEquals("'\u0007'", LiteralHelper.getLiteralFor('\u0007')); // todo
		assertEquals("'\u0018'", LiteralHelper.getLiteralFor('\u0018')); // todo
		assertEquals("'\u00e4'", LiteralHelper.getLiteralFor('\u00e4')); // german ae
		assertEquals("'\u00df'", LiteralHelper.getLiteralFor('\u00df')); // german sz
		assertEquals("'\u0100'", LiteralHelper.getLiteralFor('\u0100'));
		assertEquals("'\u9009'", LiteralHelper.getLiteralFor('\u9009')); // chinese character
	}
}

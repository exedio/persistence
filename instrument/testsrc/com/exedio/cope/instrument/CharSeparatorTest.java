package com.exedio.cope.instrument;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CharSeparatorTest
{
	@Test
	void appendTo()
	{
		final StringBuilder result = new StringBuilder();
		final CharSeparator separator = new CharSeparator(',');

		separator.appendTo(result);
		assertEquals("", result.toString());

		result.append('a');
		separator.appendTo(result);
		assertEquals("a,", result.toString());

		result.append('b');
		separator.appendTo(result);
		assertEquals("a,b,", result.toString());
	}
}

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

package com.exedio.cope.instrument;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class ParamsTest
{
	@Test void generateDeprecated() throws HumanReadableException
	{
		check("a.b.C#m(int,Bla)", "a.b.C", "m", new String[]{"int", "Bla"});
		check("X#m()", "X", "m", new String[]{});
	}

	private static void check(final String generateDeprecatedLine, final String expectedClassName, final String expectedMethodName, final String[] expectedParams) throws HumanReadableException
	{
		final Params params = new Params();
		params.addGenerateDeprecated(generateDeprecatedLine);
		assertEquals(1, params.getGenerateDeprecateds().size());
		assertEquals(expectedClassName, params.getGenerateDeprecateds().get(0).className);
		assertEquals(expectedMethodName, params.getGenerateDeprecateds().get(0).methodName);
		assertArrayEquals(expectedParams, params.getGenerateDeprecateds().get(0).parameterTypes);
	}

	@SuppressWarnings("HardcodedLineSeparator")
	@Test void generateDeprecatedInvalid()
	{
		checkRejected("broken", "invalid <generateDeprecated> syntax in broken");
		checkRejected("X#y( )", "<generateDeprecated> must not contain space or newline");
		for (final String newline: new String[]{"\n", "\r\n", "\r"})
		{
			checkRejected("two"+newline+"lines", "<generateDeprecated> must not contain space or newline");
		}
	}

	private static void checkRejected(final String generateDeprecatedLine, final String expectedExceptionMessage)
	{
		try
		{
			new Params().addGenerateDeprecated(generateDeprecatedLine);
			fail();
		}
		catch (final HumanReadableException e)
		{
			assertEquals(expectedExceptionMessage, e.getMessage());
		}
	}
}

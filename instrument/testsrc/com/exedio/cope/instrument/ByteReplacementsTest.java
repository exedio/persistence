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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class ByteReplacementsTest
{
	@Test void replacementsMustBeSequential()
	{
		final ByteReplacements replacements=new ByteReplacements();
		replacements.addReplacement(10, 15, "");
		replacements.addReplacement(15, 20, "");

		try
		{
			replacements.addReplacement(5, 7, "");
			fail();
		}
		catch (final RuntimeException e)
		{
			assertEquals("replacements must be marked from start to end; [15-20] [5-7]", e.getMessage());
		}
		try
		{
			replacements.addReplacement(19, 21, "");
			fail();
		}
		catch (final RuntimeException e)
		{
			assertEquals("replacements must be marked from start to end; [15-20] [19-21]", e.getMessage());
		}
		replacements.addReplacement(20, 20, "");
		replacements.addReplacement(20, 20, "");
	}

	@Test void replacementsMustHaveValidRange()
	{
		final ByteReplacements replacements=new ByteReplacements();
		try
		{
			replacements.addReplacement(10, 9, "");
			fail();
		}
		catch (final RuntimeException e)
		{
			assertEquals("10-9", e.getMessage());
		}
		try
		{
			replacements.addReplacement(-1, 10, "");
			fail();
		}
		catch (final RuntimeException e)
		{
			assertEquals("-1-10", e.getMessage());
		}
		replacements.addReplacement(10, 10, "");
	}

	@Test void applyReplacementsWithDifferentBufferSizes()
	{
		for (final ByteReplacements replacements: new ByteReplacements[]{new ByteReplacements(), new ByteReplacements().setBufferSize(1), new ByteReplacements().setBufferSize(3)})
		{
			replacements.addReplacement(0, 0, "x");
			assertReplacements("x0123456789", replacements);

			replacements.addReplacement(1, 2, "");
			assertReplacements("x023456789", replacements);

			replacements.addReplacement(7, 10, "");
			assertReplacements("x023456", replacements);
		}
	}

	@Test void manyReplacementsAtSameIndex()
	{
		final ByteReplacements replacements=new ByteReplacements();
		replacements.addReplacement(3, 3, "a");
		replacements.addReplacement(3, 3, "b");
		assertReplacements("012ab3456789", replacements);
	}

	@Test void replacementsAtBeginning()
	{
		final ByteReplacements replacements=new ByteReplacements();
		replacements.addReplacement(0, 3, "AB");
		assertReplacements("AB3456789", replacements);
	}

	@Test void replacementsBehindEnd()
	{
		final ByteReplacements replacements=new ByteReplacements();
		replacements.addReplacement(1, 2, "");
		replacements.addReplacement(11, 12, "");
		try
		{
			replacements.applyReplacements(new byte[10]);
			fail();
		}
		catch (final RuntimeException e)
		{
			assertEquals("unexpected EOF", e.getMessage());
		}
	}

	@Test void replacementsIncludingEnd()
	{
		final ByteReplacements replacements=new ByteReplacements();
		replacements.addReplacement(9, 12, "");
		try
		{
			replacements.applyReplacements(new byte[10]);
			fail();
		}
		catch (final RuntimeException e)
		{
			assertEquals("unexpected EOF while skipping Replacement[9-12]", e.getMessage());
		}
	}

	private static void assertReplacements(final String expected, final ByteReplacements replacements)
	{
		final byte[] bytes=new byte[10];
		for (int i=0; i<bytes.length; i++)
		{
			bytes[i]=(byte)('0'+i%10);
		}
		final byte[] replaced=replacements.applyReplacements(bytes);
		assertEquals(expected, new String(replaced, StandardCharsets.US_ASCII));
	}

	@Test void positions()
	{
		final ByteReplacements replacements=new ByteReplacements();
		assertEquals(10, replacements.translateToPositionInOutput(10));

		replacements.addReplacement(2, 3, new byte[0]);
		assertEquals(1, replacements.translateToPositionInOutput(1));
		try
		{
			replacements.translateToPositionInOutput(2);
			fail();
		}
		catch (final RuntimeException e)
		{
			assertEquals("in replaced part", e.getMessage());
		}
		assertEquals(2, replacements.translateToPositionInOutput(3));

		replacements.addReplacement(3, 3, new byte[10]);
		assertEquals(12, replacements.translateToPositionInOutput(3));

		replacements.addReplacement(3, 5, new byte[10]);
		assertEquals(22, replacements.translateToPositionInOutput(5));
	}

}

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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.TextUrlFilterDelegatorItem.fertig;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class TextUrlFilterDelegatorModelTest
{
	@Test void testRawNull()
	{
		try
		{
			new TextUrlFilterDelegator(null, null, null, null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("source", e.getMessage());
		}
	}

	@Test void testDelegateNull()
	{
		final Media roh2 = new Media();
		try
		{
			new TextUrlFilterDelegator(roh2, null, null, null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("delegate", e.getMessage());
		}
	}


	@Test void testSupportedContentTypeNull()
	{
		final Media roh2 = new Media();
		try
		{
			new TextUrlFilterDelegator(roh2, fertig, null, null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("supportedContentType", e.getMessage());
		}
	}

	@Test void testSupportedContentTypeEmpty()
	{
		final Media roh2 = new Media();
		try
		{
			new TextUrlFilterDelegator(roh2, fertig, "", null, null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("supportedContentType must not be empty", e.getMessage());
		}
	}

	@Test void testCharsetNull()
	{
		final Media roh2 = new Media();
		try
		{
			new TextUrlFilterDelegator(roh2, fertig, "text/plain", null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("charset", e.getMessage());
		}
	}

	@Test void testPasteStartNull()
	{
		final Media roh2 = new Media();
		try
		{
			new TextUrlFilterDelegator(roh2, fertig, "text/plain", UTF_8, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteStart", e.getMessage());
		}
	}

	@Test void testPasteStartEmpty()
	{
		final Media roh2 = new Media();
		try
		{
			new TextUrlFilterDelegator(roh2, fertig, "text/plain", UTF_8, "", null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteStart must not be empty", e.getMessage());
		}
	}

	@Test void testPasteStopNull()
	{
		final Media roh2 = new Media();
		try
		{
			new TextUrlFilterDelegator(roh2, fertig, "text/plain", UTF_8, "(", null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteStop", e.getMessage());
		}
	}

	@Test void testPasteStopEmpty()
	{
		final Media roh2 = new Media();
		try
		{
			new TextUrlFilterDelegator(roh2, fertig, "text/plain", UTF_8, "(", "");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteStop must not be empty", e.getMessage());
		}
	}

}

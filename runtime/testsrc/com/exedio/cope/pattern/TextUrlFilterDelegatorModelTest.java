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
import static com.exedio.cope.pattern.TextUrlFilterDelegatorItem.roh2;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.exedio.cope.junit.CopeAssert;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.Charset;
import org.junit.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class TextUrlFilterDelegatorModelTest extends CopeAssert
{
	@Test public void testRawNull()
	{
		try
		{
			new TextUrlFilterDelegator(null, null, null, (Charset)null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("source", e.getMessage());
		}
	}

	@Test public void testDelegateNull()
	{
		try
		{
			new TextUrlFilterDelegator(roh2, null, null, (Charset)null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("delegate", e.getMessage());
		}
	}


	@Test public void testSupportedContentTypeNull()
	{
		try
		{
			new TextUrlFilterDelegator(roh2, fertig, null, (Charset)null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("supportedContentType", e.getMessage());
		}
	}

	@Test public void testSupportedContentTypeEmpty()
	{
		try
		{
			new TextUrlFilterDelegator(roh2, fertig, "", (Charset)null, null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("supportedContentType must not be empty", e.getMessage());
		}
	}

	@Test public void testCharsetNull()
	{
		try
		{
			new TextUrlFilterDelegator(roh2, fertig, "text/plain", (Charset)null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("charset", e.getMessage());
		}
	}

	@Test public void testPasteStartNull()
	{
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

	@Test public void testPasteStartEmpty()
	{
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

	@Test public void testPasteStopNull()
	{
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

	@Test public void testPasteStopEmpty()
	{
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

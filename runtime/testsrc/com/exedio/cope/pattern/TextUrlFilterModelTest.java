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

import static com.exedio.cope.pattern.TextUrlFilterItem.roh;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.Charset;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class TextUrlFilterModelTest extends CopeAssert
{
	@Test public void testRawNull()
	{
		try
		{
			new TextUrlFilter(null, null, (Charset)null, null, null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("source", e.getMessage());
		}
	}
	@Test public void testSupportedContentTypeNull()
	{
		try
		{
			new TextUrlFilter(roh, null, (Charset)null, null, null, null, null);
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
			new TextUrlFilter(roh, "", (Charset)null, null, null, null, null);
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
			new TextUrlFilter(roh, "text/plain", (Charset)null, null, null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("charset", e.getMessage());
		}
	}
	@Deprecated // OK: testing deprecated API
	@Test public void testEncodingNull()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", (String)null, null, null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("encoding", e.getMessage());
		}
	}
	@Deprecated // OK: testing deprecated API
	@Test public void testEncondingWrong()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", "zack", null, null, null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("zack", e.getMessage());
		}
	}
	@Test public void testPasteStartNull()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF_8, null, null, null, null);
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
			new TextUrlFilter(roh, "text/plain", UTF_8, "", null, null, null);
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
			new TextUrlFilter(roh, "text/plain", UTF_8, "(", null, null, null);
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
			new TextUrlFilter(roh, "text/plain", UTF_8, "(", "", null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteStop must not be empty", e.getMessage());
		}
	}
	@Test public void testPasteKeyNull()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF_8, "(", ")", null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteKey", e.getMessage());
		}
	}
	@Test public void testPasteKeyOptional()
	{
		final StringField pasteKey = new StringField().optional();
		final Media pasteValue = new Media();
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF_8, "(", ")", pasteKey, pasteValue);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteKey must be mandatory", e.getMessage());
		}
	}
	@Test public void testPasteKeyUnique()
	{
		final StringField pasteKey = new StringField().unique();
		final Media pasteValue = new Media();
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF_8, "(", ")", pasteKey, pasteValue);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteKey must not be unique", e.getMessage());
		}
	}
	@Test public void testPasteValueNull()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF_8, "(", ")", new StringField(), null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteValue", e.getMessage());
		}
	}
	@Test public void testPasteValueFinal()
	{
		final StringField pasteKey = new StringField();
		final Media pasteValue = new Media().toFinal();
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF_8, "(", ")", pasteKey, pasteValue);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteValue must not be final", e.getMessage());
		}
	}
	@Test public void testPasteValueOptional()
	{
		final StringField pasteKey = new StringField();
		final Media pasteValue = new Media().optional();
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF_8, "(", ")", pasteKey, pasteValue);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteValue must be mandatory", e.getMessage());
		}
	}
}

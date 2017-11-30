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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.StringField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.Charset;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class TextUrlFilterModelTest
{
	@Test void testRawNull()
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
	@Test void testSupportedContentTypeNull()
	{
		final Media roh = new Media();
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
	@Test void testSupportedContentTypeEmpty()
	{
		final Media roh = new Media();
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
	@Test void testCharsetNull()
	{
		final Media roh = new Media();
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
	@Test void testEncodingNull()
	{
		final Media roh = new Media();
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
	@Test void testEncondingWrong()
	{
		final Media roh = new Media();
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
	@Test void testPasteStartNull()
	{
		final Media roh = new Media();
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
	@Test void testPasteStartEmpty()
	{
		final Media roh = new Media();
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
	@Test void testPasteStopNull()
	{
		final Media roh = new Media();
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
	@Test void testPasteStopEmpty()
	{
		final Media roh = new Media();
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
	@Test void testPasteKeyNull()
	{
		final Media roh = new Media();
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
	@Test void testPasteKeyOptional()
	{
		final Media roh = new Media();
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
	@Test void testPasteKeyUnique()
	{
		final Media roh = new Media();
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
	@Test void testPasteValueNull()
	{
		final Media roh = new Media();
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
	@Test void testPasteValueFinal()
	{
		final Media roh = new Media();
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
	@Test void testPasteValueOptional()
	{
		final Media roh = new Media();
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
	@Test void testMandatory()
	{
		assertEquals(false, TextUrlFilterItem.fertig.isMandatory());
		assertEquals(
			true,
			new TextUrlFilter(new Media().contentType("eins"), "eins", UTF_8, "{", "}", new StringField(), new Media()).isMandatory()
		);
		assertEquals(
			false,
			new TextUrlFilter(new Media().contentType("eins", "zwei"), "eins", UTF_8, "{", "}", new StringField(), new Media()).isMandatory()
		);
		assertEquals(
			false,
			new TextUrlFilter(new Media().contentType("eins").optional(), "eins", UTF_8, "{", "}", new StringField(), new Media()).isMandatory()
		);
	}
}

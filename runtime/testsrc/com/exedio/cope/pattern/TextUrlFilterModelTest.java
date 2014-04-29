/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.CharsetName;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.Charset;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class TextUrlFilterModelTest extends CopeAssert
{
	private static final Charset charset = Charset.forName(CharsetName.UTF8);

	public void testRawNull()
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
	public void testSupportedContentTypeNull()
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
	public void testSupportedContentTypeEmpty()
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
	public void testCharsetNull()
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
	public void testEncodingNull()
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
	public void testEncondingWrong()
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
	public void testPasteStartNull()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", charset, null, null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteStart", e.getMessage());
		}
	}
	public void testPasteStartEmpty()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", charset, "", null, null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteStart must not be empty", e.getMessage());
		}
	}
	public void testPasteStopNull()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", charset, "(", null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteStop", e.getMessage());
		}
	}
	public void testPasteStopEmpty()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", charset, "(", "", null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteStop must not be empty", e.getMessage());
		}
	}
	public void testPasteKeyNull()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", charset, "(", ")", null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteKey", e.getMessage());
		}
	}
	public void testPasteValueNull()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", charset, "(", ")", new StringField(), null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteValue", e.getMessage());
		}
	}
}

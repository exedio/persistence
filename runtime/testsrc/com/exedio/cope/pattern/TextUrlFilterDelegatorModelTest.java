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

import static com.exedio.cope.pattern.TextUrlFilterDelegatorItem.TYPE;
import static com.exedio.cope.pattern.TextUrlFilterDelegatorItem.fertig;
import static com.exedio.cope.pattern.TextUrlFilterDelegatorItem.roh2;

import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.CharsetName;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.Charset;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class TextUrlFilterDelegatorModelTest extends CopeAssert
{
	private static final Charset charset = Charset.forName(CharsetName.UTF8);

	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(TextUrlFilterDelegatorModelTest.class, "MODEL");
	}

	public void testRawNull()
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

	public void testDelegateNull()
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


	public void testSupportedContentTypeNull()
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

	public void testSupportedContentTypeEmpty()
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

	public void testCharsetNull()
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

	public void testPasteStartNull()
	{
		try
		{
			new TextUrlFilterDelegator(roh2, fertig, "text/plain", charset, null, null);
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
			new TextUrlFilterDelegator(roh2, fertig, "text/plain", charset, "", null);
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
			new TextUrlFilterDelegator(roh2, fertig, "text/plain", charset, "(", null);
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
			new TextUrlFilterDelegator(roh2, fertig, "text/plain", charset, "(", "");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteStop must not be empty", e.getMessage());
		}
	}

}

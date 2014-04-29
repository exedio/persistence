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

import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.pattern.TextUrlFilterItem.TYPE;
import static com.exedio.cope.pattern.TextUrlFilterItem.fertig;
import static com.exedio.cope.pattern.TextUrlFilterItem.roh;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.pattern.MediaPath.NotFound;
import com.exedio.cope.util.CharsetName;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.servlet.ServletOutputStream;

public class TextUrlFilterTest extends AbstractRuntimeModelTest
{
	static final Model MODEL = new Model(TYPE);

	public TextUrlFilterTest()
	{
		super(MODEL);
	}

	TextUrlFilterItem item, item2;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item  = new TextUrlFilterItem();
		item2 = new TextUrlFilterItem();
	}

	static final Charset UTF8 = Charset.forName(CharsetName.UTF8);

	public void testIt() throws IOException, NotFound
	{
		assertEquals(list("image/png"), fertig.getPasteContentTypesAllowed());

		assertEquals(null, item.getFertigContentType());
		try
		{
			fertig.doGetAndCommit(null, null, item);
			fail();
		}
		catch(final NotFound e)
		{
			assertEquals("is null", e.getMessage());
		}
		assertTrue(model.hasCurrentTransaction());

		item.setFertigRaw("<eins><paste>uno</paste><zwei>");
		assertEquals("text/plain", item.getFertigContentType());
		try
		{
			fertig.doGetAndCommit(new Request(), null, item);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expected result of size one, but was empty for query: select this from TextUrlFilterItem-fertig where (parent='" + item + "' AND key='uno')", e.getMessage());
		}
		assertTrue(model.hasCurrentTransaction());

		final String url1 = item.addFertigPaste("uno");
		final MediaPath.Locator l = fertig.getPasteLocator(item, "uno");
		assertLocator("TextUrlFilterItem-fertig/value/TextUrlFilterItem-fertig-0.png", l);
		assertEquals(l.getURLByConnect(), fertig.getPasteURL(item, "uno"));
		assertGet("<eins><override>" + url1 + "</override><zwei>");


		try
		{
			item.addFertigPaste("uno");
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals("unique violation for TextUrlFilterItem-fertig.parentAndKey", e.getMessage());
		}
		item2.addFertigPaste("uno");

		final String url2 = item.addFertigPaste("duo");
		assertGet("<eins><override>" + url1 + "</override><zwei>");

		item.setFertigRaw("<paste>uno</paste><eins><paste>duo</paste>");
		assertGet("<override>" + url1 + "</override><eins><override>" + url2 + "</override>");

		item.setFertigRaw("<eins><paste>uno</paste><zwei><paste>duo</paste><drei>");
		assertGet("<eins><override>" + url1 + "</override><zwei><override>" + url2 + "</override><drei>");

		item.setFertigRaw("<eins><Xpaste>uno</paste><zwei><Xpaste>duo</paste><drei>");
		assertGet("<eins><Xpaste>uno</paste><zwei><Xpaste>duo</paste><drei>");

		item.setFertigRaw("<eins><paste>EXTRA</paste><zwei>");
		assertGet("<eins><extra/><zwei>");

		item.setFertigRaw("<eins><paste>uno</Xpaste><zwei>");
		try
		{
			fertig.doGetAndCommit(new Request(), null, item);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("<paste>:6/</paste>", e.getMessage());
		}
		assertTrue(model.hasCurrentTransaction());

		item.setFertigRaw("<eins><paste>");
		try
		{
			fertig.doGetAndCommit(new Request(), null, item);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("<paste>:6/</paste>", e.getMessage());
		}
		assertTrue(model.hasCurrentTransaction());
	}

	private void assertGet(final String body) throws IOException, NotFound
	{
		assertEquals("text/plain", item.getFertigContentType());
		fertig.doGetAndCommit(new Request(), new Response(body), item);
		assertFalse(model.hasCurrentTransaction());
		model.startTransaction(TextUrlFilterTest.class.getName());
	}

	static class Request extends HttpServletRequestDummy
	{
		@Override
		public String getContextPath()
		{
			return "/contextPath";
		}

		@Override
		public String getServletPath()
		{
			return "/servletPath";
		}
	}

	static class Response extends HttpServletResponseDummy
	{
		final String body;
		int contentLength = -1;

		Response(final String body)
		{
			this.body = body;
		}

		@Override
		public void setContentType(final String type)
		{
			assertEquals("text/plain", type);
		}

		@Override
		public void setCharacterEncoding(final String charset)
		{
			assertEquals(CharsetName.UTF8, charset);
		}

		@Override
		public void setContentLength(final int len)
		{
			contentLength = len;
		}

		@Override
		public ServletOutputStream getOutputStream()
		{
			return new ServletOutputStream()
			{
				@Override
			   public void write(final byte b[], final int off, final int len)
			   {
			   	assertEquals(body, new String(b, off, len, UTF8));
			   	assertEquals(contentLength, len);
			   }

				@Override
				public void write(final int b)
				{
					throw new RuntimeException();
				}
			};
		}
	}

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
	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
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
			new TextUrlFilter(roh, "text/plain", UTF8, null, null, null, null);
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
			new TextUrlFilter(roh, "text/plain", UTF8, "", null, null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteStart", e.getMessage());
		}
	}
	public void testPasteStopNull()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF8, "(", null, null, null);
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
			new TextUrlFilter(roh, "text/plain", UTF8, "(", "", null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteStop", e.getMessage());
		}
	}
	public void testPasteKeyNull()
	{
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF8, "(", ")", null, null);
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
			new TextUrlFilter(roh, "text/plain", UTF8, "(", ")", new StringField(), null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteValue", e.getMessage());
		}
	}
}

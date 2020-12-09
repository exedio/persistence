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

import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.pattern.TextUrlFilterItem.TYPE;
import static com.exedio.cope.pattern.TextUrlFilterItem.fertig;
import static com.exedio.cope.tojunit.Assert.list;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.pattern.MediaPath.NotFound;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import javax.servlet.ServletOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TextUrlFilterTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(TextUrlFilterTest.class, "MODEL");
	}

	public TextUrlFilterTest()
	{
		super(MODEL);
	}

	TextUrlFilterItem item, item2;

	@BeforeEach final void setUp()
	{
		item  = new TextUrlFilterItem();
		item2 = new TextUrlFilterItem();
	}

	@Test void testPasteContentTypesAllowed()
	{
		assertEquals(list("image/png"), fertig.getPasteContentTypesAllowed());
	}

	@Test void testContentTypeNull() throws IOException
	{
		assertEquals(null, item.getFertigContentType());
		try
		{
			fertig.doGetAndCommit(new Request(), new Response(""), item);
			fail();
		}
		catch(final NotFound e)
		{
			assertEquals("is null", e.getMessage());
		}
		assertTrue(model.hasCurrentTransaction());
	}

	@Test void testTextUrlFilterItemNotExisting() throws IOException, NotFound
	{
		item.setFertigRaw("<eins><paste>uno</paste><zwei>");
		assertEquals("text/plain", item.getFertigContentType());
		try
		{
			fertig.doGetAndCommit(new Request(), new Response(""), item);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expected result of size one, but was empty for query: select this from TextUrlFilterItem-fertig where (parent='" + item + "' AND key='uno')", e.getMessage());
		}
		assertTrue(model.hasCurrentTransaction());
	}

	@Test void testTextUrlFilterGetContentItemNotExisting() throws IOException, NotFound
	{
		item.setFertigRaw("<eins><paste>uno</paste><zwei>");
		assertEquals("text/plain", item.getFertigContentType());
		try
		{
			item.getFertigContent( new Request() );
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expected result of size one, but was empty for query: select this from TextUrlFilterItem-fertig where (parent='" + item + "' AND key='uno')", e.getMessage());
		}
		assertTrue(model.hasCurrentTransaction());
	}

	@Test void testPasteLocatorUrl() throws IOException, NotFound
	{
		item.setFertigRaw("<eins><paste>uno</paste><zwei>");

		final String url1 = item.addFertigPaste("uno");
		final MediaPath.Locator l = fertig.getPasteLocator(item, "uno");
		assertLocator("TextUrlFilterItem-fertig/value/TextUrlFilterItem-fertig-0.png", l);
		assertEquals(l.getURLByConnect(), fertig.getPasteURL(item, "uno"));
		assertGet("<eins><override>" + url1 + "</override><zwei>");
	}

	@Test void testPasteLocatorNotFound()
	{
		try
		{
			fertig.getPasteLocator(item, "uno");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expected result of size one, but was empty for query: select this from TextUrlFilterItem-fertig where (parent='" + item + "' AND key='uno')", e.getMessage());
		}
	}

	@Test void testPasteUrlNotFound()
	{
		try
		{
			fertig.getPasteURL(item, "uno");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expected result of size one, but was empty for query: select this from TextUrlFilterItem-fertig where (parent='" + item + "' AND key='uno')", e.getMessage());
		}
	}

	@Test void testDuplicatePasteValue() throws IOException
	{
		item.setFertigRaw("<eins><paste>uno</paste><zwei>");
		item.addFertigPaste("uno");

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
	}

	@Test void testUrlReplacement() throws IOException, NotFound
	{
		item.setFertigRaw("<eins><paste>uno</paste><zwei>");

		final String url1 = item.addFertigPaste("uno");
		assertGet("<eins><override>" + url1 + "</override><zwei>");

		final String url2 = item.addFertigPaste("duo");
		item.setFertigRaw("<paste>uno</paste><eins><paste>duo</paste>");
		assertGet("<override>" + url1 + "</override><eins><override>" + url2 + "</override>");

		item.setFertigRaw("<eins><paste>uno</paste><zwei><paste>duo</paste><drei>");
		assertGet("<eins><override>" + url1 + "</override><zwei><override>" + url2 + "</override><drei>");

		item.setFertigRaw("<eins><Xpaste>uno</paste><zwei><Xpaste>duo</paste><drei>");
		assertGet("<eins><Xpaste>uno</paste><zwei><Xpaste>duo</paste><drei>");

		item.setFertigRaw("<eins><paste>EXTRA</paste><zwei>");
		assertGet("<eins><extra/><zwei>");

		item.setFertigRaw("<eins><paste>EXTRA</paste><paste>EXTRA</paste><zwei>");
		assertGet("<eins><extra/><extra/><zwei>");
	}

	@Test void testPasteTypo() throws IOException, NotFound
	{
		item.setFertigRaw("<eins><paste>uno</Xpaste><zwei>");
		try
		{
			fertig.doGetAndCommit(new Request(), new Response(""), item);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("<paste>:6/</paste>", e.getMessage());
		}
		assertTrue(model.hasCurrentTransaction());
	}

	@Test void testMalformedRawContent() throws IOException, NotFound
	{
		item.setFertigRaw("<eins><paste>");
		try
		{
			fertig.doGetAndCommit(new Request(), new Response(""), item);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("<paste>:6/</paste>", e.getMessage());
		}
		assertTrue(model.hasCurrentTransaction());
	}

	@Test void testCheckContentTypeNull()
	{
		try
		{
			item.checkFertig();
			fail();
		}
		catch(final NotFound e)
		{
			assertEquals("is null", e.getMessage());
		}
	}

	@Test void testCheckBrokenLink() throws IOException, NotFound
	{
		item.setFertigRaw("<eins><paste>uno</paste><zwei>");
		assertEquals(new HashSet<>(Arrays.asList("uno")), item.checkFertig());
	}

	@Test void testCheckMultipleBrokenLink() throws IOException, NotFound
	{
		item.setFertigRaw("<eins><paste>uno</paste><paste>duo</paste><zwei>");
		assertEquals(new HashSet<>(Arrays.asList("uno","duo")), item.checkFertig());
	}

	private void assertGet(final String body) throws IOException, NotFound
	{
		assertEquals("text/plain", item.getFertigContentType());
		fertig.doGetAndCommit(new Request(), new Response(body), item);
		assertFalse(model.hasCurrentTransaction());
		model.startTransaction(TextUrlFilterTest.class.getName());
		assertEquals(body, item.getFertigContent(new Request()));
		assertTrue(model.hasCurrentTransaction());
		assertEquals(Collections.emptySet(), fertig.check(item));
		assertTrue(model.hasCurrentTransaction());
	}

	static class Request extends AssertionFailedHttpServletRequest
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

	static class Response extends AssertionFailedHttpServletResponse
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
			assertEquals(UTF_8.name(), charset);
		}

		@Override
		public void setContentLength(final int len)
		{
			contentLength = len;
		}

		@Override
		public ServletOutputStream getOutputStream()
		{
			return new AssertionFailedServletOutputStream()
			{
				@Override
				public void write(final byte[] b, final int off, final int len)
				{
					assertEquals(body, new String(b, off, len, UTF_8));
					assertEquals(contentLength, len);
				}
			};
		}
	}
}

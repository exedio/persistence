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
import static com.exedio.cope.util.CharsetName.UTF8;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.pattern.MediaPath.NotFound;
import com.exedio.cope.util.CharsetName;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

	public void testIt() throws IOException, NotFound
	{
		assertEquals(list("image/png"), fertig.getPasteContentTypesAllowed());

		final String rootUrl = model.getConnectProperties().getMediaRootUrl();

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
		assertLocator(
				(MediaPath)fertig.getSourceTypes().get(0).getFeature("value"),
				"TextUrlFilterItem-fertig/value/TextUrlFilterItem-fertig-0.png",
				fertig.getPasteLocator(item, "uno"));
		assertEquals(rootUrl + "TextUrlFilterItem-fertig/value/TextUrlFilterItem-fertig-0.png", fertig.getPasteURL(item, "uno"));
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
			   public void write(final byte b[], final int off, final int len) throws IOException
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

	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
	public void testFail()
	{
		try
		{
			new TextUrlFilter(null, null, null, null, null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("source", e.getMessage());
		}
		try
		{
			new TextUrlFilter(roh, null, null, null, null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("supportedContentType", e.getMessage());
		}
		try
		{
			new TextUrlFilter(roh, "text/plain", null, null, null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("encoding", e.getMessage());
		}
		try
		{
			new TextUrlFilter(roh, "text/plain", "zack", null, null, null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(UnsupportedEncodingException.class, e.getCause().getClass());
		}
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF8, null, null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteStart", e.getMessage());
		}
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF8, "", null, null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteStart", e.getMessage());
		}
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF8, "(", null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteStop", e.getMessage());
		}
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF8, "(", "", null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteStop", e.getMessage());
		}
		try
		{
			new TextUrlFilter(roh, "text/plain", UTF8, "(", ")", null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteKey", e.getMessage());
		}
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

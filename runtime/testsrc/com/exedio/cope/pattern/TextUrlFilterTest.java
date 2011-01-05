/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.TextUrlFilterItem.TYPE;
import static com.exedio.cope.pattern.TextUrlFilterItem.fertig;
import static com.exedio.cope.pattern.TextUrlFilterItem.roh;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;

public class TextUrlFilterTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(TYPE);

	public TextUrlFilterTest()
	{
		super(MODEL);
	}

	TextUrlFilterItem item;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new TextUrlFilterItem());
	}

	public void testIt() throws IOException
	{
		final String rootUrl = model.getConnectProperties().getMediaRootUrl();
		final String URL1 = "/contextPath/servletPath/TextUrlFilterItem-fertig/value/TextUrlFilterItem-fertig-0.png";
		final String URL2 = "/contextPath/servletPath/TextUrlFilterItem-fertig/value/TextUrlFilterItem-fertig-1.png";

		assertEquals(fertig.isNull, fertig.doGetIfModified(null, null, item));

		item.setFertigRaw("<eins>paste(uno)<zwei>");
		try
		{
			fertig.doGetIfModified(new Request(), null, item);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expected result of size one, but was empty for query: select this from TextUrlFilterItem-fertig where (parent='TextUrlFilterItem-0' AND key='uno')", e.getMessage());
		}

		item.addFertigPaste("uno");
		assertEquals("TextUrlFilterItem-fertig/value/TextUrlFilterItem-fertig-0.png", fertig.getPasteLocator(item, "uno").getPath());
		assertEquals(rootUrl + "TextUrlFilterItem-fertig/value/TextUrlFilterItem-fertig-0.png", fertig.getPasteURL(item, "uno"));
		assertGet("<eins>" + URL1 + "<zwei>");

		item.addFertigPaste("duo");
		assertGet("<eins>" + URL1 + "<zwei>");

		item.setFertigRaw("paste(uno)<eins>paste(duo)");
		assertGet(URL1 + "<eins>" + URL2);
	}

	private void assertGet(final String body) throws IOException
	{
		fertig.doGetIfModified(new Request(), new Response(body), item);
	}

	static class Request extends RequestTemplate
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

	static class Response extends ResponseTemplate
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
			   	assertEquals(body, new String(b, off, len, "utf8"));
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
			new TextUrlFilter(roh, "text/plain", "utf8", null, null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteStart", e.getMessage());
		}
		try
		{
			new TextUrlFilter(roh, "text/plain", "utf8", "", null, null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteStart", e.getMessage());
		}
		try
		{
			new TextUrlFilter(roh, "text/plain", "utf8", "(", null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteStop", e.getMessage());
		}
		try
		{
			new TextUrlFilter(roh, "text/plain", "utf8", "(", "", null, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("pasteStop", e.getMessage());
		}
		try
		{
			new TextUrlFilter(roh, "text/plain", "utf8", "(", ")", null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteKey", e.getMessage());
		}
		try
		{
			new TextUrlFilter(roh, "text/plain", "utf8", "(", ")", new StringField(), null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("pasteValue", e.getMessage());
		}
	}
}

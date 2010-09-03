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

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Model;

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

	private static final String URL1 = "media/TextUrlFilterItem-fertig/value/TextUrlFilterItem-fertig-0.png";
	private static final String URL2 = "media/TextUrlFilterItem-fertig/value/TextUrlFilterItem-fertig-1.png";

	public void testIt() throws IOException
	{
		assertEquals(fertig.isNull, fertig.doGetIfModified(null, item));

		item.setFertigRaw("<eins>(uno)<zwei>");
		try
		{
			fertig.doGetIfModified(null, item);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expected result of size one, but was empty for query: select this from TextUrlFilterItem-fertig where (parent='TextUrlFilterItem-0' AND key='uno')", e.getMessage());
		}

		item.addFertigPaste("uno");
		fertig.doGetIfModified(new Response("<eins>" + URL1 + "<zwei>"), item);

		item.addFertigPaste("duo");
		assertGet("<eins>" + URL1 + "<zwei>");

		item.setFertigRaw("(uno)<eins>(duo)");
		assertGet(URL1 + "<eins>" + URL2);
	}

	private void assertGet(final String body) throws IOException
	{
		fertig.doGetIfModified(new Response(body), item);
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
}

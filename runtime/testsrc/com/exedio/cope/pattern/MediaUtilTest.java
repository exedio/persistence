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

import static com.exedio.cope.pattern.MediaUtil.send;
import static com.exedio.cope.util.Hex.encodeLower;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class MediaUtilTest
{
	@Test void testString() throws IOException
	{
		final Response r = new Response();
		send("major/minor", US_ASCII, "ABC", r);
		r.assertFinished(true);
	}

	@Test void testStringCharsetName() throws IOException
	{
		final Response r = new Response();
		send("major/minor", "US-ASCII", "ABC", r);
		r.assertFinished(true);
	}

	@Test void testBytes() throws IOException
	{
		final Response r = new Response();
		send("major/minor", new byte[]{'A','B','C'}, r);
		r.assertFinished(false);
	}

	@Test void testByteArrayOutputStream() throws IOException
	{
		final Response r = new Response();
		final ByteArrayOutputStream s = new ByteArrayOutputStream();
		s.write(new byte[]{'A','B','C'});
		send("major/minor", s, r);
		r.assertFinished(false);
	}

	private final MyTemporaryFolder files = new MyTemporaryFolder();

	@Test void testFile() throws IOException
	{
		final Response r = new Response();
		final File f = files.newFile(new byte[]{'A','B','C'});
		send("major/minor", f, r);
		r.assertFinished(false);
	}


	private static class Response extends AssertionFailedHttpServletResponse
	{
		boolean setContentType = false;
		boolean setContentLength = false;
		boolean setCharacterEncoding = false;
		ByteArrayOutputStream outputStream = null;

		Response()
		{
			// make package private
		}

		@Override
		public void setContentType(final String type)
		{
			assertFalse(setContentType);
			setContentType = true;
			assertEquals("major/minor", type);
		}

		@Override
		public void setContentLength(final int len)
		{
			assertFalse(setContentLength);
			setContentLength = true;
			assertEquals(3, len);
		}

		@Override
		public void setCharacterEncoding(final String charset)
		{
			assertFalse(setCharacterEncoding);
			setCharacterEncoding = true;
			assertEquals("US-ASCII", charset);
		}

		@Override
		public ServletOutputStream getOutputStream()
		{
			assertNull(outputStream);
			outputStream = new ByteArrayOutputStream();

			return new ServletOutputStream()
			{
				@Override
				public void write(final byte[] b, final int off, final int len)
				{
					outputStream.write(b, off, len);
				}

				@Override
				public void write(final int b)
				{
					throw new RuntimeException();
				}
			};
		}

		void assertFinished(final boolean setCharacterEncoding)
		{
			assertTrue(setContentType);
			assertTrue(setContentLength);
			assertEquals(setCharacterEncoding, this.setCharacterEncoding);
			assertNotNull(outputStream);
			assertEquals("414243", encodeLower(outputStream.toByteArray()));
		}
	}
}

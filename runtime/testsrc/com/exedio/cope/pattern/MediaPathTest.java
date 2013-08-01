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

import static com.exedio.cope.util.CharsetName.UTF8;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Model;
import com.exedio.cope.pattern.MediaPathFeature.Result;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public final class MediaPathTest extends AbstractRuntimeTest
{
	public static final Model MODEL = new Model(MediaPathItem.TYPE);

	static
	{
		MODEL.enableSerialization(MediaPathTest.class, "MODEL");
	}

	public MediaPathTest()
	{
		super(MODEL);
	}

	private MediaPathItem item;
	private String id;
	private MyMediaServlet servlet;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new MediaPathItem());
		id = item.getCopeID();
		servlet = new MyMediaServlet();
		servlet.initConnected(MODEL);
	}

	@Override
	public void tearDown() throws Exception
	{
		servlet.destroy();
		servlet = null;
		super.tearDown();
	}

	public void testNotFound() throws ServletException, IOException
	{
		item.setNormalContentType("blah/foo");
		assertNotFound("zack", "no such path");
		assertNotFound("zack/zick", "no such path");
		assertNotFound("/MediaPathItem/normal", "no such path");
		assertNotFound("/MediaPathItem/normal/", "not an item");
		assertNotFound("/MediaPathItem/normal/x", "not an item");
		assertNotFound("/MediaPathItem/normal/x.jpg", "not an item");
		assertNotFound("/MediaPathItem/normal/MediaPathItem-x.jpg", "not an item");
		assertNotFound("/MediaPathItem/normal/MediaPathItem-9999.jpg", "no such item");

		final String pathInfo = "/MediaPathItem/normal/" + id;
		assertOk(pathInfo);

		item.setNormalResult(Result.notFoundIsNull);
		assertNotFound(pathInfo, "is null");

		item.setNormalResult(Result.notFoundNotComputable);
		assertNotFound(pathInfo, "not computable");
	}

	public void testException() throws ServletException, IOException
	{
		final String pathInfo = "/MediaPathItem/normal/" + id;
		assertOk(pathInfo);

		servlet.failOnException = false;

		item.setNormalResult(Result.IOException);
		assertError(pathInfo);

		item.setNormalResult(Result.RuntimeException);
		assertError(pathInfo);
	}

	private void assertOk(
			final String pathInfo)
		throws ServletException, IOException
	{
		MODEL.commit();
		final Response response = new Response();
		servlet.service(new Request(pathInfo), response);
		MODEL.startTransaction("MediaPathTest");
		response.assertOk();
	}

	private void assertNotFound(
			final String pathInfo,
			final String reason)
		throws ServletException, IOException
	{
		MODEL.commit();
		final Response response = new Response();
		servlet.service(new Request(pathInfo), response);
		MODEL.startTransaction("MediaPathTest");
		response.assertError(SC_NOT_FOUND, "us-ascii", "text/html",
				"<html>\n" +
				"<head>\n" +
				"<title>Not Found</title>\n" +
				"<meta http-equiv=\"content-type\" content=\"text/html;charset=us-ascii\">\n" +
				"<meta name=\"generator\" content=\"cope media servlet\">\n" +
				"</head>\n" +
				"<body>\n" +
				"<h1>Not Found</h1>\n" +
				"The requested URL was not found on this server (" + reason + ").\n" +
				"</body>\n" +
				"</html>\n");
	}

	private void assertError(
			final String pathInfo)
		throws ServletException, IOException
	{
		MODEL.commit();
		final Response response = new Response();
		servlet.service(new Request(pathInfo), response);
		MODEL.startTransaction("MediaPathTest");
		response.assertError(SC_INTERNAL_SERVER_ERROR, "us-ascii", "text/html",
				"<html>\n" +
				"<head>\n" +
				"<title>Internal Server Error</title>\n" +
				"<meta http-equiv=\"content-type\" content=\"text/html;charset=us-ascii\">\n" +
				"<meta name=\"generator\" content=\"cope media servlet\">\n" +
				"</head>\n" +
				"<body>\n" +
				"<h1>Internal Server Error</h1>\n" +
				"An internal error occured on the server.\n" +
				"</body>\n" +
				"</html>\n");
	}

	private static final class Request extends HttpServletRequestDummy
	{
		private final String pathInfo;

		Request(final String pathInfo)
		{
			this.pathInfo = pathInfo;
		}

		@Override
		public String getMethod()
		{
			return "GET";
		}

		@Override
		public String getPathInfo()
		{
			return pathInfo;
		}

		@Override
		public String getQueryString()
		{
			return null;
		}
	}

	private static final class Response extends HttpServletResponseDummy
	{
		Response()
		{
			// make package private
		}


		private int status = Integer.MIN_VALUE;

		@Override
		public void setStatus(final int sc)
		{
			assertFalse(sc==Integer.MIN_VALUE);
			assertEquals(this.status, Integer.MIN_VALUE);
			assertNull(out);
			this.status = sc;
		}


		private String charset = null;

		@Override
		public void setCharacterEncoding(final String charset)
		{
			assertNotNull(charset);
			assertEquals(this.charset, null);
			assertNull(out);
			this.charset = charset;
		}


		String contentType = null;

		@Override
		public void setContentType(final String contentType)
		{
			assertNotNull(contentType);
			assertEquals(this.contentType, null);
			assertNull(out);
			this.contentType = contentType;
		}


		int contentLength = Integer.MIN_VALUE;

		@Override
		public void setContentLength(final int contentLength)
		{
			assertFalse(contentLength==Integer.MIN_VALUE);
			assertEquals(this.contentLength, Integer.MIN_VALUE);
			assertNull(out);
			this.contentLength = contentLength;
		}


		ByteArrayOutputStream out = null;

		@Override
		public ServletOutputStream getOutputStream()
		{
			assertNull(out);

			final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
			out = myOut;

			return new ServletOutputStream()
			{
				@Override
			   public void write(final byte b[], final int off, final int len)
			   {
					myOut.write(b, off, len);
			   }

				@Override
				public void write(final int b)
				{
					throw new RuntimeException();
				}
			};
		}

		void assertOk()
		{
			assertEquals("sc",            Integer.MIN_VALUE, this.status);
			assertEquals("charset",       null, this.charset);
			assertEquals("contentType",   null, this.contentType);
			assertEquals("content",       null, this.out);
			assertEquals("contentLength", Integer.MIN_VALUE, this.contentLength);
		}

		void assertError(
				final int sc,
				final String charset,
				final String contentType,
				final String content)
			throws UnsupportedEncodingException
		{
			assertEquals("sc",            sc,               this.status);
			assertEquals("charset",       charset,          this.charset);
			assertEquals("contentType",   contentType,      this.contentType);
			assertEquals("content",       content, new String(this.out.toByteArray(), UTF8));
			assertEquals("contentLength", content.length(), this.contentLength);
		}
	}

	private static final class MyMediaServlet extends MediaServlet
	{
		@SuppressFBWarnings("MSF_MUTABLE_SERVLET_FIELD")
		boolean failOnException = true;

		MyMediaServlet()
		{
			// make package private
		}

		@Override
		protected void onException(
				final HttpServletRequest request,
				final Exception exception)
		{
			if(failOnException)
				throw new RuntimeException(exception);
		}
		private static final long serialVersionUID = 1L;
	}
}

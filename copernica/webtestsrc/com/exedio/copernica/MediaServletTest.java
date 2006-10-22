/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

package com.exedio.copernica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MediaServletTest extends AbstractWebTest
{
	private static final String NO_SUCH_PATH = "no such path";
	private static final String NOT_AN_ITEM = "not an item";
	private static final String NO_SUCH_ITEM = "no such item";
	private static final String IS_NULL = "is null";

	public void testError() throws Exception
	{
		final String prefix = "http://localhost:8080/copetest-hsqldb/media/MediaServletItem/";

		final long textLastModified = assertURL(new URL(prefix + "content/MediaServletItem.0.txt"));
		final long pngLastModified = assertBinary(new URL(prefix + "content/MediaServletItem.2.txt"), "image/png");
		final long jpegLastModified = assertBinary(new URL(prefix + "content/MediaServletItem.3.txt"), "image/jpeg");
		final long unknownLastModified = assertURL(new URL(prefix + "content/MediaServletItem.4.unknownma.unknownmi"), "unknownma/unknownmi");
		
		assertEquals(textLastModified, assertURL(new URL(prefix + "content/MediaServletItem.0.zick")));
		assertEquals(textLastModified, assertURL(new URL(prefix + "content/MediaServletItem.0.")));
		assertEquals(textLastModified, assertURL(new URL(prefix + "content/MediaServletItem.0")));
		assertNotFound(new URL(prefix + "kontent/MediaServletItem.0.txt"), NO_SUCH_PATH);
		assertNotFound(new URL(prefix + "content/MediaServletItem.150.txt"), NO_SUCH_ITEM);
		assertNotFound(new URL(prefix + "content/MediaServletItem.150.zick"), NO_SUCH_ITEM);
		assertNotFound(new URL(prefix + "content/MediaServletItem.150."), NO_SUCH_ITEM);
		assertNotFound(new URL(prefix + "content/MediaServletItem.150"), NO_SUCH_ITEM);
		assertNotFound(new URL(prefix + "content/MediaServletItem."), NOT_AN_ITEM);
		assertNotFound(new URL(prefix + "content/MediaServletItem"), NOT_AN_ITEM);
		assertNotFound(new URL(prefix + "content/MediaZack"), NOT_AN_ITEM);
		assertNotFound(new URL(prefix + "content/"), NOT_AN_ITEM);
		assertNotFound(new URL(prefix + "content"), NO_SUCH_PATH);
		assertNotFound(new URL(prefix + "content/zapp"), NOT_AN_ITEM);
		assertNotFound(new URL(prefix + "content/MediaServletItem.1.jpg"), IS_NULL);
		assertNotFound(new URL(prefix + "content/MediaServletItem.1."), IS_NULL);
		assertNotFound(new URL(prefix + "content/MediaServletItem.1"), IS_NULL);
		assertNotFound(new URL(prefix + "content/MediaServletItem.1.zick"), IS_NULL);
		
		assertEquals(textLastModified, assertURL(new URL(prefix + "content/MediaServletItem.0"), textLastModified-1, false));
		assertEquals(textLastModified, assertURL(new URL(prefix + "content/MediaServletItem.0"), textLastModified, true));
		assertEquals(textLastModified, assertURL(new URL(prefix + "content/MediaServletItem.0"), textLastModified+5000, true));

		assertEquals(unknownLastModified, assertURL(new URL(prefix + "content/MediaServletItem.4.unknownma.unknownmi"), "unknownma/unknownmi"));

		assertURLRedirect(new URL(prefix + "redirect/MediaServletItem.3.jpg"), prefix + "content/MediaServletItem.3.jpg");
		assertURLRedirect(new URL(prefix + "redirect/MediaServletItem.3."), prefix + "content/MediaServletItem.3.jpg");
		assertURLRedirect(new URL(prefix + "redirect/MediaServletItem.3"), prefix + "content/MediaServletItem.3.jpg");
		
		assertEquals(textLastModified, assertURL(new URL(prefix + "thumbnail/MediaServletItem.0")));
		assertNotFound(new URL(prefix + "thumbnail/MediaServletItem.1"), IS_NULL);
		assertEquals(pngLastModified, assertBinary(new URL(prefix + "thumbnail/MediaServletItem.2"), "image/jpeg"));
		assertEquals(jpegLastModified, assertBinary(new URL(prefix + "thumbnail/MediaServletItem.3"), "image/jpeg"));
		
		assertNotFound(new URL(prefix + "content/schnickschnack"), NOT_AN_ITEM);
		assertNotFound(new URL(prefix + "content/MediaServletItem.20.jpg"), NO_SUCH_ITEM);
		assertNotFound(new URL(prefix + "content/MediaServletItem.20."), NO_SUCH_ITEM);
		assertNotFound(new URL(prefix + "content/MediaServletItem.20"), NO_SUCH_ITEM);
		assertNotFound(new URL(prefix + "content/MediaServletItem."), NOT_AN_ITEM);
		assertNotFound(new URL(prefix + "content/MediaServletItem"), NOT_AN_ITEM);
		assertNotFound(new URL(prefix + "content/"), NOT_AN_ITEM);
		assertNotFound(new URL(prefix + "content"), NO_SUCH_PATH);
		assertNotFound(new URL(prefix + "content/MediaServletItem.1.jpg"), IS_NULL);
		assertNotFound(new URL(prefix + "content/MediaServletItem.1."), IS_NULL);
		assertNotFound(new URL(prefix + "content/MediaServletItem.1"), IS_NULL);
		assertNotFound(new URL(prefix + "content/MediaServletItem.1.zick"), IS_NULL);

		assertNameURL(new URL(prefix + "nameServer/MediaServletItem.5.txt"));
		assertNameURL(new URL(prefix + "nameServer/MediaServletItem.5."));
		assertNameURL(new URL(prefix + "nameServer/MediaServletItem.5"));
		assertNotFound(new URL(prefix + "nameServer/MediaServletItem.6.txt"), IS_NULL);
		assertNotFound(new URL(prefix + "nameServer/MediaServletItem.6."), IS_NULL);
		assertNotFound(new URL(prefix + "nameServer/MediaServletItem.6"), IS_NULL);
		assertNotFound(new URL(prefix + "nameServer/MediaServletItem.20.txt"), NO_SUCH_ITEM);
		assertNotFound(new URL(prefix + "nameServer/MediaServletItem.20."), NO_SUCH_ITEM);
		assertNotFound(new URL(prefix + "nameServer/MediaServletItem.20"), NO_SUCH_ITEM);
		assertNotFound(new URL(prefix + "nameServer/MediaServletItem."), NOT_AN_ITEM);
		assertNotFound(new URL(prefix + "nameServer/MediaServletItem"), NOT_AN_ITEM);
		assertNotFound(new URL(prefix + "nameServer/"), NOT_AN_ITEM);
		assertNotFound(new URL(prefix + "nameServer"), NO_SUCH_PATH);

		assertInternalError(new URL(prefix + "nameServer/MediaServletItem.7.txt"));
		assertInternalError(new URL(prefix + "nameServer/MediaServletItem.7."));
		assertInternalError(new URL(prefix + "nameServer/MediaServletItem.7"));
	}
	
	private long assertURL(final URL url) throws IOException
	{
		return assertURL(url, -1, false);
	}
	
	private long assertURL(final URL url, final String contentType) throws IOException
	{
		return assertURL(url, contentType, -1, false);
	}
	
	private long assertURL(final URL url, final long ifModifiedSince, final boolean expectNotModified) throws IOException
	{
		return assertURL(url, "text/plain", ifModifiedSince, expectNotModified);
	}

	private long assertURL(final URL url, final String contentType, final long ifModifiedSince, final boolean expectNotModified) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setFollowRedirects(false);
		if(ifModifiedSince>=0)
			conn.setIfModifiedSince(ifModifiedSince);
		conn.connect();
		assertEquals(expectNotModified ? conn.HTTP_NOT_MODIFIED : conn.HTTP_OK, conn.getResponseCode());
		assertEquals(expectNotModified ? "Not Modified" : "OK", conn.getResponseMessage());
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
		final long lastModified = conn.getLastModified();
		//System.out.println("LastModified: "+new Date(lastModified));
		assertTrue((date+1000)>=lastModified);
		assertEquals(expectNotModified ? null : contentType, conn.getContentType()); // TODO: content type should be set on 304
		//System.out.println("Expires: "+new Date(textConn.getExpiration()));
		assertWithin(new Date(date+4000), new Date(date+6000), new Date(conn.getExpiration()));
		assertEquals(expectNotModified ? -1 : 66, conn.getContentLength());
		
		final BufferedReader is = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		if(!expectNotModified)
		{
			assertEquals("This is an example file", is.readLine());
			assertEquals("for testing data", is.readLine());
			assertEquals("attributes in copernica.", is.readLine());
		}
		assertEquals(null, is.readLine());
		is.close();
		
		//textConn.setIfModifiedSince();
		return lastModified;
	}

	private void assertURLRedirect(final URL url, final String target) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setFollowRedirects(false);
		conn.connect();
		assertEquals(conn.HTTP_MOVED_PERM, conn.getResponseCode());
		assertEquals("Moved Permanently", conn.getResponseMessage());
		assertEquals(target, conn.getHeaderField("Location"));
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
	}

	private void assertNotFound(final URL url, final String detail) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setFollowRedirects(false);
		conn.connect();
		assertEquals(conn.HTTP_NOT_FOUND, conn.getResponseCode());
		assertEquals("Not Found", conn.getResponseMessage());
		assertEquals("text/html", conn.getContentType());
		
		final BufferedReader is = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		assertEquals("<html>", is.readLine());
		assertEquals("<head>", is.readLine());
		assertEquals("<title>Not Found</title>", is.readLine());
		assertEquals("<meta name=\"generator\" content=\"cope media servlet\">", is.readLine());
		assertEquals("</head>", is.readLine());
		assertEquals("<body>", is.readLine());
		assertEquals("<h1>Not Found</h1>", is.readLine());
		assertEquals("The requested URL was not found on this server ("+detail+").", is.readLine());
		assertEquals("</body>", is.readLine());
		assertEquals("</html>", is.readLine());
		assertEquals(null, is.readLine());
		is.close();

		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
	}

	private long assertBinary(final URL url, final String contentType) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setFollowRedirects(false);
		conn.connect();
		assertEquals(conn.HTTP_OK, conn.getResponseCode());
		assertEquals("OK", conn.getResponseMessage());
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
		final long lastModified = conn.getLastModified();
		//System.out.println("LastModified: "+new Date(lastModified));
		assertTrue((date+1000)>=lastModified);
		assertEquals(contentType, conn.getContentType());
		//System.out.println("Expires: "+new Date(textConn.getExpiration()));
		assertWithin(new Date(date+4000), new Date(date+6000), new Date(conn.getExpiration()));
		
		return lastModified;
	}

	private void assertInternalError(final URL url) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setFollowRedirects(false);
		conn.connect();
		assertEquals(conn.HTTP_INTERNAL_ERROR, conn.getResponseCode());
		assertEquals("Internal Server Error", conn.getResponseMessage());
		assertEquals("text/html", conn.getContentType());
		
		final BufferedReader is = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		assertEquals("<html>", is.readLine());
		assertEquals("<head>", is.readLine());
		assertEquals("<title>Internal Server Error</title>", is.readLine());
		assertEquals("<meta name=\"generator\" content=\"cope media servlet\">", is.readLine());
		assertEquals("</head>", is.readLine());
		assertEquals("<body>", is.readLine());
		assertEquals("<h1>Internal Server Error</h1>", is.readLine());
		assertEquals("An internal error occured on the server.", is.readLine());
		assertEquals("</body>", is.readLine());
		assertEquals("</html>", is.readLine());
		assertEquals(null, is.readLine());
		is.close();

		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithinHttpDate(before, after, new Date(date));
	}

	private void assertNameURL(final URL url) throws IOException
	{
		final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setFollowRedirects(false);
		conn.connect();
		assertEquals(200, conn.getResponseCode());
		assertEquals("text/plain", conn.getContentType());
		assertEquals(12, conn.getContentLength());
		
		final BufferedReader is = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		assertEquals("media item 1", is.readLine());
		assertEquals(null, is.readLine());
		is.close();
	}

}

/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MediaServletTest extends AbstractWebTest
{

	public void testError() throws Exception
	{
		final String prefix = "http://localhost:8080/copetest-hsqldb/media/MediaItem/";

		final long textLastModified = assertURL(new URL(prefix + "file/0.txt"));
		assertEquals(textLastModified, assertURL(new URL(prefix + "file/0.zick")));
		assertEquals(textLastModified, assertURL(new URL(prefix + "file/0.")));
		assertEquals(textLastModified, assertURL(new URL(prefix + "file/0")));
		assertNotFound(new URL(prefix + "kile/0.txt"));
		assertNotFound(new URL(prefix + "file/15.txt"));
		assertNotFound(new URL(prefix + "file/15.zick"));
		assertNotFound(new URL(prefix + "file/15."));
		assertNotFound(new URL(prefix + "file/15"));
		assertNotFound(new URL(prefix + "file/"));
		assertNotFound(new URL(prefix + "file"));
		assertNotFound(new URL(prefix + "file/zapp"));
		
		assertEquals(textLastModified, assertURL(new URL(prefix + "file/0"), textLastModified-1, false));
		assertEquals(textLastModified, assertURL(new URL(prefix + "file/0"), textLastModified, true));
		assertEquals(textLastModified, assertURL(new URL(prefix + "file/0"), textLastModified+5000, true));

		assertEquals(textLastModified, assertURL(new URL(prefix + "file/2.unknownma.unknownmi"), "unknownma/unknownmi"));

		assertURLRedirect(new URL(prefix + "foto/0.jpg"), prefix + "photo/0.jpg");
		assertURLRedirect(new URL(prefix + "foto/0."), prefix + "photo/0.jpg");
		assertURLRedirect(new URL(prefix + "foto/0"), prefix + "photo/0.jpg");
		assertNotFound(new URL(prefix + "foto/schnickschnack"));
		assertNotFound(new URL(prefix + "foto/20.jpg"));
		assertNotFound(new URL(prefix + "foto/20."));
		assertNotFound(new URL(prefix + "foto/20"));
		assertNotFound(new URL(prefix + "foto/"));
		assertNotFound(new URL(prefix + "foto"));

		assertNameURL(new URL(prefix + "nameServer/0.txt"));
		assertNameURL(new URL(prefix + "nameServer/0."));
		assertNameURL(new URL(prefix + "nameServer/0"));
		assertNotFound(new URL(prefix + "nameServer/20.txt"));
		assertNotFound(new URL(prefix + "nameServer/20."));
		assertNotFound(new URL(prefix + "nameServer/20"));
		assertNotFound(new URL(prefix + "nameServer/"));
		assertNotFound(new URL(prefix + "nameServer"));
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
		assertEquals(expectNotModified ? 304 : 200, conn.getResponseCode());
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithin(3000, before, after, new Date(date));
		final long lastModified = conn.getLastModified();
		//System.out.println("LastModified: "+new Date(lastModified));
		assertTrue((date+1000)>=lastModified);
		assertEquals(expectNotModified ? null : contentType, conn.getContentType()); // TODO: content type should be set on 304
		//System.out.println("Expires: "+new Date(textConn.getExpiration()));
		assertWithin(new Date(date+4000), new Date(date+6000), new Date(conn.getExpiration()));
		assertEquals(expectNotModified ? -1 : 66, conn.getContentLength());
		
		final BufferedReader is = new BufferedReader(new InputStreamReader((InputStream)conn.getInputStream()));
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
		assertEquals(301, conn.getResponseCode());
		assertEquals(target, conn.getHeaderField("Location"));
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithin(3000, before, after, new Date(date));
	}

	private void assertNotFound(final URL url) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setFollowRedirects(false);
		conn.connect();
		assertEquals(404, conn.getResponseCode());
		assertEquals("text/html", conn.getContentType());
		/*final BufferedReader is = new BufferedReader(new InputStreamReader((InputStream)conn.getInputStream()));
		assertEquals("<html>", is.readLine());
		assertEquals("<head>", is.readLine());
		assertEquals("<title>Not Found</title>", is.readLine());
		assertEquals(null, is.readLine());
		is.close();*/
		/*assertEquals("<html>\n" +
				"<head>\n" +
				"<title>Not Found</title>\n" +
				"<meta name=\"generator\" content=\"cope media servlet\">\n" +
				"</head>\n" +
				"<body>\n" +
				"<h1>Not Found</h1>\n" +
				"The requested URL was not found on this server.\n" +
				"</body>\n" +
				"</html>\n", conn.getContent());*/
		final long date = conn.getDate();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertWithin(3000, before, after, new Date(date));
	}

	private void assertNameURL(final URL url) throws IOException
	{
		final Date before = new Date();
		final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setFollowRedirects(false);
		conn.connect();
		assertEquals(200, conn.getResponseCode());
		assertEquals("text/plain", conn.getContentType());
		assertEquals(12, conn.getContentLength());
		
		final BufferedReader is = new BufferedReader(new InputStreamReader((InputStream)conn.getInputStream()));
		assertEquals("media item 1", is.readLine());
		assertEquals(null, is.readLine());
		is.close();
	}

}

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

public class DataServletTest extends AbstractWebTest
{

	public void testError() throws Exception
	{
		final String prefix = "http://localhost:8080/copetest-mysql/xdata/";

		final long textLastModified = assertURL(new URL(prefix + "DataItem/file/0.txt"));
		assertEquals(textLastModified, assertURL(new URL(prefix + "DataItem/file/0.zick")));
		assertEquals(textLastModified, assertURL(new URL(prefix + "DataItem/file/0.")));
		assertEquals(textLastModified, assertURL(new URL(prefix + "DataItem/file/0")));
	}
	
	private long assertURL(final URL url) throws IOException
	{
		final HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		final Date before = new Date();
		conn.connect();
		final Date after = new Date();
		//System.out.println("Date: "+new Date(date));
		assertEquals(200, conn.getResponseCode());
		final long date = conn.getDate();
		assertWithin(1000, before, after, new Date(date));
		final long lastModified = conn.getLastModified();
		assertTrue(date>lastModified);
		//System.out.println("LastModified: "+new Date(textLastModified));
		assertEquals("text/plain", conn.getContentType());
		//System.out.println("Expires: "+new Date(textConn.getExpiration()));
		assertWithin(new Date(date+4000), new Date(date+6000), new Date(conn.getExpiration()));
		
		final BufferedReader is = new BufferedReader(new InputStreamReader((InputStream)conn.getInputStream()));
		assertEquals("This is an example file", is.readLine());
		assertEquals("for testing data", is.readLine());
		assertEquals("attributes in copernica.", is.readLine());
		assertEquals(null, is.readLine());
		is.close();
		
		//textConn.setIfModifiedSince();
		return lastModified;
	}


}

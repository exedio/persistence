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

package com.exedio.copernica;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class AbstractWebTest extends TestCase
{
	protected static final String SAVE_BUTTON = "Save";

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		post("console/schema.html", "schema.create");
		post("init.jsp", "INIT");
	}
	
	private static final void post(final String url, final String button) throws Exception
	{
		final byte[] content = new String(button + "=true").getBytes("UTF8");
		final URL u = new URL("http://127.0.0.1:8080/copetest-hsqldb/" + url);
		//final long start = System.currentTimeMillis();
		final HttpURLConnection con = (HttpURLConnection)u.openConnection();
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
		con.setRequestMethod("POST");
		con.setFixedLengthStreamingMode(content.length);
		con.setDoInput(true);
		con.setDoOutput(true);
		con.getOutputStream().write(content);
		final int responseCode = con.getResponseCode();
		final String responseMessage = con.getResponseMessage();
		
		con.disconnect();
		//System.out.println("test tomcat connect " + (System.currentTimeMillis() - start) + "ms");
		if(responseCode!=200)
			throw new IOException("expected http response 200 (OK), but was " + responseCode + ' ' + '(' + responseMessage + ')');
	}
	
	@Override
	public void tearDown() throws Exception
	{
		post("console/schema.html", "DROP");
		super.tearDown();
	}
	
	
	// ----------------------------------- adapted from CopeAssert
	
	private static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";

	public final static void assertWithinHttpDate(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final long resolution = 1000;
		final long leftTolerance = 995;
		final Date expectedBeforeFloor = new Date(((expectedBefore.getTime()-leftTolerance) / resolution) * resolution);
		final Date expectedAfterCeil   = new Date(((expectedAfter.getTime() / resolution) * resolution) + resolution);

		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
		final String message =
			"expected date within " + df.format(expectedBeforeFloor) + " (" + df.format(expectedBefore) + ")" +
			" and " + df.format(expectedAfterCeil) + " (" + df.format(expectedAfter) + ")" +
			", but was " + df.format(actual);

		assertTrue(message, !expectedBeforeFloor.after(actual));
		assertTrue(message, !expectedAfterCeil.before(actual));
	}

	public final static void assertWithin(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
		final String message =
			"expected date within " + df.format(expectedBefore) +
			" and " + df.format(expectedAfter) +
			", but was " + df.format(actual);

		assertTrue(message, !expectedBefore.after(actual));
		assertTrue(message, !expectedAfter.before(actual));
	}
}

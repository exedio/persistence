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

import static jakarta.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class MediaPathPostTest
{
	private MediaServlet servlet;

	@BeforeEach void setUp()
	{
		servlet = new MediaServlet();
	}

	@AfterEach void tearDown()
	{
		servlet.destroy();
	}

	@Test void testPost() throws ServletException, IOException
	{
		final Response response = new Response();
		servlet.service(new Request(), response);
		assertEquals(SC_METHOD_NOT_ALLOWED, response.sc);
		assertEquals("HTTP method POST is not supported by this URL", response.msg);
	}

	private static class Request extends AssertionFailedHttpServletRequest
	{
		Request()
		{
			// make package private
		}

		@Override
		public String getMethod()
		{
			return "POST";
		}
		@Override
		public String getProtocol()
		{
			return "HTTP 1.1";
		}
	}

	private static class Response extends AssertionFailedHttpServletResponse
	{
		int sc = Integer.MIN_VALUE;
		String msg = null;

		Response()
		{
			// make package private
		}

		@Override
		public void sendError(final int sc, final String msg)
		{
			this.sc = sc;
			this.msg = msg;
		}
	}
}

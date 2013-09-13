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

import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

class ResponseTemplate implements HttpServletResponse
{

	public void addCookie(final Cookie cookie)
	{
		throw new RuntimeException();
	}

	public void addDateHeader(final String name, final long date)
	{
		throw new RuntimeException();
	}

	public void addHeader(final String name, final String value)
	{
		throw new RuntimeException();
	}

	public void addIntHeader(final String name, final int value)
	{
		throw new RuntimeException();
	}

	public boolean containsHeader(final String name)
	{
		throw new RuntimeException();
	}

	public String encodeRedirectURL(final String url)
	{
		throw new RuntimeException();
	}

	@Deprecated
	public String encodeRedirectUrl(final String url)
	{
		throw new RuntimeException();
	}

	public String encodeURL(final String url)
	{
		throw new RuntimeException();
	}

	@Deprecated
	public String encodeUrl(final String url)
	{
		throw new RuntimeException();
	}

	public void sendError(final int sc)
	{
		throw new RuntimeException();
	}

	public void sendError(final int sc, final String msg)
	{
		throw new RuntimeException();
	}

	public void sendRedirect(final String location)
	{
		throw new RuntimeException();
	}

	public void setDateHeader(final String name, final long date)
	{
		throw new RuntimeException();
	}

	public void setHeader(final String name, final String value)
	{
		throw new RuntimeException();
	}

	public void setIntHeader(final String name, final int value)
	{
		throw new RuntimeException();
	}

	public void setStatus(final int sc)
	{
		throw new RuntimeException();
	}

	@Deprecated
	public void setStatus(final int sc, final String sm)
	{
		throw new RuntimeException();
	}

	public void flushBuffer()
	{
		throw new RuntimeException();
	}

	public int getBufferSize()
	{
		throw new RuntimeException();
	}

	public String getCharacterEncoding()
	{
		throw new RuntimeException();
	}

	public String getContentType()
	{
		throw new RuntimeException();
	}

	public Locale getLocale()
	{
		throw new RuntimeException();
	}

	public ServletOutputStream getOutputStream()
	{
		throw new RuntimeException();
	}

	public PrintWriter getWriter()
	{
		throw new RuntimeException();
	}

	public boolean isCommitted()
	{
		throw new RuntimeException();
	}

	public void reset()
	{
		throw new RuntimeException();
	}

	public void resetBuffer()
	{
		throw new RuntimeException();
	}

	public void setBufferSize(final int size)
	{
		throw new RuntimeException();
	}

	public void setCharacterEncoding(final String charset)
	{
		throw new RuntimeException();
	}

	public void setContentLength(final int len)
	{
		throw new RuntimeException();
	}

	public void setContentType(final String type)
	{
		throw new RuntimeException();
	}

	public void setLocale(final Locale loc)
	{
		throw new RuntimeException();
	}
}

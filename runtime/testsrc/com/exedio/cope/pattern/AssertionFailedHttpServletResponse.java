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

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

@SuppressWarnings("RedundantThrows") // RedundantThrows: allow subclasses to throw exceptions
class AssertionFailedHttpServletResponse implements HttpServletResponse
{

	@Override
	public String getCharacterEncoding()
	{
		throw new AssertionError();
	}

	@Override
	public String getContentType()
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unused")
	@Override
	public ServletOutputStream getOutputStream() throws IOException
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unused")
	@Override
	public PrintWriter getWriter() throws IOException
	{
		throw new AssertionError();
	}

	@Override
	public void setCharacterEncoding(final String charset)
	{
		throw new AssertionError();
	}

	@Override
	public void setContentLength(final int len)
	{
		throw new AssertionError();
	}

	@Override
	public void setContentLengthLong(final long len)
	{
		throw new AssertionError();
	}

	@Override
	public void setContentType(final String type)
	{
		throw new AssertionError();
	}

	@Override
	public void setBufferSize(final int size)
	{
		throw new AssertionError();
	}

	@Override
	public int getBufferSize()
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unused")
	@Override
	public void flushBuffer() throws IOException
	{
		throw new AssertionError();
	}

	@Override
	public void resetBuffer()
	{
		throw new AssertionError();
	}

	@Override
	public boolean isCommitted()
	{
		throw new AssertionError();
	}

	@Override
	public void reset()
	{
		throw new AssertionError();
	}

	@Override
	public void setLocale(final Locale loc)
	{
		throw new AssertionError();
	}

	@Override
	public Locale getLocale()
	{
		throw new AssertionError();
	}

	@Override
	public void addCookie(final Cookie cookie)
	{
		throw new AssertionError();
	}

	@Override
	public boolean containsHeader(final String name)
	{
		throw new AssertionError();
	}

	@Override
	public String encodeURL(final String url)
	{
		throw new AssertionError();
	}

	@Override
	public String encodeRedirectURL(final String url)
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unused")
	@Override
	public void sendError(final int sc, final String msg) throws IOException
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unused")
	@Override
	public void sendError(final int sc) throws IOException
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unused")
	@Override
	public void sendRedirect(final String location) throws IOException
	{
		throw new AssertionError();
	}

	@Override
	public void setDateHeader(final String name, final long date)
	{
		throw new AssertionError(name);
	}

	@Override
	public void addDateHeader(final String name, final long date)
	{
		throw new AssertionError();
	}

	@Override
	public void setHeader(final String name, final String value)
	{
		throw new AssertionError(name);
	}

	@Override
	public void addHeader(final String name, final String value)
	{
		throw new AssertionError();
	}

	@Override
	public void setIntHeader(final String name, final int value)
	{
		throw new AssertionError();
	}

	@Override
	public void addIntHeader(final String name, final int value)
	{
		throw new AssertionError();
	}

	@Override
	public void setStatus(final int sc)
	{
		throw new AssertionError();
	}

	@Override
	public int getStatus()
	{
		throw new AssertionError();
	}

	@Override
	public String getHeader(final String name)
	{
		throw new AssertionError();
	}

	@Override
	public Collection<String> getHeaders(final String name)
	{
		throw new AssertionError();
	}

	@Override
	public Collection<String> getHeaderNames()
	{
		throw new AssertionError();
	}
}

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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("NM_CONFUSING")
class DummyResponse implements HttpServletResponse
{
	@Override
	public String getCharacterEncoding()
	{
		throw new RuntimeException();
	}

	@Override
	public String getContentType()
	{
		throw new RuntimeException();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException
	{
		throw new IOException();
	}

	@Override
	public PrintWriter getWriter() throws IOException
	{
		throw new IOException();
	}

	@Override
	public void setCharacterEncoding(final String charset)
	{
		throw new RuntimeException();
	}

	@Override
	public void setContentLength(final int len)
	{
		throw new RuntimeException();
	}

	@Override
	public void setContentType(final String type)
	{
		throw new RuntimeException();
	}

	@Override
	public void setBufferSize(final int size)
	{
		throw new RuntimeException();
	}

	@Override
	public int getBufferSize()
	{
		throw new RuntimeException();
	}

	@Override
	public void flushBuffer() throws IOException
	{
		throw new IOException();
	}

	@Override
	public void resetBuffer()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isCommitted()
	{
		throw new RuntimeException();
	}

	@Override
	public void reset()
	{
		throw new RuntimeException();
	}

	@Override
	public void setLocale(final Locale loc)
	{
		throw new RuntimeException();
	}

	@Override
	public Locale getLocale()
	{
		throw new RuntimeException();
	}

	@Override
	public void addCookie(final Cookie cookie)
	{
		throw new RuntimeException();
	}

	@Override
	public boolean containsHeader(final String name)
	{
		throw new RuntimeException();
	}

	@Override
	public String encodeURL(final String url)
	{
		throw new RuntimeException();
	}

	@Override
	public String encodeRedirectURL(final String url)
	{
		throw new RuntimeException();
	}

	@Deprecated()
	@Override
	public String encodeUrl(final String url)
	{
		throw new RuntimeException();
	}

	@Deprecated()
	@Override
	public String encodeRedirectUrl(final String url)
	{
		throw new RuntimeException();
	}

	@Override
	public void sendError(final int sc, final String msg) throws IOException
	{
		throw new IOException();
	}

	@Override
	public void sendError(final int sc) throws IOException
	{
		throw new IOException();
	}

	@Override
	public void sendRedirect(final String location) throws IOException
	{
		throw new IOException();
	}

	@Override
	public void setDateHeader(final String name, final long date)
	{
		throw new RuntimeException();
	}

	@Override
	public void addDateHeader(final String name, final long date)
	{
		throw new RuntimeException();
	}

	@Override
	public void setHeader(final String name, final String value)
	{
		throw new RuntimeException();
	}

	@Override
	public void addHeader(final String name, final String value)
	{
		throw new RuntimeException();
	}

	@Override
	public void setIntHeader(final String name, final int value)
	{
		throw new RuntimeException();
	}

	@Override
	public void addIntHeader(final String name, final int value)
	{
		throw new RuntimeException();
	}

	@Override
	public void setStatus(final int sc)
	{
		throw new RuntimeException();
	}

	@Deprecated()
	@Override
	public void setStatus(final int sc, final String sm)
	{
		throw new RuntimeException();
	}
}

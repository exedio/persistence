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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@SuppressFBWarnings("NM_CONFUSING")
class HttpServletRequestUtilDummy implements HttpServletRequest
{
	@Override()
	public Object getAttribute(final String name)
	{
		throw new AssertionError();
	}

	@Override()
	public Enumeration<?> getAttributeNames()
	{
		throw new AssertionError();
	}

	@Override()
	public String getCharacterEncoding()
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unused")
	@Override()
	public void setCharacterEncoding(final String env) throws UnsupportedEncodingException
	{
		throw new AssertionError();
	}

	@Override()
	public int getContentLength()
	{
		throw new AssertionError();
	}

	@Override()
	public String getContentType()
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unused")
	@Override()
	public ServletInputStream getInputStream() throws IOException
	{
		throw new AssertionError();
	}

	@Override()
	public String getParameter(final String name)
	{
		throw new AssertionError(name);
	}

	@Override()
	public Enumeration<?> getParameterNames()
	{
		throw new AssertionError();
	}

	@Override()
	public String[] getParameterValues(final String name)
	{
		throw new AssertionError();
	}

	@Override()
	public Map<?, ?> getParameterMap()
	{
		throw new AssertionError();
	}

	@Override()
	public String getProtocol()
	{
		throw new AssertionError();
	}

	@Override()
	public String getScheme()
	{
		throw new AssertionError();
	}

	@Override()
	public String getServerName()
	{
		throw new AssertionError();
	}

	@Override()
	public int getServerPort()
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unused")
	@Override()
	public BufferedReader getReader() throws IOException
	{
		throw new AssertionError();
	}

	@Override()
	public String getRemoteAddr()
	{
		throw new AssertionError();
	}

	@Override()
	public String getRemoteHost()
	{
		throw new AssertionError();
	}

	@Override()
	public void setAttribute(final String name, final Object o)
	{
		throw new AssertionError();
	}

	@Override()
	public void removeAttribute(final String name)
	{
		throw new AssertionError();
	}

	@Override()
	public Locale getLocale()
	{
		throw new AssertionError();
	}

	@Override()
	public Enumeration<?> getLocales()
	{
		throw new AssertionError();
	}

	@Override()
	public boolean isSecure()
	{
		throw new AssertionError();
	}

	@Override()
	public RequestDispatcher getRequestDispatcher(final String path)
	{
		throw new AssertionError();
	}

	@Deprecated()
	@Override()
	public String getRealPath(final String path)
	{
		throw new AssertionError();
	}

	@Override()
	public int getRemotePort()
	{
		throw new AssertionError();
	}

	@Override()
	public String getLocalName()
	{
		throw new AssertionError();
	}

	@Override()
	public String getLocalAddr()
	{
		throw new AssertionError();
	}

	@Override()
	public int getLocalPort()
	{
		throw new AssertionError();
	}

	@Override()
	public String getAuthType()
	{
		throw new AssertionError();
	}

	@Override()
	public Cookie[] getCookies()
	{
		throw new AssertionError();
	}

	@Override()
	public long getDateHeader(final String name)
	{
		throw new AssertionError(name);
	}

	@Override()
	public String getHeader(final String name)
	{
		throw new AssertionError(name);
	}

	@Override()
	public Enumeration<?> getHeaders(final String name)
	{
		throw new AssertionError();
	}

	@Override()
	public Enumeration<?> getHeaderNames()
	{
		throw new AssertionError();
	}

	@Override()
	public int getIntHeader(final String name)
	{
		throw new AssertionError();
	}

	@Override()
	public String getMethod()
	{
		throw new AssertionError();
	}

	@Override()
	public String getPathInfo()
	{
		throw new AssertionError();
	}

	@Override()
	public String getPathTranslated()
	{
		throw new AssertionError();
	}

	@Override()
	public String getContextPath()
	{
		throw new AssertionError();
	}

	@Override()
	public String getQueryString()
	{
		throw new AssertionError();
	}

	@Override()
	public String getRemoteUser()
	{
		throw new AssertionError();
	}

	@Override()
	public boolean isUserInRole(final String role)
	{
		throw new AssertionError();
	}

	@Override()
	public Principal getUserPrincipal()
	{
		throw new AssertionError();
	}

	@Override()
	public String getRequestedSessionId()
	{
		throw new AssertionError();
	}

	@Override()
	public String getRequestURI()
	{
		throw new AssertionError();
	}

	@Override()
	public StringBuffer getRequestURL()
	{
		throw new AssertionError();
	}

	@Override()
	public String getServletPath()
	{
		throw new AssertionError();
	}

	@Override()
	public HttpSession getSession(final boolean create)
	{
		throw new AssertionError();
	}

	@Override()
	public HttpSession getSession()
	{
		throw new AssertionError();
	}

	@Override()
	public boolean isRequestedSessionIdValid()
	{
		throw new AssertionError();
	}

	@Override()
	public boolean isRequestedSessionIdFromCookie()
	{
		throw new AssertionError();
	}

	@Override()
	public boolean isRequestedSessionIdFromURL()
	{
		throw new AssertionError();
	}

	@Deprecated()
	@Override()
	public boolean isRequestedSessionIdFromUrl()
	{
		throw new AssertionError();
	}
}

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

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("RedundantThrows") // RedundantThrows: allow subclasses to throw exceptions
class AssertionFailedHttpServletRequest implements HttpServletRequest
{
	@Override
	public Object getAttribute(final String name)
	{
		throw new AssertionError();
	}

	@Override
	public Enumeration<String> getAttributeNames()
	{
		throw new AssertionError();
	}

	@Override
	public String getCharacterEncoding()
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unused")
	@Override
	public void setCharacterEncoding(final String env) throws UnsupportedEncodingException
	{
		throw new AssertionError();
	}

	@Override
	public int getContentLength()
	{
		throw new AssertionError();
	}

	@Override
	public long getContentLengthLong()
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
	public ServletInputStream getInputStream() throws IOException
	{
		throw new AssertionError();
	}

	@Override
	public String getParameter(final String name)
	{
		throw new AssertionError(name);
	}

	@Override
	public Enumeration<String> getParameterNames()
	{
		throw new AssertionError();
	}

	@Override
	public String[] getParameterValues(final String name)
	{
		throw new AssertionError();
	}

	@Override
	public Map<String, String[]>getParameterMap()
	{
		throw new AssertionError();
	}

	@Override
	public String getProtocol()
	{
		throw new AssertionError();
	}

	@Override
	public String getScheme()
	{
		throw new AssertionError();
	}

	@Override
	public String getServerName()
	{
		throw new AssertionError();
	}

	@Override
	public int getServerPort()
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unused")
	@Override
	public BufferedReader getReader() throws IOException
	{
		throw new AssertionError();
	}

	@Override
	public String getRemoteAddr()
	{
		throw new AssertionError();
	}

	@Override
	public String getRemoteHost()
	{
		throw new AssertionError();
	}

	@Override
	public void setAttribute(final String name, final Object o)
	{
		throw new AssertionError();
	}

	@Override
	public void removeAttribute(final String name)
	{
		throw new AssertionError();
	}

	@Override
	public Locale getLocale()
	{
		throw new AssertionError();
	}

	@Override
	public Enumeration<Locale> getLocales()
	{
		throw new AssertionError();
	}

	@Override
	public boolean isSecure()
	{
		throw new AssertionError();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(final String path)
	{
		throw new AssertionError();
	}

	@Override
	public int getRemotePort()
	{
		throw new AssertionError();
	}

	@Override
	public String getLocalName()
	{
		throw new AssertionError();
	}

	@Override
	public String getLocalAddr()
	{
		throw new AssertionError();
	}

	@Override
	public int getLocalPort()
	{
		throw new AssertionError();
	}

	@Override
	public String getAuthType()
	{
		throw new AssertionError();
	}

	@Override
	public Cookie[] getCookies()
	{
		throw new AssertionError();
	}

	@Override
	public long getDateHeader(final String name)
	{
		throw new AssertionError(name);
	}

	@Override
	public String getHeader(final String name)
	{
		throw new AssertionError(name);
	}

	@Override
	public Enumeration<String> getHeaders(final String name)
	{
		throw new AssertionError();
	}

	@Override
	public Enumeration<String> getHeaderNames()
	{
		throw new AssertionError();
	}

	@Override
	public int getIntHeader(final String name)
	{
		throw new AssertionError();
	}

	@Override
	public String getMethod()
	{
		throw new AssertionError();
	}

	@Override
	public String getPathInfo()
	{
		throw new AssertionError();
	}

	@Override
	public String getPathTranslated()
	{
		throw new AssertionError();
	}

	@Override
	public String getContextPath()
	{
		throw new AssertionError();
	}

	@Override
	public String getQueryString()
	{
		throw new AssertionError();
	}

	@Override
	public String getRemoteUser()
	{
		throw new AssertionError();
	}

	@Override
	public boolean isUserInRole(final String role)
	{
		throw new AssertionError();
	}

	@Override
	public Principal getUserPrincipal()
	{
		throw new AssertionError();
	}

	@Override
	public String getRequestedSessionId()
	{
		throw new AssertionError();
	}

	@Override
	public String getRequestURI()
	{
		throw new AssertionError();
	}

	@Override
	public StringBuffer getRequestURL()
	{
		throw new AssertionError();
	}

	@Override
	public String getServletPath()
	{
		throw new AssertionError();
	}

	@Override
	public HttpSession getSession(final boolean create)
	{
		throw new AssertionError();
	}

	@Override
	public HttpSession getSession()
	{
		throw new AssertionError();
	}

	@Override
	public String changeSessionId()
	{
		throw new AssertionError();
	}

	@Override
	public boolean isRequestedSessionIdValid()
	{
		throw new AssertionError();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie()
	{
		throw new AssertionError();
	}

	@Override
	public boolean isRequestedSessionIdFromURL()
	{
		throw new AssertionError();
	}

	@Override
	public boolean authenticate(final HttpServletResponse response)
	{
		throw new AssertionError();
	}

	@Override
	public void login(final String username, final String password)
	{
		throw new AssertionError();
	}

	@Override
	public void logout()
	{
		throw new AssertionError();
	}

	@Override
	public Collection<Part> getParts()
	{
		throw new AssertionError();
	}

	@Override
	public Part getPart(final String name)
	{
		throw new AssertionError();
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(final Class<T> handlerClass) throws IOException, ServletException
	{
		throw new AssertionError();
	}

	@Override
	public ServletContext getServletContext()
	{
		throw new AssertionError();
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException
	{
		throw new AssertionError();
	}

	@Override
	public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IllegalStateException
	{
		throw new AssertionError();
	}

	@Override
	public boolean isAsyncStarted()
	{
		throw new AssertionError();
	}

	@Override
	public boolean isAsyncSupported()
	{
		throw new AssertionError();
	}

	@Override
	public AsyncContext getAsyncContext()
	{
		throw new AssertionError();
	}

	@Override
	public DispatcherType getDispatcherType()
	{
		throw new AssertionError();
	}

	@Override
	public String getRequestId()
	{
		throw new AssertionError();
	}

	@Override
	public String getProtocolRequestId()
	{
		throw new AssertionError();
	}

	@Override
	public ServletConnection getServletConnection()
	{
		throw new AssertionError();
	}
}

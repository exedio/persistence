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

package com.exedio.cope.misc;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;

public class AssertionFailedServletContext implements ServletContext
{
	public String getInitParameter(final String name)
	{
		throw new AssertionError();
	}

	public String getServletContextName()
	{
		throw new AssertionError();
	}

	public Object getAttribute(final String arg0)
	{
		throw new AssertionError();
	}

	public Enumeration<?> getAttributeNames()
	{
		throw new AssertionError();
	}

	public ServletContext getContext(final String arg0)
	{
		throw new AssertionError();
	}

	public String getContextPath()
	{
		throw new AssertionError();
	}

	public Enumeration<?> getInitParameterNames()
	{
		throw new AssertionError();
	}

	public int getMajorVersion()
	{
		throw new AssertionError();
	}

	public String getMimeType(final String arg0)
	{
		throw new AssertionError();
	}

	public int getMinorVersion()
	{
		throw new AssertionError();
	}

	public RequestDispatcher getNamedDispatcher(final String arg0)
	{
		throw new AssertionError();
	}

	public String getRealPath(final String arg0)
	{
		throw new AssertionError();
	}

	public RequestDispatcher getRequestDispatcher(final String arg0)
	{
		throw new AssertionError();
	}

	public URL getResource(final String arg0)
	{
		throw new AssertionError();
	}

	public InputStream getResourceAsStream(final String arg0)
	{
		throw new AssertionError();
	}

	public Set<?> getResourcePaths(final String arg0)
	{
		throw new AssertionError();
	}

	public String getServerInfo()
	{
		throw new AssertionError();
	}

	@Deprecated
	public Servlet getServlet(final String arg0)
	{
		throw new AssertionError();
	}

	@Deprecated
	public Enumeration<?> getServletNames()
	{
		throw new AssertionError();
	}

	@Deprecated
	public Enumeration<?> getServlets()
	{
		throw new AssertionError();
	}

	public void log(final String arg0)
	{
		throw new AssertionError();
	}

	@Deprecated
	public void log(final Exception arg0, final String arg1)
	{
		throw new AssertionError();
	}

	public void log(final String arg0, final Throwable arg1)
	{
		throw new AssertionError();
	}

	public void removeAttribute(final String arg0)
	{
		throw new AssertionError();
	}

	public void setAttribute(final String arg0, final Object arg1)
	{
		throw new AssertionError();
	}
}

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
import java.util.EventListener;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

public class AssertionFailedServletContext implements ServletContext
{
	@Override
	public String getInitParameter(final String name)
	{
		throw new AssertionError();
	}

	@Override
	public String getServletContextName()
	{
		throw new AssertionError();
	}

	@Override
	public Object getAttribute(final String arg0)
	{
		throw new AssertionError();
	}

	@Override
	public Enumeration<String> getAttributeNames()
	{
		throw new AssertionError();
	}

	@Override
	public ServletContext getContext(final String arg0)
	{
		throw new AssertionError();
	}

	@Override
	public String getContextPath()
	{
		throw new AssertionError();
	}

	@Override
	public Enumeration<String> getInitParameterNames()
	{
		throw new AssertionError();
	}

	@Override
	public int getMajorVersion()
	{
		throw new AssertionError();
	}

	@Override
	public String getMimeType(final String arg0)
	{
		throw new AssertionError();
	}

	@Override
	public int getMinorVersion()
	{
		throw new AssertionError();
	}

	@Override
	public RequestDispatcher getNamedDispatcher(final String arg0)
	{
		throw new AssertionError();
	}

	@Override
	public String getRealPath(final String arg0)
	{
		throw new AssertionError();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(final String arg0)
	{
		throw new AssertionError();
	}

	@Override
	public URL getResource(final String arg0)
	{
		throw new AssertionError();
	}

	@Override
	public InputStream getResourceAsStream(final String arg0)
	{
		throw new AssertionError();
	}

	@Override
	public Set<String> getResourcePaths(final String arg0)
	{
		throw new AssertionError();
	}

	@Override
	public String getServerInfo()
	{
		throw new AssertionError();
	}

	@Deprecated
	@Override
	public Servlet getServlet(final String arg0)
	{
		throw new AssertionError();
	}

	@Deprecated
	@Override
	public Enumeration<String> getServletNames()
	{
		throw new AssertionError();
	}

	@Deprecated
	@Override
	public Enumeration<Servlet> getServlets()
	{
		throw new AssertionError();
	}

	@Override
	public void log(final String arg0)
	{
		throw new AssertionError();
	}

	@Deprecated
	@Override
	public void log(final Exception arg0, final String arg1)
	{
		throw new AssertionError();
	}

	@Override
	public void log(final String arg0, final Throwable arg1)
	{
		throw new AssertionError();
	}

	@Override
	public void removeAttribute(final String arg0)
	{
		throw new AssertionError();
	}

	@Override
	public void setAttribute(final String arg0, final Object arg1)
	{
		throw new AssertionError();
	}

	@Override
	public void declareRoles(final String... strings)
	{
		throw new AssertionError();
	}

	@Override
	public String getVirtualServerName()
	{
		throw new AssertionError();
	}

	@Override
	public int getSessionTimeout()
	{
		throw new AssertionError();
	}

	@Override
	public void setSessionTimeout(final int sessionTimeout)
	{
		throw new AssertionError();
	}

	@Override
	public String getRequestCharacterEncoding()
	{
		throw new AssertionError();
	}

	@Override
	public void setRequestCharacterEncoding(final String encoding)
	{
		throw new AssertionError();
	}

	@Override
	public String getResponseCharacterEncoding()
	{
		throw new AssertionError();
	}

	@Override
	public void setResponseCharacterEncoding(final String encoding)
	{
		throw new AssertionError();
	}

	@Override
	public ClassLoader getClassLoader()
	{
		throw new AssertionError();
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor()
	{
		throw new AssertionError();
	}

	@Override
	public <T extends EventListener> T createListener(final Class<T> type)
	{
		throw new AssertionError();
	}

	@Override
	public void addListener(final Class<? extends EventListener> listenerClass)
	{
		throw new AssertionError();
	}

	@Override
	public <T extends EventListener> void addListener(final T t)
	{
		throw new AssertionError();
	}

	@Override
	public void addListener(final String className)
	{
		throw new AssertionError();
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes()
	{
		throw new AssertionError();
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes()
	{
		throw new AssertionError();
	}

	@Override
	public void setSessionTrackingModes(final Set<SessionTrackingMode> sessionTrackingModes)
	{
		throw new AssertionError();
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig()
	{
		throw new AssertionError();
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations()
	{
		throw new AssertionError();
	}

	@Override
	public FilterRegistration getFilterRegistration(final String filterName)
	{
		throw new AssertionError();
	}

	@Override
	public <T extends Filter> T createFilter(final Class<T> clazz)
	{
		throw new AssertionError();
	}

	@Override
	public FilterRegistration.Dynamic addFilter(final String filterName, final Class<? extends Filter> filterClass)
	{
		throw new AssertionError();
	}

	@Override
	public FilterRegistration.Dynamic addFilter(final String filterName, final Filter filter)
	{
		throw new AssertionError();
	}

	@Override
	public FilterRegistration.Dynamic addFilter(final String filterName, final String className)
	{
		throw new AssertionError();
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations()
	{
		throw new AssertionError();
	}

	@Override
	public ServletRegistration getServletRegistration(final String servletName)
	{
		throw new AssertionError();
	}

	@Override
	public <T extends Servlet> T createServlet(final Class<T> clazz)
	{
		throw new AssertionError();
	}

	@Override
	public ServletRegistration.Dynamic addServlet(final String servletName, final Class<? extends Servlet> servletClass)
	{
		throw new AssertionError();
	}

	@Override
	public ServletRegistration.Dynamic addJspFile(final String servletName, final String jspFile)
	{
		throw new AssertionError();
	}

	@Override
	public ServletRegistration.Dynamic addServlet(final String servletName, final Servlet servlet)
	{
		throw new AssertionError();
	}

	@Override
	public ServletRegistration.Dynamic addServlet(final String servletName, final String className)
	{
		throw new AssertionError();
	}

	@Override
	public boolean setInitParameter(final String name, final String value)
	{
		throw new AssertionError();
	}

	@Override
	public int getEffectiveMajorVersion()
	{
		throw new AssertionError();
	}

	@Override
	public int getEffectiveMinorVersion()
	{
		throw new AssertionError();
	}
}

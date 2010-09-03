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

package com.exedio.cope.misc;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.PrefixSource;
import com.exedio.cope.util.Properties;

public class ServletUtilContextTest extends CopeAssert
{
	public void testIt()
	{
		{
			final Properties.Source s = ServletUtil.getPropertyContext(new TestContext("testContextPath.", "/testContextPath"));
			assertEquals("v1", s.get("p1"));
			assertEquals("v2", s.get("p2"));
			assertFails(s, "p3", "testContextPath.p3");
			assertEquals("javax.servlet.ServletContext.getInitParameter of '/testContextPath' (prefix testContextPath.)", s.getDescription());
			assertTrue(s.toString().startsWith(PrefixSource.class.getName()));
		}
		{
			final Properties.Source s = ServletUtil.getPropertyContext(new TestContext("root.", "/"));
			assertEquals("v1", s.get("p1"));
			assertEquals("v2", s.get("p2"));
			assertFails(s, "p3", "root.p3");
			assertEquals("javax.servlet.ServletContext.getInitParameter of '/' (prefix root.)", s.getDescription());
			assertTrue(s.toString().startsWith(PrefixSource.class.getName()));
		}
		{
			final Properties.Source s = ServletUtil.getPropertyContext(new TestContext("ding.", "ding"));
			assertEquals("v1", s.get("p1"));
			assertEquals("v2", s.get("p2"));
			assertFails(s, "p3", "ding.p3");
			assertEquals("javax.servlet.ServletContext.getInitParameter of 'ding' (prefix ding.)", s.getDescription());
			assertTrue(s.toString().startsWith(PrefixSource.class.getName()));
		}
		{
			final Properties.Source s = ServletUtil.getPropertyContext(new TestContext("", null));
			assertEquals("v1", s.get("p1"));
			assertEquals("v2", s.get("p2"));
			assertFails(s, "p3", "p3");
			assertEquals("javax.servlet.ServletContext.getInitParameter of 'null'", s.getDescription());
			assertEquals("javax.servlet.ServletContext.getInitParameter of 'null'", s.toString());
		}
	}

	private static final void assertFails(final Properties.Source source, final String key, final String failureKey)
	{
		try
		{
			source.get(key);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(failureKey, e.getMessage());
		}
	}

	static class TestContext implements ServletContext
	{
		private final String prefix;
		private final String contextPath;

		TestContext(final String prefix, final String contextPath)
		{
			this.prefix = prefix;
			this.contextPath = contextPath;
		}

		public String getInitParameter(final String name)
		{
			if((prefix + "p1").equals(name))
				return "v1";
			else if((prefix + "p2").equals(name))
				return "v2";
			else
				throw new IllegalArgumentException(name);
		}

		public String getServletContextName()
		{
			throw new RuntimeException();
		}

		public Object getAttribute(final String arg0)
		{
			throw new RuntimeException();
		}

		public Enumeration getAttributeNames()
		{
			throw new RuntimeException();
		}

		public ServletContext getContext(final String arg0)
		{
			throw new RuntimeException();
		}

		public String getContextPath()
		{
			return contextPath;
		}

		public Enumeration getInitParameterNames()
		{
			throw new RuntimeException();
		}

		public int getMajorVersion()
		{
			throw new RuntimeException();
		}

		public String getMimeType(final String arg0)
		{
			throw new RuntimeException();
		}

		public int getMinorVersion()
		{
			throw new RuntimeException();
		}

		public RequestDispatcher getNamedDispatcher(final String arg0)
		{
			throw new RuntimeException();
		}

		public String getRealPath(final String arg0)
		{
			throw new RuntimeException();
		}

		public RequestDispatcher getRequestDispatcher(final String arg0)
		{
			throw new RuntimeException();
		}

		public URL getResource(final String arg0)
		{
			throw new RuntimeException();
		}

		public InputStream getResourceAsStream(final String arg0)
		{
			throw new RuntimeException();
		}

		public Set getResourcePaths(final String arg0)
		{
			throw new RuntimeException();
		}

		public String getServerInfo()
		{
			throw new RuntimeException();
		}

		@Deprecated
		public Servlet getServlet(final String arg0)
		{
			throw new RuntimeException();
		}

		@Deprecated
		public Enumeration getServletNames()
		{
			throw new RuntimeException();
		}

		@Deprecated
		public Enumeration getServlets()
		{
			throw new RuntimeException();
		}

		public void log(final String arg0)
		{
			throw new RuntimeException();
		}

		@Deprecated
		public void log(final Exception arg0, final String arg1)
		{
			throw new RuntimeException();
		}

		public void log(final String arg0, final Throwable arg1)
		{
			throw new RuntimeException();
		}

		public void removeAttribute(final String arg0)
		{
			throw new RuntimeException();
		}

		public void setAttribute(final String arg0, final Object arg1)
		{
			throw new RuntimeException();
		}
	}
}

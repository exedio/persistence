/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import com.exedio.cope.junit.CopeAssert;

public class ServletUtilContextTest extends CopeAssert
{
	public void testIt()
	{
		{
			final Properties.Source s = ServletUtil.getPropertyContext(new TestContext("prfx."));
			assertEquals("v1", s.get("p1"));
			assertEquals("v2", s.get("p2"));
			assertFails(s, "p3", "prfx.p3");
			assertEquals("javax.servlet.ServletContext.getInitParameter of 'testContextName' with prefix 'prfx.'", s.toString());
		}
		{
			final Properties.Source s = ServletUtil.getPropertyContext(new TestContext(null));
			assertEquals("v1", s.get("prfx.p1"));
			assertEquals("v2", s.get("prfx.p2"));
			assertFails(s, "prfx.p3", "prfx.p3");
			assertEquals("javax.servlet.ServletContext.getInitParameter of 'testContextName'", s.toString());
		}
	}
	
	private static final void assertFails(final Properties.Source source, final String key, final String failureKey)
	{
		try
		{
			source.get(key);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(failureKey, e.getMessage());
		}
	}
	
	static class TestContext implements ServletContext
	{
		private final String prefix;
		
		TestContext(final String prefix)
		{
			this.prefix = prefix;
		}
		
		public String getInitParameter(final String name)
		{
			if("com.exedio.cope.contextPrefix".equals(name))
				return prefix;
			else if("prfx.p1".equals(name))
				return "v1";
			else if("prfx.p2".equals(name))
				return "v2";
			else
				throw new IllegalArgumentException(name);
		}

		public String getServletContextName()
		{
			return "testContextName";
		}

		public Object getAttribute(String arg0)
		{
			throw new RuntimeException();
		}

		public Enumeration getAttributeNames()
		{
			throw new RuntimeException();
		}

		public ServletContext getContext(String arg0)
		{
			throw new RuntimeException();
		}

		public String getContextPath()
		{
			throw new RuntimeException();
		}

		public Enumeration getInitParameterNames()
		{
			throw new RuntimeException();
		}

		public int getMajorVersion()
		{
			throw new RuntimeException();
		}

		public String getMimeType(String arg0)
		{
			throw new RuntimeException();
		}

		public int getMinorVersion()
		{
			throw new RuntimeException();
		}

		public RequestDispatcher getNamedDispatcher(String arg0)
		{
			throw new RuntimeException();
		}

		public String getRealPath(String arg0)
		{
			throw new RuntimeException();
		}

		public RequestDispatcher getRequestDispatcher(String arg0)
		{
			throw new RuntimeException();
		}

		public URL getResource(String arg0)
		{
			throw new RuntimeException();
		}

		public InputStream getResourceAsStream(String arg0)
		{
			throw new RuntimeException();
		}

		public Set getResourcePaths(String arg0)
		{
			throw new RuntimeException();
		}

		public String getServerInfo()
		{
			throw new RuntimeException();
		}

		@Deprecated
		public Servlet getServlet(String arg0)
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

		public void log(String arg0)
		{
			throw new RuntimeException();
		}

		@Deprecated
		public void log(Exception arg0, String arg1)
		{
			throw new RuntimeException();
		}

		public void log(String arg0, Throwable arg1)
		{
			throw new RuntimeException();
		}

		public void removeAttribute(String arg0)
		{
			throw new RuntimeException();
		}

		public void setAttribute(String arg0, Object arg1)
		{
			throw new RuntimeException();
		}
	}
}

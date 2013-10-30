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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.ServletUtil.getPropertyContext;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.util.Properties.Source;

public class ServletUtilContextTest extends CopeAssert
{
	@Deprecated
	public void testIt()
	{
		{
			final Source s = getPropertyContext(new TestContext("/testContextPath", "testContextPath."));
			assertEquals("v1", s.get("p1"));
			assertEquals("v2", s.get("p2"));
			assertFails(s, "p3", "testContextPath.p3");
			assertEquals("/testContextPath", s.get("contextPath"));
			assertEquals("ServletContext '/testContextPath' (prefix testContextPath.)", s.getDescription());
			assertEquals("ServletContext '/testContextPath' (prefix testContextPath.)", s.toString());
		}
		{
			final Source s = getPropertyContext(new TestContext("", "root."));
			assertEquals("v1", s.get("p1"));
			assertEquals("v2", s.get("p2"));
			assertFails(s, "p3", "root.p3");
			assertEquals("", s.get("contextPath"));
			assertEquals("ServletContext '' (prefix root.)", s.getDescription());
			assertEquals("ServletContext '' (prefix root.)", s.toString());
		}
		{
			final Source s = getPropertyContext(new TestContext("ding", "ding."));
			assertEquals("v1", s.get("p1"));
			assertEquals("v2", s.get("p2"));
			assertFails(s, "p3", "ding.p3");
			assertEquals("ding", s.get("contextPath"));
			assertEquals("ServletContext 'ding' (prefix ding.)", s.getDescription());
			assertEquals("ServletContext 'ding' (prefix ding.)", s.toString());
		}
		{
			final Source s = getPropertyContext(new TestContext(null, ""));
			assertEquals("v1", s.get("p1"));
			assertEquals("v2", s.get("p2"));
			assertFails(s, "p3", "p3");
			assertEquals(null, s.get("contextPath"));
			assertEquals("ServletContext 'null'", s.getDescription());
			assertEquals("ServletContext 'null'", s.toString());
		}
	}

	private static final void assertFails(final Source source, final String key, final String failureKey)
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

	private static class TestContext extends AssertionFailedServletContext
	{
		private final String contextPath;
		private final String prefix;

		TestContext(final String contextPath, final String prefix)
		{
			this.contextPath = contextPath;
			this.prefix = prefix;
		}

		@Override
		public String getInitParameter(final String name)
		{
			if((prefix + "p1").equals(name))
				return "v1";
			else if((prefix + "p2").equals(name))
				return "v2";
			else
				throw new IllegalArgumentException(name);
		}

		@Override
		public String getContextPath()
		{
			return contextPath;
		}
	}
}

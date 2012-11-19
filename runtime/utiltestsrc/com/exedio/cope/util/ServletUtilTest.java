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

package com.exedio.cope.util;

import static com.exedio.cope.misc.ConnectToken.removeProperties;
import static com.exedio.cope.misc.ConnectToken.setProperties;

import java.io.File;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.AssertionFailedServletContext;

public class ServletUtilTest extends CopeAssert
{
	public static final Model modelOk = new Model(ModelOk.TYPE);
	public static final Model modelOk2 = new Model(ModelOk2.TYPE);
	public static final Model modelContext = new Model(ModelContext.TYPE);
	public static final Model modelNull = null;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		final ConnectProperties props = new ConnectProperties(new File("runtime/utiltest.properties"));
		setProperties(modelOk, props);
		setProperties(modelOk2, props);
		setProperties(modelContext, props);
	}

	@Override
	protected void tearDown() throws Exception
	{
		removeProperties(modelOk);
		removeProperties(modelOk2);
		removeProperties(modelContext);
		super.tearDown();
	}

	public void testIt()
	{
		assertFalse(modelOk.isConnected());
		assertIt(modelOk, "nameOk", new MockServlet("com.exedio.cope.util.ServletUtilTest#modelOk", "nameOk"));
		assertSame(ModelOk.TYPE, modelOk.getType("ModelOk"));

		assertFalse(modelOk2.isConnected());
		assertIt(modelOk2, "nameOk2", new MockFilter(), new MockFilterConfig("com.exedio.cope.util.ServletUtilTest#modelOk2", "nameOk2"));
		assertSame(ModelOk2.TYPE, modelOk2.getType("ModelOk2"));

		assertFalse(modelContext.isConnected());
		assertIt(modelContext, "nameContext", new MockFilter(), new MockFilterConfig(null, "nameContext", new MockServletContext("com.exedio.cope.util.ServletUtilTest#modelContext")));
		assertSame(ModelContext.TYPE, modelContext.getType("ModelContext"));

		assertFails(
				new MockFilter(), new MockFilterConfig(null, "nameNull"),
				"nameNull", ": neither init-param nor context-param 'model' set");

		assertFails(
				new MockServlet("zick", "nameZick"),
				"nameZick", ", init-param model: does not contain '#', but was zick");

		assertFails(
				new MockFilter(), new MockFilterConfig("zack", "nameZack"),
				"nameZack", ", init-param model: does not contain '#', but was zack");

		assertFails(
				new MockFilter(), new MockFilterConfig("com.exedio.cope.util.ServletUtilTest#modelNotExists", "nameNotExists"),
				"nameNotExists", ", init-param model: field modelNotExists in class com.exedio.cope.util.ServletUtilTest does not exist or is not public.");

		assertFails(
				new MockFilter(), new MockFilterConfig("com.exedio.cope.util.ServletUtilTest#modelNull", "nameNull"),
				"nameNull", ", init-param model: field com.exedio.cope.util.ServletUtilTest#modelNull is null.");
	}

	@Deprecated
	private static final void assertIt(
			final Model model,
			final String tokenName,
			final MockServlet servlet)
	{
		final ConnectToken token = ServletUtil.getConnectedModel(servlet);
		assertSame(model, token.getModel());
		assertEquals(
				"servlet" + " \"" + tokenName + "\" " +
				"(" + MockServlet.class.getName() + '@' + System.identityHashCode(servlet) + ')',
				token.getName());
	}

	@Deprecated
	private static final void assertIt(
			final Model model,
			final String tokenName,
			final MockFilter filter,
			final MockFilterConfig config)
	{
		final ConnectToken token = ServletUtil.getConnectedModel(filter, config);
		assertSame(model, token.getModel());
		assertEquals(
				"filter" + " \"" + tokenName + "\" " +
				"(" + MockFilter.class.getName() + '@' + System.identityHashCode(filter) + ')',
				token.getName());
	}

	@Deprecated
	private static final void assertFails(
			final MockServlet servlet,
			final String name,
			final String message)
	{
		try
		{
			ServletUtil.getConnectedModel(servlet);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
				"servlet \"" + name + "\" " +
				"(" + MockServlet.class.getName() + '@' + System.identityHashCode(servlet) + ')' +
				message, e.getMessage());
		}
	}

	@Deprecated
	private static final void assertFails(
			final MockFilter filter,
			final MockFilterConfig config,
			final String name,
			final String message)
	{
		try
		{
			ServletUtil.getConnectedModel(filter, config);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
				"filter \"" + name + "\" " +
					"(" + MockFilter.class.getName() + '@' + System.identityHashCode(filter) + ')' +
					message, e.getMessage());
		}
	}

	private static class MockServlet implements Servlet
	{
		final ServletConfig config;

		MockServlet(final String model, final String name)
		{
			assert model!=null;
			assert name!=null;

			this.config = new ServletConfig()
			{
				public ServletContext getServletContext()
				{
					return new MockServletContext(null);
				}

				public String getInitParameter(final String name)
				{
					if("model".equals(name))
						return model;
					else if("cope.properties".equals(name))
						return null;
					else
						throw new RuntimeException(name);
				}

				public String getServletName()
				{
					return name;
				}

				public Enumeration<?> getInitParameterNames()
				{
					throw new RuntimeException();
				}
			};
		}

		public ServletConfig getServletConfig()
		{
			return config;
		}

		public void destroy()
		{
			throw new RuntimeException();
		}

		public String getServletInfo()
		{
			throw new RuntimeException();
		}

		public void init(final ServletConfig arg0)
		{
			throw new RuntimeException();
		}

		public void service(final ServletRequest arg0, final ServletResponse arg1)
		{
			throw new RuntimeException();
		}
	}

	private static class MockFilter implements Filter
	{
		MockFilter()
		{
			// just make it package private
		}

		public void destroy()
		{
			throw new RuntimeException();
		}

		public void doFilter(final ServletRequest arg0, final ServletResponse arg1, final FilterChain arg2)
		{
			throw new RuntimeException();
		}

		public void init(final FilterConfig arg0)
		{
			throw new RuntimeException();
		}
	}

	private static class MockFilterConfig implements FilterConfig
	{
		final String model;
		final String name;
		final ServletContext context;

		MockFilterConfig(final String model, final String name)
		{
			this(model, name, new MockServletContext(null));
		}

		MockFilterConfig(final String model, final String name, final ServletContext context)
		{
			this.model = model;
			this.name = name;
			this.context = context;
			assert name!=null;
			assert context!=null;
		}

		public ServletContext getServletContext()
		{
			return context;
		}

		public String getInitParameter(final String name)
		{
			if("model".equals(name))
				return model;
			else if("cope.properties".equals(name))
				return null;
			else
				throw new RuntimeException(name);
		}

		public String getFilterName()
		{
			return name;
		}

		public Enumeration<?> getInitParameterNames()
		{
			throw new RuntimeException();
		}
	}

	private static class MockServletContext extends AssertionFailedServletContext
	{
		final String model;

		MockServletContext(final String model)
		{
			this.model = model;
		}

		@Override
		public String getRealPath(final String name)
		{
			if("WEB-INF/cope.properties".equals(name))
			{
				return "runtime/utiltest.properties";
			}
			else
				throw new RuntimeException(name);
		}

		@Override
		public String getInitParameter(final String name)
		{
			if("model".equals(name))
				return model;
			else
			{
				//System.out.println("ServletUtilTest accessing System.getProperty(" + name + ')');
				return System.getProperty(name);
			}
		}

		@Override
		public String getContextPath()
		{
			return "contextPath";
		}
	}

	static class ModelOk extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<ModelOk> TYPE = TypesBound.newType(ModelOk.class);
		private ModelOk(final ActivationParameters ap) { super(ap); }
	}

	static class ModelOk2 extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<ModelOk2> TYPE = TypesBound.newType(ModelOk2.class);
		private ModelOk2(final ActivationParameters ap) { super(ap); }
	}

	static class ModelContext extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<ModelContext> TYPE = TypesBound.newType(ModelContext.class);
		private ModelContext(final ActivationParameters ap) { super(ap); }
	}
}

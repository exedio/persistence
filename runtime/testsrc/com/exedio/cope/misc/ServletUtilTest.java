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

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.ConnectTokenRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.TestSources;
import java.util.Enumeration;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class ServletUtilTest
{
	public static final Model modelOk = new Model(ModelOk.TYPE);
	public static final Model modelOk2 = new Model(ModelOk2.TYPE);
	public static final Model modelContext = new Model(ModelContext.TYPE);
	@SuppressWarnings("unused") // OK: read by reflection
	public static final Model modelNull = null;

	private final ConnectTokenRule ctrOk = new ConnectTokenRule(modelOk);
	private final ConnectTokenRule ctrOk2 = new ConnectTokenRule(modelOk2);
	private final ConnectTokenRule ctrContext = new ConnectTokenRule(modelContext);

	@BeforeEach final void setUp()
	{
		final ConnectProperties props = ConnectProperties.create(TestSources.minimal());
		ctrOk.set(props);
		ctrOk2.set(props);
		ctrContext.set(props);
	}

	@Test void testIt()
	{
		assertFalse(modelOk.isConnected());
		assertIt(modelOk, "nameOk", new MockServlet("com.exedio.cope.misc.ServletUtilTest#modelOk", "nameOk"));
		assertSame(ModelOk.TYPE, modelOk.getType("ModelOk"));

		assertFalse(modelOk2.isConnected());
		assertIt(modelOk2, "nameOk2", new MockFilter(), new MockFilterConfig("com.exedio.cope.misc.ServletUtilTest#modelOk2", "nameOk2"));
		assertSame(ModelOk2.TYPE, modelOk2.getType("ModelOk2"));

		assertFalse(modelContext.isConnected());
		assertIt(modelContext, "nameContext", new MockFilter(), new MockFilterConfig(null, "nameContext", new MockServletContext("com.exedio.cope.misc.ServletUtilTest#modelContext")));
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
				new MockFilter(), new MockFilterConfig("com.exedio.cope.misc.ServletUtilTest#modelNotExists", "nameNotExists"),
				"nameNotExists", ", init-param model: field modelNotExists in class com.exedio.cope.misc.ServletUtilTest does not exist or is not public.");

		assertFails(
				new MockFilter(), new MockFilterConfig("com.exedio.cope.misc.ServletUtilTest#modelNull", "nameNull"),
				"nameNull", ", init-param model: field com.exedio.cope.misc.ServletUtilTest#modelNull is null.");
	}

	private static void assertIt(
			final Model model,
			final String tokenName,
			final MockServlet servlet)
	{
		final ConnectToken token = ServletUtil.getConnectedModel(servlet);
		assertSame(model, token.getModel());
		assertEquals(
				"servlet \"" + tokenName + "\" " +
				"(" + MockServlet.class.getName() + '@' + System.identityHashCode(servlet) + ')',
				token.getName());
	}

	private static void assertIt(
			final Model model,
			final String tokenName,
			final MockFilter filter,
			final MockFilterConfig config)
	{
		final ConnectToken token = ServletUtil.getConnectedModel(filter, config);
		assertSame(model, token.getModel());
		assertEquals(
				"filter \"" + tokenName + "\" " +
				"(" + MockFilter.class.getName() + '@' + System.identityHashCode(filter) + ')',
				token.getName());
	}

	private static void assertFails(
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

	private static void assertFails(
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
				@Override
				public ServletContext getServletContext()
				{
					return new MockServletContext(null);
				}

				@Override
				public String getInitParameter(final String name)
				{
					if("model".equals(name))
						return model;
					else if("cope.properties".equals(name))
						return null;
					else
						throw new RuntimeException(name);
				}

				@Override
				public String getServletName()
				{
					return name;
				}

				@Override
				public Enumeration<String> getInitParameterNames()
				{
					throw new RuntimeException();
				}
			};
		}

		@Override
		public ServletConfig getServletConfig()
		{
			return config;
		}

		@Override
		public void destroy()
		{
			throw new RuntimeException();
		}

		@Override
		public String getServletInfo()
		{
			throw new RuntimeException();
		}

		@Override
		public void init(final ServletConfig arg0)
		{
			throw new RuntimeException();
		}

		@Override
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

		@Override
		public void destroy()
		{
			throw new RuntimeException();
		}

		@Override
		public void doFilter(final ServletRequest arg0, final ServletResponse arg1, final FilterChain arg2)
		{
			throw new RuntimeException();
		}

		@Override
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

		@Override
		public ServletContext getServletContext()
		{
			return context;
		}

		@Override
		public String getInitParameter(final String name)
		{
			if("model".equals(name))
				return model;
			else if("cope.properties".equals(name))
				return null;
			else
				throw new RuntimeException(name);
		}

		@Override
		public String getFilterName()
		{
			return name;
		}

		@Override
		public Enumeration<String> getInitParameterNames()
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

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ModelOk extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ModelOk> TYPE = com.exedio.cope.TypesBound.newType(ModelOk.class,ModelOk::new);

		@com.exedio.cope.instrument.Generated
		private ModelOk(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ModelOk2 extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ModelOk2> TYPE = com.exedio.cope.TypesBound.newType(ModelOk2.class,ModelOk2::new);

		@com.exedio.cope.instrument.Generated
		private ModelOk2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ModelContext extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ModelContext> TYPE = com.exedio.cope.TypesBound.newType(ModelContext.class,ModelContext::new);

		@com.exedio.cope.instrument.Generated
		private ModelContext(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

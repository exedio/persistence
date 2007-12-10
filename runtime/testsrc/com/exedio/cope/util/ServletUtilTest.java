/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;

public class ServletUtilTest extends CopeAssert
{
	public static final Model modelOk = new Model(ModelOk.TYPE);
	public static final Model modelOk2 = new Model(ModelOk2.TYPE);
	public static final Model modelContext = new Model(ModelContext.TYPE);
	public static final Model modelNull = null;

	public void testIt() throws ServletException
	{
		assertModelNotConnected(modelOk);
		assertIt(modelOk, false, "servlet \"nameOk\"", ServletUtil.getConnectedModel(new MockServlet("com.exedio.cope.util.ServletUtilTest#modelOk", "nameOk")));
		assertSame(ModelOk.TYPE, modelOk.findTypeByID("ModelOk"));

		assertModelNotConnected(modelOk2);
		assertIt(modelOk2, true, "filter \"nameOk2\"", ServletUtil.getConnectedModel(new MockFilter(), new MockFilterConfig("com.exedio.cope.util.ServletUtilTest#modelOk2", "nameOk2")));
		assertSame(ModelOk2.TYPE, modelOk2.findTypeByID("ModelOk2"));

		assertModelNotConnected(modelContext);
		assertIt(modelContext, true, "filter \"nameContext\"", ServletUtil.getConnectedModel(new MockFilter(), new MockFilterConfig(null, "nameContext", new MockServletContext("com.exedio.cope.util.ServletUtilTest#modelContext"))));
		assertSame(ModelContext.TYPE, modelContext.findTypeByID("ModelContext"));

		try
		{
			ServletUtil.getConnectedModel(new MockFilter(), new MockFilterConfig(null, "nameNull"));
			fail();
		}
		catch(ServletException e)
		{
			assertEquals("filter \"nameNull\" (" + MockFilter.class.getName() + "): neither init-param nor context-param 'model' set", e.getMessage());
		}

		try
		{
			ServletUtil.getConnectedModel(new MockServlet("zick", "nameZick"));
			fail();
		}
		catch(ServletException e)
		{
			assertEquals("servlet \"nameZick\" (" + MockServlet.class.getName() + "), init-param model: does not contain '#', but was zick", e.getMessage());
		}

		try
		{
			ServletUtil.getConnectedModel(new MockFilter(), new MockFilterConfig("zack", "nameZack"));
			fail();
		}
		catch(ServletException e)
		{
			assertEquals("filter \"nameZack\" (" + MockFilter.class.getName() + "), init-param model: does not contain '#', but was zack", e.getMessage());
		}

		try
		{
			ServletUtil.getConnectedModel(new MockFilter(), new MockFilterConfig("com.exedio.cope.util.ServletUtilTest#modelNotExists", "nameNotExists"));
			fail();
		}
		catch(ServletException e)
		{
			assertEquals("filter \"nameNotExists\" (" + MockFilter.class.getName() + "), init-param model: field modelNotExists in class com.exedio.cope.util.ServletUtilTest does not exist or is not public.", e.getMessage());
		}

		try
		{
			ServletUtil.getConnectedModel(new MockFilter(), new MockFilterConfig("com.exedio.cope.util.ServletUtilTest#modelNull", "nameNull"));
			fail();
		}
		catch(ServletException e)
		{
			assertEquals("filter \"nameNull\" (" + MockFilter.class.getName() + "), init-param model: field com.exedio.cope.util.ServletUtilTest#modelNull is null.", e.getMessage());
		}
	}
	
	private static final void assertIt(final Model model, final boolean filter, final String tokenName, final ConnectToken token)
	{
		assertSame(model, token.getModel());
		assertEquals(tokenName + " (" + (filter?MockFilter.class:MockServlet.class).getName() + ')', token.getName());
	}
	
	private static final void assertModelNotConnected(final Model model)
	{
		try
		{
			model.getDatabaseInfo();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("model not yet connected, use connect(Properties)", e.getMessage());
		}
	}
	
	private static class MockServlet implements Servlet
	{
		final MockServletConfig config;
		
		MockServlet(final String model, final String name)
		{
			this.config = new MockServletConfig(model, name);
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

		public void init(ServletConfig arg0)
		{
			throw new RuntimeException();
		}

		public void service(ServletRequest arg0, ServletResponse arg1)
		{
			throw new RuntimeException();
		}
	}
	
	private static class MockServletConfig implements ServletConfig
	{
		final String model;
		final String name;

		MockServletConfig(final String model, final String name)
		{
			this.model = model;
			this.name = name;
			assert name!=null;
		}

		public ServletContext getServletContext()
		{
			return new MockServletContext(null);
		}

		public String getInitParameter(final String name)
		{
			if("model".equals(name))
				return model;
			else
				throw new RuntimeException(name);
		}

		public String getServletName()
		{
			return name;
		}

		public Enumeration getInitParameterNames()
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

		public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
		{
			throw new RuntimeException();
		}

		public void init(FilterConfig arg0)
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
			else
				throw new RuntimeException(name);
		}

		public String getFilterName()
		{
			return name;
		}

		public Enumeration getInitParameterNames()
		{
			throw new RuntimeException();
		}
	}

	private static class MockServletContext implements ServletContext
	{
		final String model;
		
		MockServletContext(final String model)
		{
			this.model = model;
		}

		public ServletContext getContext(String name)
		{
			throw new RuntimeException(name);
		}

		public int getMajorVersion()
		{
			throw new RuntimeException();
		}

		public int getMinorVersion()
		{
			throw new RuntimeException();
		}

		public String getMimeType(String name)
		{
			throw new RuntimeException(name);
		}

		public Set getResourcePaths(String name)
		{
			throw new RuntimeException(name);
		}

		public URL getResource(String name)
		{
			throw new RuntimeException(name);
		}

		public InputStream getResourceAsStream(String name)
		{
			throw new RuntimeException(name);
		}

		public RequestDispatcher getRequestDispatcher(String name)
		{
			throw new RuntimeException(name);
		}

		public RequestDispatcher getNamedDispatcher(String name)
		{
			throw new RuntimeException(name);
		}

		@Deprecated // OK: implementing external api
		public Servlet getServlet(String name)
		{
			throw new RuntimeException(name);
		}

		@Deprecated // OK: implementing external api
		public Enumeration getServlets()
		{
			throw new RuntimeException();
		}

		@Deprecated // OK: implementing external api
		public Enumeration getServletNames()
		{
			throw new RuntimeException();
		}

		public void log(String name)
		{
			throw new RuntimeException(name);
		}

		@Deprecated // OK: implementing external api
		public void log(Exception e, String name)
		{
			throw new RuntimeException(name);
		}

		public void log(String name, Throwable arg1)
		{
			throw new RuntimeException(name);
		}

		public String getRealPath(String name)
		{
			if("WEB-INF/cope.properties".equals(name))
			{
				final String p = System.getProperty("com.exedio.cope.properties");
				return p!=null ? p : "cope.properties";
			}
			else
				throw new RuntimeException(name);
		}

		public String getServerInfo()
		{
			throw new RuntimeException();
		}

		public String getInitParameter(String name)
		{
			if("model".equals(name))
				return model;
			else
			{
				//System.out.println("ServletUtilTest accessing System.getProperty(" + name + ')');
				return System.getProperty(name);
			}
		}

		public Enumeration getInitParameterNames()
		{
			throw new RuntimeException();
		}

		public Object getAttribute(String name)
		{
			throw new RuntimeException(name);
		}

		public Enumeration getAttributeNames()
		{
			throw new RuntimeException();
		}

		public void setAttribute(String name, Object claue)
		{
			throw new RuntimeException(name);
		}

		public void removeAttribute(String name)
		{
			throw new RuntimeException(name);
		}

		public String getServletContextName()
		{
			throw new RuntimeException();
		}
	}

	static class ModelOk extends Item
	{
	/**

	 **
	 * Creates a new ModelOk with all the fields initially needed.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	ModelOk()
	{
		this(new com.exedio.cope.SetValue[]{
		});
	}/**

	 **
	 * Creates a new ModelOk and sets the given fields initially.
	 * This constructor is called by {@link com.exedio.cope.Type#newItem Type.newItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	private ModelOk(final com.exedio.cope.SetValue... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.util.ReactivationConstructorDummy,int)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@SuppressWarnings("unused") private ModelOk(final com.exedio.cope.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for modelOk.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	static final com.exedio.cope.Type<ModelOk> TYPE = newType(ModelOk.class)
;}

	static class ModelOk2 extends Item
	{
	/**

	 **
	 * Creates a new ModelOk2 with all the fields initially needed.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	ModelOk2()
	{
		this(new com.exedio.cope.SetValue[]{
		});
	}/**

	 **
	 * Creates a new ModelOk2 and sets the given fields initially.
	 * This constructor is called by {@link com.exedio.cope.Type#newItem Type.newItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	private ModelOk2(final com.exedio.cope.SetValue... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.util.ReactivationConstructorDummy,int)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@SuppressWarnings("unused") private ModelOk2(final com.exedio.cope.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for modelOk2.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	static final com.exedio.cope.Type<ModelOk2> TYPE = newType(ModelOk2.class)
;}
	static class ModelContext extends Item
	{
	/**

	 **
	 * Creates a new ModelContext with all the fields initially needed.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	ModelContext()
	{
		this(new com.exedio.cope.SetValue[]{
		});
	}/**

	 **
	 * Creates a new ModelContext and sets the given fields initially.
	 * This constructor is called by {@link com.exedio.cope.Type#newItem Type.newItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	private ModelContext(final com.exedio.cope.SetValue... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.util.ReactivationConstructorDummy,int)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@SuppressWarnings("unused") private ModelContext(final com.exedio.cope.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for modelContext.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	static final com.exedio.cope.Type<ModelContext> TYPE = newType(ModelContext.class)
;}
}

/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;

public class ServletUtilTest extends CopeAssert
{
	public static final Model modelOk = new Model(ModelOk.TYPE);
	public static final Model modelOk2 = new Model(ModelOk2.TYPE);

	public void testIt()
	{
		assertModelNotInitialized(modelOk);
		assertSame(modelOk, ServletUtil.getModel(new MockServletConfig("com.exedio.cope.util.ServletUtilTest#modelOk")));
		assertSame(ModelOk.TYPE, modelOk.findTypeByID("ModelOk"));

		assertModelNotInitialized(modelOk2);
		assertSame(modelOk2, ServletUtil.getModel(new MockFilterConfig("com.exedio.cope.util.ServletUtilTest#modelOk2")));
		assertSame(ModelOk2.TYPE, modelOk2.findTypeByID("ModelOk2"));
	}
	
	private static final void assertModelNotInitialized(final Model model)
	{
		try
		{
			model.findTypeByID("zack");
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("model not yet initialized, use setPropertiesInitially", e.getMessage());
		}
	}
	
	private static class MockServletConfig implements ServletConfig
	{
		final String model;

		MockServletConfig(final String model)
		{
			this.model = model;
		}

		public ServletContext getServletContext()
		{
			return new MockServletContext();
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
			throw new RuntimeException();
		}

		public Enumeration getInitParameterNames()
		{
			throw new RuntimeException();
		}
	}

	private static class MockFilterConfig implements FilterConfig
	{
		final String model;

		MockFilterConfig(final String model)
		{
			this.model = model;
		}

		public ServletContext getServletContext()
		{
			return new MockServletContext();
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
			throw new RuntimeException();
		}

		public Enumeration getInitParameterNames()
		{
			throw new RuntimeException();
		}
	}

	private static class MockServletContext implements ServletContext
	{

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

		public URL getResource(String name) throws MalformedURLException
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

		@Deprecated
		public Servlet getServlet(String name) throws ServletException
		{
			throw new RuntimeException(name);
		}

		@Deprecated
		public Enumeration getServlets()
		{
			throw new RuntimeException();
		}

		@Deprecated
		public Enumeration getServletNames()
		{
			throw new RuntimeException();
		}

		public void log(String name)
		{
			throw new RuntimeException(name);
		}

		@Deprecated
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
				return System.getProperty("com.exedio.cope.properties");
			else
				throw new RuntimeException(name);
		}

		public String getServerInfo()
		{
			throw new RuntimeException();
		}

		public String getInitParameter(String name)
		{
			throw new RuntimeException(name);
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
	 * Creates a new ModelOk with all the attributes initially needed.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of attributes.
	 */
	ModelOk()
	{
		this(new com.exedio.cope.SetValue[]{
		});
	}/**

	 **
	 * Creates a new ModelOk and sets the given attributes initially.
	 * This constructor is called by {@link com.exedio.cope.Type#newItem Type.newItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	private ModelOk(final com.exedio.cope.SetValue[] initialAttributes)
	{
		super(initialAttributes);
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.util.ReactivationConstructorDummy,int)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private ModelOk(com.exedio.cope.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * The persistent type information for modelOk.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	public static final com.exedio.cope.Type<ModelOk> TYPE = newType(ModelOk.class)
;}

	static class ModelOk2 extends Item
	{
	/**

	 **
	 * Creates a new ModelOk2 with all the attributes initially needed.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of attributes.
	 */
	ModelOk2()
	{
		this(new com.exedio.cope.SetValue[]{
		});
	}/**

	 **
	 * Creates a new ModelOk2 and sets the given attributes initially.
	 * This constructor is called by {@link com.exedio.cope.Type#newItem Type.newItem}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	private ModelOk2(final com.exedio.cope.SetValue[] initialAttributes)
	{
		super(initialAttributes);
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.util.ReactivationConstructorDummy,int)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private ModelOk2(com.exedio.cope.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * The persistent type information for modelOk2.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	public static final com.exedio.cope.Type<ModelOk2> TYPE = newType(ModelOk2.class)
;}
}

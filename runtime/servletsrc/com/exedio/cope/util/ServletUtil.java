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

package com.exedio.cope.util;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.exedio.cope.Model;

/**
 * @deprecated Use {@link com.exedio.cope.misc.ServletUtil} instead
 */
@Deprecated
public final class ServletUtil
{
	private ServletUtil()
	{
		// prevent instantiation
	}
	
	/**
	 * @deprecated Use {@link com.exedio.cope.misc.ServletUtil} instead
	 */
	@Deprecated
	public static final ConnectToken getConnectedModel(final Servlet servlet)
	throws ServletException
	{
		return new ConnectToken(com.exedio.cope.misc.ServletUtil.getConnectedModel(servlet));
	}
	
	/**
	 * @deprecated Use {@link com.exedio.cope.misc.ServletUtil} instead
	 */
	@Deprecated
	public static final ConnectToken getConnectedModel(final Filter filter, final FilterConfig config)
	throws ServletException
	{
		return new ConnectToken(com.exedio.cope.misc.ServletUtil.getConnectedModel(filter, config));
	}
	
	/**
	 * @deprecated Use {@link com.exedio.cope.misc.ServletUtil} instead
	 */
	@Deprecated
	public static final ConnectToken connect(final Model model, final ServletConfig config, final String name)
	{
		return new ConnectToken(com.exedio.cope.misc.ServletUtil.connect(model, config, name));
	}
	
	/**
	 * @deprecated Use {@link com.exedio.cope.misc.ServletUtil} instead
	 */
	@Deprecated
	public static final ConnectToken connect(final Model model, final FilterConfig config, final String name)
	{
		return new ConnectToken(com.exedio.cope.misc.ServletUtil.connect(model, config, name));
	}
	
	public static final Properties.Source getPropertyContext(final ServletContext context)
	{
		return com.exedio.cope.misc.ServletUtil.getPropertyContext(context);
	}
	
	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getConnectedModel(Servlet)} instead
	 */
	@Deprecated
	public static final ConnectToken getModel(final Servlet servlet)
	throws ServletException
	{
		return getConnectedModel(servlet);
	}

	/**
	 * @deprecated Use {@link #getConnectedModel(Filter,FilterConfig)} instead
	 */
	@Deprecated
	public static final ConnectToken getModel(final Filter filter, final FilterConfig config)
	throws ServletException
	{
		return getConnectedModel(filter, config);
	}

	/**
	 * @deprecated Renamed to {@link #connect(Model, ServletConfig, String)}.
	 */
	@Deprecated
	public static final ConnectToken initialize(final Model model, final ServletConfig config, final String name)
	{
		return connect(model, config, name);
	}
}

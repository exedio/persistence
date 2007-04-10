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

import java.io.File;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.exedio.cope.Cope;
import com.exedio.cope.Model;

public class ServletUtil
{
	private static final String PARAMETER_MODEL = "model";

	public static final Model getModel(final ServletConfig config)
	throws ServletException
	{
		return getModel(config.getInitParameter(PARAMETER_MODEL), "servlet", config.getServletName(), config.getServletContext());
	}
	
	public static final Model getModel(final FilterConfig config)
	throws ServletException
	{
		return getModel(config.getInitParameter(PARAMETER_MODEL), "filter", config.getFilterName(), config.getServletContext());
	}
	
	private static final Model getModel(final String initParam, final String kind, final String name, final ServletContext context)
	throws ServletException
	{
		//System.out.println("----------" + name + "---init-param---"+initParam+"---context-param---"+context.getInitParameter(PARAMETER_MODEL)+"---");
		final String modelName;
		final String modelNameSource;
		if(initParam==null)
		{
			final String contextParam = context.getInitParameter(PARAMETER_MODEL);
			if(contextParam==null)
				throw new ServletException(kind + ' ' + name + ": neither init-param nor context-param '"+PARAMETER_MODEL+"' set");
			modelName = contextParam;
			modelNameSource = "context-param";
		}
		else
		{
			modelName = initParam;
			modelNameSource = "init-param";
		}
		
		final Model result;
		try
		{
			result = Cope.getModel(modelName);
		}
		catch(IllegalArgumentException e)
		{
			throw new ServletException(kind + ' ' + name + ", " + modelNameSource + ' ' + PARAMETER_MODEL + ':' + ' ' + e.getMessage(), e);
		}
		connect(result, context);
		return result;
	}
	
	/**
	 * Connects the model using the properties from
	 * the file <tt>cope.properties</tt>
	 * in the directory <tt>WEB-INF</tt>
	 * of the web application.
	 * @see Model#connect(com.exedio.cope.Properties)
	 */
	public static final void connect(final Model model, final ServletContext context)
	{
		model.connect(
			new com.exedio.cope.Properties(
				new File(context.getRealPath("WEB-INF/cope.properties")), new Properties.Context(){
					public String get(final String key)
					{
						return context.getInitParameter(key);
					}
				}
			));
	}

	/**
	 * @deprecated Renamed to {@link #connect(Model, ServletContext)}.
	 */
	@Deprecated
	public static final void initialize(final Model model, final ServletContext context)
	{
		connect(model, context);
	}
}

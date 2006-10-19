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

import java.io.File;
import java.lang.reflect.Field;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.exedio.cope.Model;

public class ServletUtil
{
	private static final String PARAMETER_MODEL = "model";
	private static final char DIVIDER = '#';

	public static final Model getModel(final ServletConfig config)
	{
		return getModel(config.getInitParameter(PARAMETER_MODEL), "servlet", config.getServletName(), config.getServletContext());
	}
	
	public static final Model getModel(final FilterConfig config)
	{
		return getModel(config.getInitParameter(PARAMETER_MODEL), "filter", config.getFilterName(), config.getServletContext());
	}
	
	private static final Model getModel(final String initParam, final String kind, final String name, final ServletContext context)
	{
		//System.out.println("----------" + name + "---init-param---"+initParam+"---context-param---"+context.getInitParameter(PARAMETER_MODEL)+"---");
		final String modelName;
		final String modelNameSource;
		if(initParam==null)
		{
			final String contextParam = context.getInitParameter(PARAMETER_MODEL);
			if(contextParam==null)
				throw new NullPointerException(kind + ' ' + name + ": neither init-param nor context-param '"+PARAMETER_MODEL+"' set");
			modelName = contextParam;
			modelNameSource = "context-param";
		}
		else
		{
			modelName = initParam;
			modelNameSource = "init-param";
		}
		
		try
		{
			final int pos = modelName.indexOf(DIVIDER);
			if(pos<=0)
				throw new RuntimeException(kind + ' ' + name + ": " + modelNameSource + " '"+PARAMETER_MODEL+"' does not contain '"+DIVIDER+"', but was "+modelName);
			final String modelClassName = modelName.substring(0, pos);
			final String modelFieldName = modelName.substring(pos+1);

			final Class modelClass = Class.forName(modelClassName);

			final Field modelField;
			try
			{
				modelField = modelClass.getField(modelFieldName);
			}
			catch(NoSuchFieldException e)
			{
				throw new RuntimeException(kind + ' ' + name + ": field " + modelFieldName + " in " + modelClass.toString() + " does not exist or is not public.", e);
			}
			
			final Model result = (Model)modelField.get(null);
			connect(result, context);
			return result;
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
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
				new File(context.getRealPath("WEB-INF/cope.properties"))));
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

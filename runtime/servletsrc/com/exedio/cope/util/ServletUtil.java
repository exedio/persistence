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
		return getModelByName(config.getInitParameter(PARAMETER_MODEL), config.getServletContext());
	}
	
	public static final Model getModel(final FilterConfig config)
	{
		return getModelByName(config.getInitParameter(PARAMETER_MODEL), config.getServletContext());
	}
	
	private static final Model getModelByName(final String initParam, final ServletContext context)
	{
		if(initParam==null)
			throw new NullPointerException("init-param '"+PARAMETER_MODEL+"' missing");
		
		try
		{
			final int pos = initParam.indexOf(DIVIDER);
			if(pos<=0)
				throw new RuntimeException("init-param '"+PARAMETER_MODEL+"' does not contain '"+DIVIDER+"', but was "+initParam);
			final String modelClassName = initParam.substring(0, pos);
			final String modelAttributeName = initParam.substring(pos+1);

			final Class modelClass = Class.forName(modelClassName);

			final Field modelField;
			try
			{
				modelField = modelClass.getField(modelAttributeName);
			}
			catch(NoSuchFieldException e)
			{
				throw new RuntimeException("field " + modelAttributeName + " in " + modelClass.toString() + " does not exist or is not public.", e);
			}
			
			final Model result = (Model)modelField.get(null);
			initialize(result, context);
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
	
	public static final void initialize(final Model model, final ServletContext context)
	{
		model.setPropertiesInitially(
			new com.exedio.cope.Properties(
				new File(context.getRealPath("WEB-INF/cope.properties"))));
	}

}

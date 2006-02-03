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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.exedio.cope.Model;

public class ServletUtil
{

	public static final Model getModel(final ServletConfig config)
	{
		try
		{
			final String modelName = config.getInitParameter("model");
			if(modelName==null)
				throw new NullPointerException("init-param 'model' missing");

			final int pos = modelName.indexOf('#');
			if(pos<=0)
				throw new RuntimeException("init-param 'model' does not contain '#', but was "+modelName);
			final String modelClassName = modelName.substring(0, pos);
			final String modelAttributeName = modelName.substring(pos+1);

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
			
			final Model model = (Model)modelField.get(null);
			initialize(model, config);
			return model;
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
	
	public static final void initialize(final Model model, final ServletConfig config)
	{
		final ServletContext context = config.getServletContext();
		
		model.setPropertiesInitially(
			new com.exedio.cope.Properties(
				new File(context.getRealPath("WEB-INF/cope.properties"))));
	}

}

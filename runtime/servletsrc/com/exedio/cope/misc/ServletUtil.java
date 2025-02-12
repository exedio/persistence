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

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import java.io.File;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public final class ServletUtil
{
	private ServletUtil()
	{
		// prevent instantiation
	}

	private interface Config
	{
		String getInitParameter(String name);
		String getName();
		ServletContext getServletContext();
		String getKind();
	}

	private static Config wrap(final ServletConfig config)
	{
		return new Config()
		{
			@Override
			public String getInitParameter(final String name)
			{
				return config.getInitParameter(name);
			}
			@Override
			public String getName()
			{
				return config.getServletName();
			}
			@Override
			public ServletContext getServletContext()
			{
				return config.getServletContext();
			}
			@Override
			public String getKind()
			{
				return "servlet";
			}
		};
	}

	private static Config wrap(final FilterConfig config)
	{
		return new Config()
		{
			@Override
			public String getInitParameter(final String name)
			{
				return config.getInitParameter(name);
			}
			@Override
			public String getName()
			{
				return config.getFilterName();
			}
			@Override
			public ServletContext getServletContext()
			{
				return config.getServletContext();
			}
			@Override
			public String getKind()
			{
				return "filter";
			}
		};
	}

	public static ConnectToken getConnectedModel(final Servlet servlet)
	{
		return getConnectedModel(
				wrap(servlet.getServletConfig()),
				servlet);
	}

	public static ConnectToken getConnectedModel(final Filter filter, final FilterConfig config)
	{
		return getConnectedModel(
				wrap(config),
				filter);
	}

	private static ConnectToken getConnectedModel(
					final Config config,
					final Object nameObject)
	{
		final String PARAMETER_MODEL = "model";
		final String initParam = config.getInitParameter(PARAMETER_MODEL);

		final String description =
					config.getKind() + ' ' +
					'"' + config.getName() + '"' + ' ' +
					'(' + nameObject.getClass().getName() + '@' + System.identityHashCode(nameObject) + ')';
		final String modelName;
		final String modelNameSource;
		if(initParam==null)
		{
			final String contextParam = config.getServletContext().getInitParameter(PARAMETER_MODEL);
			if(contextParam==null)
				throw new IllegalArgumentException(description + ": neither init-param nor context-param '"+PARAMETER_MODEL+"' set");
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
			result = ModelByString.get(modelName);
		}
		catch(final IllegalArgumentException e)
		{
			throw new IllegalArgumentException(description + ", " + modelNameSource + ' ' + PARAMETER_MODEL + ':' + ' ' + e.getMessage(), e);
		}
		return ConnectToken.issue(result, description);
	}

	/**
	 * Returns connect properties from
	 * the file {@code cope.properties}
	 * in the directory {@code WEB-INF}
	 * of the web application.
	 */
	public static ConnectProperties getConnectProperties(final ServletContext context)
	{
		return
			new ConnectProperties(
				new File(context.getRealPath("WEB-INF/cope.properties"))
			);
	}
}

/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.util.ServletUtil;


/**
 * A servlet providing access to the data of cope data attributes.
 * 
 * In order to use it, you have to deploy the servlet in your <code>web.xml</code>,
 * providing the name of the cope model via an init-parameter.
 * Typically, your <code>web.xml</code> would contain a snippet like this:  
 *  
 * <pre>
 * &lt;servlet&gt;
 *    &lt;servlet-name&gt;data&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;com.exedio.cope.pattern.DataServlet&lt;/servlet-class&gt;
 *    &lt;init-param&gt;
 *       &lt;param-name&gt;model&lt;/param-name&gt;
 *       &lt;param-value&gt;{@link com.exedio.cope.Model com.bigbusiness.shop.Main#model}&lt;/param-value&gt;
 *    &lt;/init-param&gt;
 * &lt;/servlet&gt;
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;data&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/data/*&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 * 
 * @author Ralf Wiebicke
 */
public class DataServlet extends HttpServlet
{
	
	final HashMap pathes = new HashMap();
	
	public final void init()
	{
		try
		{
			final Model model = ServletUtil.getModel(getServletConfig());
			for(Iterator i = model.getTypes().iterator(); i.hasNext(); )
			{
				final Type type = (Type)i.next();
				for(Iterator j = type.getDeclaredFeatures().iterator(); j.hasNext(); )
				{
					final Feature feature = (Feature)j.next();
					if(feature instanceof HttpEntity)
					{
						final String path = '/'+type.getID()+'/'+feature.getName();
						pathes.put(path, feature);
					}
				}
			}
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	protected final void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		if(serveContent(request, response))
			return;
		
		serveError(request, response);
	}
		
	final void serveError(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setStatus(response.SC_NOT_FOUND);
		response.setContentType("text/html");
		
		final PrintStream out = new PrintStream(response.getOutputStream());
		
		out.print("<html>\n" +
				"<head>\n" +
				"<title>Not Found</title>\n" +
				"<meta name=\"generator\" content=\"cope data servlet\">\n" +
				"</head>\n" +
				"<body>\n" +
				"<h1>Not Found</h1>\n" +
				"The requested URL was not found on this server.\n" +
				"</body>\n" +
				"</html>\n");
		
		out.close();
	}
	
	final boolean serveContent(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		final String pathInfo = request.getPathInfo();
		//System.out.println("pathInfo="+pathInfo);
		if(pathInfo==null)
			return false;

		final int trailingSlash = pathInfo.lastIndexOf('/');
		if(trailingSlash<=0 && // null is leading slash, which is not allowed
			trailingSlash>=pathInfo.length()-1)
			return false;

		final String attributeString = pathInfo.substring(0, trailingSlash);
		//System.out.println("attributeString="+attributeString);

		final HttpEntity entity = (HttpEntity)pathes.get(attributeString);
		if(entity==null)
			return false;
		else
			return entity.serveContent(request, response, pathInfo, trailingSlash);
	}
	
}

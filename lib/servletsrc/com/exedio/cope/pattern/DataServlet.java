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
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Attribute;
import com.exedio.cope.DataAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.NoSuchIDException;
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
 *    &lt;servlet-name&gt;admin&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;com.exedio.cope.pattern.DataServlet&lt;/servlet-class&gt;
 *    &lt;init-param&gt;
 *       &lt;param-name&gt;data&lt;/param-name&gt;
 *       &lt;param-value&gt;{@link com.exedio.cope.Model com.bigbusiness.shop.Main#model}&lt;/param-value&gt;
 *    &lt;/init-param&gt;
 * &lt;/servlet&gt;
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;data&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/data&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 * 
 * @author Ralf Wiebicke
 */
public class DataServlet extends HttpServlet
{
	Model model = null;
	final HashMap dataAttributes = new HashMap();
	
	public final void init()
	{
		if(model!=null)
		{
			System.out.println("reinvokation of jspInit");
			return;
		}
		
		try
		{
			model = ServletUtil.getModel(getServletConfig());
			for(Iterator i = model.getTypes().iterator(); i.hasNext(); )
			{
				final Type type = (Type)i.next();
				for(Iterator j = type.getDeclaredAttributes().iterator(); j.hasNext(); )
				{
					final Attribute attribute = (Attribute)j.next();
					if(attribute instanceof DataAttribute)
					{
						dataAttributes.put('/'+type.getID()+'/'+attribute.getName(), attribute);
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
		final String pathInfo = request.getPathInfo();
		final DataAttribute attribute;

		if(pathInfo!=null)
		{
			final int trailingSlash = pathInfo.lastIndexOf('/');
			if(trailingSlash>0 && // null is leading slash, which is not allowed
					trailingSlash<pathInfo.length()-1)
			{
				final String attributeString = pathInfo.substring(0, trailingSlash);
				attribute = (DataAttribute)dataAttributes.get(attributeString);
				if(attribute!=null)
				{
					final String id = attribute.getType().getID()+'.'+pathInfo.substring(trailingSlash+1);
					System.out.println("ID="+id);
					/*final Item item;
					try
					{
						item = model.findByID(id);
						System.out.println("item="+item);
					}
					catch(NoSuchIDException e)
					{
						// do nothing
					}*/
				}
			}
			else
			{
				attribute = null;
			}
		}
		else
		{
			attribute = null;
		}
		
		response.setContentType("text/html");
		
		final String prefix = request.getContextPath()+request.getServletPath();
		
		final OutputStream out = response.getOutputStream();
		
		final PrintStream p = new PrintStream(out);
		p.println("<html>");
		p.println("<head><title>cope data servlet</title><head>");
		p.println("<body>");
		p.println("<p>Path Info "+pathInfo);
		p.println("<p>Attribute "+attribute);
		p.println("<ol>");
		for(Iterator i = dataAttributes.entrySet().iterator(); i.hasNext(); )
		{
			final Map.Entry entry = (Map.Entry)i.next();
			final String key = (String)entry.getKey();
			final DataAttribute value = (DataAttribute)entry.getValue();
			p.println("<li><a href=\""+prefix+key+"/0\">"+value+"</a>");
		}
		p.println("</ol>");
		p.println("</body>");
		p.println("</html>");
		
		out.close();
	}

}

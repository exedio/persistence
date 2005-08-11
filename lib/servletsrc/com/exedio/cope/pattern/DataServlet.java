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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
	protected final void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setContentType("text/plain");
		
		final OutputStream out = response.getOutputStream();
		
		final PrintStream p = new PrintStream(out);
		p.print("hello");
		
		out.close();
	}

}

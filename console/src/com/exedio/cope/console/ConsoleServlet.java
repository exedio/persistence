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

package com.exedio.cope.console;


import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Model;
import com.exedio.cope.util.ServletUtil;
import com.exedio.cops.CopsServlet;
import com.exedio.cops.Resource;
import com.exedio.cops.ResourceSet;

/**
 * The servlet providing the COPE Database Administration application.
 *
 * In order to use it, you have to deploy the servlet in your <tt>web.xml</tt>,
 * providing the name of the cope model via an init-parameter.
 * Typically, your <tt>web.xml</tt> would contain a snippet like this:
 *
 * <pre>
 * &lt;servlet&gt;
 *    &lt;servlet-name&gt;console&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;com.exedio.cope.console.ConsoleServlet&lt;/servlet-class&gt;
 *    &lt;init-param&gt;
 *       &lt;param-name&gt;model&lt;/param-name&gt;
 *       &lt;param-value&gt;{@link com.exedio.cope.Model com.bigbusiness.shop.Main#model}&lt;/param-value&gt;
 *    &lt;/init-param&gt;
 * &lt;/servlet&gt;
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;console&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/console/*&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 *
 * @author Ralf Wiebicke
 */
public final class ConsoleServlet extends CopsServlet
{
	private static final long serialVersionUID = 2386582365846l;
	
	final static String ENCODING = "utf-8";

	Model model = null;
	
	private static final ResourceSet resources = new ResourceSet(ConsoleServlet.class);
	static final Resource stylesheet = new Resource(resources, "console.css");
	static final Resource reportScript = new Resource(resources, "console-report.js");
	static final Resource logo = new Resource(resources, "logo.png");
	static final Resource checkFalse = new Resource(resources, "checkfalse.png");
	static final Resource checkTrue  = new Resource(resources, "checktrue.png");
	
	@Override
	public final void init() throws ServletException
	{
		super.init();
		resources.init();
		
		if(model!=null)
		{
			System.out.println("reinvokation of jspInit");
			return;
		}
		
		try
		{
			model = ServletUtil.getModel(getServletConfig());
		}
		catch(RuntimeException e)
		{
			// tomcat does not print stack trace or exception message, so we do
			System.err.println("RuntimeException in ConsoleServlet.init");
			e.printStackTrace();
			throw e;
		}
		catch(ServletException e)
		{
			// tomcat does not print stack trace or exception message, so we do
			System.err.println("ServletException in ConsoleServlet.init");
			e.printStackTrace();
			throw e;
		}
		catch(Error e)
		{
			// tomcat does not print stack trace or exception message, so we do
			System.err.println("Error in ConsoleServlet.init");
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	protected void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		// resource handling
		if("GET".equals(request.getMethod()))
		{
			if(resources.doGet(request, response))
				return;
		}
		// /resource handling

		request.setCharacterEncoding(ENCODING);
		response.setContentType("text/html; charset="+ENCODING);

		final ConsoleCop cop = ConsoleCop.getCop(model, request);
		cop.initialize(request, model);
		final PrintStream out = new PrintStream(response.getOutputStream(), false, ENCODING);
		Console_Jspm.write(out, request, response, model, cop);
		out.close();
	}
}

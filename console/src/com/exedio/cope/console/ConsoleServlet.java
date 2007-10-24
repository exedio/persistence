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
import com.exedio.cope.util.ConnectToken;
import com.exedio.cope.util.ServletUtil;
import com.exedio.cops.CopsServlet;
import com.exedio.cops.Resource;

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
	private static final long serialVersionUID = 1l;
	
	private ConnectToken connectToken = null;
	private Model model = null;
	
	static final Resource stylesheet = new Resource("console.css");
	static final Resource schemaScript = new Resource("schema.js");
	static final Resource logo = new Resource("logo.png");
	static final Resource checkFalse = new Resource("checkfalse.png");
	static final Resource checkTrue  = new Resource("checktrue.png");
	static final Resource nodeFalse = new Resource("nodefalse.png");
	static final Resource nodeTrue  = new Resource("nodetrue.png");
	static final Resource nodeWarningFalse = new Resource("nodewarningfalse.png");
	static final Resource nodeWarningTrue  = new Resource("nodewarningtrue.png");
	static final Resource nodeErrorFalse = new Resource("nodeerrorfalse.png");
	static final Resource nodeErrorTrue  = new Resource("nodeerrortrue.png");
	static final Resource nodeLeaf        = new Resource("nodeleaf.png");
	static final Resource nodeLeafWarning = new Resource("nodewarningleaf.png");
	static final Resource nodeLeafError   = new Resource("nodeerrorleaf.png");
	static final Resource warning = new Resource("warning.png");
	static final Resource error  = new Resource("error.png");
	static final Resource write  = new Resource("write.png");
	static final Resource imagebackground = new Resource("imagebackground.png");
	
	@Override
	public void init() throws ServletException
	{
		super.init();
		
		if(model!=null)
		{
			System.out.println("reinvokation of jspInit");
			return;
		}
		
		try
		{
			connectToken = ServletUtil.getConnectedModel(getServletConfig());
			model = connectToken.getModel();
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
	public void destroy()
	{
		connectToken.returnIt();
		connectToken = null;
		model = null;
		super.destroy();
	}
	
	@Override
	protected void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		final ConsoleCop cop = ConsoleCop.getCop(model, request);
		cop.initialize(request, model);
		response.setStatus(cop.getResponseStatus());
		final PrintStream out = new PrintStream(response.getOutputStream(), false, ENCODING);
		Console_Jspm.write(out, request, response, model, cop);
		out.close();
	}
}

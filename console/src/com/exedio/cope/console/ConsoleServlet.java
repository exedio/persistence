/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.util.ConnectToken;
import com.exedio.cope.util.ServletUtil;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;
import com.exedio.cops.Resource;

/**
 * The servlet providing the COPE Console application.
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
	private History history;
	
	static final Resource stylesheet = new Resource("console.css");
	static final Resource schemaScript = new Resource("schema.js");
	static final Resource logo = new Resource("logo.png");
	static final Resource shortcutIcon = new Resource("shortcutIcon.png");
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
		
		connectToken = ServletUtil.getConnectedModel(this);
		model = connectToken.getModel();
		history = new History(model);
	}
	
	@Override
	public void destroy()
	{
		history.stop();
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
		final Model model;
		final boolean historyModelShown = Cop.getBooleanParameter(request, ConsoleCop.Args.HISTORY_MODEL_SHOWN);
		ConnectToken historyConnectToken = null;
		try
		{
			if(history.isAvailable())
			{
				if(Cop.isPost(request))
				{
					if(request.getParameter(HISTORY_START)!=null)
						history.start();
					else if(request.getParameter(HISTORY_STOP)!=null)
						history.stop();
				}
				model = historyModelShown ? HistoryThread.HISTORY_MODEL : this.model;
				if(historyModelShown)
				{
					historyConnectToken =
						ConnectToken.issue(
								HistoryThread.HISTORY_MODEL,
								new ConnectProperties(new File(this.model.getProperties().getContext().
										get(HISTORY_PROPERTY_FILE))),
								"ConsoleServlet");
				}
			}
			else
			{
				model = this.model;
			}
			
			final ConsoleCop cop = ConsoleCop.getCop(model, request);
			cop.initialize(request, model);
			final Principal principal = request.getUserPrincipal();
			final String authentication = principal!=null ? principal.getName() : null;
			String hostname = null;
			try
			{
				hostname = InetAddress.getLocalHost().getHostName();
			}
			catch(UnknownHostException e)
			{
				// leave hostname==null
			}
			response.setStatus(cop.getResponseStatus());
			final PrintStream out = new PrintStream(response.getOutputStream(), false, UTF8);
			Console_Jspm.write(
					out, request, response, model, cop,
					authentication, hostname,
					history);
			out.close();
		}
		finally
		{
			if(historyConnectToken!=null)
				historyConnectToken.returnIt();
		}
	}
	
	static final String HISTORY_PROPERTY_FILE = "com.exedio.cope.console.log"; // TODO rename to history
	static final String HISTORY_START = "history.start";
	static final String HISTORY_STOP  = "history.stop";
	
}

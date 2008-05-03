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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.util.ConnectToken;
import com.exedio.cope.util.Properties;
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
	
	private final Object historyLock = new Object();
	private LogThread logThread = null;
	private boolean logEnabled = false;
	
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
		
		connectToken = ServletUtil.getConnectedModel(this);
		model = connectToken.getModel();
		startHistory();
	}
	
	@Override
	public void destroy()
	{
		stopHistory();
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
		final boolean logger;
		ConnectToken loggerConnectToken = null;
		try
		{
			if(logEnabled)
			{
				if(Cop.isPost(request))
				{
					final String d = request.getParameter(LOGGER);
					if("true".equals(d))
						request.getSession().setAttribute(LOGGER, Boolean.TRUE);
					else if("false".equals(d))
						request.getSession().removeAttribute(LOGGER);
					else if(HISTORY_START.equals(d))
						startHistory();
					else if(HISTORY_STOP.equals(d))
						stopHistory();
				}
				final HttpSession session = request.getSession(false);
				logger = session!=null && session.getAttribute(LOGGER)!=null;
				model = logger ? LogThread.loggerModel : this.model;
				if(logger)
				{
					loggerConnectToken =
						ConnectToken.issue(
								LogThread.loggerModel,
								new ConnectProperties(new File(this.model.getProperties().getContext().
										get("com.exedio.cope.console.log"))),
								"ConsoleServlet");
				}
			}
			else
			{
				logger = false;
				model = this.model;
			}
			
			final ConsoleCop cop = ConsoleCop.getCop(model, request);
			cop.initialize(request, model);
			response.setStatus(cop.getResponseStatus());
			final PrintStream out = new PrintStream(response.getOutputStream(), false, ENCODING);
			Console_Jspm.write(out, request, response, model, cop, logEnabled, logger, isHistoryRunning());
			out.close();
		}
		finally
		{
			if(loggerConnectToken!=null)
				loggerConnectToken.returnIt();
		}
	}
	
	static final String LOGGER = "logger";
	static final String HISTORY_START = "history.start";
	static final String HISTORY_STOP  = "history.stop";
	
	private boolean isHistoryRunning()
	{
		synchronized(historyLock)
		{
			return logThread!=null && logThread.isAlive();
		}
	}
	
	private void startHistory()
	{
		synchronized(historyLock)
		{
			if(logThread!=null && logThread.isAlive())
				throw new RuntimeException("already running");
			
			Properties.Source context = null;
			try
			{
				context = model.getProperties().getContext();
			}
			catch(IllegalStateException e)
			{
				// ok, then no logging
			}
			if(context!=null)
			{
				final String logPropertyFile = context.get("com.exedio.cope.console.log");
				if(logPropertyFile!=null)
				{
					logEnabled = true;
					logThread = new LogThread(model, logPropertyFile);
					logThread.start();
				}
			}
		}
	}

	private void stopHistory()
	{
		synchronized(historyLock)
		{
			if(logThread!=null)
			{
				logThread.stopAndJoin();
				logThread = null;
			}
		}
	}
}

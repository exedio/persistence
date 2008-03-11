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

package com.exedio.cope.pattern;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.util.ConnectToken;
import com.exedio.cope.util.ServletUtil;

/**
 * A servlet providing access to the contents of {@link MediaPath}
 * and its subclasses.
 *
 * In order to use it, you have to deploy the servlet in your <tt>web.xml</tt>,
 * providing the name of the cope model via an init-parameter.
 * Typically, your <tt>web.xml</tt> would contain a snippet like this:
 *
 * <pre>
 * &lt;servlet&gt;
 *    &lt;servlet-name&gt;media&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;com.exedio.cope.pattern.MediaServlet&lt;/servlet-class&gt;
 *    &lt;init-param&gt;
 *       &lt;param-name&gt;model&lt;/param-name&gt;
 *       &lt;param-value&gt;{@link com.exedio.cope.Model com.bigbusiness.shop.Main#model}&lt;/param-value&gt;
 *    &lt;/init-param&gt;
 * &lt;/servlet&gt;
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;media&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/media/*&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 *
 * @author Ralf Wiebicke
 */
public final class MediaServlet extends HttpServlet
{
	private static final long serialVersionUID = 1l;
	
	private ConnectToken connectToken = null;
	private final HashMap<String, MediaPath> pathes = new HashMap<String, MediaPath>();
	
	@Override
	public final void init() throws ServletException
	{
		super.init();

		connectToken = ServletUtil.getConnectedModel(this);
		final Model model = connectToken.getModel();
		model.migrateIfSupported();
		for(final Type<?> type : model.getTypes())
		{
			for(final Feature feature : type.getDeclaredFeatures())
			{
				if(feature instanceof MediaPath)
				{
					final MediaPath path = (MediaPath)feature;
					pathes.put('/' + path.getUrlPath(), path);
				}
			}
		}
	}

	@Override
	public void destroy()
	{
		connectToken.returnIt();
		connectToken = null;
		pathes.clear();
		super.destroy();
	}

	@Override
	protected final void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		final Media.Log log = serveContent(request, response);
		log.increment();
		serveError(response, log);
		
		// TODO make 500 error page without stack trace
	}
		
	private final void serveError(
			final HttpServletResponse response,
			final Media.Log log)
		throws IOException
	{
		if(log.responseStatus==HttpServletResponse.SC_OK)
			return;
		
		response.setStatus(log.responseStatus);
		response.setContentType("text/html");
		
		final PrintStream out = new PrintStream(response.getOutputStream());

		switch(log.responseStatus)
		{
			case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
				out.print("<html>\n" +
						"<head>\n" +
						"<title>Internal Server Error</title>\n" +
						"<meta name=\"generator\" content=\"cope media servlet\">\n" +
						"</head>\n" +
						"<body>\n" +
						"<h1>Internal Server Error</h1>\n" +
						"An internal error occured on the server.\n" +
						"</body>\n" +
						"</html>\n");
				break;

			case HttpServletResponse.SC_NOT_FOUND:
				out.print("<html>\n" +
						"<head>\n" +
						"<title>Not Found</title>\n" +
						"<meta name=\"generator\" content=\"cope media servlet\">\n" +
						"</head>\n" +
						"<body>\n" +
						"<h1>Not Found</h1>\n" +
						"The requested URL was not found on this server");
				if(log!=null)
				{
					out.print(" (");
					out.print(log.name);
					out.print(')');
				}
				out.print(".\n" +
						"</body>\n" +
						"</html>\n");
				break;
			
			default:
				throw new RuntimeException(String.valueOf(log.responseStatus));
		}
		
		out.close();
	}
	
	private final Media.Log serveContent(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		final String pathInfo = request.getPathInfo();
		//System.out.println("pathInfo="+pathInfo);
		if(pathInfo==null)
			return MediaPath.noSuchPath;

		final int trailingSlash = pathInfo.lastIndexOf('/');
		if(trailingSlash<=0 && // null is leading slash, which is not allowed
			trailingSlash>=pathInfo.length()-1)
			return MediaPath.noSuchPath;

		final String featureString = pathInfo.substring(0, trailingSlash+1);
		//System.out.println("featureString="+featureString);

		final MediaPath path = pathes.get(featureString);
		if(path==null)
			return MediaPath.noSuchPath;
		
		try
		{
			final String subPath = pathInfo.substring(trailingSlash+1);
			return path.doGet(request, response, subPath);
		}
		catch(RuntimeException e)
		{
			e.printStackTrace(); // TODO better logging
			return path.exception;
		}
	}
}

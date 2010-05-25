/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.misc.ServletUtil;

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
	private final HashMap<String, MediaPath> pathesRedirectFrom = new HashMap<String, MediaPath>();
	
	@Override
	public void init() throws ServletException
	{
		super.init();

		connectToken = ServletUtil.getConnectedModel(this);
		final Model model = connectToken.getModel();
		model.reviseIfSupported();
		for(final Type<?> type : model.getTypes())
		{
			for(final Feature feature : type.getDeclaredFeatures())
			{
				if(feature instanceof MediaPath)
				{
					final MediaPath path = (MediaPath)feature;
					final String typeID = path.getType().getID();
					final String pathName = path.getName();
					pathes.put(typeID + '/' + pathName, path);
					
					final RedirectFrom typeRedirectFrom = getAnnotationRedirectFrom(type, path);
					if(typeRedirectFrom!=null)
					{
						for(final String typeRedirectFromValue : typeRedirectFrom.value())
							put(pathesRedirectFrom, typeRedirectFromValue + '/' + pathName, path);
					}
					final RedirectFrom featureRedirectFrom = path.getAnnotation(RedirectFrom.class);
					if(featureRedirectFrom!=null)
					{
						for(final String featureRedirectFromValue : featureRedirectFrom.value())
						{
							put(pathesRedirectFrom, typeID + '/' + featureRedirectFromValue, path);
						
							if(typeRedirectFrom!=null)
							{
								for(final String typeRedirectFromValue : typeRedirectFrom.value())
									put(pathesRedirectFrom, typeRedirectFromValue + '/' + featureRedirectFromValue, path);
							}
						}
					}
				}
			}
		}
	}
	
	private static final void put(final HashMap<String, MediaPath> map, final String key, final MediaPath value)
	{
		final MediaPath collision = map.put(key, value);
		if(collision!=null)
			throw new RuntimeException("colliding path " + key + ':' + value + '/' + collision);
	}
	
	private static final RedirectFrom getAnnotationRedirectFrom(final Type<?> type, final MediaPath path)
	{
		final RedirectFrom result = type.getAnnotation(RedirectFrom.class);
		if(result==null)
			return result;
		
		if(path.isUrlGuessingPrevented())
		{
			System.out.println(
					"not yet implemented: @" + PreventUrlGuessing.class.getSimpleName() +
					" at " + path.getID() +
					" together with @" + RedirectFrom.class.getSimpleName() +
					" at type " + type.getID() +
					", " + RedirectFrom.class.getSimpleName() +
					" will be ignored for " + path.getID() +
					", but not for other medias of " + type.getID() + ".");
			return null;
		}
		
		return result;
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
	protected void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		final Media.Log log = serveContent(request, response);
		log.increment();
		serveError(response, log);
		
		// TODO make 500 error page without stack trace
	}
		
	private void serveError(
			final HttpServletResponse response,
			final Media.Log log)
		throws IOException
	{
		if(log.responseStatus==HttpServletResponse.SC_OK || log.responseStatus==HttpServletResponse.SC_MOVED_PERMANENTLY) // TODO introduce explicit boolean on Log
			return;
		
		response.setStatus(log.responseStatus);
		response.setContentType("text/html");
		
		PrintStream out = null;
		try
		{
			out = new PrintStream(response.getOutputStream());
			
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
							"The requested URL was not found on this server (");
					out.print(log.name);
					out.print(").\n" +
							"</body>\n" +
							"</html>\n");
					break;
				
				default:
					throw new RuntimeException(String.valueOf(log.responseStatus));
			}
		}
		finally
		{
			if(out!=null)
				out.close();
		}
	}
	
	private Media.Log serveContent(
			final HttpServletRequest request,
			final HttpServletResponse response)
	{
		final String pathInfo = request.getPathInfo();
		//System.out.println("pathInfo="+pathInfo);
		if(pathInfo==null || pathInfo.length()<6 || pathInfo.charAt(0)!='/')
			return MediaPath.noSuchPath;

		final int slash1 = pathInfo.indexOf('/', 1);
		if(slash1<0)
			return MediaPath.noSuchPath;

		final int slash2 = pathInfo.indexOf('/', slash1+1);
		if(slash2<0)
			return MediaPath.noSuchPath;

		final String featureString = pathInfo.substring(1, slash2);
		//System.out.println("featureString="+featureString);

		final MediaPath path = pathes.get(featureString);
		if(path==null)
		{
			final MediaPath alt = pathesRedirectFrom.get(featureString);
			if(alt!=null)
			{
				final StringBuilder location = new StringBuilder();
				location.
					append(request.getScheme()).
					append("://").
					append(request.getHeader("Host")).
					append(request.getContextPath()).
					append(request.getServletPath()).
					append('/').
					append(alt.getType().getID()).
					append('/').
					append(alt.getName()).
					append(pathInfo.substring(slash2));
				//System.out.println("location="+location);
				
				response.setStatus(response.SC_MOVED_PERMANENTLY);
				response.setHeader(RESPONSE_LOCATION, location.toString());
				
				return alt.redirectFrom;
			}
			return MediaPath.noSuchPath;
		}

		try
		{
			return path.doGet(request, response, pathInfo, slash2+1);
		}
		catch(Exception e)
		{
			System.out.println("--------MediaServlet-----");
			System.out.println("Date: " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS Z (z)").format(new Date()));
			printHeader(request, "Host");
			printHeader(request, "Referer");
			printHeader(request, "User-Agent");
			e.printStackTrace(System.out);
			System.out.println("-------/MediaServlet-----");
			return path.exception;
		}
	}
	
	private static final String RESPONSE_LOCATION = "Location";
	
	private static void printHeader(final HttpServletRequest request, final String name)
	{
		final String value = request.getHeader(name);
		if(value!=null)
			System.out.println(name + ": >" + value + '<');
		else
			System.out.println(name + " does not exist");
	}
}

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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.MediaPath.notFoundNoSuchPath;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY;

import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.misc.ServletUtil;
import com.exedio.cope.pattern.MediaPath.NotFound;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 *       &lt;param-value&gt;{@link com.exedio.cope.Model com.exedio.shop.Main#model}&lt;/param-value&gt;
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
public class MediaServlet extends HttpServlet
{
	private static final Logger logger = LoggerFactory.getLogger(MediaServlet.class);

	private static final long serialVersionUID = 1l;

	@SuppressFBWarnings({"SE_BAD_FIELD", "MSF_MUTABLE_SERVLET_FIELD", "MTIA_SUSPECT_SERVLET_INSTANCE_FIELD"})
	private ConnectToken connectToken = null;
	private final HashMap<String, MediaPath> pathes = new HashMap<>();
	private final HashMap<String, MediaPath> pathesRedirectFrom = new HashMap<>();

	@Override
	public final void init() throws ServletException
	{
		super.init();

		connectToken = ServletUtil.getConnectedModel(this).returnIfFails(connectToken ->
		{
			initPathes(connectToken.getModel()); // TODO do this before connect
			initConnected(connectToken.getModel());
		});
		// DO NOT WRITE ANYTHING HERE, BUT IN initConnected ONLY
		// OTHERWISE ConnectTokens MAY BE LOST
	}

	public void initConnected(final Model model)
	{
		model.reviseIfSupportedAndAutoEnabled();
	}

	void initPathes(final Model model)
	{
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

					final RedirectFrom featureRedirectFrom = path.getAnnotation(RedirectFrom.class);
					if(featureRedirectFrom!=null)
					{
						for(final String featureRedirectFromValue : featureRedirectFrom.value())
						{
							put(pathesRedirectFrom, typeID + '/' + featureRedirectFromValue, path);
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

	@Override
	public final void destroy()
	{
		if(connectToken!=null)
		{
			connectToken.returnStrictly();
			connectToken = null;
		}
		pathes.clear();
		pathesRedirectFrom.clear();
		super.destroy();
	}

	@Override
	protected final void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws IOException
	{
		final String pathInfo = request.getPathInfo();
		final MediaPath path;
		final int slash2;
		try
		{
			if(pathInfo==null || pathInfo.length()<6 || pathInfo.charAt(0)!='/')
				throw notFoundNoSuchPath();

			final int slash1 = pathInfo.indexOf('/', 1);
			if(slash1<0)
				throw notFoundNoSuchPath();

			slash2 = pathInfo.indexOf('/', slash1+1);
			if(slash2<0)
				throw notFoundNoSuchPath();

			final String featureString = pathInfo.substring(1, slash2);
			path = pathes.get(featureString);
			if(path==null)
			{
				final MediaPath alt = pathesRedirectFrom.get(featureString);
				if(alt!=null)
				{
					final StringBuilder location = new StringBuilder();
					location.
						// There is no need for absolute url anymore: http://en.wikipedia.org/wiki/HTTP_location
						append(request.getContextPath()).
						append(request.getServletPath()).
						append('/').
						append(alt.getType().getID()).
						append('/').
						append(alt.getName()).
						append(pathInfo.substring(slash2));

					response.setStatus(SC_MOVED_PERMANENTLY);
					response.setHeader(RESPONSE_LOCATION, location.toString());

					alt.incRedirectFrom();
					return;
				}
				throw notFoundNoSuchPath();
			}
		}
		catch(final NotFound notFound)
		{
			notFound.serve(request, response);
			return;
		}

		try
		{
			path.doGet(this, request, response, pathInfo, slash2+1);
		}
		catch(final NotFound notFound)
		{
			notFound.serve(request, response);
		}
		catch(final Exception e)
		{
			path.countException(request, e);
			onException(request, e);

			if(!response.isCommitted())
			{
				// IMPORTANT
				// Prevent headers enabling caching to be transmitted for Internal Server Error.
				// Without this, Internal Server Errors have been observed to be cached by
				// Apache mod_diskcache.
				response.reset();

				response.setStatus(SC_INTERNAL_SERVER_ERROR);
				MediaUtil.send("text/html", "us-ascii",
					"<html>\n" +
						"<head>\n" +
							"<title>Internal Server Error</title>\n" +
							"<meta http-equiv=\"content-type\" content=\"text/html;charset=us-ascii\">\n" +
							"<meta name=\"generator\" content=\"cope media servlet\">\n" +
						"</head>\n" +
						"<body>\n" +
							"<h1>Internal Server Error</h1>\n" +
							"An internal error occured on the server.\n" +
						"</body>\n" +
					"</html>\n", response);
			}
		}
	}

	private static final String RESPONSE_LOCATION = "Location";


	/**
	 * Let this method return true, if you want to add a header
	 * {@code Access-Control-Allow-Origin: *}
	 * to the response.
	 * This is typically needed for fonts served from a different domain.
	 * The default implementation returns false.
	 * @param path the media path of the current request
	 * @param item the item of the current request
	 */
	protected boolean isAccessControlAllowOriginWildcard(
			final MediaPath path,
			final Item item)
	{
		return false;
	}

	/**
	 * Tomcat automatically adds {@code Content-Length: 0} to the response
	 * if the response body is smaller than the buffer size.
	 * Unfortunatly it does so for 304 responses as well.
	 * Apache 2.4 does not accept 304 responses with a {@code Content-Length}
	 * unequal to the original 200 response.
	 * The call to {@link HttpServletResponse#flushBuffer() flushBuffer} prevents Tomcat from adding
	 * a {@code Content-Length} header to the response.
	 * Of course, this is a hot fix. Remove it, if you find a better solution
	 * to avoid the {@code Content-Length} header.
	 * @param path the media path of the current request
	 * @param item the item of the current request
	 */
	protected boolean doFlushBufferOnNotModified(
			final MediaPath path,
			final Item item)
	{
		return true;
	}

	protected void onException(
			final HttpServletRequest request,
			final Exception exception)
	{
		if(logger.isErrorEnabled())
			logger.error(
					"Path="     + request.getPathInfo() +
					" Query="   + request.getQueryString() +
					" Host="    + request.getHeader("Host") +
					" Referer=" + request.getHeader("Referer") +
					" Agent="   + request.getHeader("User-Agent"),
				exception );
	}
}

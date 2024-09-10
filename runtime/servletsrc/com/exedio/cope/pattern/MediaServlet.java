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
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.misc.ConnectToken;
import com.exedio.cope.misc.ServletUtil;
import com.exedio.cope.pattern.MediaPath.Locator;
import com.exedio.cope.pattern.MediaPath.NotFound;
import java.io.IOException;
import java.io.Serial;
import java.time.Duration;
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
 * <p>
 * In order to use it, you have to deploy the servlet in your {@code web.xml},
 * providing the name of the cope model via an init-parameter.
 * Typically, your {@code web.xml} would contain a snippet like this:
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

	@Serial
	private static final long serialVersionUID = 1l;

	private ConnectToken connectToken = null;
	private final HashMap<String, MediaPath> pathes = new HashMap<>();
	private final HashMap<String, MediaPath> pathesRedirectFrom = new HashMap<>();

	@Override
	public final void init() throws ServletException
	{
		super.init();

		connectToken = ServletUtil.getConnectedModel(this).returnOnFailureOf(connectToken ->
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
				if(feature instanceof final MediaPath path)
				{
					if (!path.isWithLocator())
						continue;
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

	private static void put(final HashMap<String, MediaPath> map, final String key, final MediaPath value)
	{
		final MediaPath collision = map.putIfAbsent(key, value);
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
					response.setStatus(SC_MOVED_PERMANENTLY);
					response.setHeader(RESPONSE_LOCATION,
							// There is no need for absolute url anymore: https://en.wikipedia.org/wiki/HTTP_location
							request.getContextPath() +
							request.getServletPath() + '/' +
							alt.getType().getID() + '/' +
							alt.getName() +
							pathInfo.substring(slash2));

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
						"""
						<html>
						<head>
						<title>Internal Server Error</title>
						<meta http-equiv="content-type" content="text/html;charset=us-ascii">
						</head>
						<body>
						<h1>Internal Server Error</h1>
						An internal error occurred on the server.
						</body>
						</html>
						""", response);
			}
		}
	}

	private static final String RESPONSE_LOCATION = "Location";


	/**
	 * A {@code max-age} directive is added to the
	 * {@code Cache-Control} header of the response
	 * iff this method does not return {@code null}.
	 * If this method returns {@code null}, no {@code max-age} directive is added.
	 * Negative values are treated like {@link Duration#ZERO zero}.
	 * Fractional seconds are ignored.
	 * This method is not called for @{@link UrlFingerPrinting} pathes -
	 * such pathes do send a maximum age of 363 days.
	 * <p>
	 * The default implementation returns
	 * {@link com.exedio.cope.ConnectProperties#getMediaServletMaximumAge()}.
	 * <p>
	 * See
	 * RFC 2616 Section 14.9.3 Modifications of the Basic Expiration Mechanism,
	 * <a href="https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/Expiration.html#ExpirationDownloadDist">Specifying the amount of time that CloudFront caches objects</a>.
	 *
	 * @param locator the locator of the current request
	 */
	protected Duration getMaximumAge(
			final Locator locator)
	{
		return locator.getFeature().getType().getModel().getConnectProperties().getMediaServletMaximumAge();
	}

	/**
	 * A {@code private} directive is added to the
	 * {@code Cache-Control} header of the response
	 * iff this method returns true.
	 * This forbids shared caches, such as company proxies to cache
	 * such urls.
	 * <p>
	 * The default implementation returns true iff
	 * {@link MediaPath#isUrlGuessingPrevented() url guessing} is prevented.
	 * <p>
	 * See
	 * RFC 2616 Section 14.9.1 What is Cacheable,
	 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control">Cache-Control</a>,
	 * <a href="https://httpd.apache.org/docs/2.2/mod/mod_cache.html#cachestoreprivate">CacheStoreNoStore Directive</a>, and
	 * <a href="https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/Expiration.html#ExpirationDownloadDist">Specifying the amount of time that CloudFront caches objects</a>.
	 *
	 * @param locator the locator of the current request
	 */
	protected boolean isCacheControlPrivate(
			final Locator locator)
	{
		return locator.getFeature().isUrlGuessingPrevented();
	}

	/**
	 * The default implementation implements the functionality of
	 * {@link #isAccessControlAllowOriginWildcard(Locator)}.
	 * @param locator the locator of the current request
	 */
	protected void filterResponse(
			final Locator locator,
			final MediaResponse response)
	{
		if(isAccessControlAllowOriginWildcard(locator))
			response.addHeader("Access-Control-Allow-Origin", "*");
	}

	/**
	 * Let this method return true, if you want to add a header
	 * {@code Access-Control-Allow-Origin: *}
	 * to the response.
	 * This is typically needed for fonts served from a different domain.
	 * The default implementation returns false.
	 * <p>
	 * <b>Note:</b>
	 * This works only, if the default implementation of
	 * {@link #filterResponse(Locator, MediaResponse)} is executed for
	 * this request.
	 * @param locator the locator of the current request
	 */
	protected boolean isAccessControlAllowOriginWildcard(
			final Locator locator)
	{
		return false;
	}

	/**
	 * Tomcat automatically adds {@code Content-Length: 0} to the response
	 * if the response body is smaller than the buffer size.
	 * Unfortunately it does so for 304 responses as well.
	 * Apache 2.4 does not accept 304 responses with a {@code Content-Length}
	 * unequal to the original 200 response.
	 * The call to {@link HttpServletResponse#flushBuffer() flushBuffer} prevents Tomcat from adding
	 * a {@code Content-Length} header to the response.
	 * Of course, this is a hot fix. Remove it, if you find a better solution
	 * to avoid the {@code Content-Length} header.
	 * @param locator the locator of the current request
	 */
	protected boolean doFlushBufferOnNotModified(
			final Locator locator)
	{
		return false;
	}

	protected void onException(
			final HttpServletRequest request,
			final Exception exception)
	{
		if(logger.isErrorEnabled())
			//noinspection StringConcatenationArgumentToLogCall
			logger.error(
					"Path="     + request.getPathInfo() +
					" Query="   + request.getQueryString() +
					" Host="    + request.getHeader("Host") +
					" Referer=" + request.getHeader("Referer") +
					" Agent="   + request.getHeader("User-Agent"),
				exception );
	}
}

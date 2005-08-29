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
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Specifies a http redirect (moved permanently) to
 * a {@link Media}.
 * <p>
 * Common usage is to maintain old urls after renaming a {@link Media}.
 * For instance, if there is a media <code>picture</code>:
 * 
 * <pre>
 * static final Media picture = new Media(OPTIONAL);
 * </pre>
 * and this media is renamed to <code>image</code>:
 * 
 * <pre>
 * static final Media image = new Media(OPTIONAL);
 * </pre>
 * then old urls created by <code>picture</code>
 * can be supported with an additional:
 * 
 * <pre>
 * static final HttpRedirect picture = new HttpRedirect(image);
 * </pre>
 *
 * @author Ralf Wiebicke
 */
public final class HttpRedirect extends HttpPath
{
	private final Media target;

	public HttpRedirect(final Media target)
	{
		if(target==null)
			throw new NullPointerException("target must not be null");

		this.target = target;
	}
	
	public final Media getTarget()
	{
		return target;
	}
	
	// logs --------------------------
	
	private long start = System.currentTimeMillis();
	private final Object startLock = new Object();
	
	public final Log redirectFound = new Log();
	public final Log fullyDelivered = new Log();
	
	public final Date getStart()
	{
		final long startLocal;
		synchronized(startLock)
		{
			startLocal = this.start;
		}
		return new Date(startLocal);
	}

	public final void resetLogs()
	{
		final long now = System.currentTimeMillis();
		synchronized(startLock)
		{
			start = now;
		}
		
		redirectFound.reset();
		fullyDelivered.reset();
	}

	// /logs -------------------------

	private static final String RESPONSE_LOCATION = "Location";
	
	final boolean serveContent(
			final HttpServletRequest request, final HttpServletResponse response,
			final String pathInfo, final int trailingSlash)
		throws ServletException, IOException
	{
		//System.out.println("media="+this);
		Log state = redirectFound;
		
		try
		{
			final String location =
				request.getScheme() + "://" +
				request.getHeader("Host") +
				request.getContextPath() + '/' +
				getMediaRootUrl() +
				target.getUrlPath() +
				pathInfo.substring(trailingSlash+1);
			//System.out.println("location="+location);
			
			response.setStatus(response.SC_MOVED_PERMANENTLY);
			response.setHeader(RESPONSE_LOCATION, location);
			
			state = fullyDelivered;
		}
		finally
		{
			state.increment();
		}
		
		return true;
	}

}

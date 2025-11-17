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

import java.util.Date;
import jakarta.servlet.http.HttpServletRequest;

public final class MediaRequestLog
{
	private final long date;
	private final Exception exception;

	private final String remoteAddr;
	private final boolean secure;
	private final String pathInfo;
	private final String queryString;
	private final String host;
	private final String referer;
	private final String userAgent;

	MediaRequestLog(
			final long date,
			final Exception exception,
			final HttpServletRequest request)
	{
		this.date = date;
		this.exception = exception;

		remoteAddr  = request.getRemoteAddr();
		secure      = request.isSecure();
		pathInfo    = request.getPathInfo();
		queryString = request.getQueryString();
		host        = request.getHeader("Host");
		referer     = request.getHeader("Referer");
		userAgent   = request.getHeader("User-Agent");
	}

	public Date getDate()
	{
		return new Date(date);
	}

	public Exception getException()
	{
		return exception;
	}

	/**
	 * @see HttpServletRequest#getRemoteAddr()
	 */
	public String getRemoteAddr()
	{
		return remoteAddr;
	}

	public boolean isSecure()
	{
		return secure;
	}

	public String getPathInfo()
	{
		return pathInfo;
	}

	public String getQueryString()
	{
		return queryString;
	}

	public String getHost()
	{
		return host;
	}

	public String getReferer()
	{
		return referer;
	}

	public String getUserAgent()
	{
		return userAgent;
	}
}

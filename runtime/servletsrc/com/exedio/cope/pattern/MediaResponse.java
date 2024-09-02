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

import static com.exedio.cope.util.Check.requireNonEmpty;
import static java.util.Locale.ENGLISH;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;

public final class MediaResponse
{
	private final HttpServletResponse response;

	MediaResponse(final HttpServletResponse response)
	{
		this.response = response;
	}


	/**
	 * @see HttpServletResponse#addHeader(String, String)
	 */
	public void addHeader(
			@Nonnull final String name,
			@Nonnull final String value)
	{
		requireNonEmpty(name, "name");
		requireNonEmpty(value, "value");
		if(!name.trim().equals(name))
			throw new IllegalArgumentException(
					"name must be trimmed, but was >" + name + '<');
		if(FORBIDDEN_HEADER_NAMES.contains(name.toLowerCase(ENGLISH)))
			throw new IllegalArgumentException(
					"name is forbidden, was >" + name + '<');

		response.addHeader(name, value);
	}

	private static final Set<String> FORBIDDEN_HEADER_NAMES = Stream.of(
			// Conditionals
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers#conditionals
			"Last-Modified",
			"ETag",
			"If-Match",
			"If-None-Match",
			"If-Modified-Since",
			"If-Unmodified-Since",
			"Vary",

			// Hop-by-hop headers
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Connection
			"Keep-Alive",
			"Transfer-Encoding",
			"TE",
			"Connection",
			"Trailer",
			"Upgrade",
			"Proxy-Authorization",
			"Proxy-Authenticate",

			// set by MediaServlet
			"Location",
			"Cache-Control",
			"Expires", // predecessor of Cache-Control
			"Pragma", // predecessor of Cache-Control
			// "Last-Modified", already in Conditional

			// Message body information
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers#message_body_information
			// should be set by MediaPath#doGetAndCommit
			"Content-Type",
			"Content-Length",
			"Content-Encoding",

			// set by Tomcat
			"Date"
	).map(s -> s.toLowerCase(ENGLISH)).collect(Collectors.toSet());


	private boolean cacheControlNoTransform = false;

	/**
	 * A {@code no-transform} directive is added to the
	 * {@code Cache-Control} header of the response
	 * iff this method is called.
	 * Subsequent calls to this method are ignored.
	 * <p>
	 * See
	 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control">Cache-Control</a>
	 */
	public void addCacheControlNoTransform()
	{
		cacheControlNoTransform = true;
	}

	void addToCacheControl(final StringBuilder bf)
	{
		if(cacheControlNoTransform)
		{
			if(!bf.isEmpty())
				bf.append(',');

			bf.append("no-transform");
		}
	}
}

/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.Item;

/**
 * Specifies a http redirect (moved permanently) to
 * a {@link Media}.
 * <p>
 * Common usage is to maintain old urls after renaming a {@link Media}.
 * For instance, if there is a media <tt>picture</tt>:
 *
 * <pre>
 * static final Media picture = new Media(OPTIONAL);
 * </pre>
 * and this media is renamed to <tt>image</tt>:
 *
 * <pre>
 * static final Media image = new Media(OPTIONAL);
 * </pre>
 * then old urls created by <tt>picture</tt>
 * can be supported with an additional:
 *
 * <pre>
 * static final MediaRedirect picture = new MediaRedirect(image);
 * </pre>
 *
 * @author Ralf Wiebicke
 */
public final class MediaRedirect extends MediaPath
{
	private final Media target;

	public MediaRedirect(final Media target)
	{
		if(target==null)
			throw new NullPointerException("target must not be null");

		this.target = target;
	}
	
	public final Media getTarget()
	{
		return target;
	}
	
	private long start = System.currentTimeMillis();
	private final Object startLock = new Object();
	
	@Override
	public final Date getStart()
	{
		final long startLocal;
		synchronized(startLock)
		{
			startLocal = this.start;
		}
		return new Date(startLocal);
	}

	private static final String RESPONSE_LOCATION = "Location";
	
	@Override
	public final Media.Log doGet(
			final HttpServletRequest request, final HttpServletResponse response,
			final Item item, final String extension)
		throws ServletException, IOException
	{
		final String url = target.getURL(item);
		if(url==null)
			return isNull;
		
		final String location =
			request.getScheme() + "://" +
			request.getHeader("Host") +
			request.getContextPath() + '/' +
			url;
		//System.out.println("location="+location);
		
		response.setStatus(response.SC_MOVED_PERMANENTLY);
		response.setHeader(RESPONSE_LOCATION, location);
		return delivered;
	}

}

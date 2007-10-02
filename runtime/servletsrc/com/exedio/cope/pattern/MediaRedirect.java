/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Condition;
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
	
	@Override
	public String getContentType(final Item item)
	{
		return target.getContentType(item);
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
		
		final StringBuffer location = new StringBuffer();
		
		if(url.startsWith("http:")||url.startsWith("https:"))
		{
			location.append(url);
		}
		else if(url.startsWith("/"))
		{
			location.
				append(request.getScheme()).
				append("://").
				append(request.getHeader("Host")).
				append(url);
		}
		else
		{
			location.
				append(request.getScheme()).
				append("://").
				append(request.getHeader("Host")).
				append(request.getContextPath()).
				append('/').
				append(url);
		}
		//System.out.println("location="+location);
		
		response.setStatus(response.SC_MOVED_PERMANENTLY);
		response.setHeader(RESPONSE_LOCATION, location.toString());
		return delivered;
	}

	@Override
	public Condition isNull()
	{
		return target.isNull();
	}

	@Override
	public Condition isNotNull()
	{
		return target.isNotNull();
	}
}

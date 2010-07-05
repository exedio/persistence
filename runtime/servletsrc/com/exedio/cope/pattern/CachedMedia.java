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
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Item;

public abstract class CachedMedia extends MediaPath
{
	private static final long serialVersionUID = 1l;

	private static final String REQUEST_IF_MODIFIED_SINCE = "If-Modified-Since";
	private static final String RESPONSE_EXPIRES = "Expires";
	private static final String RESPONSE_LAST_MODIFIED = "Last-Modified";

	@Override
	public final Media.Log doGet(
			final HttpServletRequest request, final HttpServletResponse response,
			final Item item)
		throws IOException
	{
		// NOTE
		// This code prevents a Denial of Service attack against the caching mechanism.
		// Query strings can be used to effectively disable the cache by using many urls
		// for one media value. Therefore they are forbidden completely.
		if(isUrlGuessingPrevented())
		{
			final String[] tokens = request.getParameterValues(URL_TOKEN);
			if(tokens!=null&&tokens.length>1)
				return notAnItem;
			for(Enumeration e = request.getParameterNames(); e.hasMoreElements(); )
				if(!URL_TOKEN.equals(e.nextElement()))
					return notAnItem;
		}
		else
		{
			if(request.getQueryString()!=null)
				return notAnItem;
		}

		final long lastModifiedRaw = getLastModified(item);
		// if there is no LastModified, then there is no caching
		if(lastModifiedRaw<=0)
			return doGetIfModified(response, item);

		// NOTE:
		// Last Modification Date must be rounded to full seconds,
		// otherwise comparison for SC_NOT_MODIFIED doesn't work.
		final long lastModified = (lastModifiedRaw / 1000l) * 1000l;
		//System.out.println("lastModified="+lastModified+"("+getLastModified(item)+")");
		response.setDateHeader(RESPONSE_LAST_MODIFIED, lastModified);

		final long ifModifiedSince = request.getDateHeader(REQUEST_IF_MODIFIED_SINCE);
		//System.out.println("ifModifiedSince="+request.getHeader(REQUEST_IF_MODIFIED_SINCE));
		//System.out.println("ifModifiedSince="+ifModifiedSince);

		final int mediaOffsetExpires = getType().getModel().getConnectProperties().getMediaOffsetExpires();
		if(mediaOffsetExpires>0)
			response.setDateHeader(RESPONSE_EXPIRES, System.currentTimeMillis() + mediaOffsetExpires);

		if(ifModifiedSince>=0 && ifModifiedSince>=lastModified)
		{
			//System.out.println("not modified");
			response.setStatus(response.SC_NOT_MODIFIED);

			//System.out.println(request.getMethod()+' '+request.getProtocol()+" IMS="+format(ifModifiedSince)+"  LM="+format(lastModified)+"  NOT modified");

			return notModified;
		}
		else
		{
			return doGetIfModified(response, item);
		}
	}

	public abstract long getLastModified(Item item);

	/**
	 * This method does not get the request as a parameter,
	 * because the response of a cached media must depend
	 * on the item only.
	 */
	public abstract Media.Log doGetIfModified(HttpServletResponse response, Item item) throws IOException;
}

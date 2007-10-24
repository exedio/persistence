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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Item;

public abstract class CachedMedia extends MediaPath
{
	private static final String REQUEST_IF_MODIFIED_SINCE = "If-Modified-Since";
	private static final String RESPONSE_EXPIRES = "Expires";
	private static final String RESPONSE_LAST_MODIFIED = "Last-Modified";
	
	@Override
	public Media.Log doGet(
			final HttpServletRequest request, final HttpServletResponse response,
			final Item item, final String extension)
		throws IOException
	{
		final long lastModifiedRaw = getLastModified(item);
		// if there is no LastModified, then there is no caching
		if(lastModifiedRaw<=0)
			return doGetIfModified(response, item, extension);
		
		// NOTE:
		// Last Modification Date must be rounded to full seconds,
		// otherwise comparison for SC_NOT_MODIFIED doesn't work.
		final long lastModified = (lastModifiedRaw / 1000l) * 1000l;
		//System.out.println("lastModified="+lastModified+"("+getLastModified(item)+")");
		response.setDateHeader(RESPONSE_LAST_MODIFIED, lastModified);

		final long ifModifiedSince = request.getDateHeader(REQUEST_IF_MODIFIED_SINCE);
		//System.out.println("ifModifiedSince="+request.getHeader(REQUEST_IF_MODIFIED_SINCE));
		//System.out.println("ifModifiedSince="+ifModifiedSince);
		
		final int mediaOffsetExpires = getType().getModel().getProperties().getMediaOffsetExpires();
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
			return doGetIfModified(response, item, extension);
		}
	}
	
	public abstract long getLastModified(Item item);
	
	/**
	 * This method does not get the request as a parameter,
	 * because the response of a cached media must depend
	 * on the item only.
	 */
	public abstract Media.Log doGetIfModified(HttpServletResponse response, Item item, String extension) throws IOException;
}

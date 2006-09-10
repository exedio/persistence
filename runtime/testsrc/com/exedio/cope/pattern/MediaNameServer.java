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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaPath;

/**
 * A test subclass of MediaPath for unit-testing custom extentions of MediaPath.
 * @author Ralf Wiebicke
 */
public final class MediaNameServer extends MediaPath
{
	final StringField source;

	public MediaNameServer(final StringField source)
	{
		this.source = source;
	}
	
	public StringField getSource()
	{
		return source;
	}
	
	@Override
	public void initialize()
	{
		super.initialize();
		
		final String name = getName();
		if(source!=null && !source.isInitialized())
			initialize(source, name+"Source");
	}
	
	private long start = System.currentTimeMillis();
	
	@Override
	public final Date getStart()
	{
		return new Date(start);
	}

	private static final long EXPIRES_OFFSET = 1000 * 5; // 5 seconds
	
	private static final String RESPONSE_EXPIRES = "Expires";
	private static final String RESPONSE_CONTENT_LENGTH = "Content-Length";
	
	@Override
	public Media.Log doGet(
			final HttpServletRequest request, final HttpServletResponse response,
			final Item item, final String extension)
		throws ServletException, IOException
	{
		final String content = source.get(item);
		//System.out.println("contentType="+contentType);
		if(content==null)
			return isNull;
		
		if(content.endsWith(" error"))
			throw new RuntimeException("test error in MediaNameServer");

		response.setContentType("text/plain");

		final long now = System.currentTimeMillis();
		response.setDateHeader(RESPONSE_EXPIRES, now+EXPIRES_OFFSET);
		
		final byte[] contentBytes = content.getBytes("utf-8");
		final long contentLength = contentBytes.length;
		//System.out.println("contentLength="+String.valueOf(contentLength));
		response.setHeader(RESPONSE_CONTENT_LENGTH, String.valueOf(contentLength));
		//response.setHeader("Cache-Control", "public");

		System.out.println(request.getMethod()+' '+request.getProtocol()+" modified: "+contentLength);

		ServletOutputStream out = null;
		try
		{
			out = response.getOutputStream();
			out.write(contentBytes);
		}
		finally
		{
			if(out!=null)
				out.close();
		}
		return delivered;
	}

}

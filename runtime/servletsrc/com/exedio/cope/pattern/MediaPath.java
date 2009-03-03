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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.NoSuchIDException;
import com.exedio.cope.Pattern;
import com.exedio.cope.instrument.Wrapper;

public abstract class MediaPath extends Pattern
{
	private String urlPath = null;
	private String mediaRootUrl = null;

	@Override
	protected void initialize()
	{
		final String name = getName();
		
		urlPath = getType().getID() + '/' + name + '/';
	}
	
	final String getUrlPath()
	{
		if(urlPath==null)
			throw new RuntimeException("not yet initialized");
		
		return urlPath;
	}
	
	private final String getMediaRootUrl()
	{
		if(mediaRootUrl==null)
			mediaRootUrl = getType().getModel().getProperties().getMediaRootUrl();
		
		return mediaRootUrl;
	}
	
	private static final HashMap<String, String> contentTypeToExtension = new HashMap<String, String>();
	
	static
	{
		contentTypeToExtension.put("image/jpeg", ".jpg");
		contentTypeToExtension.put("image/pjpeg", ".jpg");
		contentTypeToExtension.put("image/gif", ".gif");
		contentTypeToExtension.put("image/png", ".png");
		contentTypeToExtension.put("image/x-icon", ".ico");
		contentTypeToExtension.put("image/icon", ".ico");
		contentTypeToExtension.put("image/vnd.microsoft.icon", ".ico"); // http://en.wikipedia.org/wiki/ICO_(icon_image_file_format)
		contentTypeToExtension.put("text/html", ".html");
		contentTypeToExtension.put("text/plain", ".txt");
		contentTypeToExtension.put("text/css", ".css");
		contentTypeToExtension.put("application/java-archive", ".jar");
		contentTypeToExtension.put("application/pdf", ".pdf"); // http://en.wikipedia.org/wiki/PDF
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());

		result.add(
			new Wrapper("getURL").
			addComment("Returns a URL the content of {0} is available under.").
			setReturn(String.class));

		if((!(this instanceof Media)) || (((Media)this).getContentType()!=null))
			result.add(
				new Wrapper("getContentType").
				addComment("Returns the content type of the media {0}.").
				setReturn(String.class));
		
		return Collections.unmodifiableList(result);
	}
	
	/**
	 * Returns a URL the content of this media path is available under,
	 * if a {@link MediaServlet} is properly installed.
	 * Returns null, if there is no such content.
	 */
	public final String getURL(final Item item)
	{
		final String contentType = getContentType(item);

		if(contentType==null)
			return null;

		final StringBuilder bf = new StringBuilder(getMediaRootUrl());

		bf.append(getUrlPath()).
			append(item.getCopeID());

		final String extension = contentTypeToExtension.get(contentType);
		if(extension!=null)
			bf.append(extension);
		
		return bf.toString();
	}

	public static final Log noSuchPath = new Log("no such path", HttpServletResponse.SC_NOT_FOUND);
	public final Log exception = new Log("exception", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	public final Log notAnItem = new Log("not an item", HttpServletResponse.SC_NOT_FOUND);
	public final Log noSuchItem = new Log("no such item", HttpServletResponse.SC_NOT_FOUND);
	public final Log isNull = new Log("is null", HttpServletResponse.SC_NOT_FOUND);
	public final Log notComputable = new Log("not computable", HttpServletResponse.SC_NOT_FOUND);
	public final Log notModified = new Log("not modified", HttpServletResponse.SC_OK);
	public final Log delivered = new Log("delivered", HttpServletResponse.SC_OK);

	final Media.Log doGet(
			final HttpServletRequest request, final HttpServletResponse response,
			final String subPath)
		throws IOException
	{
		//final long start = System.currentTimeMillis();
		
		final int firstDot = subPath.indexOf('.');
		final int dot = (firstDot>=0) ? subPath.indexOf('.', firstDot+1) : firstDot;
		//System.out.println("trailingDot="+trailingDot);

		final String id;
		final String extension;
		if(dot>=0)
		{
			id = subPath.substring(0, dot);
			extension = subPath.substring(dot);
		}
		else
		{
			id = subPath;
			extension = "";
		}
		
		//System.out.println("ID="+id);
		final Model model = getType().getModel();
		try
		{
			model.startTransaction("MediaServlet");
			final Item item = model.getItem(id);
			//System.out.println("item="+item);
			
			final Media.Log result = doGet(request, response, item, extension);
			model.commit();
			
			//System.out.println("request for " + toString() + " took " + (System.currentTimeMillis() - start) + " ms, " + result.name + ", " + id);
			return result;
		}
		catch(NoSuchIDException e)
		{
			return e.notAnID() ? notAnItem : noSuchItem;
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
	}

	public abstract String getContentType(Item item);
	
	public abstract Media.Log doGet(HttpServletRequest request, HttpServletResponse response, Item item, String extension)
		throws IOException;
	
	/**
	 * Returns a condition matching all items, for which {@link #getURL(Item)} returns null.
	 */
	public abstract Condition isNull();
	
	/**
	 * Returns a condition matching all items, for which {@link #getURL(Item)} does not return null.
	 */
	public abstract Condition isNotNull();
	
	
	public final static class Log
	{
		private volatile int counter = 0;
		final String name;
		public final int responseStatus;
		
		Log(final String name, final int responseStatus)
		{
			if(name==null)
				throw new NullPointerException();
			switch(responseStatus)
			{
				case HttpServletResponse.SC_OK:
				case HttpServletResponse.SC_NOT_FOUND:
				case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
					break;
				default:
					throw new RuntimeException(String.valueOf(responseStatus));
			}
			
			this.name = name;
			this.responseStatus = responseStatus;
		}
		
		void increment()
		{
			counter++; // may loose a few counts due to concurrency, but this is ok
		}

		public int get()
		{
			return counter;
		}
	}
}

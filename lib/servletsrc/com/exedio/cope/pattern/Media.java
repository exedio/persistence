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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Attribute;
import com.exedio.cope.BooleanAttribute;
import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.DataAttribute;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.NestingRuntimeException;
import com.exedio.cope.ObjectAttribute;
import com.exedio.cope.Pattern;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.Attribute.Option;

public final class Media extends MediaPath
{
	final boolean notNull;
	final String fixedMimeMajor;
	final String fixedMimeMinor;

	final DataAttribute data;
	final StringAttribute mimeMajor;
	final StringAttribute mimeMinor;
	final BooleanAttribute exists;
	final ObjectAttribute isNull;

	public Media(final Option option, final String fixedMimeMajor, final String fixedMimeMinor)
	{
		if(option==null)
			throw new NullPointerException("option must not be null");
		if(fixedMimeMajor==null)
			throw new NullPointerException("fixedMimeMajor must not be null");
		if(fixedMimeMinor==null)
			throw new NullPointerException("fixedMimeMinor must not be null");

		this.notNull = option.mandatory;
		this.fixedMimeMajor = fixedMimeMajor;
		this.fixedMimeMinor = fixedMimeMinor;
		registerSource(this.data = new DataAttribute(option));
		this.mimeMajor = null;
		this.mimeMinor = null;
		this.exists = option.mandatory ? null : new BooleanAttribute(Item.OPTIONAL);
		this.isNull = exists;

		if(data==null)
			throw new NullPointerException("data must not be null");

		if(this.exists!=null)
			registerSource(this.exists);
	}
	
	public Media(final Option option, final String fixedMimeMajor)
	{
		if(option==null)
			throw new NullPointerException("option must not be null");
		if(fixedMimeMajor==null)
			throw new NullPointerException("fixedMimeMajor must not be null");

		this.notNull = option.mandatory;
		this.fixedMimeMajor = fixedMimeMajor;
		this.fixedMimeMinor = null;
		registerSource(this.data = new DataAttribute(option));
		this.mimeMajor = null;
		registerSource(this.mimeMinor = new StringAttribute(option, 1, 30));
		this.exists = null;
		this.isNull = mimeMinor;
		
		if(data==null)
			throw new NullPointerException("data must not be null");
		if(mimeMinor==null)
			throw new NullPointerException("mimeMinor must not be null");
		if(mimeMinor.getSingleUniqueConstraint()!=null)
			throw new RuntimeException("mimeMinor cannot be unique");
		if(mimeMinor.isMandatory())
			throw new RuntimeException("mimeMinor cannot be mandatory");
		if(mimeMinor.isReadOnly())
			throw new RuntimeException("mimeMinor cannot be read-only");
	}
	
	public Media(final Option option)
	{
		if(option==null)
			throw new NullPointerException("option must not be null");

		this.notNull = option.mandatory;
		this.fixedMimeMajor = null;
		this.fixedMimeMinor = null;
		registerSource(this.data = new DataAttribute(option));
		registerSource(this.mimeMajor = new StringAttribute(option, 1, 30));
		registerSource(this.mimeMinor = new StringAttribute(option, 1, 30));
		this.exists = null;
		this.isNull = mimeMajor;
		
		if(data==null)
			throw new NullPointerException("data must not be null");
		if(mimeMajor==null)
			throw new NullPointerException("mimeMajor must not be null");
		if(mimeMajor.getSingleUniqueConstraint()!=null)
			throw new RuntimeException("mimeMajor cannot be unique");
		if(mimeMajor.isMandatory())
			throw new RuntimeException("mimeMajor cannot be mandatory");
		if(mimeMajor.isReadOnly())
			throw new RuntimeException("mimeMajor cannot be read-only");
		if(mimeMinor==null)
			throw new NullPointerException("mimeMinor must not be null");
		if(mimeMinor.getSingleUniqueConstraint()!=null)
			throw new RuntimeException("mimeMinor cannot be unique");
		if(mimeMinor.isMandatory())
			throw new RuntimeException("mimeMinor cannot be mandatory");
		if(mimeMinor.isReadOnly())
			throw new RuntimeException("mimeMinor cannot be read-only");
	}
	
	public final String getFixedMimeMajor()
	{
		return fixedMimeMajor;
	}
	
	public final String getFixedMimeMinor()
	{
		return fixedMimeMinor;
	}
	
	public final DataAttribute getData()
	{
		return data;
	}
	
	public final StringAttribute getMimeMajor()
	{
		return mimeMajor;
	}
	
	public final StringAttribute getMimeMinor()
	{
		return mimeMinor;
	}
	
	public final BooleanAttribute getExists()
	{
		return exists;
	}
	
	public final ObjectAttribute getIsNull()
	{
		return isNull;
	}
	
	public void initialize()
	{
		super.initialize();
		
		final String name = getName();
		if(data!=null && !data.isInitialized())
			initialize(data, name+"Data");
		if(mimeMajor!=null && !mimeMajor.isInitialized())
			initialize(mimeMajor, name+"Major");
		if(mimeMinor!=null && !mimeMinor.isInitialized())
			initialize(mimeMinor, name+"Minor");
		if(exists!=null && !exists.isInitialized())
			initialize(exists, name+"Exists");
	}
	
	public boolean isNull(final Item item)
	{
		return notNull ? false : (item.get(isNull)==null);
	}
	
	/**
	 * Returns the major mime type of this media.
	 * Returns null, if there is no data for this media.
	 */
	public final String getMimeMajor(final Item item)
	{
		if(isNull(item))
			return null;

		return (mimeMajor!=null) ? (String)item.get(mimeMajor) : fixedMimeMajor;
	}

	/**
	 * Returns the minor mime type of this media.
	 * Returns null, if there is no data for this media.
	 */
	public final String getMimeMinor(final Item item)
	{
		if(isNull(item))
			return null;

		return (mimeMinor!=null) ? (String)item.get(mimeMinor) : fixedMimeMinor;
	}
	
	/**
	 * Returns the content type of this media.
	 * Returns null, if there is no data for this media.
	 */
	public final String getContentType(final Item item)
	{
		if(isNull(item))
			return null;

		return getMimeMajor(item) + '/' + getMimeMinor(item);
	}
	
	private final RuntimeException newNoDataException(final Item item)
	{
		return new RuntimeException("missing data for "+this.toString()+" on "+item.toString());
	}

	/**
	 * Returns a stream for fetching the data of this media.
	 * <b>You are responsible for closing the stream, when you are finished!</b>
	 * Returns null, if there is no data for this media.
	 */
	public final InputStream getData(final Item item)
	{
		if(isNull(item))
			return null;

		final InputStream result = data.get(item);
		if(result==null)
			throw newNoDataException(item);
		
		return result;
	}

	/**
	 * Returns the length of the data of this media.
	 * Returns -1, if there is no data for this media.
	 */
	public final long getDataLength(final Item item)
	{
		if(isNull(item))
			return -1;
		
		final long result = data.getLength(item);
		if(result<0)
			throw newNoDataException(item);

		return result;
	}

	/**
	 * Returns the date of the last modification
	 * of the data of this media.
	 * Returns -1, if there is no data for this media.
	 */
	public final long getDataLastModified(final Item item)
	{
		if(isNull(item))
			return -1;

		final long result = data.getLastModified(item);
		if(result<=0)
			throw newNoDataException(item);

		return result;
	}

	/**
	 * Returns a URL pointing to the data of this media.
	 * Returns null, if there is no data for this media.
	 */
	public final String getURL(final Item item)
	{
		if(isNull(item))
			return null;

		final StringBuffer bf = new StringBuffer(getMediaRootUrl());
		appendDataPath(item, bf);
		appendExtension(item, bf);
		return bf.toString();
	}

	/**
	 * Provides data for this persistent media.
	 * Closes <data>data</data> after reading the contents of the stream.
	 * @param data give null to remove data.
	 * @throws MandatoryViolationException
	 *         if data is null and attribute is {@link Attribute#isMandatory() mandatory}.
	 * @throws IOException if reading data throws an IOException.
	 */
	public final void set(final Item item, final InputStream data, final String mimeMajor, final String mimeMinor)
		throws IOException
	{
		try
		{
			set(item, (Object)data, mimeMajor, mimeMinor);
		}
		finally
		{
			if(data!=null)
				data.close();
		}
	}
	
	/**
	 * Provides data for this persistent media.
	 * @param data give null to remove data.
	 * @throws MandatoryViolationException
	 *         if data is null and attribute is {@link Attribute#isMandatory() mandatory}.
	 * @throws IOException if reading data throws an IOException.
	 */
	public final void set(final Item item, final File data, final String mimeMajor, final String mimeMinor)
		throws IOException
	{
		set(item, (Object)data, mimeMajor, mimeMinor);
	}
	
	private final void set(final Item item, final Object data, final String mimeMajor, final String mimeMinor)
		throws IOException
	{
		try
		{
			if(data!=null)
			{
				if((mimeMajor==null&&fixedMimeMajor==null) ||
					(mimeMinor==null&&fixedMimeMinor==null))
					throw new RuntimeException("if data is not null, mime types must also be not null");
			}
			else
			{
				if(mimeMajor!=null||mimeMinor!=null)
					throw new RuntimeException("if data is null, mime types must also be null");
			}
	
			// TODO use Item.set(AttributeValue[])
			if(this.mimeMajor!=null)
				item.set(this.mimeMajor, mimeMajor);
			if(this.mimeMinor!=null)
				item.set(this.mimeMinor, mimeMinor);
			if(this.exists!=null)
				item.set(this.exists, (data!=null) ? Boolean.TRUE : null);
			
			if(data instanceof InputStream)
				this.data.set(item, (InputStream)data);
			else
				this.data.set(item, (File)data);
	
		}
		catch(ConstraintViolationException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
	
	private final void appendDataPath(final Item item, final StringBuffer bf)
	{
		final String id = item.getCopeID();
		final int dot = id.indexOf('.');
		if(dot<0)
			throw new RuntimeException(id);
		
		bf.append(getUrlPath()).
			append(id.substring(dot+1));
	}

	private final void appendExtension(final Item item, final StringBuffer bf)
	{
		final String major = getMimeMajor(item);
		final String minor = getMimeMinor(item);

		final String compactExtension = getCompactExtension(major, minor);
		if(compactExtension==null)
		{
			bf.append('.').
				append(major).
				append('.').
				append(minor);
		}
		else
			bf.append(compactExtension);
	}

	private static final String getCompactExtension(final String mimeMajor, final String mimeMinor)
	{
		if("image".equals(mimeMajor))
		{
			if("jpeg".equals(mimeMinor) || "pjpeg".equals(mimeMinor))
				return ".jpg";
			else if("gif".equals(mimeMinor))
				return ".gif";
			else if("png".equals(mimeMinor))
				return ".png";
			else
				return null;
		}
		else if("text".equals(mimeMajor))
		{
			if("html".equals(mimeMinor))
				return ".html";
			else if("plain".equals(mimeMinor))
				return ".txt";
			else if("css".equals(mimeMinor))
				return ".css";
			else
				return null;
		}
		else
			return null;
	}
	
	public final static Media get(final DataAttribute attribute)
	{
		for(Iterator i = attribute.getPatterns().iterator(); i.hasNext(); )
		{
			final Pattern pattern = (Pattern)i.next();
			if(pattern instanceof Media)
			{
				final Media media = (Media)pattern;
				if(media.getData()==attribute)
					return media;
			}
		}
		throw new NullPointerException(attribute.toString());
	}
	
	// logs --------------------------
	
	private long start = System.currentTimeMillis();
	
	public final Date getStart()
	{
		return new Date(start);
	}

	// /logs -------------------------

	/**
	 * Sets the offset, the Expires http header is set into the future.
	 * Together with a http reverse proxy this ensures,
	 * that for that time no request for that data will reach the servlet.
	 * This may reduce the load on the server.
	 * 
	 * TODO: make this configurable, at best per media.
	 */
	private static final long EXPIRES_OFFSET = 1000 * 5; // 5 seconds
	
	private static final String REQUEST_IF_MODIFIED_SINCE = "If-Modified-Since";
	private static final String RESPONSE_EXPIRES = "Expires";
	private static final String RESPONSE_LAST_MODIFIED = "Last-Modified";
	
	public final Media.Log doGet(
			final HttpServletRequest request, final HttpServletResponse response,
			final Item item, final String extension)
		throws ServletException, IOException
	{
		final String contentType = getContentType(item);
		//System.out.println("contentType="+contentType);
		if(contentType==null)
			return dataIsNull;

		response.setContentType(contentType);

		final long lastModified = getDataLastModified(item);
		//System.out.println("lastModified="+formatHttpDate(lastModified));
		response.setDateHeader(RESPONSE_LAST_MODIFIED, lastModified);

		final long now = System.currentTimeMillis();
		response.setDateHeader(RESPONSE_EXPIRES, now+EXPIRES_OFFSET);
		
		final long ifModifiedSince = request.getDateHeader(REQUEST_IF_MODIFIED_SINCE);
		//System.out.println("ifModifiedSince="+request.getHeader(REQUEST_IF_MODIFIED_SINCE));
		//System.out.println("ifModifiedSince="+ifModifiedSince);
		
		if(ifModifiedSince>=0 && ifModifiedSince>=lastModified)
		{
			//System.out.println("not modified");
			response.setStatus(response.SC_NOT_MODIFIED);
			
			System.out.println(request.getMethod()+' '+request.getProtocol()+" IMS="+format(ifModifiedSince)+"  LM="+format(lastModified)+"  NOT modified");
			
			return notModified;
		}
		else
		{
			final long contentLength = getDataLength(item);
			//System.out.println("contentLength="+String.valueOf(contentLength));
			response.setContentLength((int)contentLength);
			//response.setHeader("Cache-Control", "public");

			System.out.println(request.getMethod()+' '+request.getProtocol()+" IMS="+format(ifModifiedSince)+"  LM="+format(lastModified)+"  modified: "+contentLength);

			ServletOutputStream out = null;
			InputStream in = null;
			try
			{
				out = response.getOutputStream();
				in = getData(item);

				final byte[] buffer = new byte[Math.max((int)contentLength, 50*1024)];
				for(int len = in.read(buffer); len != -1; len = in.read(buffer))
					out.write(buffer, 0, len);

				return delivered;
			}
			finally
			{
				if(in!=null)
					in.close();
				if(out!=null)
					out.close();
			}
		}
	}

	private final static String format(final long date)
	{
		final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return df.format(new Date(date));
	}
	
}

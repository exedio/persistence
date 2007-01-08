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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.DataField;
import com.exedio.cope.DataLengthViolationException;
import com.exedio.cope.DateField;
import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Field.Option;

public final class Media extends CachedMedia
{
	private final Option option;
	final boolean optional;
	final DataField body;
	final ContentType contentType;
	final DateField lastModified;

	public static final long DEFAULT_LENGTH = DataField.DEFAULT_LENGTH;
	
	private Media(final Option option, final String fixedMimeMajor, final String fixedMimeMinor, final long bodyMaximumLength)
	{
		if(option==null)
			throw new NullPointerException("option must not be null");
		
		this.option = option;
		this.optional = option.optional;
		registerSource(this.body = new DataField(option).lengthMax(bodyMaximumLength));
		
		if(fixedMimeMajor!=null && fixedMimeMinor!=null)
		{
			this.contentType = new FixedContentType(fixedMimeMajor, fixedMimeMinor);
		}
		else if(fixedMimeMajor!=null && fixedMimeMinor==null)
		{
			final StringField mimeMinor = new StringField(option).lengthRange(1, 30);
			this.contentType = new HalfFixedContentType(fixedMimeMajor, mimeMinor);
		}
		else if(fixedMimeMajor==null && fixedMimeMinor==null)
		{
			final StringField mimeMajor = new StringField(option).lengthRange(1, 30);
			final StringField mimeMinor = new StringField(option).lengthRange(1, 30);
			this.contentType = new StoredContentType(mimeMajor, mimeMinor);
		}
		else
			throw new RuntimeException();
		
		registerSource(this.lastModified = new DateField(option));
	}
	
	public Media(final Option option, final String fixedMimeMajor, final String fixedMimeMinor)
	{
		this(option, fixedMimeMajor, fixedMimeMinor, DEFAULT_LENGTH);
	}
	
	public Media(final Option option, final String fixedMimeMajor)
	{
		this(option, fixedMimeMajor, null, DEFAULT_LENGTH);
	}
	
	public Media(final Option option)
	{
		this(option, null, null, DEFAULT_LENGTH);
	}
	
	public Media lengthMax(final long maximumLength)
	{
		return new Media(option, getFixedMimeMajor(), getFixedMimeMinor(), maximumLength);
	}
	
	public String getFixedMimeMajor()
	{
		return contentType.getFixedMimeMajor();
	}
	
	public String getFixedMimeMinor()
	{
		return contentType.getFixedMimeMinor();
	}
	
	public long getMaximumLength()
	{
		return body.getMaximumLength();
	}
	
	public DataField getBody()
	{
		return body;
	}
	
	public StringField getMimeMajor()
	{
		return contentType.getMimeMajor();
	}
	
	public StringField getMimeMinor()
	{
		return contentType.getMimeMinor();
	}
	
	public DateField getLastModified()
	{
		return lastModified;
	}
	
	public FunctionField getIsNull()
	{
		return lastModified;
	}
	
	@Override
	public void initialize()
	{
		super.initialize();
		
		final String name = getName();
		if(body!=null && !body.isInitialized())
			initialize(body, name+"Body");
		contentType.initialize(name);
		initialize(lastModified, name+"LastModified");
	}
	
	public boolean isNull(final Item item)
	{
		return optional ? (lastModified.get(item)==null) : false;
	}

	/**
	 * Returns the major mime type for the given content type.
	 * Returns null, if content type is null.
	 */
	public final static String toMajor(final String contentType) throws IllegalContentTypeException
	{
		return toMajorMinor(contentType, true);
	}
	
	/**
	 * Returns the minor mime type for the given content type.
	 * Returns null, if content type is null.
	 */
	public final static String toMinor(final String contentType) throws IllegalContentTypeException
	{
		return toMajorMinor(contentType, false);
	}

	private final static String toMajorMinor(final String contentType, final boolean major) throws IllegalContentTypeException
	{
		if(contentType!=null)
		{
			final int pos = contentType.indexOf('/');
			if(pos<0)
				throw new IllegalContentTypeException(contentType);
			return
				major
				? contentType.substring(0, pos)
				: contentType.substring(contentType.indexOf('/')+1);
		}
		else
			return null;
	}

	/**
	 * Returns the content type of this media.
	 * Returns null, if this media is null.
	 */
	@Override
	public String getContentType(final Item item)
	{
		if(isNull(item))
			return null;

		return contentType.getContentType(item);
	}
	
	/**
	 * Returns the date of the last modification
	 * of this media.
	 * Returns -1, if this media is null.
	 */
	@Override
	public long getLastModified(final Item item)
	{
		if(isNull(item))
			return -1;

		return lastModified.get(item).getTime();
	}

	/**
	 * Returns the length of the body of this media.
	 * Returns -1, if this media is null.
	 */
	public long getLength(final Item item)
	{
		if(isNull(item))
			return -1;
		
		final long result = body.getLength(item);
		
		assert result>=0 : item.getCopeID();
		return result;
	}

	/**
	 * Returns the body of this media.
	 * Returns null, if this media is null.
	 */
	public byte[] getBody(final Item item)
	{
		return this.body.get(item);
	}

	/**
	 * Sets the contents of this media.
	 * @param body give null to make this media null.
	 * @throws MandatoryViolationException
	 *         if body is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if body is longer than {@link #getMaximumLength()}
	 */
	public void set(final Item item, final byte[] body, final String contentType)
		throws DataLengthViolationException
	{
		try
		{
			set(item, (Object)body, contentType);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Writes the body of this media into the given steam.
	 * Does nothing, if this media is null.
	 * @throws NullPointerException
	 *         if <tt>body</tt> is null.
	 * @throws IOException if writing <tt>body</tt> throws an IOException.
	 */
	public void getBody(final Item item, final OutputStream body) throws IOException
	{
		this.body.get(item, body);
	}

	/**
	 * Sets the contents of this media.
	 * Closes <tt>body</tt> after reading the contents of the stream.
	 * @param body give null to make this media null.
	 * @throws MandatoryViolationException
	 *         if <tt>body</tt> is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if <tt>body</tt> is longer than {@link #getMaximumLength()}
	 * @throws IOException if reading <tt>body</tt> throws an IOException.
	 */
	public void set(final Item item, final InputStream body, final String contentType)
		throws DataLengthViolationException, IOException
	{
		try
		{
			set(item, (Object)body, contentType);
		}
		finally
		{
			if(body!=null)
				body.close();
		}
	}
	
	/**
	 * Writes the body of this media into the given file.
	 * Does nothing, if this media is null.
	 * @throws NullPointerException
	 *         if <tt>body</tt> is null.
	 * @throws IOException if writing <tt>body</tt> throws an IOException.
	 */
	public void getBody(final Item item, final File body) throws IOException
	{
		this.body.get(item, body);
	}

	/**
	 * Sets the contents of this media.
	 * @param body give null to make this media null.
	 * @throws MandatoryViolationException
	 *         if <tt>body</tt> is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if <tt>body</tt> is longer than {@link #getMaximumLength()}
	 * @throws IOException if reading <tt>body</tt> throws an IOException.
	 */
	public void set(final Item item, final File body, final String contentType)
		throws DataLengthViolationException, IOException
	{
		set(item, (Object)body, contentType);
	}
	
	private void set(final Item item, final Object body, final String contentType)
		throws DataLengthViolationException, IOException
	{
		if(body!=null)
		{
			if(contentType==null)
				throw new RuntimeException("if body is not null, content type must also be not null");
			
			final long length;
			if(body instanceof byte[])
				length = ((byte[])body).length;
			else if(body instanceof InputStream)
				length = -1;
			else
				length = ((File)body).length();
			
			if(length>this.body.getMaximumLength())
				throw new DataLengthViolationException(this.body, item, length, true);
		}
		else
		{
			if(contentType!=null)
				throw new RuntimeException("if body is null, content type must also be null");
		}

		final ArrayList<SetValue> values = new ArrayList<SetValue>(4);
		this.contentType.map(values, contentType);
		values.add(this.lastModified.map(body!=null ? new Date() : null));
		if(body instanceof byte[])
			values.add(this.body.map((byte[])body));
		
		try
		{
			item.set(values.toArray(new SetValue[values.size()]));
		}
		catch(CustomAttributeException e)
		{
			// cannot happen, since FunctionField only are allowed for source
			throw new RuntimeException(e);
		}
		
		// TODO set InputStream/File via Item.set(SetValue[]) as well
		if(body instanceof byte[])
			/* already set above */;
		else if(body instanceof InputStream)
			this.body.set(item, (InputStream)body);
		else
			this.body.set(item, (File)body);
	}
	
	public final static Media get(final DataField field)
	{
		for(final Pattern pattern : field.getPatterns())
		{
			if(pattern instanceof Media)
			{
				final Media media = (Media)pattern;
				if(media.getBody()==field)
					return media;
			}
		}
		throw new NullPointerException(field.toString());
	}
	
	@Override
	public Media.Log doGetIfModified(
			final HttpServletRequest request, final HttpServletResponse response,
			final Item item, final String extension)
		throws ServletException, IOException
	{
		final String contentType = getContentType(item);
		//System.out.println("contentType="+contentType);
		if(contentType==null)
			return isNull;

		response.setContentType(contentType);

		final int contentLength = (int)getLength(item);
		//System.out.println("contentLength="+String.valueOf(contentLength));
		response.setContentLength(contentLength);
		//response.setHeader("Cache-Control", "public");

		//System.out.println(request.getMethod()+' '+request.getProtocol()+" IMS="+format(ifModifiedSince)+"  LM="+format(lastModified)+"  modified: "+contentLength);

		ServletOutputStream out = null;
		try
		{
			out = response.getOutputStream();
			getBody(item, out);
			return delivered;
		}
		finally
		{
			if(out!=null)
				out.close();
		}
	}
	
	abstract class ContentType
	{
		abstract String getFixedMimeMajor();
		abstract String getFixedMimeMinor();
		abstract StringField getMimeMajor();
		abstract StringField getMimeMinor();
		abstract void initialize(String name);
		abstract String getContentType(Item item);
		abstract void map(ArrayList<SetValue> values, String contentType);
	}
	
	final class FixedContentType extends ContentType
	{
		private final String major;
		private final String minor;
		private final String full;
		
		FixedContentType(final String major, final String minor)
		{
			this.major = major;
			this.minor = minor;
			this.full = major + '/' + minor;
			
			if(major==null)
				throw new NullPointerException("fixedMimeMajor must not be null");
			if(minor==null)
				throw new NullPointerException("fixedMimeMinor must not be null");
		}
		
		@Override
		String getFixedMimeMajor()
		{
			return major;
		}
		
		@Override
		String getFixedMimeMinor()
		{
			return minor;
		}
		
		@Override
		StringField getMimeMajor()
		{
			return null;
		}
		
		@Override
		StringField getMimeMinor()
		{
			return null;
		}
		
		@Override
		void initialize(final String name)
		{
			// no attributes to be initialized
		}
		
		@Override
		String getContentType(final Item item)
		{
			return full;
		}
		
		@Override
		void map(final ArrayList<SetValue> values, final String contentType)
		{
			if(contentType!=null && !full.equals(contentType))
				throw new IllegalContentTypeException(contentType);
		}
	}

	final class HalfFixedContentType extends ContentType
	{
		private final String major;
		private final String prefix;
		private final StringField minor;
		
		HalfFixedContentType(final String major, final StringField minor)
		{
			this.major = major;
			this.prefix = major + '/';
			this.minor = minor;
			
			if(major==null)
				throw new NullPointerException("fixedMimeMajor must not be null");
			if(minor==null)
				throw new NullPointerException("mimeMinor must not be null");
			
			registerSource(this.minor);
		}
		
		@Override
		String getFixedMimeMajor()
		{
			return major;
		}
		
		@Override
		String getFixedMimeMinor()
		{
			return null;
		}
		
		@Override
		StringField getMimeMajor()
		{
			return null;
		}
		
		@Override
		StringField getMimeMinor()
		{
			return minor;
		}
		
		@Override
		void initialize(final String name)
		{
			if(!minor.isInitialized())
				Media.this.initialize(minor, name+"Minor");
		}
		
		@Override
		String getContentType(final Item item)
		{
			return prefix + minor.get(item);
		}
		
		@Override
		void map(final ArrayList<SetValue> values, final String contentType)
		{
			if(contentType!=null && !contentType.startsWith(prefix))
				throw new IllegalContentTypeException(contentType);
			
			values.add(this.minor.map(toMinor(contentType)));
		}
	}

	final class StoredContentType extends ContentType
	{
		private final StringField major;
		private final StringField minor;
		
		StoredContentType(final StringField major, final StringField minor)
		{
			this.major = major;
			this.minor = minor;
			
			if(major==null)
				throw new NullPointerException("mimeMajor must not be null");
			if(minor==null)
				throw new NullPointerException("mimeMinor must not be null");

			registerSource(this.major);
			registerSource(this.minor);
		}
		
		@Override
		String getFixedMimeMajor()
		{
			return null;
		}
		
		@Override
		String getFixedMimeMinor()
		{
			return null;
		}
		
		@Override
		StringField getMimeMajor()
		{
			return major;
		}
		
		@Override
		StringField getMimeMinor()
		{
			return minor;
		}
		
		@Override
		void initialize(final String name)
		{
			if(!major.isInitialized())
				Media.this.initialize(major, name+"Major");
			if(!minor.isInitialized())
				Media.this.initialize(minor, name+"Minor");
		}
		
		@Override
		String getContentType(final Item item)
		{
			return major.get(item) + '/' + minor.get(item);
		}
		
		@Override
		void map(final ArrayList<SetValue> values, final String contentType)
		{
			values.add(this.major.map(toMajor(contentType)));
			values.add(this.minor.map(toMinor(contentType)));
		}
	}
	

	private final static String format(final long date)
	{
		final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.S");
		return df.format(new Date(date));
	}
	
}

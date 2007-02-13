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
	final boolean optional;
	final DataField body;
	final ContentType contentType;
	final DateField lastModified;

	public static final long DEFAULT_LENGTH = DataField.DEFAULT_LENGTH;
	
	private Media(final boolean optional, final long bodyMaximumLength, final ContentType contentType)
	{
		this.optional = optional;
		registerSource(this.body = optional(new DataField(), optional).lengthMax(bodyMaximumLength));
		this.contentType = contentType;
		for(final StringField source : contentType.getSources())
			registerSource(source);
		registerSource(this.lastModified = optional(new DateField(), optional));
		
		assert contentType!=null;
	}
	
	private static final DataField optional(final DataField field, final boolean optional)
	{
		return optional ? field.optional() : field;
	}
	
	private static final DateField optional(final DateField field, final boolean optional)
	{
		return optional ? field.optional() : field;
	}
	
	/**
	 * @deprecated use {@link #contentType(String)} instead. 
	 */
	@Deprecated
	public Media(final String fixedMimeMajor, final String fixedMimeMinor)
	{
		this(false, DEFAULT_LENGTH, new FixedContentType(fixedMimeMajor, fixedMimeMinor));
	}

	/**
	 * @deprecated use {@link #optional()} and {@link #contentType(String)} instead. 
	 */
	@Deprecated
	public Media(final Option option, final String fixedMimeMajor, final String fixedMimeMinor)
	{
		this(option.optional, DEFAULT_LENGTH, new FixedContentType(fixedMimeMajor, fixedMimeMinor));

		if(option.unique)
			throw new RuntimeException("Media cannot be unique");
	}
	
	/**
	 * @deprecated use {@link #contentTypeMajor(String)} instead. 
	 */
	@Deprecated
	public Media(final String fixedMimeMajor)
	{
		this(false, DEFAULT_LENGTH, new HalfFixedContentType(fixedMimeMajor, false));
	}
	
	/**
	 * @deprecated use {@link #optional()} and {@link #contentTypeMajor(String)} instead. 
	 */
	@Deprecated
	public Media(final Option option, final String fixedMimeMajor)
	{
		this(option.optional, DEFAULT_LENGTH, new HalfFixedContentType(fixedMimeMajor, option.optional));

		if(option.unique)
			throw new RuntimeException("Media cannot be unique");
	}
	
	public Media()
	{
		this(false, DEFAULT_LENGTH, new StoredContentType(false));
	}
	
	/**
	 * @deprecated use {@link #optional()} instead. 
	 */
	@Deprecated
	public Media(final Option option)
	{
		this(option.optional, DEFAULT_LENGTH, new StoredContentType(option.optional));

		if(option.unique)
			throw new RuntimeException("Media cannot be unique");
	}
	
	public Media optional()
	{
		return new Media(true, body.getMaximumLength(), contentType.optional());
	}
	
	public Media lengthMax(final long maximumLength)
	{
		return new Media(optional, maximumLength, contentType.copy());
	}
	
	/**
	 * Creates a new media, that must contain the given content type only.
	 */
	public Media contentType(final String contentType)
	{
		return new Media(optional, body.getMaximumLength(), new FixedContentType(contentType));
	}
	
	/**
	 * Creates a new media, that must contain the a content type with the given major part only.
	 */
	public Media contentTypeMajor(final String majorContentType)
	{
		return new Media(optional, body.getMaximumLength(), new HalfFixedContentType(majorContentType, optional));
	}
	
	public boolean checkContentType(final String contentType)
	{
		return this.contentType.check(contentType);
	}
	
	public String getContentTypeDescription()
	{
		return contentType.describe();
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
		if(!body.isInitialized())
			initialize(body, name+"Body");
		contentType.initialize(this, name);
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
				throw new RuntimeException(contentType);
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
		if(!this.contentType.check(contentType))
			throw new IllegalContentTypeException(this, item, contentType);
		
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
	
	static abstract class ContentType
	{
		abstract StringField[] getSources();
		abstract ContentType copy();
		abstract ContentType optional();
		abstract boolean check(String contentType);
		abstract String describe();
		abstract StringField getMimeMajor();
		abstract StringField getMimeMinor();
		abstract void initialize(Media media, String name);
		abstract String getContentType(Item item);
		abstract void map(ArrayList<SetValue> values, String contentType);
		
		protected static final StringField makeField(final boolean optional)
		{
			final StringField f = new StringField().lengthRange(1, 30);
			return optional ? f.optional() : f;
		}
	}
	
	static final class FixedContentType extends ContentType
	{
		private final String full;
		
		FixedContentType(final String full)
		{
			this.full = full;
		}
		
		/**
		 * @deprecated is used from deprecated code only
		 */
		@Deprecated
		FixedContentType(final String major, final String minor)
		{
			this(major + '/' + minor);
			
			if(major==null)
				throw new NullPointerException("fixedMimeMajor must not be null");
			if(minor==null)
				throw new NullPointerException("fixedMimeMinor must not be null");
		}
		
		@Override
		StringField[] getSources()
		{
			return new StringField[]{};
		}
		
		@Override
		FixedContentType copy()
		{
			return new FixedContentType(full);
		}
		
		@Override
		FixedContentType optional()
		{
			return copy();
		}
		
		@Override
		boolean check(final String contentType)
		{
			return contentType==null || this.full.equals(contentType);
		}
		
		@Override
		String describe()
		{
			return full;
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
		void initialize(final Media media, final String name)
		{
			// no fields to be initialized
		}
		
		@Override
		String getContentType(final Item item)
		{
			return full;
		}
		
		@Override
		void map(final ArrayList<SetValue> values, final String contentType)
		{
			// no fields to be mapped
		}
	}

	static final class HalfFixedContentType extends ContentType
	{
		private final String major;
		private final String prefix;
		private final StringField minor;
		
		HalfFixedContentType(final String major, final boolean optional)
		{
			this.major = major;
			this.prefix = major + '/';
			this.minor = makeField(optional);
			
			if(major==null)
				throw new NullPointerException("fixedMimeMajor must not be null");
		}
		
		@Override
		StringField[] getSources()
		{
			return new StringField[]{minor};
		}
		
		@Override
		HalfFixedContentType copy()
		{
			return new HalfFixedContentType(major, !minor.isMandatory());
		}
		
		@Override
		HalfFixedContentType optional()
		{
			return new HalfFixedContentType(major, true);
		}
		
		@Override
		boolean check(final String contentType)
		{
			return contentType==null || contentType.startsWith(prefix);
		}
		
		@Override
		String describe()
		{
			return prefix + '*';
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
		void initialize(final Media media, final String name)
		{
			if(!minor.isInitialized())
				media.initialize(minor, name+"Minor");
		}
		
		@Override
		String getContentType(final Item item)
		{
			return prefix + minor.get(item);
		}
		
		@Override
		void map(final ArrayList<SetValue> values, final String contentType)
		{
			values.add(this.minor.map(toMinor(contentType)));
		}
	}

	static final class StoredContentType extends ContentType
	{
		private final StringField major;
		private final StringField minor;
		
		StoredContentType(final boolean optional)
		{
			this.major = makeField(optional);
			this.minor = makeField(optional);
		}
		
		@Override
		StoredContentType copy()
		{
			return new StoredContentType(!major.isMandatory());
		}
		
		@Override
		StoredContentType optional()
		{
			return new StoredContentType(true);
		}
		
		@Override
		StringField[] getSources()
		{
			return new StringField[]{major, minor};
		}
		
		@Override
		boolean check(final String contentType)
		{
			return contentType==null || contentType.indexOf('/')>=0;
		}
		
		@Override
		String describe()
		{
			return "*/*";
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
		void initialize(final Media media, final String name)
		{
			if(!major.isInitialized())
				media.initialize(major, name+"Major");
			if(!minor.isInitialized())
				media.initialize(minor, name+"Minor");
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
}

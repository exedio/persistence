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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.DataField;
import com.exedio.cope.DataLengthViolationException;
import com.exedio.cope.DateField;
import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import com.exedio.cope.Wrapper;
import com.exedio.cope.Field.Option;

public final class Media extends CachedMedia implements Settable<Media.Value>
{
	final boolean optional;
	final DataField body;
	final ContentType<?> contentType;
	final DateField lastModified;

	public static final long DEFAULT_LENGTH = DataField.DEFAULT_LENGTH;
	
	private Media(final boolean optional, final long bodyMaximumLength, final ContentType contentType)
	{
		this.optional = optional;
		registerSource(this.body = optional(new DataField(), optional).lengthMax(bodyMaximumLength));
		this.contentType = contentType;
		final FunctionField contentTypeField = contentType.field;
		if(contentTypeField!=null)
			registerSource(contentTypeField);
		registerSource(this.lastModified = optional(new DateField(), optional));
		
		assert optional == !body.isMandatory();
		assert (contentTypeField==null) || (optional == !contentTypeField.isMandatory());
		assert optional == !lastModified.isMandatory();
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
		this(false, DEFAULT_LENGTH, new MajorContentType(fixedMimeMajor, false));
	}
	
	/**
	 * @deprecated use {@link #optional()} and {@link #contentTypeMajor(String)} instead.
	 */
	@Deprecated
	public Media(final Option option, final String fixedMimeMajor)
	{
		this(option.optional, DEFAULT_LENGTH, new MajorContentType(fixedMimeMajor, option.optional));

		if(option.unique)
			throw new RuntimeException("Media cannot be unique");
	}
	
	public Media()
	{
		this(false, DEFAULT_LENGTH, new DefaultContentType(false));
	}
	
	/**
	 * @deprecated use {@link #optional()} instead.
	 */
	@Deprecated
	public Media(final Option option)
	{
		this(option.optional, DEFAULT_LENGTH, new DefaultContentType(option.optional));

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
	 * Creates a new media, that must contain one the given content types only.
	 */
	public Media contentType(final String contentType1, final String contentType2)
	{
		return contentTypes(contentType1, contentType2);
	}
	
	private Media contentTypes(final String... types)
	{
		return new Media(optional, body.getMaximumLength(), new EnumContentType(types, optional));
	}
	
	/**
	 * Creates a new media, that must contain the a content type with the given major part only.
	 */
	public Media contentTypeMajor(final String majorContentType)
	{
		return new Media(optional, body.getMaximumLength(), new MajorContentType(majorContentType, optional));
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
	
	public FunctionField<?> getContentType()
	{
		return contentType.field;
	}
	
	public DateField getLastModified()
	{
		return lastModified;
	}
	
	public FunctionField getIsNull()
	{
		return lastModified;
	}
	
	public Class getWrapperSetterType()
	{
		return Value.class;
	}
	
	public Set<Class> getSetterExceptions()
	{
		final HashSet<Class> result = new HashSet<Class>();
		if(!optional)
			result.add(MandatoryViolationException.class);
		return result;
	}
	
	private static final String IO_EXCEPTION_COMMENT = "@throws " + IOException.class.getName() + " if accessing <tt>body</tt> throws an IOException.";
	
	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());

		if(optional)
			result.add(0/*TODO*/, new Wrapper(
				boolean.class, "isNull",
				"Returns whether media {0} is null.", // TODO better text
				"getter"
				));
		
		result.add(new Wrapper(
			long.class, "getLastModified",
			"Returns the last modification date of media {0}.",
			"getter"
			));
		
		result.add(new Wrapper(
			long.class, "getLength",
			"Returns the body length of the media {0}.",
			"getter"
			));
		
		result.add(new Wrapper(
			byte[].class, "getBody",
			"Returns the body of the media {0}.",
			"getter"
			));
			
		result.add(new Wrapper(
			void.class, "getBody",
			"Writes the body of media {0} into the given stream.",
			"getter",
			new Class[]{IOException.class}
			).
			addComment("Does nothing, if the media is null.").
			addComment(IO_EXCEPTION_COMMENT). // TODO make an extra method for exceptions
			addParameter(OutputStream.class, "body"));
		
		result.add(new Wrapper(
			void.class, "getBody",
			"Writes the body of media {0} into the given file.",
			"getter",
			new Class[]{IOException.class}
			).
			addComment("Does nothing, if the media is null.").
			addComment(IO_EXCEPTION_COMMENT). // TODO make an extra method for exceptions
			addParameter(File.class, "body"));
		
		result.add(new Wrapper(
			void.class, "set",
			"Sets the content of media {0}.",
			"setter").
			addParameter(byte[].class, "body").
			addParameter(String.class, "contentType"));
			
		result.add(new Wrapper(
			void.class, "set",
			"Sets the content of media {0}.",
			"setter",
			new Class[]{IOException.class}).
			addComment(IO_EXCEPTION_COMMENT). // TODO make an extra method for exceptions
			addParameter(InputStream.class, "body").
			addParameter(String.class, "contentType"));
		
		result.add(new Wrapper(
			void.class, "set",
			"Sets the content of media {0}.",
			"setter",
			new Class[]{IOException.class}).
			addComment(IO_EXCEPTION_COMMENT). // TODO make an extra method for exceptions
			addParameter(File.class, "body").
			addParameter(String.class, "contentType"));
		
		return Collections.unmodifiableList(result);
	}
	
	public boolean isFinal()
	{
		return false; // TODO
	}

	public boolean isInitial()
	{
		return !optional;
	}

	public SetValue map(final Value value)
	{
		return new SetValue<Value>(this, value);
	}
	
	@Override
	public void initialize()
	{
		super.initialize();
		
		final String name = getName();
		if(!body.isInitialized())
			initialize(body, name + "Body");
		final FunctionField contentTypeField = contentType.field;
		if(contentTypeField!=null && !contentTypeField.isInitialized())
			initialize(contentTypeField, name + contentType.name);
		initialize(lastModified, name + "LastModified");
	}
	
	public boolean isNull(final Item item)
	{
		return optional ? (lastModified.get(item)==null) : false;
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

		return contentType.get(item);
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
		return this.body.getArray(item);
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
			set(item, DataField.toValue(body), contentType);
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
			set(item, DataField.toValue(body), contentType);
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
		set(item, DataField.toValue(body), contentType);
	}
	
	private void set(final Item item, final DataField.Value body, final String contentType)
		throws DataLengthViolationException, IOException
	{
		item.set(execute(toValue(body, contentType), item));
	}
	
	public static final Value toValue(final DataField.Value body, final String contentType)
	{
		if(body!=null)
		{
			if(contentType!=null)
				return new Value(body, contentType);
			else
				throw new IllegalArgumentException("if body is not null, content type must also be not null");
		}
		else
		{
			if(contentType!=null)
				throw new IllegalArgumentException("if body is null, content type must also be null");
			else
				return null;
		}
	}
	
	public static final Value toValue(final byte[] body, final String contentType)
	{
		return toValue(DataField.toValue(body), contentType);
	}
	
	public static final Value toValue(final InputStream body, final String contentType)
	{
		return toValue(DataField.toValue(body), contentType);
	}
	
	public static final Value toValue(final File body, final String contentType)
	{
		return toValue(DataField.toValue(body), contentType);
	}
	
	public SetValue[] execute(final Value value, final Item exceptionItem)
	{
		if(value!=null)
		{
			final DataField.Value body = value.body;
			final String contentType = value.contentType;
			if(!this.contentType.check(contentType))
				throw new IllegalContentTypeException(this, exceptionItem, contentType);
			
			final ArrayList<SetValue> values = new ArrayList<SetValue>(4);
			final FunctionField contentTypeField = this.contentType.field;
			if(contentTypeField!=null)
				values.add(this.contentType.map(contentType));
			values.add(this.lastModified.map(new Date()));
			values.add(this.body.map(body));
			
			return values.toArray(new SetValue[values.size()]);
		}
		else
		{
			final ArrayList<SetValue> values = new ArrayList<SetValue>(4);
			final FunctionField<?> contentTypeField = this.contentType.field;
			if(contentTypeField!=null)
				values.add(contentTypeField.map(null));
			values.add(this.lastModified.map(null));
			values.add(this.body.mapNull());
			
			return values.toArray(new SetValue[values.size()]);
		}
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
			final HttpServletResponse response,
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
	
	public static final class Value // TODO add getter methods
	{
		private final String contentType;
		private final DataField.Value body;
		
		private Value(final DataField.Value body, final String contentType)
		{
			assert body!=null;
			assert contentType!=null;

			this.body = body;
			this.contentType = contentType;
		}
	}
	
	private static abstract class ContentType<B>
	{
		final FunctionField<B> field;
		final String name;
		
		ContentType()
		{
			this.field = null;
			this.name = null;
		}
		
		ContentType(final FunctionField<B> field, final boolean optional, final String name)
		{
			this.field = optional ? (FunctionField<B>)field.optional() : field;
			this.name = name;
			
			assert field!=null;
			assert name!=null;
		}
		
		abstract ContentType copy();
		abstract ContentType optional();
		abstract boolean check(String contentType);
		abstract String describe();
		abstract String get(Item item);
		abstract B set(String contentType);
		
		final SetValue<B> map(final String contentType)
		{
			return field.map(set(contentType));
		}
		
		protected static final StringField makeField(final int maxLength)
		{
			return new StringField().lengthRange(1, maxLength);
		}
	}

	private static final class DefaultContentType extends ContentType<String>
	{
		DefaultContentType(final boolean optional)
		{
			super(makeField(61), optional, "ContentType");
		}
		
		@Override
		DefaultContentType copy()
		{
			return new DefaultContentType(!field.isMandatory());
		}
		
		@Override
		DefaultContentType optional()
		{
			return new DefaultContentType(true);
		}
		
		@Override
		boolean check(final String contentType)
		{
			return contentType.indexOf('/')>=0;
		}
		
		@Override
		String describe()
		{
			return "*/*";
		}
		
		@Override
		String get(final Item item)
		{
			return field.get(item);
		}
		
		@Override
		String set(final String contentType)
		{
			return contentType;
		}
	}
	
	private static final class EnumContentType extends ContentType<Integer>
	{
		private final String[] types;
		private final HashMap<String, Integer> typeSet;
		
		EnumContentType(final String[] types, final boolean optional)
		{
			super(new IntegerField().range(0, types.length), optional, "ContentType");
			this.types = types;
			final HashMap<String, Integer> typeSet = new HashMap<String, Integer>();
			for(int i = 0; i<types.length; i++)
				typeSet.put(types[i], i);
			
			if(typeSet.containsKey(null))
				throw new IllegalArgumentException("null is not allowed in content type enumeration");
			if(typeSet.size()!=types.length)
				throw new IllegalArgumentException("duplicates are not allowed for content type enumeration");
			this.typeSet = typeSet;
		}
		
		@Override
		EnumContentType copy()
		{
			return new EnumContentType(types, !field.isMandatory());
		}
		
		@Override
		EnumContentType optional()
		{
			return new EnumContentType(types, true);
		}
		
		@Override
		boolean check(final String contentType)
		{
			return typeSet.containsKey(contentType);
		}
		
		@Override
		String describe()
		{
			final StringBuffer bf = new StringBuffer();
			boolean first = true;
			for(final String t : types)
			{
				if(first)
					first = false;
				else
					bf.append(',');
				
				bf.append(t);
			}
			return bf.toString();
		}
		
		@Override
		String get(final Item item)
		{
			return types[field.get(item).intValue()];
		}
		
		@Override
		Integer set(final String contentType)
		{
			final Integer result = typeSet.get(contentType);
			assert result!=null;
			return result;
		}
	}
	
	private static final class FixedContentType extends ContentType<Void>
	{
		private final String full;
		
		FixedContentType(final String full)
		{
			super();
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
			return this.full.equals(contentType);
		}
		
		@Override
		String describe()
		{
			return full;
		}
		
		@Override
		String get(final Item item)
		{
			return full;
		}
		
		@Override
		Void set(final String contentType)
		{
			throw new RuntimeException();
		}
	}

	private static final class MajorContentType extends ContentType<String>
	{
		private final String major;
		private final String prefix;
		private final int prefixLength;
		
		MajorContentType(final String major, final boolean optional)
		{
			super(makeField(30), optional, "Minor");
			this.major = major;
			this.prefix = major + '/';
			this.prefixLength = this.prefix.length();
			
			if(major==null)
				throw new NullPointerException("fixedMimeMajor must not be null");
		}
		
		@Override
		MajorContentType copy()
		{
			return new MajorContentType(major, !field.isMandatory());
		}
		
		@Override
		MajorContentType optional()
		{
			return new MajorContentType(major, true);
		}
		
		@Override
		boolean check(final String contentType)
		{
			return contentType.startsWith(prefix);
		}
		
		@Override
		String describe()
		{
			return prefix + '*';
		}
		
		@Override
		String get(final Item item)
		{
			return prefix + field.get(item);
		}
		
		@Override
		String set(final String contentType)
		{
			assert check(contentType);
			return contentType.substring(prefixLength);
		}
	}
}

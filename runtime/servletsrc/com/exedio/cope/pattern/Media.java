/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.DataField;
import com.exedio.cope.DataLengthViolationException;
import com.exedio.cope.DateField;
import com.exedio.cope.Field;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.instrument.Wrapped;
import com.exedio.cope.instrument.WrappedParam;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperSuppressor;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.SetValueUtil;

public final class Media extends CachedMedia implements Settable<Media.Value>
{
	private static final long serialVersionUID = 1l;

	private final boolean isfinal;
	private final boolean optional;
	private final DataField body;
	private final ContentType<?> contentType;
	private final DateField lastModified;
	private final CheckConstraint unison;

	public static final long DEFAULT_LENGTH = DataField.DEFAULT_LENGTH;

	private Media(
			final boolean isfinal,
			final boolean optional,
			final long bodyMaximumLength,
			final ContentType contentType)
	{
		this.isfinal = isfinal;
		this.optional = optional;
		addSource(
				this.body = applyConstraints(new DataField(), isfinal, optional).lengthMax(bodyMaximumLength),
				"body",
				ComputedElement.get());
		this.contentType = contentType;
		final FunctionField contentTypeField = contentType.field;
		if(contentTypeField!=null)
			addSource(
					contentTypeField,
					contentType.name,
					ComputedElement.get());
		addSource(
				this.lastModified = applyConstraints(new DateField(), isfinal, optional),
				"lastModified",
				ComputedElement.get());

		if(optional)
		{
			final ArrayList<Condition> isNull    = new ArrayList<Condition>();
			final ArrayList<Condition> isNotNull = new ArrayList<Condition>();
			// TODO include body as well, needs DataField#isNull
			if(contentTypeField!=null)
			{
				isNull   .add(contentTypeField.isNull());
				isNotNull.add(contentTypeField.isNotNull());
			}
			isNull   .add(this.lastModified.isNull());
			isNotNull.add(this.lastModified.isNotNull());
			addSource(
					this.unison = new CheckConstraint(Cope.and(isNull).or(Cope.and(isNotNull))),
					"unison");
		}
		else
		{
			this.unison = null;
		}

		assert optional == !body.isMandatory();
		assert (contentTypeField==null) || (optional == !contentTypeField.isMandatory());
		assert optional == !lastModified.isMandatory();
		assert optional == (unison!=null);
	}

	@SuppressWarnings("unchecked")
	static final <F extends Field> F applyConstraints(
			F field,
			final boolean isfinal,
			final boolean optional)
	{
		if(isfinal)
			field = (F)field.toFinal();
		if(optional)
			field = (F)field.optional();
		return field;
	}

	public Media()
	{
		this(false, false, DEFAULT_LENGTH, new DefaultContentType(false, false));
	}

	public Media toFinal()
	{
		return new Media(true, optional, body.getMaximumLength(), contentType.toFinal());
	}

	public Media optional()
	{
		return new Media(isfinal, true, body.getMaximumLength(), contentType.optional());
	}

	public Media lengthMax(final long maximumLength)
	{
		return new Media(isfinal, optional, maximumLength, contentType.copy());
	}

	/**
	 * Creates a new media, that must contain the given content type only.
	 */
	public Media contentType(final String contentType)
	{
		return new Media(isfinal, optional, body.getMaximumLength(), new FixedContentType(contentType));
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 */
	public Media contentType(final String contentType1, final String contentType2)
	{
		return contentTypes(contentType1, contentType2);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 */
	public Media contentType(final String contentType1, final String contentType2, final String contentType3)
	{
		return contentTypes(contentType1, contentType2, contentType3);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 */
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4)
	{
		return contentTypes(contentType1, contentType2, contentType3, contentType4);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 */
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4, final String contentType5)
	{
		return contentTypes(contentType1, contentType2, contentType3, contentType4, contentType5);
	}

	// cannot make this method public, because the instrumentor (i.e. beanshell) does not work with varargs
	private Media contentTypes(final String... types)
	{
		return new Media(isfinal, optional, body.getMaximumLength(), new EnumContentType(types, isfinal, optional));
	}

	/**
	 * Creates a new media, that must contain the a content type with the given major part only.
	 */
	public Media contentTypeSub(final String majorContentType)
	{
		return new Media(isfinal, optional, body.getMaximumLength(), new SubContentType(majorContentType, isfinal, optional));
	}

	public final boolean isMandatory()
	{
		return !optional;
	}

	public boolean checkContentType(final String contentType)
	{
		return this.contentType.check(contentType);
	}

	public String getContentTypeDescription()
	{
		return contentType.describe();
	}

	/**
	 * Returns a list of content types allowed for this media.
	 * Returns null, if such a list would not contain a finite
	 * number of elements.
	 */
	public List<String> getContentTypesAllowed()
	{
		return contentType.getAllowed();
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

	public CheckConstraint getUnison()
	{
		return unison;
	}

	public Class getInitialType()
	{
		return Value.class;
	}

	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final LinkedHashSet<Class<? extends Throwable>> result = new LinkedHashSet<Class<? extends Throwable>>();
		if(isfinal)
			result.add(FinalViolationException.class);
		if(!optional)
			result.add(MandatoryViolationException.class);
		return result;
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		return Wrapper.makeByReflection(Media.class, this, super.getWrappers());
	}

	@Override
	public boolean isContentTypeWrapped()
	{
		return !(contentType instanceof FixedContentType);
	}

	public boolean isFinal()
	{
		return isfinal;
	}

	public boolean isInitial()
	{
		return isfinal || !optional;
	}

	public SetValue map(final Value value)
	{
		return new SetValue<Value>(this, value);
	}

	@Wrapped(pos=10, comment = "Returns whether media {0} is null.", suppressor=MandatorySuppressor.class)
	public boolean isNull(final Item item)
	{
		return optional ? (lastModified.get(item)==null) : false;
	}

	private static final class MandatorySuppressor implements WrapperSuppressor<Media>
	{
		@Override public boolean isSuppressed(final Media feature)
		{
			return feature.isMandatory();
		}
	}

	/**
	 * Returns the content type of this media.
	 * Returns null, if this media is null.
	 */
	@Override
	public String getContentType(final Item item)
	{
		return contentType.get(item, lastModified);
	}

	/**
	 * Returns the date of the last modification
	 * of this media.
	 * Returns -1, if this media is null.
	 */
	@Wrapped(pos=20, comment = "Returns the last modification date of media {0}.")
	@Override
	public long getLastModified(final Item item)
	{
		final Date date = lastModified.get(item);
		return date!=null ? date.getTime() : -1;
	}

	/**
	 * Returns the length of the body of this media.
	 * Returns -1, if this media is null.
	 */
	@Wrapped(pos=30, comment = "Returns the body length of the media {0}.")
	public long getLength(final Item item)
	{
		// do check before, because this check is supported by the item cache
		if(isNull(item))
			return -1;

		return body.getLength(item);
	}

	/**
	 * Sets the contents of this media.
	 * @param value give null to make this media null.
	 * @throws MandatoryViolationException
	 *         if body is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if body is longer than {@link #getMaximumLength()}
	 * @throws IOException if reading value throws an IOException.
	 */
	@Wrapped(pos=110, comment = "Sets the content of media {0}.", suppressor=FinalSuppressor.class,
		thrown={@Wrapped.Thrown(clazz=IOException.class, comment="if accessing <tt>body</tt> throws an IOException.")})
	public void set(
			final Item item,
			final Media.Value value)
		throws DataLengthViolationException, IOException
	{
		if(value==null && !optional)
			throw new MandatoryViolationException(this, this, item);

		item.set(execute(value, item));
	}

	/**
	 * Returns the body of this media.
	 * Returns null, if this media is null.
	 */
	@Wrapped(pos=40, comment = "Returns the body of the media {0}.")
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
	@Wrapped(pos=120, comment = "Sets the content of media {0}.", suppressor=FinalSuppressor.class)
	public void set(
			final Item item,
			@WrappedParam("body") final byte[] body,
			@WrappedParam("contentType") final String contentType)
		throws DataLengthViolationException
	{
		if((body==null||contentType==null) && !optional)
			throw new MandatoryViolationException(this, this, item);

		try
		{
			set(item, DataField.toValue(body), contentType);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(toString(), e);
		}
	}

	/**
	 * Writes the body of this media into the given steam.
	 * Does nothing, if this media is null.
	 * @throws NullPointerException
	 *         if <tt>body</tt> is null.
	 * @throws IOException if writing <tt>body</tt> throws an IOException.
	 */
	@Wrapped(pos=50, comment = {
		"Writes the body of media {0} into the given stream.",
		"Does nothing, if the media is null."},
		thrown={@Wrapped.Thrown(clazz=IOException.class, comment="if accessing <tt>body</tt> throws an IOException.")})
	public void getBody(
			final Item item,
			@WrappedParam("body") final OutputStream body)
	throws IOException
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
	@Wrapped(pos=130, comment = "Sets the content of media {0}.", suppressor=FinalSuppressor.class,
		thrown={@Wrapped.Thrown(clazz=IOException.class, comment="if accessing <tt>body</tt> throws an IOException.")})
	public void set(
			final Item item,
			@WrappedParam("body") final InputStream body,
			@WrappedParam("contentType") final String contentType)
		throws DataLengthViolationException, IOException
	{
		if((body==null||contentType==null) && !optional)
			throw new MandatoryViolationException(this, this, item);

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
	@Wrapped(pos=60, comment = {
		"Writes the body of media {0} into the given file.",
		"Does nothing, if the media is null."},
		thrown={@Wrapped.Thrown(clazz=IOException.class, comment="if accessing <tt>body</tt> throws an IOException.")})
	public void getBody(
			final Item item,
			@WrappedParam("body") final File body)
	throws IOException
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
	@Wrapped(pos=140, comment = "Sets the content of media {0}.", suppressor=FinalSuppressor.class,
		thrown={@Wrapped.Thrown(clazz=IOException.class, comment="if accessing <tt>body</tt> throws an IOException.")})
	public void set(
			final Item item,
			@WrappedParam("body") final File body,
			@WrappedParam("contentType") final String contentType)
		throws DataLengthViolationException, IOException
	{
		if((body==null||contentType==null) && !optional)
			throw new MandatoryViolationException(this, this, item);

		set(item, DataField.toValue(body), contentType);
	}

	private static final class FinalSuppressor implements WrapperSuppressor<Media>
	{
		@Override public boolean isSuppressed(final Media feature)
		{
			return feature.isFinal();
		}
	}

	/**
	 * @throws IOException if reading data throws an IOException.
	 */
	private void set(
			final Item item,
			final DataField.Value body,
			final String contentType)
		throws DataLengthViolationException, IOException
	{
		assert !((body==null||contentType==null) && !optional) : getID();

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

			return SetValueUtil.toArray(values);
		}
		else
		{
			if(!optional)
				throw new MandatoryViolationException(this, this, exceptionItem);

			final ArrayList<SetValue> values = new ArrayList<SetValue>(4);
			final FunctionField<?> contentTypeField = this.contentType.field;
			if(contentTypeField!=null)
				values.add(contentTypeField.map(null));
			values.add(this.lastModified.map(null));
			values.add(this.body.mapNull());

			return SetValueUtil.toArray(values);
		}
	}

	public final static Media get(final DataField field)
	{
		final Pattern pattern = field.getPattern();
		if(pattern instanceof Media)
		{
			final Media media = (Media)pattern;
			if(media.getBody()==field)
				return media;
		}
		throw new NullPointerException(field.toString());
	}

	@Override
	public Media.Log doGetIfModified(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
		throws IOException
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

		final ServletOutputStream out = response.getOutputStream();
		try
		{
			getBody(item, out);
			return delivered;
		}
		finally
		{
			if(out!=null)
				out.close();
		}
	}

	@Override
	public Condition isNull()
	{
		return lastModified.isNull();
	}

	@Override
	public Condition isNotNull()
	{
		return lastModified.isNotNull();
	}

	public Condition contentTypeEqual(final String contentType)
	{
		return
			contentType!=null
			? this.contentType.equal(contentType)
			: this.lastModified.isNull();
	}

	public Condition bodyMismatchesContentType()
	{
		return MediaType.mismatches(this);
	}

	public static final class Value
	{
		final DataField.Value body;
		final String contentType;

		Value(final DataField.Value body, final String contentType)
		{
			assert body!=null;
			assert contentType!=null;

			this.body = body;
			this.contentType = contentType;
		}

		public DataField.Value getBody()
		{
			return body;
		}

		public String getContentType()
		{
			return contentType;
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated use {@link #contentType(String)} instead.
	 */
	@Deprecated
	public Media(final String fixedMimeMajor, final String fixedMimeMinor)
	{
		this(false, false, DEFAULT_LENGTH, new FixedContentType(fixedMimeMajor, fixedMimeMinor));
	}

	/**
	 * @deprecated use {@link #contentTypeSub(String)} instead.
	 */
	@Deprecated
	public Media(final String fixedMimeMajor)
	{
		this(false, false, DEFAULT_LENGTH, new SubContentType(fixedMimeMajor, false, false));
	}

	/**
	 * @deprecated Use {@link #contentTypeSub(String)} instead
	 */
	@Deprecated
	public Media contentTypeMajor(final String majorContentType)
	{
		return contentTypeSub(majorContentType);
	}
}

/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.CopyMapper;
import com.exedio.cope.Copyable;
import com.exedio.cope.DataField;
import com.exedio.cope.DataLengthViolationException;
import com.exedio.cope.DateField;
import com.exedio.cope.Field;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Function;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.UnsupportedQueryException;
import com.exedio.cope.Vault;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.Conditions;
import com.exedio.cope.misc.SetValueUtil;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import com.exedio.cope.util.Clock;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class Media extends MediaPath implements Settable<Media.Value>, Copyable
{
	private static final long serialVersionUID = 1l;

	private final boolean isfinal;
	private final boolean optional;
	private final DataField body;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final ContentType<?> contentType;
	private final DateField lastModified;
	private final CheckConstraint unison;

	public static final long DEFAULT_LENGTH = DataField.DEFAULT_LENGTH;

	private Media(
			final boolean isfinal,
			final boolean optional,
			final long bodyMaximumLength,
			final ContentType<?> contentType)
	{
		this.isfinal = isfinal;
		this.optional = optional;
		//noinspection ThisEscapedInObjectConstruction
		this.body = addSourceFeature(
				applyConstraints(new DataField(), isfinal, optional).lengthMax(bodyMaximumLength),
				"body",
				new MediaVaultAnnotationProxy(this));
		this.contentType = contentType;
		final FunctionField<?> contentTypeField = contentType.field;
		if(contentTypeField!=null)
			addSourceFeature(
					contentTypeField,
					contentType.name,
					ComputedElement.get());
		this.lastModified = addSourceFeature(
				applyConstraints(new DateField(), isfinal, optional),
				"lastModified",
				ComputedElement.get());

		if(optional)
		{
			final ArrayList<Function<?>> functions  = new ArrayList<>();
			// TODO include body as well, needs DataField#isNull
			if(contentTypeField!=null)
				functions.add(contentTypeField);
			functions.add(this.lastModified);
			final Condition condition = Conditions.unisonNull(functions);
			this.unison = (condition!=Condition.TRUE) ? addSourceFeature(new CheckConstraint(condition), "unison") : null;
		}
		else
		{
			this.unison = null;
		}

		assert optional == !body.isMandatory();
		assert (contentTypeField==null) || (optional == !contentTypeField.isMandatory());
		assert optional == !lastModified.isMandatory();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	static <F extends Field> F applyConstraints(
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
		this(false, false, DEFAULT_LENGTH, new DefaultContentType(false, false, 61));
	}

	@Override
	public Media copy(final CopyMapper mapper)
	{
		final Media result = new Media(isfinal, optional, body.getMaximumLength(), contentType.copy());
		// TODO implement some generic mapping
		if(contentType.field!=null)
			mapper.put(contentType.field, result.contentType.field);
		mapper.put(lastModified, result.lastModified);
		return result;
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

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 */
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4, final String contentType5, final String contentType6)
	{
		return contentTypes(contentType1, contentType2, contentType3, contentType4, contentType5, contentType6);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 */
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4, final String contentType5, final String contentType6, final String contentType7)
	{
		return contentTypes(contentType1, contentType2, contentType3, contentType4, contentType5, contentType6, contentType7);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 */
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4, final String contentType5, final String contentType6, final String contentType7, final String contentType8)
	{
		return contentTypes(contentType1, contentType2, contentType3, contentType4, contentType5, contentType6, contentType7, contentType8);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 */
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4, final String contentType5, final String contentType6, final String contentType7, final String contentType8, final String contentType9)
	{
		return contentTypes(contentType1, contentType2, contentType3, contentType4, contentType5, contentType6, contentType7, contentType8, contentType9);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 */
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4, final String contentType5, final String contentType6, final String contentType7, final String contentType8, final String contentType9, final String contentType10)
	{
		return contentTypes(contentType1, contentType2, contentType3, contentType4, contentType5, contentType6, contentType7, contentType8, contentType9, contentType10);
	}

	// cannot make this method public, because the instrumentor (i.e. beanshell) does not work with varargs
	// BEWARE: if this method was public it had to copy parameter
	private Media contentTypes(final String... types)
	{
		return new Media(isfinal, optional, body.getMaximumLength(), new EnumContentType(types, isfinal, optional));
	}

	/**
	 * Creates a new media, that must contain the a content type with the given major part only.
	 */
	public Media contentTypeSub(final String majorContentType)
	{
		return new Media(isfinal, optional, body.getMaximumLength(), new SubContentType(majorContentType, isfinal, optional, SubContentType.DEFAULT_LENGTH));
	}

	public Media contentTypeLengthMax(final int maximumLength)
	{
		return new Media(isfinal, optional, body.getMaximumLength(), contentType.lengthMax(maximumLength));
	}

	@Override
	public boolean isFinal()
	{
		return isfinal;
	}

	@Override
	public boolean isMandatory()
	{
		return !optional;
	}

	@Override
	public Class<?> getInitialType()
	{
		return Value.class;
	}

	public boolean checkContentType(final String contentType)
	{
		return this.contentType.check(contentType);
	}

	public int getContentTypeMaximumLength()
	{
		return contentType.getMaximumLength();
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

	public boolean isAnnotatedVault()
	{
		return body.isAnnotatedVault();
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final LinkedHashSet<Class<? extends Throwable>> result = new LinkedHashSet<>();
		if(isfinal)
			result.add(FinalViolationException.class);
		if(!optional)
			result.add(MandatoryViolationException.class);
		return result;
	}

	@Override
	public boolean isContentTypeWrapped()
	{
		return !(contentType instanceof FixedContentType);
	}

	@Override
	public boolean isInitial()
	{
		return isfinal || !optional;
	}

	@Wrap(order=10, doc="Returns whether media {0} is null.", hide=MandatoryGetter.class)
	public boolean isNull(@Nonnull final Item item)
	{
		//noinspection SimplifiableConditionalExpression
		return optional ? (lastModified.get(item)==null) : false;
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
	 * Returns null, if this media is null.
	 */
	@Wrap(order=20, doc="Returns the last modification date of media {0}.", nullability=NullableIfOptional.class)
	@Override
	public Date getLastModified(@Nonnull final Item item)
	{
		return lastModified.get(item);
	}

	public void setLastModified(@Nonnull final Item item, @Nonnull final Date value)
	{
		lastModified.set(item, requireNonNull(value));
	}

	/**
	 * Returns the length of the body of this media.
	 * Returns -1, if this media is null.
	 */
	@Wrap(order=30, doc="Returns the body length of the media {0}.")
	public long getLength(@Nonnull final Item item)
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
	@Wrap(order=110,
			doc = "Sets the content of media {0}.",
			hide=FinalSettableGetter.class,
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing <tt>body</tt> throws an IOException."))
	@SuppressWarnings({"RedundantThrows", "RedundantThrowsDeclaration"}) // TODO should not wrap IOException into RuntimeException
	public void set(
			@Nonnull final Item item,
			@Parameter(nullability=NullableIfOptional.class) final Value value)
		throws IOException
	{
		FinalViolationException.check(this, item);
		if(value==null && !optional)
			throw MandatoryViolationException.create(this, item);

		item.set(execute(value, item));
	}

	/**
	 * Returns the body of this media.
	 * Returns null, if this media is null.
	 */
	@Wrap(order=40, doc="Returns the body of the media {0}.", nullability=NullableIfOptional.class)
	public byte[] getBody(@Nonnull final Item item)
	{
		return body.getArray(item);
	}

	/**
	 * Sets the contents of this media.
	 * @param body give null to make this media null.
	 * @throws MandatoryViolationException
	 *         if body is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if body is longer than {@link #getMaximumLength()}
	 */
	@Wrap(order=120, doc="Sets the content of media {0}.", hide=FinalSettableGetter.class)
	public void set(
			@Nonnull final Item item,
			@Parameter(value="body", nullability=NullableIfOptional.class) final byte[] body,
			@Parameter(value="contentType", nullability=NullableIfOptional.class) final String contentType)
	{
		FinalViolationException.check(this, item);
		if((body==null||contentType==null) && !optional)
			throw MandatoryViolationException.create(this, item);

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
	@Wrap(order=50,
			doc={"Writes the body of media {0} into the given stream.",
					"Does nothing, if the media is null."},
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing <tt>body</tt> throws an IOException."))
	public void getBody(
			@Nonnull final Item item,
			@Nonnull @Parameter("body") final OutputStream body)
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
	@Wrap(order=130,
			doc="Sets the content of media {0}.",
			hide=FinalSettableGetter.class,
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing <tt>body</tt> throws an IOException."))
	public void set(
			@Nonnull final Item item,
			@Parameter(value="body", nullability=NullableIfOptional.class) final InputStream body,
			@Parameter(value="contentType", nullability=NullableIfOptional.class) final String contentType)
		throws IOException
	{
		FinalViolationException.check(this, item);
		if((body==null||contentType==null) && !optional)
			throw MandatoryViolationException.create(this, item);

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

	@Wrap(order=58,
			doc={"Writes the body of media {0} into the given file.",
					"Does nothing, if the media is null."},
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing <tt>body</tt> throws an IOException."))
	public void getBody(
			@Nonnull final Item item,
			@Nonnull @Parameter("body") final Path body)
			throws IOException
	{
		this.body.get(item, body);
	}

	/**
	 * Writes the body of this media into the given file.
	 * Does nothing, if this media is null.
	 * @throws NullPointerException
	 *         if <tt>body</tt> is null.
	 * @throws IOException if writing <tt>body</tt> throws an IOException.
	 */
	@Wrap(order=60,
			doc={"Writes the body of media {0} into the given file.",
					"Does nothing, if the media is null."},
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing <tt>body</tt> throws an IOException."))
	public void getBody(
			@Nonnull final Item item,
			@Nonnull @Parameter("body") final File body)
	throws IOException
	{
		this.body.get(item, body);
	}

	@Wrap(order=138,
			doc="Sets the content of media {0}.",
			hide=FinalSettableGetter.class,
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing <tt>body</tt> throws an IOException."))
	public void set(
			@Nonnull final Item item,
			@Parameter(value="body", nullability=NullableIfOptional.class) final Path body,
			@Parameter(value="contentType", nullability=NullableIfOptional.class) final String contentType)
		throws IOException
	{
		FinalViolationException.check(this, item);
		if((body==null||contentType==null) && !optional)
			throw MandatoryViolationException.create(this, item);

		set(item, DataField.toValue(body), contentType);
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
	@Wrap(order=140,
			doc="Sets the content of media {0}.",
			hide=FinalSettableGetter.class,
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing <tt>body</tt> throws an IOException."))
	public void set(
			@Nonnull final Item item,
			@Parameter(value="body", nullability=NullableIfOptional.class) final File body,
			@Parameter(value="contentType", nullability=NullableIfOptional.class) final String contentType)
		throws IOException
	{
		set(item, body!=null ? body.toPath() : null, contentType);
	}

	/**
	 * @throws IOException if reading data throws an IOException.
	 */
	@SuppressWarnings({"RedundantThrows", "RedundantThrowsDeclaration"}) // TODO should not wrap IOException into RuntimeException
	private void set(
			final Item item,
			final DataField.Value body,
			final String contentType)
		throws IOException
	{
		assert !((body==null||contentType==null) && !optional) : getID();

		item.set(execute(toValue(body, contentType), item));
	}

	public static Value toValue(final DataField.Value body, final String contentType)
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

	public static Value toValue(final byte[] body, final String contentType)
	{
		return toValue(DataField.toValue(body), contentType);
	}

	public static Value toValue(final InputStream body, final String contentType)
	{
		return toValue(DataField.toValue(body), contentType);
	}

	public static Value toValue(final Path body, final String contentType)
	{
		return toValue(DataField.toValue(body), contentType);
	}

	public static Value toValue(final File body, final String contentType)
	{
		return toValue(DataField.toValue(body), contentType);
	}

	@Override
	public SetValue<?>[] execute(final Value value, final Item exceptionItem)
	{
		if(value!=null)
		{
			final DataField.Value body = value.body;
			final String contentType = value.contentType;
			if(!this.contentType.check(contentType))
				throw new IllegalContentTypeException(this, exceptionItem, contentType);

			final ArrayList<SetValue<?>> values = new ArrayList<>(4);
			final FunctionField<?> contentTypeField = this.contentType.field;
			if(contentTypeField!=null)
				values.add(this.contentType.map(contentType));
			values.add(this.lastModified.map(Clock.newDate()));
			values.add(this.body.map(body));

			return SetValueUtil.toArray(values);
		}
		else
		{
			if(!optional)
				throw MandatoryViolationException.create(this, exceptionItem);

			final ArrayList<SetValue<?>> values = new ArrayList<>(4);
			final FunctionField<?> contentTypeField = this.contentType.field;
			if(contentTypeField!=null)
				values.add(contentTypeField.map(null));
			values.add(this.lastModified.map(null));
			values.add(this.body.mapNull());

			return SetValueUtil.toArray(values);
		}
	}

	public static Media get(final DataField field)
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
	public void doGetAndCommit(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
		throws IOException, NotFound
	{
		final String contentType = getContentType(item);
		if(contentType==null)
			throw notFoundIsNull();

		final byte[] body = getBody(item);

		commit();

		MediaUtil.send(contentType, body, response);
	}

	@Override
	public Condition isNull()
	{
		return lastModified.isNull();
	}

	@Override
	public Condition isNull(final Join join)
	{
		return lastModified.bind(join).isNull();
	}

	@Override
	public Condition isNotNull()
	{
		return lastModified.isNotNull();
	}

	@Override
	public Condition isNotNull(final Join join)
	{
		return lastModified.bind(join).isNotNull();
	}

	public Condition contentTypeEqual(final String contentType)
	{
		return
			contentType!=null
			? this.contentType.equal(contentType)
			: this.lastModified.isNull();
	}

	/**
	 * The result may cause an {@link UnsupportedQueryException} when used,
	 * if the field is stored in a {@link Vault vault}.
	 */
	public Condition bodyMismatchesContentTypeIfSupported()
	{
		return MediaType.mismatchesIfSupported(this);
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
	 * @deprecated Use {@link #bodyMismatchesContentTypeIfSupported()} instead.
	 */
	@Deprecated
	public Condition bodyMismatchesContentType()
	{
		return bodyMismatchesContentTypeIfSupported();
	}

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
		this(false, false, DEFAULT_LENGTH, new SubContentType(fixedMimeMajor, false, false, SubContentType.DEFAULT_LENGTH));
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

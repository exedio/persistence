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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.Contract;

public final class Media extends MediaPath implements Settable<Media.Value>, Copyable
{
	private static final long serialVersionUID = 1l;

	private final boolean isfinal;
	private final boolean optional;
	private final DataField body;
	final boolean isBodySmall;
	private final ContentType<?> contentType;
	private final DateField lastModified;
	private final CheckConstraint unison;

	public static final long DEFAULT_LENGTH = DataField.DEFAULT_LENGTH;

	private Media(
			final DataField body,
			final ContentType<?> contentType,
			final DateField lastModified)
	{
		this.isfinal = body.isFinal();
		this.optional = !body.isMandatory();
		//noinspection ThisEscapedInObjectConstruction
		this.body = addSourceFeature(
				body,
				"body",
				new MediaVaultAnnotationProxy(this));
		this.isBodySmall = body.getMaximumLength()<=DEFAULT_LENGTH;
		this.contentType = contentType;
		final FunctionField<?> contentTypeField = contentType.field;
		if(contentTypeField!=null)
			addSourceFeature(
					contentTypeField,
					contentType.name,
					ComputedElement.get());
		this.lastModified = addSourceFeature(
				lastModified,
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
			this.unison = (condition!=Condition.ofTrue()) ? addSourceFeature(new CheckConstraint(condition), "unison") : null;
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
			final DataField constraintSource)
	{
		if(constraintSource.isFinal())
			field = (F)field.toFinal();
		if(!constraintSource.isMandatory())
			field = (F)field.optional();
		return field;
	}

	public Media()
	{
		this(new DataField(), new DefaultContentType(), new DateField());
	}

	@Override
	public Media copy(final CopyMapper mapper)
	{
		final Media result = new Media(body.copy(), contentType.copy(), lastModified.copy());
		// TODO implement some generic mapping
		if(contentType.field!=null)
			mapper.put(contentType.field, result.contentType.field);
		mapper.put(lastModified, result.lastModified);
		return result;
	}

	public Media toFinal()
	{
		return new Media(body.toFinal(), contentType.toFinal(), lastModified.toFinal());
	}

	public Media optional()
	{
		return new Media(body.optional(), contentType.optional(), lastModified.optional());
	}

	public Media lengthMax(final long maximumLength)
	{
		return new Media(body.lengthMax(maximumLength), contentType.copy(), lastModified.copy());
	}

	/**
	 * Creates a new media, that must contain the given content type only.
	 */
	public Media contentType(final String contentType)
	{
		return new Media(body.copy(), new FixedContentType(contentType), lastModified.copy());
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 * @deprecated use {@link #contentTypes(String...)}
	 */
	@Deprecated
	public Media contentType(final String contentType1, final String contentType2)
	{
		return contentTypesInternal(contentType1, contentType2);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 * @deprecated use {@link #contentTypes(String...)}
	 */
	@Deprecated
	public Media contentType(final String contentType1, final String contentType2, final String contentType3)
	{
		return contentTypesInternal(contentType1, contentType2, contentType3);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 * @deprecated use {@link #contentTypes(String...)}
	 */
	@Deprecated
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4)
	{
		return contentTypesInternal(contentType1, contentType2, contentType3, contentType4);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 * @deprecated use {@link #contentTypes(String...)}
	 */
	@Deprecated
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4, final String contentType5)
	{
		return contentTypesInternal(contentType1, contentType2, contentType3, contentType4, contentType5);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 * @deprecated use {@link #contentTypes(String...)}
	 */
	@Deprecated
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4, final String contentType5, final String contentType6)
	{
		return contentTypesInternal(contentType1, contentType2, contentType3, contentType4, contentType5, contentType6);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 * @deprecated use {@link #contentTypes(String...)}
	 */
	@Deprecated
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4, final String contentType5, final String contentType6, final String contentType7)
	{
		return contentTypesInternal(contentType1, contentType2, contentType3, contentType4, contentType5, contentType6, contentType7);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 * @deprecated use {@link #contentTypes(String...)}
	 */
	@Deprecated
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4, final String contentType5, final String contentType6, final String contentType7, final String contentType8)
	{
		return contentTypesInternal(contentType1, contentType2, contentType3, contentType4, contentType5, contentType6, contentType7, contentType8);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 * @deprecated use {@link #contentTypes(String...)}
	 */
	@Deprecated
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4, final String contentType5, final String contentType6, final String contentType7, final String contentType8, final String contentType9)
	{
		return contentTypesInternal(contentType1, contentType2, contentType3, contentType4, contentType5, contentType6, contentType7, contentType8, contentType9);
	}

	/**
	 * Creates a new media, that must contain one of the given content types only.
	 * @deprecated use {@link #contentTypes(String...)}
	 */
	@Deprecated
	public Media contentType(final String contentType1, final String contentType2, final String contentType3, final String contentType4, final String contentType5, final String contentType6, final String contentType7, final String contentType8, final String contentType9, final String contentType10)
	{
		return contentTypesInternal(contentType1, contentType2, contentType3, contentType4, contentType5, contentType6, contentType7, contentType8, contentType9, contentType10);
	}

	/**
	 * Creates a new media that must contain one of the given content types only.
	 */
	public Media contentTypes(final String... types)
	{
		switch (types.length)
		{
			case 0:
				throw new IllegalArgumentException("must provide at least one content type");
			case 1:
				return contentType(types[0]);
			default:
				return contentTypesInternal(types);
		}
	}

	private Media contentTypesInternal(final String... types)
	{
		return new Media(body.copy(), new EnumContentType(types, body), lastModified.copy());
	}

	/**
	 * Creates a new media, that must contain the a content type with the given major part only.
	 */
	public Media contentTypeSub(final String majorContentType)
	{
		return new Media(body.copy(), new SubContentType(majorContentType, body), lastModified.copy());
	}

	public Media contentTypeLengthMax(final int maximumLength)
	{
		return new Media(body.copy(), contentType.lengthMax(maximumLength), lastModified.copy());
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

	public String getAnnotatedVaultValue()
	{
		return body.getAnnotatedVaultValue();
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
	@Wrap(order=20, doc=Wrap.MEDIA_LAST_MODIFIED, nullability=NullableIfOptional.class)
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
	@Wrap(order=30, doc=Wrap.MEDIA_LENGTH)
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
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing '{@code body}' throws an IOException."))
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
	@Wrap(order=40, doc=Wrap.MEDIA_BODY, nullability=NullableIfOptional.class)
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
	 *         if {@code body} is null.
	 * @throws IOException if writing {@code body} throws an IOException.
	 */
	@Wrap(order=50,
			doc={"Writes the body of media {0} into the given stream.",
					"Does nothing, if the media is null."},
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing '{@code body}' throws an IOException."))
	public void getBody(
			@Nonnull final Item item,
			@Nonnull @Parameter("body") final OutputStream body)
	throws IOException
	{
		this.body.get(item, body);
	}

	/**
	 * Sets the contents of this media.
	 * Closes {@code body} after reading the contents of the stream.
	 * @param body give null to make this media null.
	 * @throws MandatoryViolationException
	 *         if {@code body} is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if {@code body} is longer than {@link #getMaximumLength()}
	 * @throws IOException if reading {@code body} throws an IOException.
	 */
	@Wrap(order=130,
			doc="Sets the content of media {0}.",
			hide=FinalSettableGetter.class,
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing '{@code body}' throws an IOException."))
	public void set(
			@Nonnull final Item item,
			@Parameter(value="body", nullability=NullableIfOptional.class) final InputStream body,
			@Parameter(value="contentType", nullability=NullableIfOptional.class) final String contentType)
		throws IOException
	{
		FinalViolationException.check(this, item);
		if((body==null||contentType==null) && !optional)
			throw MandatoryViolationException.create(this, item);

		set(item, DataField.toValue(body), contentType);
	}

	@Wrap(order=58,
			doc={"Writes the body of media {0} into the given file.",
					"Does nothing, if the media is null."},
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing '{@code body}' throws an IOException."))
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
	 *         if {@code body} is null.
	 * @throws IOException if writing {@code body} throws an IOException.
	 */
	@Wrap(order=60,
			doc={"Writes the body of media {0} into the given file.",
					"Does nothing, if the media is null."},
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing '{@code body}' throws an IOException."))
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
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing '{@code body}' throws an IOException."))
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
	 *         if {@code body} is null and field is {@link Field#isMandatory() mandatory}.
	 * @throws DataLengthViolationException
	 *         if {@code body} is longer than {@link #getMaximumLength()}
	 * @throws IOException if reading {@code body} throws an IOException.
	 */
	@Wrap(order=140,
			doc="Sets the content of media {0}.",
			hide=FinalSettableGetter.class,
			thrown=@Wrap.Thrown(value=IOException.class, doc="if accessing '{@code body}' throws an IOException."))
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

	@Contract("null, null -> null; !null, !null -> !null; null, !null -> fail; !null, null -> fail")
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

	@Contract("null, null -> null; !null, !null -> !null; null, !null -> fail; !null, null -> fail")
	public static Value toValue(final byte[] body, final String contentType)
	{
		return toValue(DataField.toValue(body), contentType);
	}

	@Contract("null, null -> null; !null, !null -> !null; null, !null -> fail; !null, null -> fail")
	public static Value toValue(final InputStream body, final String contentType)
	{
		return toValue(DataField.toValue(body), contentType);
	}

	@Contract("null, null -> null; !null, !null -> !null; null, !null -> fail; !null, null -> fail")
	public static Value toValue(final Path body, final String contentType)
	{
		return toValue(DataField.toValue(body), contentType);
	}

	@Contract("null, null -> null; !null, !null -> !null; null, !null -> fail; !null, null -> fail")
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
			values.add(SetValue.map(this.lastModified, Clock.newDate()));
			values.add(SetValue.map(this.body, body));

			return SetValueUtil.toArray(values);
		}
		else
		{
			if(!optional)
				throw MandatoryViolationException.create(this, exceptionItem);

			final ArrayList<SetValue<?>> values = new ArrayList<>(4);
			final FunctionField<?> contentTypeField = this.contentType.field;
			if(contentTypeField!=null)
				values.add(SetValue.map(contentTypeField, null));
			values.add(SetValue.map(this.lastModified, null));
			values.add(SetValue.map(this.body, null));

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

		if(isBodySmall)
		{
			final byte[] body = getBody(item);

			commit();

			MediaUtil.send(contentType, body, response);
		}
		else
		{
			final Path body = Files.createTempFile(
					Media.class.getName() + '#' + getID() + '#' + item.getCopeID(), "");
			getBody(item, body);

			commit();

			MediaUtil.send(contentType, body, response);
			Files.delete(body);
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
		return contentTypeIn(contentType);
	}

	public Condition contentTypeIn(final String... contentTypes)
	{
		return this.contentType.in(contentTypes, this.lastModified);
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
}

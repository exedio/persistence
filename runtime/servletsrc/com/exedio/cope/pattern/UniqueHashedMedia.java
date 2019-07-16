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

import static com.exedio.cope.util.CharSet.HEX_LOWER;

import com.exedio.cope.Condition;
import com.exedio.cope.CopyMapper;
import com.exedio.cope.Copyable;
import com.exedio.cope.DataField;
import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.MysqlExtendedVarchar;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.UnsupportedQueryException;
import com.exedio.cope.Vault;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.pattern.Media.Value;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestFactory;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Pattern which wraps a {@link Media} and applies a hash string. This allows
 * uniqueness.
 * @author knoefel
 */
@WrapFeature
public final class UniqueHashedMedia extends Pattern implements Settable<Value>, Copyable
{
	private static final long serialVersionUID = 1l;
	private static final SetValue<?>[] EMPTY_SET_VALUE_ARRAY = new SetValue<?>[0];

	private final Media media;
	private final StringField hash;
	private final MessageDigestFactory messageDigestAlgorithm;
	private final HashConstraint hashConstraint;


	/**
	 * Creates a new HashedMedia on the given media template using default values
	 * for other properties.
	 */
	public UniqueHashedMedia(final Media mediaTemplate)
	{
		this(mediaTemplate, "SHA-512");
	}

	/**
	 * Creates a new HashedMedia on the given media template.
	 *
	 * Note: given media template must be mandatory
	 */
	public UniqueHashedMedia(final Media mediaTemplate, final String messageDigestAlgorithm)
	{
		this(mediaTemplate, new MessageDigestFactory(messageDigestAlgorithm));
	}

	private UniqueHashedMedia(
			final Media mediaTemplate,
			final MessageDigestFactory messageDigestAlgorithm)
	{
		if(!mediaTemplate.isMandatory())
			throw new IllegalArgumentException("mediaTemplate must be mandatory");

		final int digestStringLength = messageDigestAlgorithm.getLengthHex();
		this.messageDigestAlgorithm = messageDigestAlgorithm;
		//noinspection ThisEscapedInObjectConstruction
		this.media = addSourceFeature(
				mediaTemplate.toFinal(),
				"media",
				new MediaPathFeatureAnnotationProxy(this, true));
		//noinspection AnonymousInnerClassMayBeStatic
		this.hash = addSourceFeature(
				new StringField().toFinal().unique().lengthExact(digestStringLength).charSet(HEX_LOWER),
				"hash",
				digestStringLength<=32
				? ComputedElement.get()
				: CustomAnnotatedElement2.create(
						new Computed() { @Override public Class<? extends Annotation> annotationType() { return Computed.class; } },
						new MysqlExtendedVarchar() { @Override public Class<? extends Annotation> annotationType() { return MysqlExtendedVarchar.class; } }));
		this.hashConstraint = addSourceFeature(
				new HashConstraint(hash, messageDigestAlgorithm.getAlgorithm(), media.getBody()),
				"hashConstraint");
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();
		if(type.isAbstract())
			throw new IllegalArgumentException(
					UniqueHashedMedia.class.getSimpleName() + ' ' + getID() +
					" cannot create instances of type " + getType() +
					", because it is abstract.");
	}

	@Override
	public void afterModelCreated()
	{
		super.afterModelCreated();

		final HashSet<Field<?>> ownFields = new HashSet<>();
		collectFields(ownFields, this, 20);
		//System.out.println(ownFields);

		for(final Field<?> field : getType().getFields())
		{
			if(field.isMandatory() &&
				(!(field instanceof FunctionField<?>) || !((FunctionField<?>)field).hasDefault()) &&
				!ownFields.contains(field))
				throw new IllegalArgumentException(
						UniqueHashedMedia.class.getSimpleName() + ' ' + getID() +
						" cannot create instances of type " + getType() +
						", because " + field + " is mandatory and has no default.");
		}
	}

	private static void collectFields(final HashSet<Field<?>> result, final Pattern pattern, final int recursionBreaker)
	{
		if(recursionBreaker<0)
			throw new AssertionError();

		for(final Feature feature : pattern.getSourceFeatures())
		{
			if(feature instanceof Field<?>)
				result.add((Field<?>)feature);
			else if(feature instanceof Pattern)
				collectFields(result, (Pattern)feature, recursionBreaker-1);
		}
	}

	@Wrap(order = 10, name = "getURL", doc="Returns a URL the content of {0} is available under.")
	@Nonnull
	public String getURL(@Nonnull final Item item)
	{
		//noinspection ConstantConditions OK: media is mandatory
		return media.getURL(item);
	}

	@Wrap(order = 20, name = "getLocator", doc="Returns a Locator the content of {0} is available under.")
	@Nonnull
	public MediaPath.Locator getLocator(@Nonnull final Item item)
	{
		//noinspection ConstantConditions OK: media is mandatory
		return media.getLocator(item);
	}

	/**
	 * Returns the content type of this media.
	 * Does never return null.
	 */
	@Wrap(order = 30, name = "getContentType", doc = "Returns the content type of the media {0}.")
	@Nonnull
	public String getContentType(@Nonnull final Item item)
	{
		return media.getContentType(item);
	}

	/**
	 * Returns the date of the last modification of the media.
	 * Does never return null.
	 */
	@Wrap(order = 40, name = "getLastModified", doc = "Returns the last modification date of media {0}.")
	@Nonnull
	public Date getLastModified(@Nonnull final Item item)
	{
		return media.getLastModified(item);
	}

	/**
	 * Returns the length of the body of the media.
	 */
	@Wrap(order = 50, name = "getLength", doc = "Returns the body length of the media {0}.")
	public long getLength(@Nonnull final Item item)
	{
		return media.getLength(item);
	}

	/**
	 * Returns the body of the media.
	 * Does never return null.
	 */
	@Wrap(order = 60, name = "getBody", doc = "Returns the body of the media {0}.")
	@Nonnull
	public byte[] getBody(@Nonnull final Item item)
	{
		return media.getBody(item);
	}

	/**
	 * Returns the hash of the body of this media.
	 * Does never return null.
	 */
	@Wrap(order = 70, name = "getHash", doc = "Returns the hash of the media body {0}.")
	@Nonnull
	public String getHash(@Nonnull final Item item)
	{
		return hash.get(item);
	}

	/**
	 * Finds an item by the hash value of it's unique hashed media.
	 *
	 * @return null if there is no matching item.
	 * @throws NullPointerException
	 *            if value is null.
	 */
	@Wrap(
			order = 100,
			name = "forHash",
			doc = "Finds a {2} by it''s hash.",
			docReturn = "null if there is no matching item.")
	@Nullable
	public <P extends Item> P forHash(@Nonnull final Class<P> typeClass, @Nonnull @Parameter("{1}Hash") final String hash)
	{
		return this.hash.searchUnique(typeClass, hash);
	}

	/**
	 * Returns the item containing given media value or creates a new one.
	 *
	 * @return null if and only if value is null.
	 * @throws IOException
	 *            if reading mediaValue throws an IOException.
	 * @throws IllegalArgumentException
	 *            if given mediaValue has a content type which is not equal to the content type of an already stored value
	 * @throws IllegalContentTypeException
	 *            if given mediaValue has a content type which is not valid for the implicit Media
	 */
	@Wrap(
			order = 200,
			name = "getOrCreate",
			doc = "Returns a {2} containing given media value or creates a new one.",
			thrown = @Wrap.Thrown(value = IOException.class, doc = "if reading <tt>value</tt> throws an IOException."))
	@Nullable
	public <P extends Item> P getOrCreate(
			@Nonnull final Class<P> typeClass,
			@Nullable @Parameter(doc = "shall be equal to field {0}.") final Value value)
		throws IOException, IllegalArgumentException, IllegalContentTypeException
	{
		final Type<P> type =
				requireParentClass(typeClass, "typeClass");
		if(value==null)
			return null;
		final ValueWithHash valueWithHash = createValueWithHash(value);
		final P existingItem = forHash(typeClass, valueWithHash.hash);
		if (existingItem != null)
		{
			final String existingContentType = getContentType(existingItem);
			if (!existingContentType.equals(value.getContentType()))
				throw new IllegalArgumentException("Given content type '"+value.getContentType()+"' does not match content type of already stored value '"+existingContentType+"' for "+existingItem);
			return existingItem;
		}
		else
		{
			// throws an IllegalContentTypeException
			return type.newItem(
					media.map(valueWithHash.media),
					hash .map(valueWithHash.hash));
		}
	}

	public UniqueConstraint getImplicitUniqueConstraint()
	{
		return hash.getImplicitUniqueConstraint();
	}

	public StringField getHash()
	{
		return hash;
	}

	public String getMessageDigestAlgorithm()
	{
		return messageDigestAlgorithm.getAlgorithm();
	}

	public Media getMedia()
	{
		return media;
	}

	public HashConstraint getHashConstraint()
	{
		return hashConstraint;
	}

	@Override
	public Feature copy(final CopyMapper mapper)
	{
		return new UniqueHashedMedia(media.copy(mapper), messageDigestAlgorithm);
	}

	@Override
	public SetValue<?>[] execute(final Value value, final Item exceptionItem)
	{
		final Value media;
		final String hash;
		if(value!=null)
		{
			final ValueWithHash valueWithHash;
			try
			{
				valueWithHash = createValueWithHash(value);
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
			media = valueWithHash.media;
			hash  = valueWithHash.hash;
		}
		else
		{
			if(isMandatory())
				throw MandatoryViolationException.create(this, exceptionItem);
			media = null;
			hash  = null;
		}
		final List<SetValue<?>> setValues = new ArrayList<>(Arrays.asList(this.media.execute(media, exceptionItem)));
		setValues.add(this.hash.map(hash));
		return setValues.toArray(EMPTY_SET_VALUE_ARRAY);
	}


	@Override
	public boolean isFinal()
	{
		return media.isFinal();
	}

	@Override
	public boolean isMandatory()
	{
		return media.isMandatory();
	}

	@Override
	public java.lang.reflect.Type getInitialType()
	{
		return Value.class;
	}

	@Override
	public boolean isInitial()
	{
		return media.isInitial();
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return media.getInitialExceptions();
	}

	/**
	 * Creates an Value object for the given media value which can be used as value for this HashedMedia.
	 */
	private ValueWithHash createValueWithHash(Value mediaValue) throws IOException
	{
		DataField.Value dataValue = mediaValue.getBody();
		final MessageDigest messageDigest = messageDigestAlgorithm.newInstance();
		// calculate the hash

		dataValue = dataValue.update(messageDigest);
		//  original DataField.Value is exhausted so we have to create a new Media.Value
		mediaValue = Media.toValue(dataValue, mediaValue.getContentType());

		final byte[] hash = messageDigest.digest();

		final String hashAsHex = Hex.encodeLower(hash);

		return new ValueWithHash(mediaValue, hashAsHex);
	}


	/**
	 * Container for media value + hash
	 */
	private static final class ValueWithHash
	{
		final Value media;
		final String hash;

		ValueWithHash(
				final Value media,
				final String hash)
		{
			this.media = media;
			this.hash = hash;
		}
	}

	/**
	 * The result may cause an {@link UnsupportedQueryException} when used,
	 * if the field is stored in a {@link Vault vault},
	 * or the {@link #getMessageDigestAlgorithm() algorithm} is not supported by the database.
	 */
	@Nonnull
	public Condition hashMatchesIfSupported()
	{
		return hashConstraint.hashMatchesIfSupported();
	}

	/**
	 * The result may cause an {@link UnsupportedQueryException} when used,
	 * if the field is stored in a {@link Vault vault},
	 * or the {@link #getMessageDigestAlgorithm() algorithm} is not supported by the database.
	 */
	@Nonnull
	public Condition hashDoesNotMatchIfSupported()
	{
		return hashConstraint.hashDoesNotMatchIfSupported();
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #hashMatchesIfSupported()} instead.
	 */
	@Deprecated
	@Nonnull
	public Condition hashMatches()
	{
		return hashMatchesIfSupported();
	}

	/**
	 * @deprecated Use {@link #hashDoesNotMatchIfSupported()} instead.
	 */
	@Deprecated
	@Nonnull
	public Condition hashDoesNotMatch()
	{
		return hashDoesNotMatchIfSupported();
	}
}

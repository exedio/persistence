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

import com.exedio.cope.CopyMapper;
import com.exedio.cope.Copyable;
import com.exedio.cope.DataField;
import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.pattern.Media.Value;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestUtil;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pattern which wraps a {@link Media} and applies a hash string. This allows
 * uniqueness.
 *
 * TODO implement a check if source type has no other initial (non-default &amp; mandatory) features beside this pattern and its source features, this can be done when a something like a postMount() method is possible
 *
 * @author knoefel
 */
public final class UniqueHashedMedia extends Pattern implements Settable<Value>, Copyable
{
	private static final long serialVersionUID = 1l;

	private final Media media;
	private final StringField hash;
	private final String messageDigestAlgorithm;


	/**
	 * Creates a new HashedMedia on the given media template using default values
	 * for other properties.
	 */
	public UniqueHashedMedia(final Media mediaTemplate)
	{
		this(mediaTemplate, "MD5");
	}

	/**
	 * Creates a new HashedMedia on the given media template.
	 *
	 * Note: given media template must be mandatory
	 */
	private UniqueHashedMedia(final Media mediaTemplate, final String messageDigestAlgorithm)
	{
		// will never be null as MessageDigestUtil return non-null or throws IllegalArgumentException
		final MessageDigest messageDigest = MessageDigestUtil.getInstance(messageDigestAlgorithm);
		final int digestLength = messageDigest.getDigestLength(); // digest length in bytes
		if (digestLength == 0)
			throw new IllegalArgumentException("MessageDigest "+messageDigestAlgorithm+" does no specify digest length, can't create field for hash.");

		if (!mediaTemplate.isMandatory())
			throw new IllegalArgumentException("Media template must be mandatory");

		final int digestStringLength = digestLength * 2; // 1 byte is 2 hexadecimal chars
		this.messageDigestAlgorithm = messageDigestAlgorithm;
		this.media = mediaTemplate.toFinal();
		addSource(this.media, "media", new MediaPathAnnotationProxy(this, true));
		final StringField hashField = new StringField().
				toFinal().unique().
				lengthExact(digestStringLength).
				charSet(CharSet.HEX_LOWER);
		hash = hashField;
		addSource(hash, "hash", ComputedElement.get());
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
	public String getURL(final Item item)
	{
		return media.getURL(item);
	}

	@Wrap(order = 20, name = "getLocator", doc="Returns a Locator the content of {0} is available under.")
	public MediaPath.Locator getLocator(final Item item)
	{
		return media.getLocator(item);
	}

	/**
	 * Returns the content type of this media. Returns null, if this media is
	 * null.
	 */
	@Wrap(order = 30, name = "getContentType", doc = "Returns the content type of the media {0}.")
	public String getContentType(final Item item)
	{
		return media.getContentType(item);
	}

	/**
	 * Returns the date of the last modification of the media. Returns null, if
	 * the media is null.
	 */
	@Wrap(order = 40, name = "getLastModified", doc = "Returns the last modification date of media {0}.")
	public Date getLastModified(final Item item)
	{
		return media.getLastModified(item);
	}

	/**
	 * Returns the length of the body of the media. Returns -1, if the media is
	 * null.
	 */
	@Wrap(order = 50, name = "getLength", doc = "Returns the body length of the media {0}.")
	public long getLength(final Item item)
	{
		return media.getLength(item);
	}

	/**
	 * Returns the body of the media. Returns null, if the media is null.
	 */
	@Wrap(order = 60, name = "getBody", doc = "Returns the body of the media {0}.")
	public byte[] getBody(final Item item)
	{
		return media.getBody(item);
	}

	/**
	 * Returns the hash of the body of this media. Returns null, if this media is
	 * null.
	 */
	@Wrap(order = 70, name = "getHash", doc = "Returns the hash of the media body {0}.")
	public String getHash(final Item item)
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
	public <P extends Item> P forHash(final Class<P> typeClass, @Parameter("{1}Hash") final String hash)
	{
		return this.hash.searchUnique(typeClass, hash);
	}

	/**
	 * Returns the item containing given media value or creates a new one.
	 *
	 * @return null if any only if value is null.
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
	public <P extends Item> P getOrCreate(
			final Class<P> typeClass,
			@Parameter(doc = "shall be equal to field {0}.") final Value value)
		throws IOException, IllegalArgumentException, IllegalContentTypeException
	{
		if(value==null)
			return null;
		final ValueWithHash valueWithHash = createValueWithHash(value);
		final P existingItem = forHash(typeClass, valueWithHash.hashValue);
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
			return getType().as(typeClass).newItem(
					this.media.map(valueWithHash.mediaValue),
					this.hash .map(valueWithHash.hashValue));
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
		return messageDigestAlgorithm;
	}

	public Media getMedia()
	{
		return media;
	}

	@Override
	public Feature copy(final CopyMapper mapper)
	{
		return new UniqueHashedMedia(media.copy(mapper), messageDigestAlgorithm);
	}

	@Override
	public SetValue<Value> map(final Value value)
	{
		return SetValue.map(this, value);
	}

	@Override
	public SetValue<?>[] execute(final Value value, final Item exceptionItem)
	{
		final Value mediaValue;
		final String hashValue;
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
			mediaValue = valueWithHash.mediaValue;
			hashValue  = valueWithHash.hashValue;
		}
		else
		{
			if(isMandatory())
				throw MandatoryViolationException.create(this, exceptionItem);
			mediaValue = null;
			hashValue  = null;
		}
		final List<SetValue<?>> setValues = new ArrayList<>(Arrays.asList(this.media.execute(mediaValue, exceptionItem)));
		setValues.add(this.hash.map(hashValue));
		return setValues.toArray(new SetValue<?>[setValues.size()]);
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
		final MessageDigest messageDigest = MessageDigestUtil.getInstance(messageDigestAlgorithm);
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
		final Value mediaValue;
		final String hashValue;

		ValueWithHash(
				final Value mediaValue,
				final String hashValue)
		{
			this.mediaValue = mediaValue;
			this.hashValue = hashValue;
		}
	}
}

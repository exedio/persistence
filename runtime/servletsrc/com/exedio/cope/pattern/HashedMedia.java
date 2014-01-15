/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.CopyMapper;
import com.exedio.cope.Copyable;
import com.exedio.cope.DataField;
import com.exedio.cope.DataLengthViolationException;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Pattern which wraps a {@link Media} and applies a hash string. This allows
 * uniqueness.
 *
 * @author knoefel
 */
public class HashedMedia extends MediaPath implements Settable<Media.Value>, Copyable
{
	private static final long serialVersionUID = 1l;

	private final Media media;
	private final StringField hash;
	private final String messageDigestAlgorithm;

	private final boolean unique;

	private final CheckConstraint unison;

	/**
	 * Creates a new HashedMedia on the given media template using default values
	 * for other properties.
	 */
	public HashedMedia(final Media mediaTemplate)
	{
		this(mediaTemplate, "MD5", false);
	}

	/**
	 * Creates a new HashedMedia on the given media template.
	 *
	 * Note: the aspects 'mandatory', 'final' are taken from the given media.
	 */
	private HashedMedia(final Media mediaTemplate, final String messageDigestAlgorithm, final boolean unique)
	{
		// will never be null as MessageDigestUtil return non-null or throws IllegalArgumentException
		final MessageDigest messageDigest = MessageDigestUtil.getInstance(messageDigestAlgorithm);
		final int digestLength = messageDigest.getDigestLength(); // digest length in bytes
		if (digestLength == 0)
			throw new IllegalArgumentException("MessageDigest "+messageDigestAlgorithm+" does no specify digest length, can't create field for hash.");
		final int digestStringLength = digestLength * 2; // 1 byte is 2 hexadecimal chars
		this.unique = unique;
		this.messageDigestAlgorithm = messageDigestAlgorithm;
		this.media = mediaTemplate.copy(new CopyMapper());
		addSource(this.media, "media", ComputedElement.get());
		StringField hashField = new StringField().lengthExact(digestStringLength);
		if(mediaTemplate.isFinal())
			hashField = hashField.toFinal();
		if(!mediaTemplate.isMandatory())
			hashField = hashField.optional();
		if(unique)
			hashField = hashField.unique();
		hash = hashField;
		addSource(hash, "hash", ComputedElement.get());
		if(!media.isMandatory())
		{
			final Condition mediaIsNull = media.isNull();
			final Condition mediaIsNotNull = media.isNotNull();
			final Condition check = Cope.and(mediaIsNull, hashField.isNull()).or(Cope.and(mediaIsNotNull, hashField.isNotNull()));
			addSource(this.unison = new CheckConstraint(check), "unison");
		}
		else
		{
			this.unison = null;
		}
	}

	@Wrap(order = 10, doc = "Returns whether media {0} is null.", hide = MandatoryGetter.class)
	public boolean isNull(final Item item)
	{
		return media.isNull(item);
	}

	private static final class MandatoryGetter implements BooleanGetter<HashedMedia>
	{
		@Override
		public boolean get(final HashedMedia feature)
		{
			return feature.isMandatory();
		}
	}

	/**
	 * Returns the content type of this media. Returns null, if this media is
	 * null.
	 */
	@Override
	public String getContentType(final Item item)
	{
		return media.getContentType(item);
	}

	/**
	 * Returns the date of the last modification of the media. Returns null, if
	 * the media is null.
	 */
	@Wrap(order = 20, doc = "Returns the last modification date of media {0}.")
	@Override
	public Date getLastModified(final Item item)
	{
		return media.getLastModified(item);
	}

	/**
	 * Returns the length of the body of the media. Returns -1, if the media is
	 * null.
	 */
	@Wrap(order = 30, doc = "Returns the body length of the media {0}.")
	public long getLength(final Item item)
	{
		return media.getLength(item);
	}

	/**
	 * Returns the body of the media. Returns null, if the media is null.
	 */
	@Wrap(order = 40, doc = "Returns the body of the media {0}.")
	public byte[] getBody(final Item item)
	{
		return media.getBody(item);
	}

	/**
	 * Returns the hash of the body of this media. Returns null, if this media is
	 * null.
	 */
	@Wrap(order = 50, doc = "Returns the hash of the media body {0}.")
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
	@Wrap(order = 100, name = "for{0}", doc = "Finds a {2} by it''s {0}.", docReturn = "null if there is no matching item.", hide = NonUniqueGetter.class)
	public final <P extends Item> P searchUnique(final Class<P> typeClass, @Parameter(doc = "shall be equal to field {0}.") final String value)
	{
		return hash.searchUnique(typeClass, value);
	}

	static final class NonUniqueGetter implements BooleanGetter<HashedMedia>
	{
		@Override
		public boolean get(final HashedMedia feature)
		{
			return feature.getImplicitUniqueConstraint() == null;
		}
	}

	/**
	 * Sets the contents of the media.
	 *
	 * @param value
	 *           give null to make media null.
	 * @throws MandatoryViolationException
	 *            if body is null and media is {@link Media#isMandatory()
	 *            mandatory}.
	 * @throws DataLengthViolationException
	 *            if body is longer than {@link Media#getMaximumLength()}
	 * @throws IOException
	 *            if reading value throws an IOException.
	 */
	@Wrap(order = 110, doc = "Sets the content of media {0}.", hide = FinalSettableGetter.class, thrown = @Wrap.Thrown(value = IOException.class, doc = "if accessing <tt>body</tt> throws an IOException."))
	public void set(final Item item, final Media.Value value) throws DataLengthViolationException, IOException
	{
		if(value == null && media.isMandatory())
			throw MandatoryViolationException.create(this, item);

		item.set(execute(value, item));
	}

	/**
	 * Returns the unique constraint when the HashedMedia is unique.
	 *
	 * Does return null, if there is no such unique constraint.
	 */
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

	public DataField getBody()
	{
		return media.getBody();
	}

	public FunctionField<?> getContentType()
	{
		return media.getContentType();
	}

	public DateField getLastModified()
	{
		return media.getLastModified();
	}

	public CheckConstraint getUnison()
	{
		return unison;
	}

	@Override
	public void doGetAndCommit(final HttpServletRequest request, final HttpServletResponse response, final Item item) throws IOException, NotFound
	{
		media.doGetAndCommit(request, response, item);
	}

	@Override
	public Condition isNull()
	{
		return media.isNull();
	}

	@Override
	public Condition isNull(final Join join)
	{
		return media.isNull(join);
	}

	@Override
	public Condition isNotNull()
	{
		return media.isNotNull();
	}

	@Override
	public Condition isNotNull(final Join join)
	{
		return media.isNotNull(join);
	}

	@Override
	public Feature copy(final CopyMapper mapper)
	{
		return new HashedMedia(media.copy(mapper), messageDigestAlgorithm, unique);
	}

	/**
	 * Creates a new HashedMedia that is unique.
	 */
	public HashedMedia unique()
	{
		return new HashedMedia(media, messageDigestAlgorithm, true);
	}

	/**
	 * Creates a new HashedMedia with the given message digest algorithm
	 */
	public HashedMedia algorithm(final String messageDigestAlgorithm)
	{
		return new HashedMedia(media, messageDigestAlgorithm, unique);
	}

	@Override
	public SetValue<Media.Value> map(final Media.Value value)
	{
		return SetValue.map(this, value);
	}

	@Override
	public SetValue<?>[] execute(Media.Value value, final Item exceptionItem)
	{
		if(value != null)
		{
			DataField.Value dataValue = value.getBody();
			final MessageDigest messageDigest = MessageDigestUtil.getInstance(messageDigestAlgorithm);
			// calculate the hash
			try
			{
				dataValue = dataValue.update(messageDigest);
				//  original DataField.Value is exhausted so we have to create a new Media.Value
				value = Media.toValue(dataValue, value.getContentType());
			}
			catch(final IOException e)
			{
				throw new RuntimeException(toString(), e);
			}
			final byte[] hash = messageDigest.digest();

			final String hashAsHex = Hex.encodeUpper(hash);
			final List<SetValue<?>> setValues = new ArrayList<SetValue<?>>(Arrays.asList(this.media.execute(value, exceptionItem)));

			setValues.add(this.hash.map(hashAsHex));
			return setValues.toArray(new SetValue[setValues.size()]);
		}
		else
		{
			if(isMandatory())
				throw MandatoryViolationException.create(this, exceptionItem);
			final List<SetValue<?>> setValues = new ArrayList<SetValue<?>>(Arrays.asList(this.media.execute(null, exceptionItem)));
			setValues.add(this.hash.map(null));
			return setValues.toArray(new SetValue[setValues.size()]);
		}
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
	public boolean isInitial()
	{
		return media.isInitial();
	}

	public boolean isUnique()
	{
		return unique;
	}

	@Deprecated
	public Type getInitialType()
	{
		return Media.Value.class;
	}

	@Override
	public Set<Class<? extends Throwable>> getInitialExceptions()
	{
		return media.getInitialExceptions();
	}
}

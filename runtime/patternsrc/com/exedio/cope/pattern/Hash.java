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

import static com.exedio.cope.pattern.AlgorithmAdapter.wrap;
import static com.exedio.cope.util.CharsetName.UTF8;

import java.security.SecureRandom;
import java.util.Set;

import com.exedio.cope.Condition;
import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringCharSetViolationException;
import com.exedio.cope.StringField;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.StringGetter;
import com.exedio.cope.instrument.ThrownGetter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.NonNegativeRandom;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class Hash extends Pattern implements HashInterface
{
	private static final DefaultPlainTextValidator DEFAULT_VALIDATOR = new DefaultPlainTextValidator();
	private static final long serialVersionUID = 1l;

	private final StringField storage;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final HashAlgorithm algorithm;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final PlainTextValidator validator;

	public Hash(final StringField storage, final Algorithm algorithm, final String encoding)
	{
		this(storage, wrap(algorithm, encoding), DEFAULT_VALIDATOR);
	}

	public Hash(final StringField storage, final Algorithm algorithm)
	{
		this(storage, wrap(algorithm, UTF8), DEFAULT_VALIDATOR);
	}

	public Hash(final Algorithm algorithm, final String encoding)
	{
		this(newStorage(wrap(algorithm, encoding)), wrap(algorithm, encoding), DEFAULT_VALIDATOR);
	}

	public Hash(final Algorithm algorithm)
	{
		this(newStorage(wrap(algorithm, UTF8)), algorithm);
	}

	public Hash(final HashAlgorithm algorithm)
	{
		this(newStorage(algorithm), algorithm, DEFAULT_VALIDATOR);
	}

	public Hash(final StringField storage, final HashAlgorithm algorithm)
	{
		this(storage, algorithm, DEFAULT_VALIDATOR);
	}

	public Hash validate(final PlainTextValidator validator)
	{
		return new Hash(this.storage.copy(), this.algorithm, validator);
	}


	private Hash(
			final StringField storage,
			final HashAlgorithm algorithm,
			final PlainTextValidator validator)
	{
		if(storage==null)
			throw new NullPointerException("storage");
		if(algorithm==null)
			throw new NullPointerException("algorithm");
		if (validator==null)
			throw new NullPointerException("validator");

		this.algorithm = algorithm;
		final String algorithmID = algorithm.getID();
		if(algorithmID.isEmpty())
			throw new IllegalArgumentException("algorithmID must not be empty");

		addSource(this.storage = storage, algorithmID, ComputedElement.get());

		this.validator = validator;
	}

	public final StringField getStorage()
	{
		return storage;
	}

	public final HashAlgorithm getAlgorithm2()
	{
		return algorithm;
	}

	public final String getAlgorithmID()
	{
		return algorithm.getID();
	}

	/**
	 * @deprecated
	 * Throws exception if not initialized via {@link Algorithm}.
	 */
	@Deprecated
	public final String getEncoding()
	{
		return AlgorithmAdapter.unwrapEncoding(algorithm);
	}

	public final boolean isInitial()
	{
		return storage.isInitial();
	}

	public final boolean isFinal()
	{
		return storage.isFinal();
	}

	public final boolean isMandatory()
	{
		return storage.isMandatory();
	}

	@Deprecated
	public final Class<?> getInitialType()
	{
		return String.class;
	}

	public final Set<Class<? extends Throwable>> getInitialExceptions()
	{
		final Set<Class<? extends Throwable>> result = storage.getInitialExceptions();
		result.remove(StringLengthViolationException.class);
		result.remove(StringCharSetViolationException.class);
		return result;
	}

	private static StringField newStorage(final HashAlgorithm algorithm)
	{
		return algorithm.constrainStorage(new StringField());
	}

	private String algorithmHash(final String plainText)
	{
		final String result = algorithm.hash(plainText);
		if(result==null)
			throw new NullPointerException();
		return result;
	}

	private boolean algorithmCheck(final String plainText, final String hash)
	{
		if(plainText==null)
			throw new NullPointerException();
		if(hash==null)
			throw new NullPointerException();

		return algorithm.check(plainText, hash);
	}


	public interface Algorithm
	{
		String name();
		int length();

		/**
		 * Returns a hash for the given plain text.
		 * The result is not required to be deterministic -
		 * this means, multiple calls for the same plain text
		 * do not have to return the same hash.
		 * This is especially true for salted hashes.
		 * @param plainText the text to be hashed. Is never null.
		 * @return the hash of plainText. Must never return null.
		 */
		byte[] hash(byte[] plainText);

		/**
		 * Returns whether the given plain text matches the given hash.
		 * @param plainText the text to be hashed. Is never null.
		 * @param hash the hash of plainText. Is never null.
		 * @throws IllegalArgumentException if hash.length!={@link #length()}.
		 */
		boolean check(byte[] plainText, byte[] hash);

		/**
		 * Returns whether this algorithm can consistently check
		 * hash values created by the given algorithm.
		 * @throws NullPointerException if other is null
		 */
		boolean compatibleTo(Algorithm other);
	}

	public final Hash toFinal()
	{
		return new Hash(storage.toFinal(), algorithm, validator);
	}

	public final Hash optional()
	{
		return new Hash(storage.optional(), algorithm, validator);
	}

	@Wrap(order=30,
			doc="Sets a new value for {0}.",
			hide=FinalGetter.class,
			thrownGetter=Thrown.class)
	public final void set(final Item item, final String plainText)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			StringLengthViolationException,
			FinalViolationException
	{
		storage.set(item, hash(plainText, item));
	}

	private static final class FinalGetter implements BooleanGetter<Hash>
	{
		public boolean get(final Hash feature)
		{
			return feature.isFinal();
		}
	}

	private static final class Thrown implements ThrownGetter<Hash>
	{
		public Set<Class<? extends Throwable>> get(final Hash feature)
		{
			return feature.getInitialExceptions();
		}
	}

	@Wrap(order=10,
			doc="Returns whether the given value corresponds to the hash in {0}.")
	public final boolean check(final Item item, final String actualPlainText)
	{
		final String expectedHash = storage.get(item);
		if(actualPlainText!=null)
			return (expectedHash!=null) && algorithmCheck(actualPlainText, expectedHash); // Algorithm#hash(String) must not return null
		else
			return expectedHash==null;
	}

	private transient volatile String hashForBlind = null;

	/**
	 * Wastes (almost) as much cpu cycles, as a call to
	 * {@link #check(Item, String)}  would have needed.
	 * Needed to prevent Timing Attacks.
	 * See http://en.wikipedia.org/wiki/Timing_attack
	 */
	@Wrap(order=20,
			doc={"Wastes (almost) as much cpu cycles, as a call to <tt>check{3}</tt> would have needed.",
					"Needed to prevent Timing Attacks."})
	public final void blind(final String actualPlainText)
	{
		if(actualPlainText!=null)
		{
			if(hashForBlind==null)
				hashForBlind = algorithmHash("1234");

			algorithmCheck(actualPlainText, hashForBlind);
		}
	}

	public final SetValue<String> map(final String value)
	{
		return SetValue.map(this, value);
	}

	public final SetValue<?>[] execute(final String value, final Item exceptionItem) throws InvalidPlainTextException
	{
		final String hash = hash(value, exceptionItem);
		return new SetValue<?>[]{ storage.map(hash) };
	}

	@Wrap(order=40, nameGetter=GetNameGetter.class, doc="Returns the encoded hash value for hash {0}.")
	public final String getHash(final Item item)
	{
		return storage.get(item);
	}

	private static final class GetNameGetter implements StringGetter<Hash>
	{
		public String get(final Hash feature)
		{
			return "get{0}" + feature.getAlgorithmID();
		}
	}

	@Wrap(order=50,
			nameGetter=SetNameGetter.class,
			doc="Sets the encoded hash value for hash {0}.",
			hide=FinalGetter.class,
			thrownGetter=Thrown.class)
	public final void setHash(final Item item, final String hash)
	{
		storage.set(item, hash);
	}

	private static final class SetNameGetter implements StringGetter<Hash>
	{
		public String get(final Hash feature)
		{
			return "set{0}" + feature.getAlgorithmID();
		}
	}

	public final String hash(final String plainText)
	{
		return hash(plainText, null);
	}

	private String hash(final String plainText, final Item exceptionItem)
	{
		if(plainText==null)
			return null;

		validator.validate(plainText, exceptionItem, this);

		final String result = algorithmHash(plainText);
		if(result==null)
			throw new NullPointerException();
		return result;
	}

	public final Condition isNull()
	{
		return storage.isNull();
	}

	public final Condition isNull(final Join join)
	{
		return storage.bind(join).isNull();
	}

	public final Condition isNotNull()
	{
		return storage.isNotNull();
	}

	public final Condition isNotNull(final Join join)
	{
		return storage.bind(join).isNotNull();
	}

	public String newRandomPassword(final SecureRandom random)
	{
		return validator.newRandomPlainText(random);
	}

	/** Validate plain text for potential limits, to be specified in sub classes */
	public abstract static class PlainTextValidator
	{
		abstract protected void validate(String plainText, Item exceptionItem, Hash hash) throws
			InvalidPlainTextException;

		/**
		 * Creates a plain text variant to redeem an existing password (password forgotten).
		 * The result MUST be valid according to {@link #validate(String, Item, Hash)}.
		 */
		abstract protected String newRandomPlainText(SecureRandom secureRandom);
	}

	/** Default implementation  */
	static final class DefaultPlainTextValidator extends PlainTextValidator
	{
		@Override protected void validate(final String plainText, final Item exceptionItem, final Hash hash) throws
			InvalidPlainTextException
		{
			if(plainText==null)
				throw new NullPointerException();
		}

		@Override protected String newRandomPlainText(final SecureRandom secureRandom)
		{
			return Long.toString(NonNegativeRandom.nextLong(secureRandom), 36);
		}
	}

	/**
	 * A plain text is either too short, too long or doesn't match the format requirement */
	public static final class InvalidPlainTextException extends ConstraintViolationException
	{
		private static final long serialVersionUID = 1l;
		private final String plainText;
		private final String message;
		private final Hash feature;

		public InvalidPlainTextException(
				final String message,
				final String plainText,
				final Item item,
				final Hash feature)
		{
			super(item, /*cause*/ null);
			this.message = message;
			this.plainText = plainText;
			this.feature = feature;
		}

		@Override public Hash getFeature()
		{
			return feature;
		}

		@Override public String getMessage(final boolean withFeature)
		{
			String message = this.message;
			if (withFeature)
				message += " for " + feature;
			return message;
		}

		public String getPlainText()
		{
			return plainText;
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getAlgorithmID()} instead
	 */
	@Deprecated
	public final String getAlgorithmName()
	{
		return getAlgorithmID();
	}

	/**
	 * @deprecated
	 * Use {@link #getAlgorithm2()} instead.
	 * Throws exception if not initialized via {@link Algorithm}.
	 */
	@Deprecated
	public final Algorithm getAlgorithm()
	{
		return AlgorithmAdapter.unwrap(algorithm, storage);
	}

	/**
	 * @deprecated Use {@link #blind(String)} instead.
	 */
	@Deprecated
	public final void blind(@SuppressWarnings("unused") final Class<? extends Item> parentClass, final String actualPlainText)
	{
		blind(actualPlainText);
	}
}

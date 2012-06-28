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

import com.exedio.cope.*;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.StringGetter;
import com.exedio.cope.instrument.ThrownGetter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Set;

public class Hash extends Pattern implements Settable<String>
{
	private static final long serialVersionUID = 1l;

	private final StringField storage;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Algorithm algorithm;
	private final String encoding;
	private final PlainTextValidator validator;

	public Hash(final StringField storage, final Algorithm algorithm, final String encoding, PlainTextValidator validator)
	{
		if(storage==null)
			throw new NullPointerException("storage");
		if(algorithm==null)
			throw new NullPointerException("algorithm");
		if(encoding==null)
			throw new NullPointerException("encoding");
		if (validator==null)
			throw new NullPointerException("validator");

		this.algorithm = algorithm;
		final String algorithmName = algorithm.name();
		if(algorithmName.isEmpty())
			throw new IllegalArgumentException("algorithmName must not be empty");

		addSource(this.storage = storage, algorithmName, ComputedElement.get());

		this.encoding = encoding;
		try
		{
			encode("test");
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new IllegalArgumentException(e);
		}

		this.validator = validator;
	}

	public Hash(final StringField storage, final Algorithm algorithm)
	{
		this(storage, algorithm, "utf8", new DefaultPlainTextValidator());
	}

	public Hash(final Algorithm algorithm, final String encoding)
	{
		this(newStorage(algorithm), algorithm, encoding, new DefaultPlainTextValidator());
	}

	public Hash(final Algorithm algorithm)
	{
		this(newStorage(algorithm), algorithm);
	}

	public Hash(MessageDigestAlgorithm algorithm, PlainTextValidator validator)
	{
		this(newStorage(algorithm), algorithm, "utf8", validator);
	}

	public final StringField getStorage()
	{
		return storage;
	}

	public final Algorithm getAlgorithm()
	{
		return algorithm;
	}

	public final String getAlgorithmName()
	{
		return algorithm.name();
	}

	public final String getEncoding()
	{
		return encoding;
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

	private static StringField newStorage(final Algorithm algorithm)
	{
		final StringField result =
			new StringField().
				charSet(CharSet.HEX_LOWER).
				lengthExact(2 * algorithm.length()); // factor two is because hex encoding needs two characters per byte
		return result;
	}

	private String algorithmHash(final String plainText) throws IllegalArgumentException
	{
		validator.validate(plainText);

		try
		{
			final byte[] resultBytes = algorithm.hash(encode(plainText));
			if(resultBytes==null)
				throw new NullPointerException();
			return Hex.encodeLower(resultBytes);
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new RuntimeException(encoding, e);
		}
	}

	private boolean algorithmCheck(final String plainText, final String hash)
	{
		if(plainText==null)
			throw new NullPointerException();
		if(hash==null)
			throw new NullPointerException();

		try
		{
			return algorithm.check(encode(plainText), Hex.decodeLower(hash));
		}
		catch(final UnsupportedEncodingException e)
		{
			throw new RuntimeException(encoding, e);
		}
	}

	private byte[] encode(final String s) throws UnsupportedEncodingException
	{
		return s.getBytes(encoding);
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
		return new Hash(storage.toFinal(), algorithm, encoding, validator);
	}

	public final Hash optional()
	{
		return new Hash(storage.optional(), algorithm, encoding, validator);
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
		storage.set(item, hash(plainText));
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
			final char[] expectedHash = new char[storage.getMinimumLength()];
			Arrays.fill(expectedHash, 'a');
			algorithmCheck(actualPlainText, new String(expectedHash));
		}
	}

	public final SetValue<String> map(final String value)
	{
		return SetValue.map(this, value);
	}

	public final SetValue<?>[] execute(final String value, final Item exceptionItem)
	{
		try
		{
			return new SetValue[]{ storage.map(hash(value)) };
		}
		catch (final IllegalArgumentException e)
		{
			throw new ConstraintViolationException(exceptionItem, e)
			{
				@Override public Feature getFeature()
				{
					return Hash.this;
				}
				@Override protected String getMessage(boolean withFeature)
				{
					return e.getMessage();
				}
			};
		}
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
			return "get{0}" + feature.getAlgorithmName();
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
			return "set{0}" + feature.getAlgorithmName();
		}
	}

	public final String hash(final String plainText)
	{
		if(plainText==null)
			return null;
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

	/** Validate plain text for potential limits, to be specified in sub classes */
	protected abstract static class PlainTextValidator
	{
		protected final static SecureRandom secureRandom = new SecureRandom();

		abstract void validate(String plainText) throws IllegalArgumentException;

		/** create a plain text variant to redeem an existing password (password forgotten) */
		abstract String newRandomPlainText();
	}

	/** Default implementation  */
	static final class DefaultPlainTextValidator extends PlainTextValidator
	{
		@Override void validate(String plainText) throws IllegalArgumentException
		{
			if(plainText==null)
				throw new NullPointerException();
		}

		@Override String newRandomPlainText()
		{
			return Long.toString(Math.abs(secureRandom.nextLong()), 36);
		}
	}

	/** Allow only digits as pin, pin len can be specified, reference implementation */
	public final static class DigitPinValidator extends PlainTextValidator
	{
		private static final int MAX_PIN_LEN = Integer.toString(Integer.MAX_VALUE).length();

		private final int pinLen;
		private final int min;
		private final int max;

		public DigitPinValidator(int pinLen)
		{
			if (pinLen<1)
				throw new IllegalArgumentException("pinLen must be greater 0");

			if (pinLen>MAX_PIN_LEN)
				throw new IllegalArgumentException("pinLen exceeds limit of max " + MAX_PIN_LEN);

			this.pinLen = pinLen;
			this.min = (int) Math.pow(10, pinLen -1);
			this.max = (int) Math.pow(10, pinLen); // exclusive
		}

		@Override void validate(String plainText) throws IllegalArgumentException
		{
			if(plainText==null)
				throw new NullPointerException();

			plainText = plainText.trim();

			if (plainText.length() < pinLen)
				throw new IllegalArgumentException("Pin less than " + pinLen + " digits");

			if (plainText.length() > pinLen)
				throw new IllegalArgumentException("Pin greater than " + pinLen + " digits");

			// throws an number format exception the text contains others than digits
			try
			{
				Integer.parseInt(plainText);
			}
			catch (NumberFormatException e)
			{
				// use another message than the default one, is displayed in copaiba
				throw new NumberFormatException("Pin '"+plainText+"' is not a number");
			}
		}

		@Override String newRandomPlainText()
		{
			int l = 0;

			while (l < min)
				l = Math.abs(secureRandom.nextInt(max));

			return  Integer.toString(l);
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #blind(String)} instead.
	 */
	@Deprecated
	public final void blind(@SuppressWarnings("unused") final Class<? extends Item> parentClass, final String actualPlainText)
	{
		blind(actualPlainText);
	}
}

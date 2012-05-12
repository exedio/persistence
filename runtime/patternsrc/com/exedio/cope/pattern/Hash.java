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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Set;

import com.exedio.cope.Condition;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.Item;
import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Pattern;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.StringCharSetViolationException;
import com.exedio.cope.StringField;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.StringGetter;
import com.exedio.cope.instrument.ThrownGetter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Hex;

public class Hash extends Pattern implements Settable<String>
{
	private static final long serialVersionUID = 1l;

	private final StringField storage;
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Algorithm algorithm;
	private final String encoding;

	public Hash(final StringField storage, final Algorithm algorithm, final String encoding)
	{
		if(storage==null)
			throw new NullPointerException("storage");
		if(algorithm==null)
			throw new NullPointerException("algorithm");
		if(encoding==null)
			throw new NullPointerException("encoding");

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
	}

	public Hash(final StringField storage, final Algorithm algorithm)
	{
		this(storage, algorithm, "utf8");
	}

	public Hash(final Algorithm algorithm, final String encoding)
	{
		this(newStorage(algorithm), algorithm, encoding);
	}

	public Hash(final Algorithm algorithm)
	{
		this(newStorage(algorithm), algorithm);
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
	public final Class getInitialType()
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

	private String algorithmHash(final String plainText)
	{
		if(plainText==null)
			throw new NullPointerException();

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
		 * This is especially true for salted hashs.
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
		return new Hash(storage.toFinal(), algorithm, encoding);
	}

	public final Hash optional()
	{
		return new Hash(storage.optional(), algorithm, encoding);
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

	public final SetValue[] execute(final String value, final Item exceptionItem)
	{
		return new SetValue[]{ storage.map(hash(value)) };
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

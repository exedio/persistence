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

import static com.exedio.cope.pattern.AlgorithmAdapter.wrap;
import static com.exedio.cope.pattern.FeatureTimer.timer;
import static com.exedio.cope.util.Check.requireNonEmpty;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

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
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.misc.ComputedElement;
import com.exedio.cope.misc.NonNegativeRandom;
import com.exedio.cope.misc.instrument.FinalSettableGetter;
import com.exedio.cope.misc.instrument.InitialExceptionsSettableGetter;
import com.exedio.cope.misc.instrument.NullableIfOptional;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Timer;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WrapFeature
public class Hash extends Pattern implements HashInterface
{
	private static final Logger logger = LoggerFactory.getLogger(Hash.class);

	private static final int DEFAULT_PLAINTEXT_LIMIT = 150;
	private static final DefaultPlainTextValidator DEFAULT_VALIDATOR = new DefaultPlainTextValidator();
	private static final long serialVersionUID = 1l;

	private final StringField storage;
	private final boolean isfinal;
	private final int plainTextLimit;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final HashAlgorithm algorithm;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final PlainTextValidator validator;

	public Hash(final StringField storage, final Algorithm algorithm, final Charset charset)
	{
		this(storage, DEFAULT_PLAINTEXT_LIMIT, wrap(algorithm, charset), DEFAULT_VALIDATOR);
	}

	public Hash(final StringField storage, final Algorithm algorithm)
	{
		this(storage, DEFAULT_PLAINTEXT_LIMIT, wrap(algorithm, UTF_8), DEFAULT_VALIDATOR);
	}

	public Hash(final Algorithm algorithm, final Charset charset)
	{
		this(newStorage(wrap(algorithm, charset)), DEFAULT_PLAINTEXT_LIMIT, wrap(algorithm, charset), DEFAULT_VALIDATOR);
	}

	public Hash(final Algorithm algorithm)
	{
		this(newStorage(wrap(algorithm, UTF_8)), algorithm);
	}

	public Hash(final HashAlgorithm algorithm)
	{
		this(newStorage(algorithm), DEFAULT_PLAINTEXT_LIMIT, algorithm, DEFAULT_VALIDATOR);
	}

	public Hash(final StringField storage, final HashAlgorithm algorithm)
	{
		this(storage, DEFAULT_PLAINTEXT_LIMIT, algorithm, DEFAULT_VALIDATOR);
	}

	private Hash(
			final StringField storage,
			final int plainTextLimit,
			final HashAlgorithm algorithm,
			final PlainTextValidator validator)
	{
		requireNonNull(storage, "storage");
		if(plainTextLimit<10)
			throw new IllegalArgumentException("plainTextLimit must be at least 10, but was " + plainTextLimit);
		requireNonNull(algorithm, "algorithm");
		requireNonNull(validator, "validator");

		this.algorithm = algorithm;
		this.storage = addSourceFeature(
				storage,
				requireNonEmpty(algorithm.getID(), "algorithmID"),
				ComputedElement.get());
		this.isfinal = storage.isFinal();
		this.plainTextLimit = plainTextLimit;

		this.validator = validator;
	}

	public final StringField getStorage()
	{
		return storage;
	}

	/**
	 * @see #limit(int)
	 */
	public final int getPlainTextLimit()
	{
		return plainTextLimit;
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
	 * @see #validate(PlainTextValidator)
	 */
	public final PlainTextValidator getPlainTextValidator()
	{
		return validator!=DEFAULT_VALIDATOR ? validator : null;
	}

	@Override
	public final boolean isInitial()
	{
		return storage.isInitial();
	}

	@Override
	public final boolean isFinal()
	{
		return isfinal;
	}

	@Override
	public final boolean isMandatory()
	{
		return storage.isMandatory();
	}

	@Override
	public final Class<?> getInitialType()
	{
		return String.class;
	}

	@Override
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
		final Timer.Sample start = Timer.start();
		final String result = algorithm.hash(plainText);
		hashTimer.stop(start);
		if(result==null)
			throw new NullPointerException(algorithm.getID());
		return result;
	}

	private boolean algorithmCheck(final String plainText, final String hash)
	{
		if(plainText==null)
			throw new NullPointerException();
		if(hash==null)
			throw new NullPointerException();

		final Timer.Sample start = Timer.start();
		final boolean result = algorithm.check(plainText, hash);
		(result ? checkTimerMatch : checkTimerMismatch).stop(start);
		return result;
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
		return new Hash(storage.toFinal(), plainTextLimit, algorithm, validator);
	}

	public final Hash optional()
	{
		return new Hash(storage.optional(), plainTextLimit, algorithm, validator);
	}

	/**
	 * Creates a new hash with a new plain text limit.
	 * The default is 150.
	 * Setting hashes longer than the limit will cause an {@link InvalidPlainTextException}.
	 * {@link #check(Item,String) Checking} for hashes longer than the limit will silently return false.
	 * This is a precaution against DOS attacks with very long plain texts.
	 * @see #getPlainTextLimit()
	 */
	public final Hash limit(final int plainTextLimit)
	{
		return new Hash(storage.copy(), plainTextLimit, algorithm, validator);
	}

	/**
	 * @see #getPlainTextValidator()
	 */
	public final Hash validate(final PlainTextValidator validator)
	{
		return new Hash(storage.copy(), plainTextLimit, algorithm, validator);
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		FeatureMeter.onMount(Hash.class, this, hashTimer, checkTimerMatch, checkTimerMismatch);
	}

	@Override
	@Wrap(order=30,
			doc=Wrap.SET_DOC,
			hide=FinalSettableGetter.class,
			thrownGetter=InitialExceptionsSettableGetter.class)
	public final void set(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final String plainText)
	{
		FinalViolationException.check(this, item);

		storage.set(item, hash(plainText, item));
	}

	@Override
	@Wrap(order=10, doc=Wrap.HASH_CHECK_DOC)
	public final boolean check(@Nonnull final Item item, @Nullable final String actualPlainText)
	{
		final String expectedHash = storage.get(item);
		if(actualPlainText!=null)
			return
					(expectedHash!=null) &&
					checkPlainTextLimit(actualPlainText) &&
					algorithmCheck(actualPlainText, expectedHash); // Algorithm#hash(String) must not return null
		else
			return expectedHash==null;
	}

	@Override
	public final boolean isNull(@Nonnull final Item item)
	{
		return storage.get(item)==null;
	}

	@SuppressWarnings("TransientFieldNotInitialized") // OK: lazy initialization
	private transient volatile String hashForBlind = null;

	/**
	 * Wastes (almost) as much cpu cycles, as a call to
	 * {@link #check(Item, String)}  would have needed.
	 * Needed to prevent Timing Attacks.
	 * See https://en.wikipedia.org/wiki/Timing_attack
	 */
	@Override
	@Wrap(order=20, doc={Wrap.HASH_BLIND_DOC_1, Wrap.HASH_BLIND_DOC_2})
	public final void blind(@Nullable final String actualPlainText)
	{
		if(actualPlainText!=null)
		{
			if(hashForBlind==null)
				hashForBlind = algorithmHash("1234");

			algorithmCheck(actualPlainText, hashForBlind);
		}
	}

	@Override
	public final SetValue<?>[] execute(final String value, final Item exceptionItem) throws InvalidPlainTextException
	{
		final String hash = hash(value, exceptionItem);
		return new SetValue<?>[]{ storage.map(hash) };
	}

	@Override
	@Wrap(order=40, nameGetter=GetNameGetter.class, doc="Returns the encoded hash value for hash {0}.", nullability=NullableIfOptional.class)
	public final String getHash(@Nonnull final Item item)
	{
		return storage.get(item);
	}

	@Wrap(order=50,
			nameGetter=SetNameGetter.class,
			doc="Sets the encoded hash value for hash {0}.",
			hide=FinalSettableGetter.class,
			thrownGetter=InitialExceptionsSettableGetter.class)
	public final void setHash(@Nonnull final Item item, @Parameter(nullability=NullableIfOptional.class) final String hash)
	{
		FinalViolationException.check(this, item);

		storage.set(item, hash);
	}

	static String getMethodSuffixAlgorithm(final Hash feature)
	{
		return feature.getAlgorithmID().replaceAll("\\W", "");
	}

	public final String hash(final String plainText)
	{
		return hash(plainText, null);
	}

	private String hash(final String plainText, final Item exceptionItem)
	{
		if(plainText==null)
			return null;

		checkPlainText(plainText, exceptionItem);

		return algorithmHash(plainText);
	}

	public final void checkPlainText(final String plainText)
	{
		if(plainText==null)
		{
			if(isMandatory())
				throw MandatoryViolationException.create(this, null);
		}
		else
		{
			checkPlainText(plainText, null);
		}
	}

	private void checkPlainText(final String plainText, final Item exceptionItem)
	{
		if(!checkPlainTextLimit(plainText))
			throw new InvalidPlainTextException(
					"plain text length violation, " +
					"must be no longer than " + plainTextLimit + ", " +
					"but was " + plainText.length(),
					plainText, true, exceptionItem, this);

		validator.validate(plainText, exceptionItem, this);
	}

	private boolean checkPlainTextLimit(final String plainText)
	{
		return plainText.length()<=plainTextLimit;
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

	@Override
	public final String newRandomPassword(final SecureRandom random)
	{
		return validator.newRandomPlainText(random);
	}

	/** Validate plain text for potential limits, to be specified in sub classes */
	public abstract static class PlainTextValidator
	{
		protected abstract void validate(String plainText, Item exceptionItem, Hash hash) throws
			InvalidPlainTextException;

		/**
		 * Creates a plain text variant to redeem an existing password (password forgotten).
		 * The result MUST be valid according to {@link #validate(String, Item, Hash)}.
		 */
		protected abstract String newRandomPlainText(SecureRandom secureRandom);
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
		private final boolean wasLimit;
		private final String message;
		private final Hash feature;

		public InvalidPlainTextException(
				final String message,
				final String plainText,
				final Item item,
				final Hash feature)
		{
			this(message, plainText, false, item, feature);
		}

		private InvalidPlainTextException(
				final String message,
				final String plainText,
				final boolean wasLimit,
				final Item item,
				final Hash feature)
		{
			super(item, /*cause*/ null);
			this.message = wipePlainTextFromMessage(message, plainText, feature);
			this.plainText = plainText;
			this.wasLimit = wasLimit;
			this.feature = feature;
		}

		private static String wipePlainTextFromMessage(
				final String message,
				final String plainText,
				final Hash feature)
		{
			if(message==null ||
				plainText==null ||
				plainText.length()<=3 ||
				!message.contains(plainText))
				return message;

			logger.warn("wipePlainTextFromMessage {}", feature);
			return message.replace(plainText, "<plainText wiped>");
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

		/**
		 * Returns true if this exception was raised because the
		 * {@link Hash#getPlainTextLimit() plain text limit}
		 * was exceeded.
		 */
		public boolean wasLimit()
		{
			return wasLimit;
		}
	}

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureTimer hashTimer = timer("hash", "Creates a new hash from plain text.");
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureTimer checkTimerMatch = timer("check", "Checks a hash against plain text.", "result", "match");
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureTimer checkTimerMismatch = checkTimerMatch.newValue("mismatch");

	// ------------------- deprecated stuff -------------------

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
}

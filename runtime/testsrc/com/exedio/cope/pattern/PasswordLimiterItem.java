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

import static com.exedio.cope.instrument.Visibility.PRIVATE;
import static java.time.Duration.ofMinutes;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.pattern.PasswordLimiter.ExceededException;

public final class PasswordLimiterItem extends Item
{
	static final Hash password = new Hash(MessageDigestHash.algorithm(8));

	@Wrapper(wrap="check", visibility=PRIVATE)
	@Wrapper(wrap="checkVerbosely", visibility=PRIVATE)
	static final PasswordLimiter passwordLimited = new PasswordLimiter(password, ofMinutes(1), 2);

	boolean checkPasswordLimited(
			final String password,
			final AbsoluteMockClockStrategy clock,
			final String date)
	{
		clock.add(date);
		final boolean result = checkPasswordLimited(password);
		clock.assertEmpty();
		return result;
	}

	boolean checkPasswordLimitedVerbosely(
			final String password,
			final AbsoluteMockClockStrategy clock,
			final String date)
	throws ExceededException
	{
		clock.add(date);
		final boolean result = checkPasswordLimitedVerbosely(password);
		clock.assertEmpty();
		return result;
	}

	void checkPasswordLimitedVerboselyFails(
			final String password,
			final AbsoluteMockClockStrategy clock,
			final String date,
			final String releaseDate)
	{
		clock.add(date);
		try
		{
			checkPasswordLimitedVerbosely(password);
			fail("should have thrown ExceededException");
		}
		catch(final ExceededException e)
		{
			assertSame(passwordLimited, e.getLimiter());
			assertSame(this, e.getItem());
			clock.assertEqualsFormatted(releaseDate, e.getReleaseDate());
			assertEquals(
					"password limit exceeded on " + this +
					" for PasswordLimiterItem.passwordLimited until " +
					e.getReleaseDate(),
					e.getMessage());
		}
		clock.assertEmpty();
	}

	static final String period0  = "2005-05-12 13:11:22.333";
	static final String period1M = "2005-05-12 13:12:22.332";
	static final String period1  = "2005-05-12 13:12:22.333";
	static final String period1P = "2005-05-12 13:12:22.334";
	static final String period2  = "2005-05-12 13:13:22.333";
	static final String period2P = "2005-05-12 13:13:22.334";


	/**
	 * Creates a new PasswordLimiterItem with all the fields initially needed.
	 * @param password the initial value for field {@link #password}.
	 * @throws com.exedio.cope.MandatoryViolationException if password is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	PasswordLimiterItem(
				@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			PasswordLimiterItem.password.map(password),
		});
	}

	/**
	 * Creates a new PasswordLimiterItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private PasswordLimiterItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkPassword(@javax.annotation.Nullable final java.lang.String password)
	{
		return PasswordLimiterItem.password.check(this,password);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkPassword} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindPassword(@javax.annotation.Nullable final java.lang.String password)
	{
		PasswordLimiterItem.password.blind(password);
	}

	/**
	 * Sets a new value for {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setPassword(@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		PasswordLimiterItem.password.set(this,password);
	}

	/**
	 * Returns the encoded hash value for hash {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getSHA512s8i8")
	@javax.annotation.Nonnull
	java.lang.String getPasswordSHA512s8i8()
	{
		return PasswordLimiterItem.password.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setSHA512s8i8")
	void setPasswordSHA512s8i8(@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		PasswordLimiterItem.password.setHash(this,password);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	private boolean checkPasswordLimited(@javax.annotation.Nullable final java.lang.String password)
	{
		return PasswordLimiterItem.passwordLimited.check(this,password);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="checkVerbosely")
	private boolean checkPasswordLimitedVerbosely(@javax.annotation.Nullable final java.lang.String password)
			throws
				com.exedio.cope.pattern.PasswordLimiter.ExceededException
	{
		return PasswordLimiterItem.passwordLimited.checkVerbosely(this,password);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="reset")
	void resetPasswordLimited()
	{
		PasswordLimiterItem.passwordLimited.reset(this);
	}

	/**
	 * @return the number of refusals purged
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="purge")
	@java.lang.Deprecated
	static int purgePasswordLimited(@javax.annotation.Nullable final com.exedio.cope.util.Interrupter interrupter)
	{
		return PasswordLimiterItem.passwordLimited.purge(interrupter);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="purge")
	static void purgePasswordLimited(@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		PasswordLimiterItem.passwordLimited.purge(ctx);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for passwordLimiterItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PasswordLimiterItem> TYPE = com.exedio.cope.TypesBound.newType(PasswordLimiterItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private PasswordLimiterItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

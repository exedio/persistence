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

import com.exedio.cope.Item;

@SuppressWarnings("UnusedReturnValue")
public final class PasswordRecoveryItem extends Item
{
	static final Hash password = new Hash(MessageDigestHash.algorithm(5));
	static final PasswordRecovery passwordRecovery = new PasswordRecovery(password);



	/**
	 * Creates a new PasswordRecoveryItem with all the fields initially needed.
	 * @param password the initial value for field {@link #password}.
	 * @throws com.exedio.cope.MandatoryViolationException if password is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	PasswordRecoveryItem(
				@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			PasswordRecoveryItem.password.map(password),
		});
	}

	/**
	 * Creates a new PasswordRecoveryItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private PasswordRecoveryItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkPassword(@javax.annotation.Nullable final java.lang.String password)
	{
		return PasswordRecoveryItem.password.check(this,password);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkPassword} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindPassword(@javax.annotation.Nullable final java.lang.String password)
	{
		PasswordRecoveryItem.password.blind(password);
	}

	/**
	 * Sets a new value for {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setPassword(@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		PasswordRecoveryItem.password.set(this,password);
	}

	/**
	 * Returns the encoded hash value for hash {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getSHA512s8i5")
	@javax.annotation.Nonnull
	java.lang.String getPasswordSHA512s8i5()
	{
		return PasswordRecoveryItem.password.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setSHA512s8i5")
	void setPasswordSHA512s8i5(@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		PasswordRecoveryItem.password.setHash(this,password);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="issue")
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.PasswordRecovery.Token issuePasswordRecovery(@javax.annotation.Nonnull final com.exedio.cope.pattern.PasswordRecovery.Config config)
	{
		return PasswordRecoveryItem.passwordRecovery.issue(this,config);
	}

	/**
	 * @param expiryMillis the time span, after which this token will not be valid anymore, in milliseconds
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="issue")
	@java.lang.Deprecated
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.PasswordRecovery.Token issuePasswordRecovery(final int expiryMillis)
	{
		return PasswordRecoveryItem.passwordRecovery.issue(this,expiryMillis);
	}

	/**
	 * @param secret a secret for password recovery
	 * @return a valid token, if existing, otherwise null
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getValidToken")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.PasswordRecovery.Token getValidPasswordRecoveryToken(final long secret)
	{
		return PasswordRecoveryItem.passwordRecovery.getValidToken(this,secret);
	}

	/**
	 * @param secret a secret for password recovery
	 * @return a new password, if the secret was valid, otherwise null
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="redeem")
	@javax.annotation.Nullable
	java.lang.String redeemPasswordRecovery(final long secret)
	{
		return PasswordRecoveryItem.passwordRecovery.redeem(this,secret);
	}

	/**
	 * @return the number of tokens purged
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="purge")
	@java.lang.Deprecated
	static int purgePasswordRecovery(@javax.annotation.Nullable final com.exedio.cope.util.Interrupter interrupter)
	{
		return PasswordRecoveryItem.passwordRecovery.purge(interrupter);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="purge")
	static void purgePasswordRecovery(@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		PasswordRecoveryItem.passwordRecovery.purge(ctx);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for passwordRecoveryItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PasswordRecoveryItem> TYPE = com.exedio.cope.TypesBound.newType(PasswordRecoveryItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private PasswordRecoveryItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

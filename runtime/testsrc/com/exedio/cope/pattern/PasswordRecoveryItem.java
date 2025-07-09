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
import com.exedio.cope.testmodel.WrapHash;

@SuppressWarnings("UnusedReturnValue")
public final class PasswordRecoveryItem extends Item
{
	static final Hash password = new Hash(WrapHash.ALGORITHM);
	static final PasswordRecovery passwordRecovery = new PasswordRecovery(password);



	/**
	 * Creates a new PasswordRecoveryItem with all the fields initially needed.
	 * @param password the initial value for field {@link #password}.
	 * @throws com.exedio.cope.MandatoryViolationException if password is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	PasswordRecoveryItem(
				@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(PasswordRecoveryItem.password,password),
		});
	}

	/**
	 * Creates a new PasswordRecoveryItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private PasswordRecoveryItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #password}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean checkPassword(@javax.annotation.Nullable final java.lang.String password)
	{
		return PasswordRecoveryItem.password.check(this,password);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkPassword} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void blindPassword(@javax.annotation.Nullable final java.lang.String password)
	{
		PasswordRecoveryItem.password.blind(password);
	}

	/**
	 * Sets a new value for {@link #password}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPassword(@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		PasswordRecoveryItem.password.set(this,password);
	}

	/**
	 * Returns the encoded hash value for hash {@link #password}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getPasswordwrap()
	{
		return PasswordRecoveryItem.password.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #password}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setPasswordwrap(@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		PasswordRecoveryItem.password.setHash(this,password);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="issue")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.PasswordRecovery.Token issuePasswordRecovery(@javax.annotation.Nonnull final com.exedio.cope.pattern.PasswordRecovery.Config config)
	{
		return PasswordRecoveryItem.passwordRecovery.issue(this,config);
	}

	/**
	 * @param secret a secret for password recovery
	 * @return a valid token, if existing, otherwise null
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getValidToken")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.PasswordRecovery.Token getValidPasswordRecoveryToken(final long secret)
	{
		return PasswordRecoveryItem.passwordRecovery.getValidToken(this,secret);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="purge")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void purgePasswordRecovery(@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		PasswordRecoveryItem.passwordRecovery.purge(ctx);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for passwordRecoveryItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PasswordRecoveryItem> TYPE = com.exedio.cope.TypesBound.newType(PasswordRecoveryItem.class,PasswordRecoveryItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private PasswordRecoveryItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

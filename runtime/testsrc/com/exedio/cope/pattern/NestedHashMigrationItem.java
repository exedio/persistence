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
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

final class NestedHashMigrationItem extends Item
{
	static final NestedHashMigration password = new NestedHashMigration(
			MessageDigestHashAlgorithm.create(StandardCharsets.UTF_8, "MD5", 0, null, 1),
			MessageDigestHashAlgorithm.create(StandardCharsets.UTF_8, "MD5", 1, new SecureRandom(), 1));


	@SuppressWarnings("unused")
	NestedHashMigrationItem(
			final String password,
			final double dummy)
	{
		this(
			NestedHashMigrationItem.password.getLegacyHash().map(password),
			NestedHashMigrationItem.password.getTargetHash().map(null));
	}

	/**
	 * Creates a new NestedHashMigrationItem with all the fields initially needed.
	 * @param password the initial value for field {@link #password}.
	 * @throws com.exedio.cope.MandatoryViolationException if password is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	NestedHashMigrationItem(
				@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			NestedHashMigrationItem.password.map(password),
		});
	}

	/**
	 * Creates a new NestedHashMigrationItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private NestedHashMigrationItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	final boolean checkPassword(@javax.annotation.Nullable final java.lang.String password)
	{
		return NestedHashMigrationItem.password.check(this,password);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to <tt>checkPassword</tt> would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static final void blindPassword(@javax.annotation.Nonnull final java.lang.String password)
	{
		NestedHashMigrationItem.password.blind(password);
	}

	/**
	 * Sets a new value for {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setPassword(@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		NestedHashMigrationItem.password.set(this,password);
	}

	/**
	 * Re-hashes all legacy passwords to target ones.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="migrate")
	static final void migratePassword(@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		NestedHashMigrationItem.password.migrate(ctx);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for nestedHashMigrationItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<NestedHashMigrationItem> TYPE = com.exedio.cope.TypesBound.newType(NestedHashMigrationItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private NestedHashMigrationItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
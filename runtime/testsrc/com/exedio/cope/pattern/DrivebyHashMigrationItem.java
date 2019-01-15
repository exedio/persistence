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

final class DrivebyHashMigrationItem extends Item
{
	static final DrivebyHashMigration password = new DrivebyHashMigration(
			MessageDigestHashAlgorithm.create(StandardCharsets.UTF_8, "MD5"    , 0, null, 1),
			MessageDigestHashAlgorithm.create(StandardCharsets.UTF_8, "SHA-512", 0, null, 1));


	@SuppressWarnings("unused")
	DrivebyHashMigrationItem(
			final String password,
			final double dummy)
	{
		this(
			DrivebyHashMigrationItem.password.getLegacyHash().map(password),
			DrivebyHashMigrationItem.password.getTargetHash().map(null));
	}

	/**
	 * Creates a new DrivebyHashMigrationItem with all the fields initially needed.
	 * @param password the initial value for field {@link #password}.
	 * @throws com.exedio.cope.MandatoryViolationException if password is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	DrivebyHashMigrationItem(
				@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			DrivebyHashMigrationItem.password.map(password),
		});
	}

	/**
	 * Creates a new DrivebyHashMigrationItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private DrivebyHashMigrationItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkPassword(@javax.annotation.Nullable final java.lang.String password)
	{
		return DrivebyHashMigrationItem.password.check(this,password);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkPassword} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindPassword(@javax.annotation.Nullable final java.lang.String password)
	{
		DrivebyHashMigrationItem.password.blind(password);
	}

	/**
	 * Sets a new value for {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setPassword(@javax.annotation.Nonnull final java.lang.String password)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		DrivebyHashMigrationItem.password.set(this,password);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for drivebyHashMigrationItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<DrivebyHashMigrationItem> TYPE = com.exedio.cope.TypesBound.newType(DrivebyHashMigrationItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private DrivebyHashMigrationItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

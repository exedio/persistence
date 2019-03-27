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

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.util.Hex;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.nio.charset.StandardCharsets;

public final class MessageDigestHashItem extends Item
{
	@Wrapper(wrap="set", visibility=NONE)
	static final Hash password = new Hash(new MessageDigestAlgorithm("SHA-512", 0, 5).salt(8, new MockSecureRandom())).optional().limit(200);
	@Wrapper(wrap="set", visibility=NONE)
	static final Hash passwordLatin = new Hash(new MessageDigestAlgorithm("SHA-512", 0, 5).salt(8, new MockSecureRandom()), StandardCharsets.ISO_8859_1).optional();
	static final Hash passwordFinal = new Hash(new MessageDigestAlgorithm("SHA-512", 0, 5).salt(8, new MockSecureRandom())).toFinal();
	@Wrapper(wrap="set", visibility=NONE)
	static final Hash passwordMandatory = new Hash(new MessageDigestAlgorithm("SHA-512", 0, 5).salt(8, new MockSecureRandom()));

	void setPassword(final String password)
	{
		set(MessageDigestHashItem.password, password);
	}

	void setPasswordLatin(final String password)
	{
		set(passwordLatin, password);
	}

	void setPasswordMandatory(final String password)
	{
		set(passwordMandatory, password);
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	private void set(final Hash hash, final String password)
	{
		@SuppressWarnings("deprecation")
		final Hash.Algorithm algo = hash.getAlgorithm();
		((MockSecureRandom)((MessageDigestAlgorithm)algo).getSaltSource()).expectNextBytes(Hex.decodeLower("aeab417a9b5a7cf3"));
		hash.set(this, password);
	}

	/**
	 * Creates a new MessageDigestHashItem with all the fields initially needed.
	 * @param passwordFinal the initial value for field {@link #passwordFinal}.
	 * @param passwordMandatory the initial value for field {@link #passwordMandatory}.
	 * @throws com.exedio.cope.MandatoryViolationException if passwordFinal, passwordMandatory is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	MessageDigestHashItem(
				@javax.annotation.Nonnull final java.lang.String passwordFinal,
				@javax.annotation.Nonnull final java.lang.String passwordMandatory)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			MessageDigestHashItem.passwordFinal.map(passwordFinal),
			MessageDigestHashItem.passwordMandatory.map(passwordMandatory),
		});
	}

	/**
	 * Creates a new MessageDigestHashItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private MessageDigestHashItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkPassword(@javax.annotation.Nullable final java.lang.String password)
	{
		return MessageDigestHashItem.password.check(this,password);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkPassword} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindPassword(@javax.annotation.Nullable final java.lang.String password)
	{
		MessageDigestHashItem.password.blind(password);
	}

	/**
	 * Returns the encoded hash value for hash {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getSHA512s8i5")
	@javax.annotation.Nullable
	java.lang.String getPasswordSHA512s8i5()
	{
		return MessageDigestHashItem.password.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #password}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setSHA512s8i5")
	void setPasswordSHA512s8i5(@javax.annotation.Nullable final java.lang.String password)
	{
		MessageDigestHashItem.password.setHash(this,password);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #passwordLatin}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkPasswordLatin(@javax.annotation.Nullable final java.lang.String passwordLatin)
	{
		return MessageDigestHashItem.passwordLatin.check(this,passwordLatin);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkPasswordLatin} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindPasswordLatin(@javax.annotation.Nullable final java.lang.String passwordLatin)
	{
		MessageDigestHashItem.passwordLatin.blind(passwordLatin);
	}

	/**
	 * Returns the encoded hash value for hash {@link #passwordLatin}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getSHA512s8i5")
	@javax.annotation.Nullable
	java.lang.String getPasswordLatinSHA512s8i5()
	{
		return MessageDigestHashItem.passwordLatin.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #passwordLatin}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setSHA512s8i5")
	void setPasswordLatinSHA512s8i5(@javax.annotation.Nullable final java.lang.String passwordLatin)
	{
		MessageDigestHashItem.passwordLatin.setHash(this,passwordLatin);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #passwordFinal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkPasswordFinal(@javax.annotation.Nullable final java.lang.String passwordFinal)
	{
		return MessageDigestHashItem.passwordFinal.check(this,passwordFinal);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkPasswordFinal} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindPasswordFinal(@javax.annotation.Nullable final java.lang.String passwordFinal)
	{
		MessageDigestHashItem.passwordFinal.blind(passwordFinal);
	}

	/**
	 * Returns the encoded hash value for hash {@link #passwordFinal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getSHA512s8i5")
	@javax.annotation.Nonnull
	java.lang.String getPasswordFinalSHA512s8i5()
	{
		return MessageDigestHashItem.passwordFinal.getHash(this);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #passwordMandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkPasswordMandatory(@javax.annotation.Nullable final java.lang.String passwordMandatory)
	{
		return MessageDigestHashItem.passwordMandatory.check(this,passwordMandatory);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkPasswordMandatory} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindPasswordMandatory(@javax.annotation.Nullable final java.lang.String passwordMandatory)
	{
		MessageDigestHashItem.passwordMandatory.blind(passwordMandatory);
	}

	/**
	 * Returns the encoded hash value for hash {@link #passwordMandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getSHA512s8i5")
	@javax.annotation.Nonnull
	java.lang.String getPasswordMandatorySHA512s8i5()
	{
		return MessageDigestHashItem.passwordMandatory.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #passwordMandatory}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setSHA512s8i5")
	void setPasswordMandatorySHA512s8i5(@javax.annotation.Nonnull final java.lang.String passwordMandatory)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		MessageDigestHashItem.passwordMandatory.setHash(this,passwordMandatory);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for messageDigestHashItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MessageDigestHashItem> TYPE = com.exedio.cope.TypesBound.newType(MessageDigestHashItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private MessageDigestHashItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

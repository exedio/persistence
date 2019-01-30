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

import static com.exedio.cope.instrument.Visibility.PACKAGE;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.DigitPinValidator;
import com.exedio.cope.testmodel.WrapHash;

@WrapperType(genericConstructor=PACKAGE)
public final class HashItem extends Item
{
	static final StringField explicitExternalWrap = new StringField().optional();
	static final Hash explicitExternal = new WrapHash(explicitExternalWrap);

	static final Hash implicitExternal = new WrapHash(new StringField().optional());

	static final Hash internal = new WrapHash().optional();

	static final Hash limited15 = new WrapHash().optional().limit(15);

	static final Hash withCorruptValidator = new WrapHash().validate(new WrapHash.CorruptValidator()).optional();

	static final Hash with3PinValidator = new WrapHash().validate(new DigitPinValidator(3)).optional();

	/**
	 * Maybe instrumentor should create this.
	 */
	boolean isInternalNull()
	{
		return internal.isNull(this);
	}

	/**
	 * Creates a new HashItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public HashItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new HashItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	HashItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #explicitExternalWrap}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getExplicitExternalWrap()
	{
		return HashItem.explicitExternalWrap.get(this);
	}

	/**
	 * Sets a new value for {@link #explicitExternalWrap}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setExplicitExternalWrap(@javax.annotation.Nullable final java.lang.String explicitExternalWrap)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HashItem.explicitExternalWrap.set(this,explicitExternalWrap);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #explicitExternal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkExplicitExternal(@javax.annotation.Nullable final java.lang.String explicitExternal)
	{
		return HashItem.explicitExternal.check(this,explicitExternal);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkExplicitExternal} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindExplicitExternal(@javax.annotation.Nullable final java.lang.String explicitExternal)
	{
		HashItem.explicitExternal.blind(explicitExternal);
	}

	/**
	 * Sets a new value for {@link #explicitExternal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setExplicitExternal(@javax.annotation.Nullable final java.lang.String explicitExternal)
	{
		HashItem.explicitExternal.set(this,explicitExternal);
	}

	/**
	 * Returns the encoded hash value for hash {@link #explicitExternal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getwrap")
	@javax.annotation.Nullable
	java.lang.String getExplicitExternalwrap()
	{
		return HashItem.explicitExternal.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #explicitExternal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setwrap")
	void setExplicitExternalwrap(@javax.annotation.Nullable final java.lang.String explicitExternal)
	{
		HashItem.explicitExternal.setHash(this,explicitExternal);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #implicitExternal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkImplicitExternal(@javax.annotation.Nullable final java.lang.String implicitExternal)
	{
		return HashItem.implicitExternal.check(this,implicitExternal);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkImplicitExternal} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindImplicitExternal(@javax.annotation.Nullable final java.lang.String implicitExternal)
	{
		HashItem.implicitExternal.blind(implicitExternal);
	}

	/**
	 * Sets a new value for {@link #implicitExternal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setImplicitExternal(@javax.annotation.Nullable final java.lang.String implicitExternal)
	{
		HashItem.implicitExternal.set(this,implicitExternal);
	}

	/**
	 * Returns the encoded hash value for hash {@link #implicitExternal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getwrap")
	@javax.annotation.Nullable
	java.lang.String getImplicitExternalwrap()
	{
		return HashItem.implicitExternal.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #implicitExternal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setwrap")
	void setImplicitExternalwrap(@javax.annotation.Nullable final java.lang.String implicitExternal)
	{
		HashItem.implicitExternal.setHash(this,implicitExternal);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #internal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkInternal(@javax.annotation.Nullable final java.lang.String internal)
	{
		return HashItem.internal.check(this,internal);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkInternal} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindInternal(@javax.annotation.Nullable final java.lang.String internal)
	{
		HashItem.internal.blind(internal);
	}

	/**
	 * Sets a new value for {@link #internal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setInternal(@javax.annotation.Nullable final java.lang.String internal)
	{
		HashItem.internal.set(this,internal);
	}

	/**
	 * Returns the encoded hash value for hash {@link #internal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getwrap")
	@javax.annotation.Nullable
	java.lang.String getInternalwrap()
	{
		return HashItem.internal.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #internal}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setwrap")
	void setInternalwrap(@javax.annotation.Nullable final java.lang.String internal)
	{
		HashItem.internal.setHash(this,internal);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #limited15}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkLimited15(@javax.annotation.Nullable final java.lang.String limited15)
	{
		return HashItem.limited15.check(this,limited15);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkLimited15} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindLimited15(@javax.annotation.Nullable final java.lang.String limited15)
	{
		HashItem.limited15.blind(limited15);
	}

	/**
	 * Sets a new value for {@link #limited15}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setLimited15(@javax.annotation.Nullable final java.lang.String limited15)
	{
		HashItem.limited15.set(this,limited15);
	}

	/**
	 * Returns the encoded hash value for hash {@link #limited15}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getwrap")
	@javax.annotation.Nullable
	java.lang.String getLimited15wrap()
	{
		return HashItem.limited15.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #limited15}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setwrap")
	void setLimited15wrap(@javax.annotation.Nullable final java.lang.String limited15)
	{
		HashItem.limited15.setHash(this,limited15);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #withCorruptValidator}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkWithCorruptValidator(@javax.annotation.Nullable final java.lang.String withCorruptValidator)
	{
		return HashItem.withCorruptValidator.check(this,withCorruptValidator);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkWithCorruptValidator} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindWithCorruptValidator(@javax.annotation.Nullable final java.lang.String withCorruptValidator)
	{
		HashItem.withCorruptValidator.blind(withCorruptValidator);
	}

	/**
	 * Sets a new value for {@link #withCorruptValidator}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setWithCorruptValidator(@javax.annotation.Nullable final java.lang.String withCorruptValidator)
	{
		HashItem.withCorruptValidator.set(this,withCorruptValidator);
	}

	/**
	 * Returns the encoded hash value for hash {@link #withCorruptValidator}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getwrap")
	@javax.annotation.Nullable
	java.lang.String getWithCorruptValidatorwrap()
	{
		return HashItem.withCorruptValidator.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #withCorruptValidator}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setwrap")
	void setWithCorruptValidatorwrap(@javax.annotation.Nullable final java.lang.String withCorruptValidator)
	{
		HashItem.withCorruptValidator.setHash(this,withCorruptValidator);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #with3PinValidator}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="check")
	boolean checkWith3PinValidator(@javax.annotation.Nullable final java.lang.String with3PinValidator)
	{
		return HashItem.with3PinValidator.check(this,with3PinValidator);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkWith3PinValidator} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="blind")
	static void blindWith3PinValidator(@javax.annotation.Nullable final java.lang.String with3PinValidator)
	{
		HashItem.with3PinValidator.blind(with3PinValidator);
	}

	/**
	 * Sets a new value for {@link #with3PinValidator}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setWith3PinValidator(@javax.annotation.Nullable final java.lang.String with3PinValidator)
	{
		HashItem.with3PinValidator.set(this,with3PinValidator);
	}

	/**
	 * Returns the encoded hash value for hash {@link #with3PinValidator}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getwrap")
	@javax.annotation.Nullable
	java.lang.String getWith3PinValidatorwrap()
	{
		return HashItem.with3PinValidator.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #with3PinValidator}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setwrap")
	void setWith3PinValidatorwrap(@javax.annotation.Nullable final java.lang.String with3PinValidator)
	{
		HashItem.with3PinValidator.setHash(this,with3PinValidator);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hashItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<HashItem> TYPE = com.exedio.cope.TypesBound.newType(HashItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private HashItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

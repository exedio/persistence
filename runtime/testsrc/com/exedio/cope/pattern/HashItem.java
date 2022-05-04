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
	@SuppressWarnings("deprecation") // OK: testing deprecated API
	static final Hash explicitExternal = new Hash(explicitExternalWrap, WrapHash.ALGORITHM);

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	static final Hash implicitExternal = new Hash(new StringField().optional(), WrapHash.ALGORITHM);

	static final Hash internal = new Hash(WrapHash.ALGORITHM).optional();

	static final Hash limited15 = new Hash(WrapHash.ALGORITHM).optional().limit(15);

	static final Hash withCorruptValidator = new Hash(WrapHash.ALGORITHM).validate(new WrapHash.CorruptValidator()).optional();

	static final Hash with3PinValidator = new Hash(WrapHash.ALGORITHM).validate(new DigitPinValidator(3)).optional();

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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public HashItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new HashItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	HashItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #explicitExternalWrap}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getExplicitExternalWrap()
	{
		return HashItem.explicitExternalWrap.get(this);
	}

	/**
	 * Sets a new value for {@link #explicitExternalWrap}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setExplicitExternalWrap(@javax.annotation.Nullable final java.lang.String explicitExternalWrap)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		HashItem.explicitExternalWrap.set(this,explicitExternalWrap);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #explicitExternal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean checkExplicitExternal(@javax.annotation.Nullable final java.lang.String explicitExternal)
	{
		return HashItem.explicitExternal.check(this,explicitExternal);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkExplicitExternal} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void blindExplicitExternal(@javax.annotation.Nullable final java.lang.String explicitExternal)
	{
		HashItem.explicitExternal.blind(explicitExternal);
	}

	/**
	 * Sets a new value for {@link #explicitExternal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setExplicitExternal(@javax.annotation.Nullable final java.lang.String explicitExternal)
	{
		HashItem.explicitExternal.set(this,explicitExternal);
	}

	/**
	 * Returns the encoded hash value for hash {@link #explicitExternal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getExplicitExternalwrap()
	{
		return HashItem.explicitExternal.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #explicitExternal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setExplicitExternalwrap(@javax.annotation.Nullable final java.lang.String explicitExternal)
	{
		HashItem.explicitExternal.setHash(this,explicitExternal);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #implicitExternal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean checkImplicitExternal(@javax.annotation.Nullable final java.lang.String implicitExternal)
	{
		return HashItem.implicitExternal.check(this,implicitExternal);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkImplicitExternal} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void blindImplicitExternal(@javax.annotation.Nullable final java.lang.String implicitExternal)
	{
		HashItem.implicitExternal.blind(implicitExternal);
	}

	/**
	 * Sets a new value for {@link #implicitExternal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setImplicitExternal(@javax.annotation.Nullable final java.lang.String implicitExternal)
	{
		HashItem.implicitExternal.set(this,implicitExternal);
	}

	/**
	 * Returns the encoded hash value for hash {@link #implicitExternal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getImplicitExternalwrap()
	{
		return HashItem.implicitExternal.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #implicitExternal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setImplicitExternalwrap(@javax.annotation.Nullable final java.lang.String implicitExternal)
	{
		HashItem.implicitExternal.setHash(this,implicitExternal);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #internal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean checkInternal(@javax.annotation.Nullable final java.lang.String internal)
	{
		return HashItem.internal.check(this,internal);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkInternal} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void blindInternal(@javax.annotation.Nullable final java.lang.String internal)
	{
		HashItem.internal.blind(internal);
	}

	/**
	 * Sets a new value for {@link #internal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setInternal(@javax.annotation.Nullable final java.lang.String internal)
	{
		HashItem.internal.set(this,internal);
	}

	/**
	 * Returns the encoded hash value for hash {@link #internal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getInternalwrap()
	{
		return HashItem.internal.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #internal}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setInternalwrap(@javax.annotation.Nullable final java.lang.String internal)
	{
		HashItem.internal.setHash(this,internal);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #limited15}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean checkLimited15(@javax.annotation.Nullable final java.lang.String limited15)
	{
		return HashItem.limited15.check(this,limited15);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkLimited15} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void blindLimited15(@javax.annotation.Nullable final java.lang.String limited15)
	{
		HashItem.limited15.blind(limited15);
	}

	/**
	 * Sets a new value for {@link #limited15}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setLimited15(@javax.annotation.Nullable final java.lang.String limited15)
	{
		HashItem.limited15.set(this,limited15);
	}

	/**
	 * Returns the encoded hash value for hash {@link #limited15}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getLimited15wrap()
	{
		return HashItem.limited15.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #limited15}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setLimited15wrap(@javax.annotation.Nullable final java.lang.String limited15)
	{
		HashItem.limited15.setHash(this,limited15);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #withCorruptValidator}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean checkWithCorruptValidator(@javax.annotation.Nullable final java.lang.String withCorruptValidator)
	{
		return HashItem.withCorruptValidator.check(this,withCorruptValidator);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkWithCorruptValidator} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void blindWithCorruptValidator(@javax.annotation.Nullable final java.lang.String withCorruptValidator)
	{
		HashItem.withCorruptValidator.blind(withCorruptValidator);
	}

	/**
	 * Sets a new value for {@link #withCorruptValidator}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setWithCorruptValidator(@javax.annotation.Nullable final java.lang.String withCorruptValidator)
	{
		HashItem.withCorruptValidator.set(this,withCorruptValidator);
	}

	/**
	 * Returns the encoded hash value for hash {@link #withCorruptValidator}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getWithCorruptValidatorwrap()
	{
		return HashItem.withCorruptValidator.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #withCorruptValidator}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setWithCorruptValidatorwrap(@javax.annotation.Nullable final java.lang.String withCorruptValidator)
	{
		HashItem.withCorruptValidator.setHash(this,withCorruptValidator);
	}

	/**
	 * Returns whether the given value corresponds to the hash in {@link #with3PinValidator}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="check")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean checkWith3PinValidator(@javax.annotation.Nullable final java.lang.String with3PinValidator)
	{
		return HashItem.with3PinValidator.check(this,with3PinValidator);
	}

	/**
	 * Wastes (almost) as much cpu cycles, as a call to {@code checkWith3PinValidator} would have needed.
	 * Needed to prevent Timing Attacks.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="blind")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void blindWith3PinValidator(@javax.annotation.Nullable final java.lang.String with3PinValidator)
	{
		HashItem.with3PinValidator.blind(with3PinValidator);
	}

	/**
	 * Sets a new value for {@link #with3PinValidator}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setWith3PinValidator(@javax.annotation.Nullable final java.lang.String with3PinValidator)
	{
		HashItem.with3PinValidator.set(this,with3PinValidator);
	}

	/**
	 * Returns the encoded hash value for hash {@link #with3PinValidator}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getWith3PinValidatorwrap()
	{
		return HashItem.with3PinValidator.getHash(this);
	}

	/**
	 * Sets the encoded hash value for hash {@link #with3PinValidator}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setwrap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setWith3PinValidatorwrap(@javax.annotation.Nullable final java.lang.String with3PinValidator)
	{
		HashItem.with3PinValidator.setHash(this,with3PinValidator);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for hashItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<HashItem> TYPE = com.exedio.cope.TypesBound.newType(HashItem.class,HashItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private HashItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

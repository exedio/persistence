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

package com.exedio.cope;

import static com.exedio.cope.instrument.Visibility.NONE;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.util.CharSet;

final class StringCharSetItem extends Item
{
	static final StringField code = new StringField().unique();

	@WrapperInitial
	static final StringField any = new StringField().optional();

	static final StringField alpha = new StringField().optional().charSet(CharSet.ALPHA);

	static final StringField printable = new StringField().optional().charSet(new CharSet(' ', '~'));

	static final StringField apos = new StringField().optional().charSet(new CharSet('\'', '\'', 'A', 'Z'));

	static final StringField email = new StringField().optional().charSet(CharSet.EMAIL_INTERNATIONAL);

	/** all allowed chars are non-ascii */
	@Wrapper(wrap="*", visibility=NONE)
	static final StringField nonascii = new StringField().optional().charSet(new CharSet('\u00e4', '\u00f6'));

	/** all ascii chars are allowed, plus others */
	@Wrapper(wrap="*", visibility=NONE)
	static final StringField asciiplus = new StringField().optional().charSet(new CharSet('\u0000', '\u007f', '\u00e4', '\u00f6'));

	@Override
	public String toString()
	{
		return getCode();
	}


	/**
	 * Creates a new StringCharSetItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @param any the initial value for field {@link #any}.
	 * @throws com.exedio.cope.MandatoryViolationException if code is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code, any violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if code is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	StringCharSetItem(
				@javax.annotation.Nonnull final java.lang.String code,
				@javax.annotation.Nullable final java.lang.String any)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			StringCharSetItem.code.map(code),
			StringCharSetItem.any.map(any),
		});
	}

	/**
	 * Creates a new StringCharSetItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private StringCharSetItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getCode()
	{
		return StringCharSetItem.code.get(this);
	}

	/**
	 * Sets a new value for {@link #code}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setCode(@javax.annotation.Nonnull final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		StringCharSetItem.code.set(this,code);
	}

	/**
	 * Finds a stringCharSetItem by it's {@link #code}.
	 * @param code shall be equal to field {@link #code}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="for")
	@javax.annotation.Nullable
	static StringCharSetItem forCode(@javax.annotation.Nonnull final java.lang.String code)
	{
		return StringCharSetItem.code.searchUnique(StringCharSetItem.class,code);
	}

	/**
	 * Finds a stringCharSetItem by its {@link #code}.
	 * @param code shall be equal to field {@link #code}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="forStrict")
	@javax.annotation.Nonnull
	static StringCharSetItem forCodeStrict(@javax.annotation.Nonnull final java.lang.String code)
			throws
				java.lang.IllegalArgumentException
	{
		return StringCharSetItem.code.searchUniqueStrict(StringCharSetItem.class,code);
	}

	/**
	 * Returns the value of {@link #any}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getAny()
	{
		return StringCharSetItem.any.get(this);
	}

	/**
	 * Sets a new value for {@link #any}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAny(@javax.annotation.Nullable final java.lang.String any)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		StringCharSetItem.any.set(this,any);
	}

	/**
	 * Returns the value of {@link #alpha}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getAlpha()
	{
		return StringCharSetItem.alpha.get(this);
	}

	/**
	 * Sets a new value for {@link #alpha}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAlpha(@javax.annotation.Nullable final java.lang.String alpha)
			throws
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.StringCharSetViolationException
	{
		StringCharSetItem.alpha.set(this,alpha);
	}

	/**
	 * Returns the value of {@link #printable}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getPrintable()
	{
		return StringCharSetItem.printable.get(this);
	}

	/**
	 * Sets a new value for {@link #printable}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setPrintable(@javax.annotation.Nullable final java.lang.String printable)
			throws
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.StringCharSetViolationException
	{
		StringCharSetItem.printable.set(this,printable);
	}

	/**
	 * Returns the value of {@link #apos}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getApos()
	{
		return StringCharSetItem.apos.get(this);
	}

	/**
	 * Sets a new value for {@link #apos}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setApos(@javax.annotation.Nullable final java.lang.String apos)
			throws
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.StringCharSetViolationException
	{
		StringCharSetItem.apos.set(this,apos);
	}

	/**
	 * Returns the value of {@link #email}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getEmail()
	{
		return StringCharSetItem.email.get(this);
	}

	/**
	 * Sets a new value for {@link #email}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setEmail(@javax.annotation.Nullable final java.lang.String email)
			throws
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.StringCharSetViolationException
	{
		StringCharSetItem.email.set(this,email);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for stringCharSetItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<StringCharSetItem> TYPE = com.exedio.cope.TypesBound.newType(StringCharSetItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private StringCharSetItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

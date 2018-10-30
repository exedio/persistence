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

import com.exedio.cope.instrument.WrapperInitial;

final class CopySimpleTarget extends Item
{
	@WrapperInitial
	static final StringField templateString = new StringField().toFinal().optional();
	static final StringField otherString = new StringField();

	@WrapperInitial
	static final ItemField<CopyValue> templateItem = ItemField.create(CopyValue.class).toFinal().optional();
	static final ItemField<CopyValue> otherItem = ItemField.create(CopyValue.class);

	@Override
	public String toString()
	{
		// for testing, that CopyViolation#getMessage does not call toString(), but getCopeID()
		return "toString(" + getCopeID() + ')';
	}

	/**
	 * Creates a new CopySimpleTarget with all the fields initially needed.
	 * @param templateString the initial value for field {@link #templateString}.
	 * @param otherString the initial value for field {@link #otherString}.
	 * @param templateItem the initial value for field {@link #templateItem}.
	 * @param otherItem the initial value for field {@link #otherItem}.
	 * @throws com.exedio.cope.MandatoryViolationException if otherString, otherItem is null.
	 * @throws com.exedio.cope.StringLengthViolationException if templateString, otherString violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	CopySimpleTarget(
				@javax.annotation.Nullable final java.lang.String templateString,
				@javax.annotation.Nonnull final java.lang.String otherString,
				@javax.annotation.Nullable final CopyValue templateItem,
				@javax.annotation.Nonnull final CopyValue otherItem)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CopySimpleTarget.templateString.map(templateString),
			CopySimpleTarget.otherString.map(otherString),
			CopySimpleTarget.templateItem.map(templateItem),
			CopySimpleTarget.otherItem.map(otherItem),
		});
	}

	/**
	 * Creates a new CopySimpleTarget and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private CopySimpleTarget(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #templateString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.String getTemplateString()
	{
		return CopySimpleTarget.templateString.get(this);
	}

	/**
	 * Returns the value of {@link #otherString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getOtherString()
	{
		return CopySimpleTarget.otherString.get(this);
	}

	/**
	 * Sets a new value for {@link #otherString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setOtherString(@javax.annotation.Nonnull final java.lang.String otherString)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		CopySimpleTarget.otherString.set(this,otherString);
	}

	/**
	 * Returns the value of {@link #templateItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	CopyValue getTemplateItem()
	{
		return CopySimpleTarget.templateItem.get(this);
	}

	/**
	 * Returns the value of {@link #otherItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	CopyValue getOtherItem()
	{
		return CopySimpleTarget.otherItem.get(this);
	}

	/**
	 * Sets a new value for {@link #otherItem}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setOtherItem(@javax.annotation.Nonnull final CopyValue otherItem)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CopySimpleTarget.otherItem.set(this,otherItem);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for copySimpleTarget.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CopySimpleTarget> TYPE = com.exedio.cope.TypesBound.newType(CopySimpleTarget.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private CopySimpleTarget(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

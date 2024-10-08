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
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
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
			com.exedio.cope.SetValue.map(CopySimpleTarget.templateString,templateString),
			com.exedio.cope.SetValue.map(CopySimpleTarget.otherString,otherString),
			com.exedio.cope.SetValue.map(CopySimpleTarget.templateItem,templateItem),
			com.exedio.cope.SetValue.map(CopySimpleTarget.otherItem,otherItem),
		});
	}

	/**
	 * Creates a new CopySimpleTarget and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CopySimpleTarget(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #templateString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getTemplateString()
	{
		return CopySimpleTarget.templateString.get(this);
	}

	/**
	 * Returns the value of {@link #otherString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getOtherString()
	{
		return CopySimpleTarget.otherString.get(this);
	}

	/**
	 * Sets a new value for {@link #otherString}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
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
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	CopyValue getTemplateItem()
	{
		return CopySimpleTarget.templateItem.get(this);
	}

	/**
	 * Returns the value of {@link #otherItem}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	CopyValue getOtherItem()
	{
		return CopySimpleTarget.otherItem.get(this);
	}

	/**
	 * Sets a new value for {@link #otherItem}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setOtherItem(@javax.annotation.Nonnull final CopyValue otherItem)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		CopySimpleTarget.otherItem.set(this,otherItem);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for copySimpleTarget.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CopySimpleTarget> TYPE = com.exedio.cope.TypesBound.newType(CopySimpleTarget.class,CopySimpleTarget::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CopySimpleTarget(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

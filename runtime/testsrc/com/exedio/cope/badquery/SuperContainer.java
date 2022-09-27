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

package com.exedio.cope.badquery;

import com.exedio.cope.BooleanField;
import com.exedio.cope.ItemField;

class SuperContainer extends SuperItem
{
	public static final ItemField<QueryItem> queryItem = ItemField.create(QueryItem.class).toFinal();
	public static final BooleanField superflag = new BooleanField();

	/**
	 * Creates a new SuperContainer with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @param queryItem the initial value for field {@link #queryItem}.
	 * @param superflag the initial value for field {@link #superflag}.
	 * @throws com.exedio.cope.MandatoryViolationException if code, queryItem is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	SuperContainer(
				@javax.annotation.Nonnull final java.lang.String code,
				@javax.annotation.Nonnull final QueryItem queryItem,
				final boolean superflag)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.badquery.SuperItem.code.map(code),
			SuperContainer.queryItem.map(queryItem),
			SuperContainer.superflag.map(superflag),
		});
	}

	/**
	 * Creates a new SuperContainer and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected SuperContainer(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #queryItem}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public final QueryItem getQueryItem()
	{
		return SuperContainer.queryItem.get(this);
	}

	/**
	 * Returns the value of {@link #superflag}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final boolean getSuperflag()
	{
		return SuperContainer.superflag.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #superflag}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void setSuperflag(final boolean superflag)
	{
		SuperContainer.superflag.set(this,superflag);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for superContainer.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<SuperContainer> TYPE = com.exedio.cope.TypesBound.newType(SuperContainer.class,SuperContainer::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected SuperContainer(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

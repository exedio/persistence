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

final class SubContainer extends SuperContainer
{
	public static final ItemField<SuperContainer> superContainer = ItemField.create(SuperContainer.class).toFinal();
	public static final BooleanField subflag = new BooleanField().optional().defaultTo(false);

	/**
	 * Creates a new SubContainer with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @param queryItem the initial value for field {@link #queryItem}.
	 * @param superflag the initial value for field {@link #superflag}.
	 * @param superContainer the initial value for field {@link #superContainer}.
	 * @throws com.exedio.cope.MandatoryViolationException if code, queryItem, superContainer is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	SubContainer(
				@javax.annotation.Nonnull final java.lang.String code,
				@javax.annotation.Nonnull final com.exedio.cope.badquery.QueryItem queryItem,
				final boolean superflag,
				@javax.annotation.Nonnull final SuperContainer superContainer)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(com.exedio.cope.badquery.SuperItem.code,code),
			com.exedio.cope.SetValue.map(com.exedio.cope.badquery.SuperContainer.queryItem,queryItem),
			com.exedio.cope.SetValue.map(com.exedio.cope.badquery.SuperContainer.superflag,superflag),
			com.exedio.cope.SetValue.map(SubContainer.superContainer,superContainer),
		});
	}

	/**
	 * Creates a new SubContainer and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private SubContainer(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #superContainer}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	public SuperContainer getSuperContainer()
	{
		return SubContainer.superContainer.get(this);
	}

	/**
	 * Returns the value of {@link #subflag}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	public java.lang.Boolean getSubflag()
	{
		return SubContainer.subflag.get(this);
	}

	/**
	 * Sets a new value for {@link #subflag}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public void setSubflag(@javax.annotation.Nullable final java.lang.Boolean subflag)
	{
		SubContainer.subflag.set(this,subflag);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for subContainer.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<SubContainer> TYPE = com.exedio.cope.TypesBound.newType(SubContainer.class,SubContainer::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private SubContainer(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

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

package com.exedio.cope.instrument.testmodel;


class SubOfInnerSub extends InnerContainer.Sub
{


	/**
	 * Creates a new SubOfInnerSub with all the fields initially needed.
	 * @param superField the initial value for field {@link #superField}.
	 * @param superField2 the initial value for field {@link #superField2}.
	 * @param externalEnum the initial value for field {@link #externalEnum}.
	 * @param shortExternalEnum the initial value for field {@link #shortExternalEnum}.
	 * @param subField the initial value for field {@link #subField}.
	 * @param subReference the initial value for field {@link #subReference}.
	 * @throws com.exedio.cope.MandatoryViolationException if superField, superField2, externalEnum, shortExternalEnum, subField, subReference is null.
	 * @throws com.exedio.cope.StringLengthViolationException if subField violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	SubOfInnerSub(
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.InnerContainer.Target superField,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.InnerContainer.Super.InnerSuperEnum superField2,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.EnumContainer.Enum3 externalEnum,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.EnumContainer.Enum4 shortExternalEnum,
				@javax.annotation.Nonnull final java.lang.String subField,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.InnerContainer.Sub subReference)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.instrument.testmodel.InnerContainer.Super.superField.map(superField),
			com.exedio.cope.instrument.testmodel.InnerContainer.Super.superField2.map(superField2),
			com.exedio.cope.instrument.testmodel.InnerContainer.Super.externalEnum.map(externalEnum),
			com.exedio.cope.instrument.testmodel.InnerContainer.Super.shortExternalEnum.map(shortExternalEnum),
			com.exedio.cope.instrument.testmodel.InnerContainer.Sub.subField.map(subField),
			com.exedio.cope.instrument.testmodel.InnerContainer.Sub.subReference.map(subReference),
		});
	}

	/**
	 * Creates a new SubOfInnerSub and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected SubOfInnerSub(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for subOfInnerSub.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<SubOfInnerSub> TYPE = com.exedio.cope.TypesBound.newType(SubOfInnerSub.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected SubOfInnerSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

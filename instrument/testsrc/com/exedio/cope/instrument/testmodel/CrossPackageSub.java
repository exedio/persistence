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

import com.exedio.cope.StringField;

final class CrossPackageSub extends CrossPackageSuper
{
	static final StringField subField = new StringField().toFinal();

	/**
	 * Creates a new CrossPackageSub with all the fields initially needed.
	 * @param superField the initial value for field {@link #superField}.
	 * @param superField2 the initial value for field {@link #superField2}.
	 * @param externalEnum the initial value for field {@link #externalEnum}.
	 * @param shortExternalEnum the initial value for field {@link #shortExternalEnum}.
	 * @param compositeImported the initial value for field {@link #compositeImported}.
	 * @param compositeFully the initial value for field {@link #compositeFully}.
	 * @param subField the initial value for field {@link #subField}.
	 * @throws com.exedio.cope.MandatoryViolationException if superField, superField2, externalEnum, shortExternalEnum, compositeImported, compositeFully, subField is null.
	 * @throws com.exedio.cope.StringLengthViolationException if subField violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CrossPackageSub(
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.sub.SubTarget superField,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.CrossPackageSuper.SuperEnum superField2,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.EnumContainer.Enum3 externalEnum,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.EnumContainer.Enum4 shortExternalEnum,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.CompositeImported compositeImported,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.CompositeFully compositeFully,
				@javax.annotation.Nonnull final java.lang.String subField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.instrument.testmodel.CrossPackageSuper.superField.map(superField),
			com.exedio.cope.instrument.testmodel.CrossPackageSuper.superField2.map(superField2),
			com.exedio.cope.instrument.testmodel.CrossPackageSuper.externalEnum.map(externalEnum),
			com.exedio.cope.instrument.testmodel.CrossPackageSuper.shortExternalEnum.map(shortExternalEnum),
			com.exedio.cope.instrument.testmodel.CrossPackageSuper.compositeImported.map(compositeImported),
			com.exedio.cope.instrument.testmodel.CrossPackageSuper.compositeFully.map(compositeFully),
			CrossPackageSub.subField.map(subField),
		});
	}

	/**
	 * Creates a new CrossPackageSub and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CrossPackageSub(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #subField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getSubField()
	{
		return CrossPackageSub.subField.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for crossPackageSub.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CrossPackageSub> TYPE = com.exedio.cope.TypesBound.newType(CrossPackageSub.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private CrossPackageSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

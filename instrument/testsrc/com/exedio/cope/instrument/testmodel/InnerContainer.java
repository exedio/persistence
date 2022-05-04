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

import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.testmodel.EnumContainer.Enum4;

@SuppressWarnings("EmptyClass")
final class InnerContainer
{
	static class Super extends Item
	{
		static final ItemField<Target> superField = ItemField.create(Target.class).toFinal();
		static final EnumField<InnerSuperEnum> superField2 = EnumField.create(InnerSuperEnum.class).toFinal();
		static final EnumField<EnumContainer.Enum3> externalEnum = EnumField.create(EnumContainer.Enum3.class).toFinal();
		static final EnumField<Enum4> shortExternalEnum = EnumField.create(Enum4.class).toFinal();

		@SuppressWarnings("unused") // OK: Enum for EnumField must not be empty
		enum InnerSuperEnum
		{
			A, B
		}


	/**
	 * Creates a new Super with all the fields initially needed.
	 * @param superField the initial value for field {@link #superField}.
	 * @param superField2 the initial value for field {@link #superField2}.
	 * @param externalEnum the initial value for field {@link #externalEnum}.
	 * @param shortExternalEnum the initial value for field {@link #shortExternalEnum}.
	 * @throws com.exedio.cope.MandatoryViolationException if superField, superField2, externalEnum, shortExternalEnum is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	Super(
				@javax.annotation.Nonnull final Target superField,
				@javax.annotation.Nonnull final InnerSuperEnum superField2,
				@javax.annotation.Nonnull final EnumContainer.Enum3 externalEnum,
				@javax.annotation.Nonnull final Enum4 shortExternalEnum)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(Super.superField,superField),
			com.exedio.cope.SetValue.map(Super.superField2,superField2),
			com.exedio.cope.SetValue.map(Super.externalEnum,externalEnum),
			com.exedio.cope.SetValue.map(Super.shortExternalEnum,shortExternalEnum),
		});
	}

	/**
	 * Creates a new Super and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected Super(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #superField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final Target getSuperField()
	{
		return Super.superField.get(this);
	}

	/**
	 * Returns the value of {@link #superField2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final InnerSuperEnum getSuperField2()
	{
		return Super.superField2.get(this);
	}

	/**
	 * Returns the value of {@link #externalEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final EnumContainer.Enum3 getExternalEnum()
	{
		return Super.externalEnum.get(this);
	}

	/**
	 * Returns the value of {@link #shortExternalEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final Enum4 getShortExternalEnum()
	{
		return Super.shortExternalEnum.get(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for super.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<Super> TYPE = com.exedio.cope.TypesBound.newType(Super.class,Super::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected Super(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	@SuppressWarnings("UnnecessarilyQualifiedInnerClassAccess")
	static class Sub extends Super
	{
		static final StringField subField = new StringField().toFinal();
		static final ItemField<InnerContainer.Sub> subReference=ItemField.create(InnerContainer.Sub.class).toFinal();

	/**
	 * Creates a new Sub with all the fields initially needed.
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
	Sub(
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.InnerContainer.Target superField,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.InnerContainer.Super.InnerSuperEnum superField2,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.EnumContainer.Enum3 externalEnum,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.EnumContainer.Enum4 shortExternalEnum,
				@javax.annotation.Nonnull final java.lang.String subField,
				@javax.annotation.Nonnull final InnerContainer.Sub subReference)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(com.exedio.cope.instrument.testmodel.InnerContainer.Super.superField,superField),
			com.exedio.cope.SetValue.map(com.exedio.cope.instrument.testmodel.InnerContainer.Super.superField2,superField2),
			com.exedio.cope.SetValue.map(com.exedio.cope.instrument.testmodel.InnerContainer.Super.externalEnum,externalEnum),
			com.exedio.cope.SetValue.map(com.exedio.cope.instrument.testmodel.InnerContainer.Super.shortExternalEnum,shortExternalEnum),
			com.exedio.cope.SetValue.map(Sub.subField,subField),
			com.exedio.cope.SetValue.map(Sub.subReference,subReference),
		});
	}

	/**
	 * Creates a new Sub and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected Sub(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #subField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getSubField()
	{
		return Sub.subField.get(this);
	}

	/**
	 * Returns the value of {@link #subReference}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final InnerContainer.Sub getSubReference()
	{
		return Sub.subReference.get(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for sub.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<Sub> TYPE = com.exedio.cope.TypesBound.newType(Sub.class,Sub::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected Sub(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	static class Target extends Item
	{

	/**
	 * Creates a new Target with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	Target()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new Target and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected Target(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for target.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<Target> TYPE = com.exedio.cope.TypesBound.newType(Target.class,Target::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected Target(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

}

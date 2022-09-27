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
import com.exedio.cope.instrument.testmodel.EnumContainer.Enum4;
import com.exedio.cope.instrument.testmodel.sub.SubTarget;
import com.exedio.cope.pattern.BlockField;
import com.exedio.cope.pattern.CompositeField;

class CrossPackageSuper extends Item
{
	static final ItemField<SubTarget> superField = ItemField.create(SubTarget.class).toFinal();
	static final EnumField<SuperEnum> superField2 = EnumField.create(SuperEnum.class).toFinal();
	static final EnumField<EnumContainer.Enum3> externalEnum = EnumField.create(EnumContainer.Enum3.class).toFinal();
	static final EnumField<Enum4> shortExternalEnum = EnumField.create(Enum4.class).toFinal();

	@SuppressWarnings("unused") // OK: Enum for EnumField must not be empty
	enum SuperEnum
	{
		A, B
	}

	static final CompositeField<CompositeImported> compositeImported = CompositeField.create(CompositeImported.class).toFinal();
	static final CompositeField<CompositeFully   > compositeFully    = CompositeField.create(CompositeFully   .class).toFinal();
	static final BlockField<BlockImported> blockImported = BlockField.create(BlockImported.TYPE);
	static final BlockField<BlockFully   > blockFully    = BlockField.create(BlockFully   .TYPE);

	/**
	 * Creates a new CrossPackageSuper with all the fields initially needed.
	 * @param superField the initial value for field {@link #superField}.
	 * @param superField2 the initial value for field {@link #superField2}.
	 * @param externalEnum the initial value for field {@link #externalEnum}.
	 * @param shortExternalEnum the initial value for field {@link #shortExternalEnum}.
	 * @param compositeImported the initial value for field {@link #compositeImported}.
	 * @param compositeFully the initial value for field {@link #compositeFully}.
	 * @throws com.exedio.cope.MandatoryViolationException if superField, superField2, externalEnum, shortExternalEnum, compositeImported, compositeFully is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CrossPackageSuper(
				@javax.annotation.Nonnull final SubTarget superField,
				@javax.annotation.Nonnull final SuperEnum superField2,
				@javax.annotation.Nonnull final EnumContainer.Enum3 externalEnum,
				@javax.annotation.Nonnull final Enum4 shortExternalEnum,
				@javax.annotation.Nonnull final CompositeImported compositeImported,
				@javax.annotation.Nonnull final CompositeFully compositeFully)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			CrossPackageSuper.superField.map(superField),
			CrossPackageSuper.superField2.map(superField2),
			CrossPackageSuper.externalEnum.map(externalEnum),
			CrossPackageSuper.shortExternalEnum.map(shortExternalEnum),
			CrossPackageSuper.compositeImported.map(compositeImported),
			CrossPackageSuper.compositeFully.map(compositeFully),
		});
	}

	/**
	 * Creates a new CrossPackageSuper and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected CrossPackageSuper(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #superField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final SubTarget getSuperField()
	{
		return CrossPackageSuper.superField.get(this);
	}

	/**
	 * Returns the value of {@link #superField2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final SuperEnum getSuperField2()
	{
		return CrossPackageSuper.superField2.get(this);
	}

	/**
	 * Returns the value of {@link #externalEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final EnumContainer.Enum3 getExternalEnum()
	{
		return CrossPackageSuper.externalEnum.get(this);
	}

	/**
	 * Returns the value of {@link #shortExternalEnum}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final Enum4 getShortExternalEnum()
	{
		return CrossPackageSuper.shortExternalEnum.get(this);
	}

	/**
	 * Returns the value of {@link #compositeImported}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final CompositeImported getCompositeImported()
	{
		return CrossPackageSuper.compositeImported.get(this);
	}

	/**
	 * Returns the value of {@link #compositeFully}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final CompositeFully getCompositeFully()
	{
		return CrossPackageSuper.compositeFully.get(this);
	}

	/**
	 * Returns the value of {@link #blockImported}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final BlockImported blockImported()
	{
		return CrossPackageSuper.blockImported.get(this);
	}

	/**
	 * Returns the value of {@link #blockFully}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final BlockFully blockFully()
	{
		return CrossPackageSuper.blockFully.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for crossPackageSuper.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<CrossPackageSuper> TYPE = com.exedio.cope.TypesBound.newType(CrossPackageSuper.class,CrossPackageSuper::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected CrossPackageSuper(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

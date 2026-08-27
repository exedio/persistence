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

import com.exedio.cope.Item;
import com.exedio.cope.instrument.testfeature.NullabilityFeature;

public final class NullabilityItem extends Item
{
	static final NullabilityFeature optional = new NullabilityFeature(true);
	static final NullabilityFeature mandatory = new NullabilityFeature(false);

	/**
	 * Creates a new NullabilityItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public NullabilityItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new NullabilityItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private NullabilityItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="defaultReturnAndParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void defaultOptionalReturnAndParameter(final java.lang.Object optional)
	{
		NullabilityItem.optional.defaultReturnAndParameter(this,optional);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="conditionalNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static java.lang.Object conditionalOptionalNullableReturn()
	{
		return NullabilityItem.optional.conditionalNullableReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="conditionalNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void conditionalOptionalNullableParameter(@javax.annotation.Nullable final java.lang.Object optional)
	{
		NullabilityItem.optional.conditionalNullableParameter(this,optional);
	}

	/**
	 * This method can return null.
	 * @return may be null
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static java.lang.Object javaxOptionalNullableReturn()
	{
		return NullabilityItem.optional.javaxNullableReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.lang.Object javaxOptionalNonnullReturn()
	{
		return NullabilityItem.optional.javaxNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.lang.Object javaxOptionalNullableNonnullReturn()
	{
		return NullabilityItem.optional.javaxNullableNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void javaxOptionalNullableParameter(@javax.annotation.Nullable final java.lang.Object optional)
	{
		NullabilityItem.optional.javaxNullableParameter(optional);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNonnullParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void javaxOptionalNonnullParameter(@javax.annotation.Nonnull final java.lang.Object optional)
	{
		NullabilityItem.optional.javaxNonnullParameter(optional);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="defaultReturnAndParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void defaultMandatoryReturnAndParameter(final java.lang.Object mandatory)
	{
		NullabilityItem.mandatory.defaultReturnAndParameter(this,mandatory);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="conditionalNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.lang.Object conditionalMandatoryNullableReturn()
	{
		return NullabilityItem.mandatory.conditionalNullableReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="conditionalNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void conditionalMandatoryNullableParameter(@javax.annotation.Nonnull final java.lang.Object mandatory)
	{
		NullabilityItem.mandatory.conditionalNullableParameter(this,mandatory);
	}

	/**
	 * This method can return null.
	 * @return may be null
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static java.lang.Object javaxMandatoryNullableReturn()
	{
		return NullabilityItem.mandatory.javaxNullableReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.lang.Object javaxMandatoryNonnullReturn()
	{
		return NullabilityItem.mandatory.javaxNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableNonnullReturn")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.lang.Object javaxMandatoryNullableNonnullReturn()
	{
		return NullabilityItem.mandatory.javaxNullableNonnullReturn();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNullableParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void javaxMandatoryNullableParameter(@javax.annotation.Nullable final java.lang.Object mandatory)
	{
		NullabilityItem.mandatory.javaxNullableParameter(mandatory);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="javaxNonnullParameter")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void javaxMandatoryNonnullParameter(@javax.annotation.Nonnull final java.lang.Object mandatory)
	{
		NullabilityItem.mandatory.javaxNonnullParameter(mandatory);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for nullabilityItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<NullabilityItem> TYPE = com.exedio.cope.TypesBound.newType(NullabilityItem.class,NullabilityItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private NullabilityItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

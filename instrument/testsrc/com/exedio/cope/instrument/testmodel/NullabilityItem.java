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

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCanReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static java.lang.Object allOptionalCanReturnNull()
	{
		return NullabilityItem.optional.allCanReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCannotReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.lang.Object allOptionalCannotReturnNull()
	{
		return NullabilityItem.optional.allCannotReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="onlyOptionalsCanReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static java.lang.Object onlyOptionalOptionalsCanReturnNull()
	{
		return NullabilityItem.optional.onlyOptionalsCanReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCanTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void allOptionalCanTakeNull(@javax.annotation.Nullable final java.lang.Object optional)
	{
		NullabilityItem.optional.allCanTakeNull(optional);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCannotTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void allOptionalCannotTakeNull(@javax.annotation.Nonnull final java.lang.Object optional)
	{
		NullabilityItem.optional.allCannotTakeNull(optional);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="onlyOptionalsCanTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void onlyOptionalOptionalsCanTakeNull(@javax.annotation.Nullable final java.lang.Object optional)
	{
		NullabilityItem.optional.onlyOptionalsCanTakeNull(this,optional);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCanReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static java.lang.Object allMandatoryCanReturnNull()
	{
		return NullabilityItem.mandatory.allCanReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCannotReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.lang.Object allMandatoryCannotReturnNull()
	{
		return NullabilityItem.mandatory.allCannotReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="onlyOptionalsCanReturnNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static java.lang.Object onlyMandatoryOptionalsCanReturnNull()
	{
		return NullabilityItem.mandatory.onlyOptionalsCanReturnNull();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCanTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void allMandatoryCanTakeNull(@javax.annotation.Nullable final java.lang.Object mandatory)
	{
		NullabilityItem.mandatory.allCanTakeNull(mandatory);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="allCannotTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void allMandatoryCannotTakeNull(@javax.annotation.Nonnull final java.lang.Object mandatory)
	{
		NullabilityItem.mandatory.allCannotTakeNull(mandatory);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="onlyOptionalsCanTakeNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void onlyMandatoryOptionalsCanTakeNull(@javax.annotation.Nonnull final java.lang.Object mandatory)
	{
		NullabilityItem.mandatory.onlyOptionalsCanTakeNull(this,mandatory);
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

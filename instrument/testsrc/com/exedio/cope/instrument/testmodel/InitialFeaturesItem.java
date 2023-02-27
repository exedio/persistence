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
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.testfeature.SimpleSettable;

final class InitialFeaturesItem extends Item
{
	static final SimpleSettable notInitial=new SimpleSettable(false);

	static final SimpleSettable initialByDefault=new SimpleSettable(true);

	@WrapperInitial
	static final SimpleSettable customInitial=new SimpleSettable(false);

	@WrapperInitial(false)
	static final SimpleSettable customNonInitial=new SimpleSettable(true);

	@WrapperInitial
	static final SimpleSettable redundantInitial=new SimpleSettable(true);

	@WrapperInitial(false)
	static final SimpleSettable redundantNonInitial=new SimpleSettable(false);


	/**
	 * Creates a new InitialFeaturesItem with all the fields initially needed.
	 * @param initialByDefault the initial value for field {@link #initialByDefault}.
	 * @param customInitial the initial value for field {@link #customInitial}.
	 * @param redundantInitial the initial value for field {@link #redundantInitial}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	InitialFeaturesItem(
				@javax.annotation.Nullable final java.lang.String initialByDefault,
				@javax.annotation.Nullable final java.lang.String customInitial,
				@javax.annotation.Nullable final java.lang.String redundantInitial)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(InitialFeaturesItem.initialByDefault,initialByDefault),
			com.exedio.cope.SetValue.map(InitialFeaturesItem.customInitial,customInitial),
			com.exedio.cope.SetValue.map(InitialFeaturesItem.redundantInitial,redundantInitial),
		});
	}

	/**
	 * Creates a new InitialFeaturesItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private InitialFeaturesItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	java.lang.String oneNotInitial()
	{
		return InitialFeaturesItem.notInitial.one(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	java.lang.String oneInitialByDefault()
	{
		return InitialFeaturesItem.initialByDefault.one(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	java.lang.String oneCustomInitial()
	{
		return InitialFeaturesItem.customInitial.one(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	java.lang.String oneCustomNonInitial()
	{
		return InitialFeaturesItem.customNonInitial.one(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	java.lang.String oneRedundantInitial()
	{
		return InitialFeaturesItem.redundantInitial.one(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="one")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	java.lang.String oneRedundantNonInitial()
	{
		return InitialFeaturesItem.redundantNonInitial.one(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for initialFeaturesItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<InitialFeaturesItem> TYPE = com.exedio.cope.TypesBound.newType(InitialFeaturesItem.class,InitialFeaturesItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private InitialFeaturesItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

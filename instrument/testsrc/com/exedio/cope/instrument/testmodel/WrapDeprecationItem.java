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
import com.exedio.cope.instrument.testfeature.WrapDeprecationFeature;

final class WrapDeprecationItem extends Item
{
	static final WrapDeprecationFeature good = new WrapDeprecationFeature();
	@Deprecated
	static final WrapDeprecationFeature bad  = new WrapDeprecationFeature();


	/**
	 * Creates a new WrapDeprecationItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	WrapDeprecationItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new WrapDeprecationItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private WrapDeprecationItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="bare")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int bareGood()
	{
		return WrapDeprecationItem.good.bare(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="goneAlmost")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	int goneGoodAlmost()
	{
		return WrapDeprecationItem.good.goneAlmost(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="bare")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	int bareBad()
	{
		return WrapDeprecationItem.bad.bare(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="goneAlmost")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@java.lang.Deprecated
	int goneBadAlmost()
	{
		return WrapDeprecationItem.bad.goneAlmost(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for wrapDeprecationItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<WrapDeprecationItem> TYPE = com.exedio.cope.TypesBound.newType(WrapDeprecationItem.class,WrapDeprecationItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private WrapDeprecationItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

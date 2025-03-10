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

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.instrument.testlib.OwnerTypeSuper;

@WrapperType(constructorSuppressWarnings="rawtypes") // TODO needed to avoid compiler error in initial constructor create by instrumentor: method map in class SetValue cannot be applied to given types;
public class OwnerTypeItem extends OwnerTypeSuper
{
	/**
	 * Creates a new OwnerTypeItem with all the fields initially needed.
	 * @param plainFeature the initial value for field {@link #plainFeature}.
	 * @param byClassFeature the initial value for field {@link #byClassFeature}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess","rawtypes"})
	protected OwnerTypeItem(
				@javax.annotation.Nullable final com.exedio.cope.instrument.testlib.OwnerTypeSuper.MyInterface<?> plainFeature,
				@javax.annotation.Nullable final java.lang.Class<? extends com.exedio.cope.instrument.testlib.OwnerTypeSuper.MyInterface> byClassFeature)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(com.exedio.cope.instrument.testlib.OwnerTypeSuper.plainFeature,plainFeature),
			com.exedio.cope.SetValue.map(com.exedio.cope.instrument.testlib.OwnerTypeSuper.byClassFeature,byClassFeature),
		});
	}

	/**
	 * Creates a new OwnerTypeItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected OwnerTypeItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for ownerTypeItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<OwnerTypeItem> TYPE = com.exedio.cope.TypesBound.newType(OwnerTypeItem.class,OwnerTypeItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected OwnerTypeItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

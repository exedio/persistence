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

package com.exedio.cope.instrument.testlib;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.instrument.testfeature.OwnerTypeFeatureByClass;
import com.exedio.cope.instrument.testfeature.OwnerTypeFeaturePlain;

@WrapperType(constructorSuppressWarnings="rawtypes") // TODO needed to avoid compiler error in initial constructor create by instrumentor: method map in class SetValue cannot be applied to given types;
public class OwnerTypeSuper extends Item
{
	/**
	 * @see WrapperType#wildcardClass()
	 */
	@WrapInterim
	@SuppressWarnings("unchecked")
	private static final Class<MyInterface<?>> myInterfaceClassWildcard = (Class<MyInterface<?>>)((Class<?>)MyInterface.class);
	protected static final OwnerTypeFeaturePlain<MyInterface<?>> plainFeature = OwnerTypeFeaturePlain.create(myInterfaceClassWildcard);
	@SuppressWarnings("rawtypes") // TODO needed to avoid compiler error in initial constructor create by instrumentor: method map in class SetValue cannot be applied to given types;
	protected static final OwnerTypeFeatureByClass<MyInterface> byClassFeature = OwnerTypeFeatureByClass.create(MyInterface.class);

	@SuppressWarnings({"InterfaceNeverImplemented", "MarkerInterface", "unused"}) // OK: just a dummy
	public interface MyInterface<DUMMY>
	{
	}


	/**
	 * Creates a new OwnerTypeSuper with all the fields initially needed.
	 * @param plainFeature the initial value for field {@link #plainFeature}.
	 * @param byClassFeature the initial value for field {@link #byClassFeature}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess","rawtypes"})
	protected OwnerTypeSuper(
				@javax.annotation.Nullable final MyInterface<?> plainFeature,
				@javax.annotation.Nullable final java.lang.Class<? extends MyInterface> byClassFeature)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(OwnerTypeSuper.plainFeature,plainFeature),
			com.exedio.cope.SetValue.map(OwnerTypeSuper.byClassFeature,byClassFeature),
		});
	}

	/**
	 * Creates a new OwnerTypeSuper and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected OwnerTypeSuper(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1L;

	/**
	 * The persistent type information for ownerTypeSuper.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<OwnerTypeSuper> TYPE = com.exedio.cope.TypesBound.newType(OwnerTypeSuper.class,OwnerTypeSuper::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected OwnerTypeSuper(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

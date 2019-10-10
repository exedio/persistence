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

import com.exedio.cope.instrument.testfeature.GenericFeatureClass;

public final class GenericSimpleSub extends GenericSimpleSuper<Long>
{
	static final GenericFeatureClass fromSub = new GenericFeatureClass();

	/**
	 * Creates a new GenericSimpleSub with all the fields initially needed.
	 * @param toSub the initial value for field {@link #toSub}.
	 * @param toSuper the initial value for field {@link #toSuper}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	GenericSimpleSub(
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.GenericSimpleSub toSub,
				@javax.annotation.Nonnull final com.exedio.cope.instrument.testmodel.GenericSimpleSuper<?> toSuper)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.instrument.testmodel.GenericSimpleSuper.toSub.map(toSub),
			com.exedio.cope.instrument.testmodel.GenericSimpleSuper.toSuper.map(toSuper),
		});
	}

	/**
	 * Creates a new GenericSimpleSub and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private GenericSimpleSub(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="method")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static GenericSimpleSub methodFromSub()
	{
		return GenericSimpleSub.fromSub.method(GenericSimpleSub.class);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="methodNested")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static java.util.List<GenericSimpleSub> methodFromSubNested()
	{
		return GenericSimpleSub.fromSub.methodNested(GenericSimpleSub.class);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for genericSimpleSub.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<GenericSimpleSub> TYPE = com.exedio.cope.TypesBound.newType(GenericSimpleSub.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private GenericSimpleSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

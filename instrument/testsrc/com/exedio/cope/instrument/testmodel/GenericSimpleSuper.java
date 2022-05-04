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
import com.exedio.cope.instrument.testfeature.GenericFeatureClass;
import com.exedio.cope.instrument.testfeature.GenericFeatureReference;
import com.exedio.cope.misc.ReflectionTypes;

/**
 * @param <N> just for tests
 */
@SuppressWarnings("UnnecessarilyQualifiedInnerClassAccess")
public class GenericSimpleSuper<N extends Number> extends Item
{
	static final GenericFeatureReference<GenericSimpleSub  > toSub   = GenericFeatureReference.create(GenericSimpleSub.class, GenericSimpleSub.class);
	static final GenericFeatureReference<GenericSimpleSuper<?>> toSuper = GenericFeatureReference.create(GenericSimpleSuper.classWildcard.value, ReflectionTypes.parameterized(GenericSimpleSuper.class, ReflectionTypes.sub(Object.class)));

	static final GenericFeatureClass fromSuper = new GenericFeatureClass();


	/**
	 * Creates a new GenericSimpleSuper with all the fields initially needed.
	 * @param toSub the initial value for field {@link #toSub}.
	 * @param toSuper the initial value for field {@link #toSuper}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	GenericSimpleSuper(
				@javax.annotation.Nonnull final GenericSimpleSub toSub,
				@javax.annotation.Nonnull final GenericSimpleSuper<?> toSuper)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(GenericSimpleSuper.toSub,toSub),
			com.exedio.cope.SetValue.map(GenericSimpleSuper.toSuper,toSuper),
		});
	}

	/**
	 * Creates a new GenericSimpleSuper and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected GenericSimpleSuper(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="method")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final GenericSimpleSub methodToSub(final GenericSimpleSub toSub)
	{
		return GenericSimpleSuper.toSub.method(this,toSub);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="method")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final GenericSimpleSuper<?> methodToSuper(final GenericSimpleSuper<?> toSuper)
	{
		return GenericSimpleSuper.toSuper.method(this,toSuper);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="method")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final GenericSimpleSuper<?> methodFromSuper()
	{
		return GenericSimpleSuper.fromSuper.method(classWildcard.value);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="methodNested")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static final java.util.List<GenericSimpleSuper<?>> methodFromSuperNested()
	{
		return GenericSimpleSuper.fromSuper.methodNested(classWildcard.value);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * Use GenericSimpleSuper.classWildcard.value instead of GenericSimpleSuper.class to avoid rawtypes warnings.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(wildcardClass=...)
	public static final class classWildcard { public static final java.lang.Class<GenericSimpleSuper<?>> value = com.exedio.cope.ItemWildcardCast.cast(GenericSimpleSuper.class); private classWildcard(){} }

	/**
	 * The persistent type information for genericSimpleSuper.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<GenericSimpleSuper<?>> TYPE = com.exedio.cope.TypesBound.newType(classWildcard.value,GenericSimpleSuper::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected GenericSimpleSuper(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

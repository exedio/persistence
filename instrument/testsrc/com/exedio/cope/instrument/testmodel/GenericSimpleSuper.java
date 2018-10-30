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
import com.exedio.cope.instrument.WrapInterim;
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


	@WrapInterim
	public static final class classWildcard
	{
		@WrapInterim
		public static final Class<GenericSimpleSuper<?>> value = make();

		// method needed because there is probably a bug in javac, not needed in eclipse
		@SuppressWarnings("unchecked")
		@WrapInterim
		private static Class<GenericSimpleSuper<?>> make()
		{
			return (Class<GenericSimpleSuper<?>>)(Class<?>)GenericSimpleSuper.class;
		}

		private classWildcard()
		{
			// prevent instantiation
		}
	}


	/**
	 * Creates a new GenericSimpleSuper with all the fields initially needed.
	 * @param toSub the initial value for field {@link #toSub}.
	 * @param toSuper the initial value for field {@link #toSuper}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	GenericSimpleSuper(
				@javax.annotation.Nonnull final GenericSimpleSub toSub,
				@javax.annotation.Nonnull final GenericSimpleSuper<?> toSuper)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			GenericSimpleSuper.toSub.map(toSub),
			GenericSimpleSuper.toSuper.map(toSuper),
		});
	}

	/**
	 * Creates a new GenericSimpleSuper and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected GenericSimpleSuper(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	final GenericSimpleSub methodToSub(final GenericSimpleSub toSub)
	{
		return GenericSimpleSuper.toSub.method(this,toSub);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	final GenericSimpleSuper<?> methodToSuper(final GenericSimpleSuper<?> toSuper)
	{
		return GenericSimpleSuper.toSuper.method(this,toSuper);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	static final GenericSimpleSuper<?> methodFromSuper()
	{
		return GenericSimpleSuper.fromSuper.method(classWildcard.value);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="methodNested")
	static final java.util.List<GenericSimpleSuper<?>> methodFromSuperNested()
	{
		return GenericSimpleSuper.fromSuper.methodNested(classWildcard.value);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for genericSimpleSuper.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<GenericSimpleSuper<?>> TYPE = com.exedio.cope.TypesBound.newType(classWildcard.value);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected GenericSimpleSuper(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

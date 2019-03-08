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
import java.util.Collection;

/**
 * @param <N> just for tests
 * @param <L> just for tests
 */
@SuppressWarnings("UnnecessarilyQualifiedInnerClassAccess")
public class GenericComplexSuper<N extends Number, L extends Collection<String>> extends Item
{
	static final GenericFeatureReference<GenericComplexSub> toSub = GenericFeatureReference.create(GenericComplexSub.class, GenericComplexSub.class);
	@SuppressWarnings("StaticInitializerReferencesSubClass")
	static final GenericFeatureReference<GenericComplexMid<?>> toMid  = GenericFeatureReference.create(GenericComplexMid.classWildcard.value, ReflectionTypes.parameterized(GenericComplexMid.class, ReflectionTypes.sub(Object.class)));
	static final GenericFeatureReference<GenericComplexSuper<?,?>> toSuper = GenericFeatureReference.create(GenericComplexSuper.classWildcard.value, ReflectionTypes.parameterized(GenericComplexSuper.class, ReflectionTypes.sub(Object.class), ReflectionTypes.sub(Object.class)));

	static final GenericFeatureClass fromSuper = new GenericFeatureClass();


	/**
	 * Creates a new GenericComplexSuper with all the fields initially needed.
	 * @param toSub the initial value for field {@link #toSub}.
	 * @param toMid the initial value for field {@link #toMid}.
	 * @param toSuper the initial value for field {@link #toSuper}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	GenericComplexSuper(
				@javax.annotation.Nonnull final GenericComplexSub toSub,
				@javax.annotation.Nonnull final GenericComplexMid<?> toMid,
				@javax.annotation.Nonnull final GenericComplexSuper<?,?> toSuper)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			GenericComplexSuper.toSub.map(toSub),
			GenericComplexSuper.toMid.map(toMid),
			GenericComplexSuper.toSuper.map(toSuper),
		});
	}

	/**
	 * Creates a new GenericComplexSuper and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	protected GenericComplexSuper(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	final GenericComplexSub methodToSub(final GenericComplexSub toSub)
	{
		return GenericComplexSuper.toSub.method(this,toSub);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	final GenericComplexMid<?> methodToMid(final GenericComplexMid<?> toMid)
	{
		return GenericComplexSuper.toMid.method(this,toMid);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	final GenericComplexSuper<?,?> methodToSuper(final GenericComplexSuper<?,?> toSuper)
	{
		return GenericComplexSuper.toSuper.method(this,toSuper);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	static final GenericComplexSuper<?,?> methodFromSuper()
	{
		return GenericComplexSuper.fromSuper.method(classWildcard.value);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="methodNested")
	static final java.util.List<GenericComplexSuper<?,?>> methodFromSuperNested()
	{
		return GenericComplexSuper.fromSuper.methodNested(classWildcard.value);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * Use GenericComplexSuper.classWildcard.value instead of GenericComplexSuper.class to avoid rawtypes warnings.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(wildcardClass=...)
	public static final class classWildcard { public static final java.lang.Class<GenericComplexSuper<?,?>> value = com.exedio.cope.ItemWildcardCast.cast(GenericComplexSuper.class); private classWildcard(){} }

	/**
	 * The persistent type information for genericComplexSuper.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<GenericComplexSuper<?,?>> TYPE = com.exedio.cope.TypesBound.newType(classWildcard.value);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	protected GenericComplexSuper(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

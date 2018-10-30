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
import com.exedio.cope.instrument.testfeature.SettableFixed;
import com.exedio.cope.instrument.testfeature.SettableFixedParam;
import com.exedio.cope.instrument.testfeature.SettableFixedParamBound;
import com.exedio.cope.instrument.testfeature.SettableFixedParamBoundSub;
import com.exedio.cope.instrument.testfeature.SettableFixedParamImpl;
import com.exedio.cope.instrument.testfeature.SettableFixedParamSub;
import com.exedio.cope.instrument.testfeature.SettableFixedParamWildcard;
import com.exedio.cope.instrument.testfeature.SettableFixedParamWildcardSub;
import com.exedio.cope.instrument.testfeature.SettableFixedSub;
import com.exedio.cope.instrument.testfeature.SettableOpen;
import com.exedio.cope.misc.ReflectionTypes;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

final class SettableItem extends Item
{
	static final SettableOpen<AtomicInteger> open = new SettableOpen<>(AtomicInteger.class);
	static final SettableOpen<AtomicReference<AtomicLong>> openParam = new SettableOpen<>(
		ReflectionTypes.parameterized(AtomicReference.class, AtomicLong.class)
	);
	static final SettableOpen<AtomicReference<?>> openParamWildcard = new SettableOpen<>(
		ReflectionTypes.parameterized(AtomicReference.class, ReflectionTypes.sub(Object.class))
	);
	static final SettableOpen<AtomicReference<? extends AtomicLong>> openParamBound = new SettableOpen<>(
		ReflectionTypes.parameterized(AtomicReference.class, ReflectionTypes.sub(AtomicLong.class))
	);

	static final SettableFixed fixed = new SettableFixed();
	static final SettableFixedSub fixedSub = new SettableFixedSub();

	static final SettableFixedParam fixedParam = new SettableFixedParam();
	static final SettableFixedParamSub fixedParamSub = new SettableFixedParamSub();
	static final SettableFixedParamImpl fixedParamImpl = new SettableFixedParamImpl();

	static final SettableFixedParamWildcard fixedParamWildcard = new SettableFixedParamWildcard();
	static final SettableFixedParamWildcardSub fixedParamWildcardSub = new SettableFixedParamWildcardSub();

	static final SettableFixedParamBound fixedParamBound = new SettableFixedParamBound();
	static final SettableFixedParamBoundSub fixedParamBoundSub = new SettableFixedParamBoundSub();


	/**
	 * Creates a new SettableItem with all the fields initially needed.
	 * @param open the initial value for field {@link #open}.
	 * @param openParam the initial value for field {@link #openParam}.
	 * @param openParamWildcard the initial value for field {@link #openParamWildcard}.
	 * @param openParamBound the initial value for field {@link #openParamBound}.
	 * @param fixed the initial value for field {@link #fixed}.
	 * @param fixedSub the initial value for field {@link #fixedSub}.
	 * @param fixedParam the initial value for field {@link #fixedParam}.
	 * @param fixedParamSub the initial value for field {@link #fixedParamSub}.
	 * @param fixedParamImpl the initial value for field {@link #fixedParamImpl}.
	 * @param fixedParamWildcard the initial value for field {@link #fixedParamWildcard}.
	 * @param fixedParamWildcardSub the initial value for field {@link #fixedParamWildcardSub}.
	 * @param fixedParamBound the initial value for field {@link #fixedParamBound}.
	 * @param fixedParamBoundSub the initial value for field {@link #fixedParamBoundSub}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	SettableItem(
				@javax.annotation.Nonnull final AtomicInteger open,
				@javax.annotation.Nonnull final AtomicReference<AtomicLong> openParam,
				@javax.annotation.Nonnull final AtomicReference<?> openParamWildcard,
				@javax.annotation.Nonnull final AtomicReference<? extends AtomicLong> openParamBound,
				@javax.annotation.Nonnull final java.util.concurrent.atomic.AtomicBoolean fixed,
				@javax.annotation.Nonnull final java.util.concurrent.atomic.AtomicBoolean fixedSub,
				@javax.annotation.Nonnull final java.util.concurrent.atomic.AtomicReference<java.util.concurrent.atomic.AtomicBoolean> fixedParam,
				@javax.annotation.Nonnull final java.util.concurrent.atomic.AtomicReference<java.util.concurrent.atomic.AtomicBoolean> fixedParamSub,
				@javax.annotation.Nonnull final java.util.concurrent.atomic.AtomicReference<java.util.concurrent.atomic.AtomicBoolean> fixedParamImpl,
				@javax.annotation.Nonnull final java.util.concurrent.atomic.AtomicReference<?> fixedParamWildcard,
				@javax.annotation.Nonnull final java.util.concurrent.atomic.AtomicReference<?> fixedParamWildcardSub,
				@javax.annotation.Nonnull final java.util.concurrent.atomic.AtomicReference<? extends java.util.concurrent.atomic.AtomicLong> fixedParamBound,
				@javax.annotation.Nonnull final java.util.concurrent.atomic.AtomicReference<? extends java.util.concurrent.atomic.AtomicLong> fixedParamBoundSub)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			SettableItem.open.map(open),
			SettableItem.openParam.map(openParam),
			SettableItem.openParamWildcard.map(openParamWildcard),
			SettableItem.openParamBound.map(openParamBound),
			SettableItem.fixed.map(fixed),
			SettableItem.fixedSub.map(fixedSub),
			SettableItem.fixedParam.map(fixedParam),
			SettableItem.fixedParamSub.map(fixedParamSub),
			SettableItem.fixedParamImpl.map(fixedParamImpl),
			SettableItem.fixedParamWildcard.map(fixedParamWildcard),
			SettableItem.fixedParamWildcardSub.map(fixedParamWildcardSub),
			SettableItem.fixedParamBound.map(fixedParamBound),
			SettableItem.fixedParamBoundSub.map(fixedParamBoundSub),
		});
	}

	/**
	 * Creates a new SettableItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private SettableItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	static AtomicInteger methodOpen(final AtomicInteger open)
	{
		return SettableItem.open.method(open);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	static AtomicReference<AtomicLong> methodOpenParam(final AtomicReference<AtomicLong> openParam)
	{
		return SettableItem.openParam.method(openParam);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	static AtomicReference<?> methodOpenParamWildcard(final AtomicReference<?> openParamWildcard)
	{
		return SettableItem.openParamWildcard.method(openParamWildcard);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="method")
	static AtomicReference<? extends AtomicLong> methodOpenParamBound(final AtomicReference<? extends AtomicLong> openParamBound)
	{
		return SettableItem.openParamBound.method(openParamBound);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for settableItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<SettableItem> TYPE = com.exedio.cope.TypesBound.newType(SettableItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private SettableItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

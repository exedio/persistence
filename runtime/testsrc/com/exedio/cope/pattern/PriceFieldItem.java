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

package com.exedio.cope.pattern;

import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static com.exedio.cope.pattern.Price.storeOf;

import com.exedio.cope.Item;
import com.exedio.cope.SetValue;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(genericConstructor=PACKAGE)
public final class PriceFieldItem extends Item
{
	static final PriceField finalPrice = new PriceField().toFinal();
	static final PriceField optionalPrice = new PriceField().optional();
	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // TODO instrumentor does not support static imports
	static final PriceField bigPrice = new PriceField().min(Price.storeOf(5000));

	static PriceFieldItem n(final Price optionalPrice, final Price finalPrice)
	{
		//noinspection UnnecessarilyQualifiedStaticUsage
		return new PriceFieldItem(new com.exedio.cope.SetValue<?>[]{
				SetValue.map(PriceFieldItem.finalPrice, finalPrice),
				SetValue.map(PriceFieldItem.optionalPrice, optionalPrice),
				SetValue.map(PriceFieldItem.bigPrice, storeOf(8888)),
			});
	}


	/**
	 * Creates a new PriceFieldItem with all the fields initially needed.
	 * @param finalPrice the initial value for field {@link #finalPrice}.
	 * @param bigPrice the initial value for field {@link #bigPrice}.
	 * @throws com.exedio.cope.LongRangeViolationException if bigPrice violates its range constraint.
	 * @throws com.exedio.cope.MandatoryViolationException if finalPrice, bigPrice is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	PriceFieldItem(
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Price finalPrice,
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Price bigPrice)
			throws
				com.exedio.cope.LongRangeViolationException,
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(PriceFieldItem.finalPrice,finalPrice),
			com.exedio.cope.SetValue.map(PriceFieldItem.bigPrice,bigPrice),
		});
	}

	/**
	 * Creates a new PriceFieldItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	PriceFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #finalPrice}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.Price getFinalPrice()
	{
		return PriceFieldItem.finalPrice.get(this);
	}

	/**
	 * Returns the value of {@link #optionalPrice}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.Price getOptionalPrice()
	{
		return PriceFieldItem.optionalPrice.get(this);
	}

	/**
	 * Sets a new value for {@link #optionalPrice}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setOptionalPrice(@javax.annotation.Nullable final com.exedio.cope.pattern.Price optionalPrice)
	{
		PriceFieldItem.optionalPrice.set(this,optionalPrice);
	}

	/**
	 * Returns the value of {@link #bigPrice}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.Price getBigPrice()
	{
		return PriceFieldItem.bigPrice.get(this);
	}

	/**
	 * Sets a new value for {@link #bigPrice}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setBigPrice(@javax.annotation.Nonnull final com.exedio.cope.pattern.Price bigPrice)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.LongRangeViolationException
	{
		PriceFieldItem.bigPrice.set(this,bigPrice);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for priceFieldItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<PriceFieldItem> TYPE = com.exedio.cope.TypesBound.newType(PriceFieldItem.class,PriceFieldItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private PriceFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

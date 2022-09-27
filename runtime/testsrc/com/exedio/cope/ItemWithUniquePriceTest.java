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

package com.exedio.cope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Price;
import com.exedio.cope.pattern.PriceField;
import org.junit.jupiter.api.Test;

public class ItemWithUniquePriceTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(ItemWithUniquePrice.TYPE);

	public ItemWithUniquePriceTest()
	{
		super(MODEL);
	}

	@Test void search()
	{
		final ItemWithUniquePrice f = new ItemWithUniquePrice(Price.storeOf(234), "x");
		assertEquals(null, ItemWithUniquePrice.forPriceAndOther(123L, "other"));
		assertEquals(null, ItemWithUniquePrice.forPriceAndOther(234L, "other"));
		assertEquals(null, ItemWithUniquePrice.forPriceAndOther(123L, "x"));
		assertEquals(f,    ItemWithUniquePrice.forPriceAndOther(234L, "x"));
	}

	@Test void noDuplicates()
	{
		new ItemWithUniquePrice(Price.storeOf(234), "x");
		new ItemWithUniquePrice(Price.storeOf(234), "y"); // different 'other'
		new ItemWithUniquePrice(Price.storeOf(235), "x"); // different 'price'
		try
		{
			new ItemWithUniquePrice(Price.storeOf(234), "x");
			fail();
		}
		catch (final UniqueViolationException e)
		{
			assertEquals(ItemWithUniquePrice.priceAndOther, e.getFeature());
		}
		assertEquals(3, ItemWithUniquePrice.TYPE.search().size());
	}

	@WrapperType(indent=2)
	private static final class ItemWithUniquePrice extends Item
	{
		public static final PriceField price = new PriceField();

		public static final StringField other = new StringField();

		public static final UniqueConstraint priceAndOther = UniqueConstraint.create(price.getInt(), other);

		/**
		 * Creates a new ItemWithUniquePrice with all the fields initially needed.
		 * @param price the initial value for field {@link #price}.
		 * @param other the initial value for field {@link #other}.
		 * @throws com.exedio.cope.MandatoryViolationException if price, other is null.
		 * @throws com.exedio.cope.StringLengthViolationException if other violates its length constraint.
		 * @throws com.exedio.cope.UniqueViolationException if price, other is not unique.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ItemWithUniquePrice(
					@javax.annotation.Nonnull final com.exedio.cope.pattern.Price price,
					@javax.annotation.Nonnull final java.lang.String other)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				ItemWithUniquePrice.price.map(price),
				ItemWithUniquePrice.other.map(other),
			});
		}

		/**
		 * Creates a new ItemWithUniquePrice and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private ItemWithUniquePrice(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #price}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		public com.exedio.cope.pattern.Price getPrice()
		{
			return ItemWithUniquePrice.price.get(this);
		}

		/**
		 * Sets a new value for {@link #price}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		public void setPrice(@javax.annotation.Nonnull final com.exedio.cope.pattern.Price price)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException
		{
			ItemWithUniquePrice.price.set(this,price);
		}

		/**
		 * Returns the value of {@link #other}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		public java.lang.String getOther()
		{
			return ItemWithUniquePrice.other.get(this);
		}

		/**
		 * Sets a new value for {@link #other}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		public void setOther(@javax.annotation.Nonnull final java.lang.String other)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			ItemWithUniquePrice.other.set(this,other);
		}

		/**
		 * Finds a itemWithUniquePrice by it's unique fields.
		 * @param price_int shall be equal to field 'int' of {@link #price}.
		 * @param other shall be equal to field {@link #other}.
		 * @return null if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finder")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		public static ItemWithUniquePrice forPriceAndOther(final long price_int,@javax.annotation.Nonnull final java.lang.String other)
		{
			return ItemWithUniquePrice.priceAndOther.search(ItemWithUniquePrice.class,price_int,other);
		}

		/**
		 * Finds a itemWithUniquePrice by its unique fields.
		 * @param price_int shall be equal to field 'int' of {@link #price}.
		 * @param other shall be equal to field {@link #other}.
		 * @throws java.lang.IllegalArgumentException if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finderStrict")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		public static ItemWithUniquePrice forPriceAndOtherStrict(final long price_int,@javax.annotation.Nonnull final java.lang.String other)
				throws
					java.lang.IllegalArgumentException
		{
			return ItemWithUniquePrice.priceAndOther.searchStrict(ItemWithUniquePrice.class,price_int,other);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for itemWithUniquePrice.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<ItemWithUniquePrice> TYPE = com.exedio.cope.TypesBound.newType(ItemWithUniquePrice.class,ItemWithUniquePrice::new);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private ItemWithUniquePrice(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

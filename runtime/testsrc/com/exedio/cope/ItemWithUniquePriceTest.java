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

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
	static final class ItemWithUniquePrice extends Item
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
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		ItemWithUniquePrice(
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
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		private ItemWithUniquePrice(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #price}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		public com.exedio.cope.pattern.Price getPrice()
		{
			return ItemWithUniquePrice.price.get(this);
		}

		/**
		 * Sets a new value for {@link #price}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
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
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nonnull
		public java.lang.String getOther()
		{
			return ItemWithUniquePrice.other.get(this);
		}

		/**
		 * Sets a new value for {@link #other}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
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
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finder")
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
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finderStrict")
		@javax.annotation.Nonnull
		public static ItemWithUniquePrice forPriceAndOtherStrict(final long price_int,@javax.annotation.Nonnull final java.lang.String other)
				throws
					java.lang.IllegalArgumentException
		{
			return ItemWithUniquePrice.priceAndOther.searchStrict(ItemWithUniquePrice.class,price_int,other);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for itemWithUniquePrice.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		static final com.exedio.cope.Type<ItemWithUniquePrice> TYPE = com.exedio.cope.TypesBound.newType(ItemWithUniquePrice.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private ItemWithUniquePrice(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

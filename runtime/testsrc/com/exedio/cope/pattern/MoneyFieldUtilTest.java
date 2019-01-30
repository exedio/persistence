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

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class MoneyFieldUtilTest
{
	@Test void testFixedCurrencyNull()
	{
		try
		{
			MoneyField.fixed(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("currency", e.getMessage());
		}
	}

	@Test void testSharedCurrencyNull()
	{
		try
		{
			MoneyField.shared(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("currency", e.getMessage());
		}
	}

	enum CurrencyEnum implements Money.Currency
	{
		A, B
	}

	@Test void testSharedCurrencyOptional()
	{
		try
		{
			MoneyField.shared(EnumField.create(CurrencyEnum.class).optional());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("currency must be mandatory", e.getMessage());
		}
	}

	@Test void testSharedCurrencyOtherType()
	{
		try
		{
			TypesBound.newType(FieldItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"FieldItem.field: shared currency must be on the same type: CurrencyItem.currency",
					e.getMessage());
		}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class CurrencyItem extends Item
	{
		@WrapperIgnore
		static final EnumField<CurrencyEnum> currency = EnumField.create(CurrencyEnum.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<CurrencyItem> TYPE = com.exedio.cope.TypesBound.newType(CurrencyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private CurrencyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class FieldItem extends Item
	{
		@WrapperIgnore
		static final MoneyField<CurrencyEnum> field = MoneyField.shared(CurrencyItem.currency);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private FieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@Test void testExclusiveCurrencyNull()
	{
		try
		{
			MoneyField.exclusive(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("currency", e.getMessage());
		}
	}
}

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
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

	@SuppressWarnings("unused") // OK: Enum for EnumField must not be empty
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
	private static final class CurrencyItem extends Item
	{
		@WrapperIgnore
		static final EnumField<CurrencyEnum> currency = EnumField.create(CurrencyEnum.class);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<CurrencyItem> TYPE = com.exedio.cope.TypesBound.newType(CurrencyItem.class);

		@com.exedio.cope.instrument.Generated
		private CurrencyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class FieldItem extends Item
	{
		@WrapperIgnore
		@SuppressWarnings("unused")
		static final MoneyField<CurrencyEnum> field = MoneyField.shared(CurrencyItem.currency);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
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

	@Test void testFinal()
	{
		final MoneyField<?> f = MoneyField.fixed(CurrencyEnum.A).toFinal();

		assertEquals(true, f.isFinal());
		assertEquals(true, f.getAmount().isFinal());
		assertEquals(true, f.getAmount().getInt().isFinal());

		assertEquals(true, f.isMandatory());
		assertEquals(true, f.getAmount().isMandatory());
		assertEquals(true, f.getAmount().getInt().isMandatory());

		assertEquals(Price.MIN_VALUE,  f.getAmount().getMinimum());
		assertEquals(Price.MAX_VALUE,  f.getAmount().getMaximum());
		assertEquals(Long.MIN_VALUE+1, f.getAmount().getInt().getMinimum());
		assertEquals(Long.MAX_VALUE,   f.getAmount().getInt().getMaximum());
	}

	@Test void testOptional()
	{
		final MoneyField<?> f = MoneyField.fixed(CurrencyEnum.A).optional();

		assertEquals(false, f.isFinal());
		assertEquals(false, f.getAmount().isFinal());
		assertEquals(false, f.getAmount().getInt().isFinal());

		assertEquals(false, f.isMandatory());
		assertEquals(false, f.getAmount().isMandatory());
		assertEquals(false, f.getAmount().getInt().isMandatory());

		assertEquals(Price.MIN_VALUE,  f.getAmount().getMinimum());
		assertEquals(Price.MAX_VALUE,  f.getAmount().getMaximum());
		assertEquals(Long.MIN_VALUE+1, f.getAmount().getInt().getMinimum());
		assertEquals(Long.MAX_VALUE,   f.getAmount().getInt().getMaximum());
	}

	@Test void testMinZero()
	{
		final MoneyField<?> f = MoneyField.fixed(CurrencyEnum.A).minZero();

		assertEquals(false, f.isFinal());
		assertEquals(false, f.getAmount().isFinal());
		assertEquals(false, f.getAmount().getInt().isFinal());

		assertEquals(true, f.isMandatory());
		assertEquals(true, f.getAmount().isMandatory());
		assertEquals(true, f.getAmount().getInt().isMandatory());

		assertEquals(Price.ZERO,      f.getAmount().getMinimum());
		assertEquals(Price.MAX_VALUE, f.getAmount().getMaximum());
		assertEquals(0,               f.getAmount().getInt().getMinimum());
		assertEquals(Long.MAX_VALUE,  f.getAmount().getInt().getMaximum());
	}

	@Test void testConditions()
	{
		final MoneyField<?> f = MoneyField.fixed(CurrencyEnum.A);
		final LongField i = f.getAmount().getInt();
		assertEquals(i + " is null", f.isNull().toString());
		assertEquals(i + " is not null", f.isNotNull().toString());
	}
}

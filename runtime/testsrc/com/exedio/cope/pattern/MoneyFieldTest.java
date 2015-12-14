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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.pattern.CurrencyFixed.fix;
import static com.exedio.cope.pattern.CurrencyFixed.fixOther;
import static com.exedio.cope.pattern.Money.valueOf;
import static com.exedio.cope.pattern.MoneyFieldItem.TYPE;
import static com.exedio.cope.pattern.MoneyFieldItem.currency;
import static com.exedio.cope.pattern.MoneyFieldItem.exclusive;
import static com.exedio.cope.pattern.MoneyFieldItem.fixed;
import static com.exedio.cope.pattern.MoneyFieldItem.fixedEnum;
import static com.exedio.cope.pattern.MoneyFieldItem.shared;
import static com.exedio.cope.pattern.MoneyFieldItem.sharedMandatory;
import static com.exedio.cope.pattern.MoneyFieldItem.Currency.eur;
import static com.exedio.cope.pattern.MoneyFieldItem.Currency.gbp;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.pattern.MoneyFieldItem.Currency;
import org.junit.Test;

public class MoneyFieldTest extends AbstractRuntimeModelTest
{
	private static final Model MODEL = new Model(TYPE);

	public MoneyFieldTest()
	{
		super(MODEL);
	}

	@Test public void testNames()
	{
		assertEquals("fixed",                fixed                     .getName());
		assertEquals("fixed-amount",         fixed.getAmount()         .getName());
		assertEquals("fixed-amount-int",     fixed.getAmount().getInt().getName());
		assertEquals(null,                   fixed.getCurrencyField());
		assertEquals(CurrencyFixed.fix,      fixed.getCurrencyValue());
		assertEquals("fixedEnum",            fixedEnum                     .getName());
		assertEquals("fixedEnum-amount",     fixedEnum.getAmount()         .getName());
		assertEquals("fixedEnum-amount-int", fixedEnum.getAmount().getInt().getName());
		assertEquals(null,                   fixedEnum.getCurrencyField());
		assertEquals(Currency.eur,           fixedEnum.getCurrencyValue());
		assertEquals("shared",               shared                     .getName());
		assertEquals("shared-amount",        shared.getAmount()         .getName());
		assertEquals("shared-amount-int",    shared.getAmount().getInt().getName());
		assertEquals("sharedMandatory",            sharedMandatory                     .getName());
		assertEquals("sharedMandatory-amount",     sharedMandatory.getAmount()         .getName());
		assertEquals("sharedMandatory-amount-int", sharedMandatory.getAmount().getInt().getName());
		assertEquals("currency",             shared.getCurrencyField()     .getName());
		assertEquals(null,                   shared.getCurrencyValue());
		assertEquals("exclusive",            exclusive                     .getName());
		assertEquals("exclusive-amount",     exclusive.getAmount()         .getName());
		assertEquals("exclusive-amount-int", exclusive.getAmount().getInt().getName());
		assertEquals("exclusive-currency",   exclusive.getCurrencyField()  .getName());
		assertEquals(null,                   exclusive.getCurrencyValue());
		assertSame(shared.getCurrencyField(), sharedMandatory.getCurrencyField());

		assertEquals(CurrencyFixed.class, fixed     .getCurrencyClass());
		assertEquals(Currency.class, fixedEnum      .getCurrencyClass());
		assertEquals(Currency.class, shared         .getCurrencyClass());
		assertEquals(Currency.class, sharedMandatory.getCurrencyClass());
		assertEquals(Currency.class, exclusive      .getCurrencyClass());

		assertEquals("com.exedio.cope.pattern.Money<" + CurrencyFixed.class.getName() + ">", fixed     .getInitialType().toString());
		assertEquals("com.exedio.cope.pattern.Money<" + Currency.class.getName() + ">", fixedEnum      .getInitialType().toString());
		assertEquals("com.exedio.cope.pattern.Money<" + Currency.class.getName() + ">", shared         .getInitialType().toString());
		assertEquals("com.exedio.cope.pattern.Money<" + Currency.class.getName() + ">", sharedMandatory.getInitialType().toString());
		assertEquals("com.exedio.cope.pattern.Money<" + Currency.class.getName() + ">", exclusive      .getInitialType().toString());

		assertEquals("fixed_int",           getColumnName(fixed.getAmount().getInt()));
		assertEquals("fixedEnum_int",       getColumnName(fixedEnum.getAmount().getInt()));
		assertEquals("shared_int",          getColumnName(shared.getAmount().getInt()));
		assertEquals("currency" ,           getColumnName(shared.getCurrencyField()));
		assertEquals("exclusive_int",       getColumnName(exclusive.getAmount().getInt()));
		assertEquals("exclusive_currency" , getColumnName(exclusive.getCurrencyField()));
	}
	@Test public void testFixedConsistencyOkSingle()
	{
		final MoneyFieldItem i = fixed(valueOf(5.55, fix));
		assertEquals(valueOf(5.55, fix), i.getFixed());

		i.setFixed(valueOf(6.66, fix));
		assertEquals(valueOf(6.66, fix), i.getFixed());

		i.setFixed(null);
		assertEquals(null, i.getFixed());
	}
	@Test public void testFixedConsistencyCreateNull()
	{
		final MoneyFieldItem i = fixed(null);
		assertEquals(null , i.getFixed());
	}
	@Test public void testFixedConsistencyOkMulti()
	{
		final MoneyFieldItem i = fixed(valueOf(5.55, fix));
		assertEquals(valueOf(5.55, fix), i.getFixed());

		i.set(fixed.map(valueOf(6.66, fix)));
		assertEquals(valueOf(6.66, fix), i.getFixed());

		i.set(fixed.map(null));
		assertEquals(null, i.getFixed());
	}
	@Test public void testFixedConsistencyBrokenCreate()
	{
		try
		{
			fixed(valueOf(5.55, fixOther));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(fixed, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(valueOf(5.55, fixOther), e.getValue());
			assertEquals(fix, e.getAllowed());
			assertEquals(
					"illegal currency at '5.55fixOther' " +
					"for MoneyFieldItem.fixed, " +
					"allowed is 'fix'.",
					e.getMessage());
		}
	}
	@Test public void testFixedConsistencyBrokenSingle()
	{
		final MoneyFieldItem i = fixed(valueOf(5.55, fix));
		assertEquals(valueOf(5.55, fix), i.getFixed());

		try
		{
			i.setFixed(valueOf(6.66, fixOther));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(fixed, e.getFeature());
			assertEquals(i, e.getItem());
			assertEquals(valueOf(6.66, fixOther), e.getValue());
			assertEquals(fix, e.getAllowed());
			assertEquals(
					"illegal currency at '6.66fixOther' " +
					"on " + i + " for MoneyFieldItem.fixed, " +
					"allowed is 'fix'.",
					e.getMessage());
		}
		assertEquals(valueOf(5.55, fix), i.getFixed());
	}
	@Test public void testFixedConsistencyBrokenMulti()
	{
		final MoneyFieldItem i = fixed(valueOf(5.55, fix));
		assertEquals(valueOf(5.55, fix), i.getFixed());

		try
		{
			i.set(fixed.map(valueOf(6.66, fixOther)));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(fixed, e.getFeature());
			assertEquals(i, e.getItem());
			assertEquals(valueOf(6.66, fixOther), e.getValue());
			assertEquals(fix, e.getAllowed());
			assertEquals(
					"illegal currency at '6.66fixOther' " +
					"on " + i + " for MoneyFieldItem.fixed, " +
					"allowed is 'fix'.",
					e.getMessage());
		}
		assertEquals(valueOf(5.55, fix), i.getFixed());
	}
	@Test public void testFixedEnum()
	{
		final MoneyFieldItem i = fixedEnum(valueOf(5.55, eur));
		assertEquals(valueOf(5.55, eur), i.getFixedEnum());

		i.setFixedEnum(valueOf(6.66, eur));
		assertEquals(valueOf(6.66, eur), i.getFixedEnum());

		i.setFixedEnum(null);
		assertEquals(null, i.getFixedEnum());
	}
	@Test public void testSharedConsistencyOkSingle()
	{
		final MoneyFieldItem i = shared(eur, valueOf(5.55, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getShared());

		i.setShared(valueOf(6.66, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getShared());

		i.setShared(null);
		assertEquals(eur , i.getCurrency());
		assertEquals(null, i.getShared());
	}
	@Test public void testSharedConsistencyCreateNull()
	{
		final MoneyFieldItem i = shared(eur, null);
		assertEquals(eur , i.getCurrency());
		assertEquals(null, i.getShared());
	}
	@Test public void testSharedConsistencyOkMulti()
	{
		final MoneyFieldItem i = shared(eur, valueOf(5.55, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getShared());

		i.set(shared.map(valueOf(6.66, eur)));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getShared());

		i.set(shared.map(null));
		assertEquals(eur , i.getCurrency());
		assertEquals(null, i.getShared());
	}
	@Test public void testSharedConsistencyOkMultiWithCurrency()
	{
		final MoneyFieldItem i = shared(eur, valueOf(5.55, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getShared());

		i.set(shared.map(valueOf(6.66, eur)), currency.map(eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getShared());

		i.set(shared.map(null), currency.map(eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(null, i.getShared());
	}
	@Test public void testSharedConsistencyOkMultiWithOtherCurrency()
	{
		final MoneyFieldItem i = sharedMandatory(eur, valueOf(15.55, eur), valueOf(25.55, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(15.55, eur), i.getShared());
		assertEquals(valueOf(25.55, eur), i.getSharedMandatory());

		i.set(
				shared.map(valueOf(16.66, gbp)),
				sharedMandatory.map(valueOf(26.66, gbp)),
				currency.map(gbp));
		assertEquals(gbp , i.getCurrency());
		assertEquals(valueOf(16.66, gbp), i.getShared());
		assertEquals(valueOf(26.66, gbp), i.getSharedMandatory());

		i.set(shared.map(null), currency.map(gbp));
		assertEquals(gbp , i.getCurrency());
		assertEquals(null, i.getShared());
	}
	@Test public void testSharedMandatorySingle()
	{
		final MoneyFieldItem i = sharedMandatory(eur, valueOf(5.55, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharedMandatory());

		i.setSharedMandatory(valueOf(6.66, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getSharedMandatory());

		try
		{
			i.setSharedMandatory(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(i, e.getItem());
			assertEquals(sharedMandatory, e.getFeature());
		}
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getSharedMandatory());
	}
	@Test public void testSharedMandatoryMulti()
	{
		final MoneyFieldItem i = sharedMandatory(eur, valueOf(5.55, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharedMandatory());

		i.set(sharedMandatory.map(valueOf(6.66, eur)));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getSharedMandatory());

		try
		{
			i.set(sharedMandatory.map(null));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(i, e.getItem());
			assertEquals(sharedMandatory, e.getFeature());
		}
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getSharedMandatory());
	}
	@Test public void testSharedConsistencyBrokenCreate()
	{
		try
		{
			shared(eur, valueOf(5.55, gbp));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(shared, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(valueOf(5.55, gbp), e.getValue());
			assertEquals(eur, e.getAllowed());
			assertEquals(
					"illegal currency at '5.55gbp' " +
					"for MoneyFieldItem.shared, " +
					"allowed is 'eur'.",
					e.getMessage());
		}
	}
	@Test public void testSharedConsistencyBrokenSingle()
	{
		final MoneyFieldItem i = shared(eur, valueOf(5.55, eur));
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getShared());

		try
		{
			i.setShared(valueOf(6.66, gbp));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(shared, e.getFeature());
			assertEquals(i, e.getItem());
			assertEquals(valueOf(6.66, gbp), e.getValue());
			assertEquals(eur, e.getAllowed());
			assertEquals(
					"illegal currency at '6.66gbp' " +
					"on " + i + " for MoneyFieldItem.shared, " +
					"allowed is 'eur'.",
					e.getMessage());
		}
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getShared());
	}
	@Test public void testSharedConsistencyBrokenMulti()
	{
		final MoneyFieldItem i = shared(eur, valueOf(5.55, eur));
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getShared());

		try
		{
			i.set(shared.map(valueOf(6.66, gbp)));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(shared, e.getFeature());
			assertEquals(i, e.getItem());
			assertEquals(valueOf(6.66, gbp), e.getValue());
			assertEquals(eur, e.getAllowed());
			assertEquals(
					"illegal currency at '6.66gbp' " +
					"on " + i + " for MoneyFieldItem.shared, " +
					"allowed is 'eur'.",
					e.getMessage());
		}
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getShared());
	}
	@Test public void testSharedConsistencyBrokenMultiWithCurrency()
	{
		final MoneyFieldItem i = shared(eur, valueOf(5.55, eur));
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getShared());

		try
		{
			i.set(shared.map(valueOf(6.66, gbp)), currency.map(eur));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(shared, e.getFeature());
			assertEquals(i, e.getItem());
			assertEquals(valueOf(6.66, gbp), e.getValue());
			assertEquals(eur, e.getAllowed());
			assertEquals(
					"illegal currency at '6.66gbp' " +
					"on " + i + " for MoneyFieldItem.shared, " +
					"allowed is 'eur'.",
					e.getMessage());
		}
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getShared());
	}
	@Test public void testExclusiveSingle()
	{
		final MoneyFieldItem i = exclusive(valueOf(5.55, eur));
		assertEquals(valueOf(5.55, eur), i.getExclusive());
		assertEquals(eur, i.getExclusiveCurrency());

		i.setExclusive(valueOf(6.66, eur));
		assertEquals(valueOf(6.66, eur), i.getExclusive());
		assertEquals(eur, i.getExclusiveCurrency());

		i.setExclusive(valueOf(7.77, gbp));
		assertEquals(valueOf(7.77, gbp), i.getExclusive());
		assertEquals(gbp, i.getExclusiveCurrency());

		i.setExclusive(null);
		assertEquals(null, i.getExclusive());
		assertEquals(null, i.getExclusiveCurrency());
	}
	@Test public void testExclusiveCreateNull()
	{
		final MoneyFieldItem i = exclusive(null);
		assertEquals(null, i.getExclusive());
		assertEquals(null, i.getExclusiveCurrency());
	}
	@Test public void testExclusiveMulti()
	{
		final MoneyFieldItem i = exclusive(valueOf(5.55, eur));
		assertEquals(valueOf(5.55, eur), i.getExclusive());
		assertEquals(eur, i.getExclusiveCurrency());

		i.set(exclusive.map(valueOf(6.66, eur)));
		assertEquals(valueOf(6.66, eur), i.getExclusive());
		assertEquals(eur, i.getExclusiveCurrency());

		i.set(exclusive.map(valueOf(7.77, gbp)));
		assertEquals(valueOf(7.77, gbp), i.getExclusive());
		assertEquals(gbp, i.getExclusiveCurrency());

		i.set(exclusive.map(null));
		assertEquals(null, i.getExclusive());
		assertEquals(null, i.getExclusiveCurrency());
	}
}

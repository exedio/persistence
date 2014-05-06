/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.pattern.Money.valueOf;
import static com.exedio.cope.pattern.MoneyFieldItem.TYPE;
import static com.exedio.cope.pattern.MoneyFieldItem.currency;
import static com.exedio.cope.pattern.MoneyFieldItem.exclusive;
import static com.exedio.cope.pattern.MoneyFieldItem.shared;
import static com.exedio.cope.pattern.MoneyFieldItem.sharedMandatory;
import static com.exedio.cope.pattern.MoneyFieldItem.Currency.eur;
import static com.exedio.cope.pattern.MoneyFieldItem.Currency.gbp;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.pattern.MoneyFieldItem.Currency;

public class MoneyFieldTest extends AbstractRuntimeModelTest
{
	private static final Model MODEL = new Model(TYPE);

	public MoneyFieldTest()
	{
		super(MODEL);
	}

	public void testNames()
	{
		assertEquals("shared",               shared                     .getName());
		assertEquals("shared-amount",        shared.getAmount()         .getName());
		assertEquals("shared-amount-int",    shared.getAmount().getInt().getName());
		assertEquals("sharedMandatory",            sharedMandatory                     .getName());
		assertEquals("sharedMandatory-amount",     sharedMandatory.getAmount()         .getName());
		assertEquals("sharedMandatory-amount-int", sharedMandatory.getAmount().getInt().getName());
		assertEquals("currency",             shared.getCurrency()          .getName());
		assertEquals("exclusive",            exclusive                     .getName());
		assertEquals("exclusive-amount",     exclusive.getAmount()         .getName());
		assertEquals("exclusive-amount-int", exclusive.getAmount().getInt().getName());
		assertEquals("exclusive-currency",   exclusive.getCurrency()       .getName());
		assertSame(shared.getCurrency(), sharedMandatory.getCurrency());

		assertEquals("com.exedio.cope.pattern.Money<" + Currency.class.getName() + ">", shared         .getInitialType().toString());
		assertEquals("com.exedio.cope.pattern.Money<" + Currency.class.getName() + ">", sharedMandatory.getInitialType().toString());
		assertEquals("com.exedio.cope.pattern.Money<" + Currency.class.getName() + ">", exclusive      .getInitialType().toString());

		assertEquals("shared_int",          getColumnName(shared.getAmount().getInt()));
		assertEquals("currency" ,           getColumnName(shared.getCurrency()));
		assertEquals("exclusive_int",       getColumnName(exclusive.getAmount().getInt()));
		assertEquals("exclusive_currency" , getColumnName(exclusive.getCurrency()));
	}
	public void testSharedConsistencyOkSingle()
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
	public void testSharedConsistencyCreateNull()
	{
		final MoneyFieldItem i = shared(eur, null);
		assertEquals(eur , i.getCurrency());
		assertEquals(null, i.getShared());
	}
	public void testSharedConsistencyOkMulti()
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
	public void testSharedConsistencyOkMultiWithCurrency()
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
	public void testSharedConsistencyOkMultiWithOtherCurrency()
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
	public void testSharedMandatorySingle()
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
	public void testSharedMandatoryMulti()
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
	public void testSharedConsistencyBrokenCreate()
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
	public void testSharedConsistencyBrokenSingle()
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
	public void testSharedConsistencyBrokenMulti()
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
	public void testSharedConsistencyBrokenMultiWithCurrency()
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
	public void testExclusiveSingle()
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
	public void testExclusiveCreateNull()
	{
		final MoneyFieldItem i = exclusive(null);
		assertEquals(null, i.getExclusive());
		assertEquals(null, i.getExclusiveCurrency());
	}
	public void testExclusiveMulti()
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

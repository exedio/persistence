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
import static com.exedio.cope.pattern.CurrFix.fix;
import static com.exedio.cope.pattern.CurrFix.fixOther;
import static com.exedio.cope.pattern.Money.valueOf;
import static com.exedio.cope.pattern.MoneyFieldItem.Currency.eur;
import static com.exedio.cope.pattern.MoneyFieldItem.Currency.gbp;
import static com.exedio.cope.pattern.MoneyFieldItem.TYPE;
import static com.exedio.cope.pattern.MoneyFieldItem.byItem;
import static com.exedio.cope.pattern.MoneyFieldItem.currency;
import static com.exedio.cope.pattern.MoneyFieldItem.exclMan;
import static com.exedio.cope.pattern.MoneyFieldItem.exclOpt;
import static com.exedio.cope.pattern.MoneyFieldItem.fixeEnu;
import static com.exedio.cope.pattern.MoneyFieldItem.fixeOpt;
import static com.exedio.cope.pattern.MoneyFieldItem.sharMan;
import static com.exedio.cope.pattern.MoneyFieldItem.sharOpt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.MoneyFieldItem.Currency;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

public class MoneyFieldTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE, CurrencyItem.TYPE);

	public MoneyFieldTest()
	{
		super(MODEL);
	}

	@Test public void testNames()
	{
		assertEquals("fixeOpt",            fixeOpt                     .getName());
		assertEquals("fixeOpt-amount",     fixeOpt.getAmount()         .getName());
		assertEquals("fixeOpt-amount-int", fixeOpt.getAmount().getInt().getName());
		assertEquals(null,                 fixeOpt.getCurrencyField());
		assertEquals(fix,                  fixeOpt.getCurrencyValue());
		assertEquals("fixeEnu",            fixeEnu                     .getName());
		assertEquals("fixeEnu-amount",     fixeEnu.getAmount()         .getName());
		assertEquals("fixeEnu-amount-int", fixeEnu.getAmount().getInt().getName());
		assertEquals(null,                 fixeEnu.getCurrencyField());
		assertEquals(eur,                  fixeEnu.getCurrencyValue());
		assertEquals("sharOpt",            sharOpt                     .getName());
		assertEquals("sharOpt-amount",     sharOpt.getAmount()         .getName());
		assertEquals("sharOpt-amount-int", sharOpt.getAmount().getInt().getName());
		assertEquals("sharMan",            sharMan                     .getName());
		assertEquals("sharMan-amount",     sharMan.getAmount()         .getName());
		assertEquals("sharMan-amount-int", sharMan.getAmount().getInt().getName());
		assertEquals("currency",           sharOpt.getCurrencyField()  .getName());
		assertEquals(null,                 sharOpt.getCurrencyValue());
		assertEquals("exclOpt",            exclOpt                     .getName());
		assertEquals("exclOpt-amount",     exclOpt.getAmount()         .getName());
		assertEquals("exclOpt-amount-int", exclOpt.getAmount().getInt().getName());
		assertEquals("exclOpt-currency",   exclOpt.getCurrencyField()  .getName());
		assertEquals("exclOpt-unison",     exclOpt.getUnison()         .getName());
		assertEquals(null,                 exclOpt.getCurrencyValue());
		assertEquals("exclMan",            exclMan                     .getName());
		assertEquals("exclMan-amount",     exclMan.getAmount()         .getName());
		assertEquals("exclMan-amount-int", exclMan.getAmount().getInt().getName());
		assertEquals("exclMan-currency",   exclMan.getCurrencyField()  .getName());
		assertEquals(null,                 exclMan.getCurrencyValue());
		assertSame(sharOpt.getCurrencyField(), sharMan.getCurrencyField());

		assertEquals(CurrFix.class , fixeOpt.getCurrencyClass());
		assertEquals(Currency.class, fixeEnu.getCurrencyClass());
		assertEquals(Currency.class, sharOpt.getCurrencyClass());
		assertEquals(Currency.class, sharMan.getCurrencyClass());
		assertEquals(Currency.class, exclOpt.getCurrencyClass());
		assertEquals(Currency.class, exclMan.getCurrencyClass());

		assertEquals(null, fixeOpt.getUnison());
		assertEquals(null, fixeEnu.getUnison());
		assertEquals(null, sharOpt.getUnison());
		assertEquals(null, sharMan.getUnison());
		assertEquals(null, exclMan.getUnison());
		assertEquals("(" +
				"(MoneyFieldItem.exclOpt-amount-int is "+ "null AND MoneyFieldItem.exclOpt-currency "+ "is null) OR " +
				"(MoneyFieldItem.exclOpt-amount-int is not null AND MoneyFieldItem.exclOpt-currency is not null))",
				exclOpt.getUnison().getCondition().toString());

		assertEquals("com.exedio.cope.pattern.Money<" + CurrFix .class.getName() + ">", fixeOpt.getInitialType().toString());
		assertEquals("com.exedio.cope.pattern.Money<" + Currency.class.getName() + ">", fixeEnu.getInitialType().toString());
		assertEquals("com.exedio.cope.pattern.Money<" + Currency.class.getName() + ">", sharOpt.getInitialType().toString());
		assertEquals("com.exedio.cope.pattern.Money<" + Currency.class.getName() + ">", sharMan.getInitialType().toString());
		assertEquals("com.exedio.cope.pattern.Money<" + Currency.class.getName() + ">", exclOpt.getInitialType().toString());
		assertEquals("com.exedio.cope.pattern.Money<" + Currency.class.getName() + ">", exclMan.getInitialType().toString());

		assertEquals("fixeOpt_int",      getColumnName(fixeOpt.getAmount().getInt()));
		assertEquals("fixeEnu_int",      getColumnName(fixeEnu.getAmount().getInt()));
		assertEquals("sharOpt_int",      getColumnName(sharOpt.getAmount().getInt()));
		assertEquals("currency" ,        getColumnName(sharOpt.getCurrencyField()));
		assertEquals("exclOpt_int",      getColumnName(exclOpt.getAmount().getInt()));
		assertEquals("exclOpt_currency", getColumnName(exclOpt.getCurrencyField()));
		assertEquals("exclMan_int",      getColumnName(exclMan.getAmount().getInt()));
		assertEquals("exclMan_currency", getColumnName(exclMan.getCurrencyField()));
	}
	@Test public void testFixedConsistencyOkSingle()
	{
		final MoneyFieldItem i = fixeOpt(valueOf(5.55, fix));
		assertEquals(valueOf(5.55, fix), i.getFixeOpt());

		i.setFixeOpt(valueOf(6.66, fix));
		assertEquals(valueOf(6.66, fix), i.getFixeOpt());

		i.setFixeOpt(null);
		assertEquals(null, i.getFixeOpt());
	}
	@Test public void testFixedConsistencyCreateNull()
	{
		final MoneyFieldItem i = fixeOpt(null);
		assertEquals(null , i.getFixeOpt());
	}
	@Test public void testFixedConsistencyOkMulti()
	{
		final MoneyFieldItem i = fixeOpt(valueOf(5.55, fix));
		assertEquals(valueOf(5.55, fix), i.getFixeOpt());

		i.set(fixeOpt.map(valueOf(6.66, fix)));
		assertEquals(valueOf(6.66, fix), i.getFixeOpt());

		i.set(fixeOpt.map(null));
		assertEquals(null, i.getFixeOpt());
	}
	@Test public void testFixedConsistencyBrokenCreate()
	{
		try
		{
			fixeOpt(valueOf(5.55, fixOther));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(fixeOpt, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(valueOf(5.55, fixOther), e.getValue());
			assertEquals(fix, e.getAllowed());
			assertEquals(
					"illegal currency at '5.55fixOther' " +
					"for MoneyFieldItem.fixeOpt, " +
					"allowed is 'fix'.",
					e.getMessage());
		}
	}
	@Test public void testFixedConsistencyBrokenSingle()
	{
		final MoneyFieldItem i = fixeOpt(valueOf(5.55, fix));
		assertEquals(valueOf(5.55, fix), i.getFixeOpt());

		try
		{
			i.setFixeOpt(valueOf(6.66, fixOther));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(fixeOpt, e.getFeature());
			assertEquals(i, e.getItem());
			assertEquals(valueOf(6.66, fixOther), e.getValue());
			assertEquals(fix, e.getAllowed());
			assertEquals(
					"illegal currency at '6.66fixOther' " +
					"on " + i + " for MoneyFieldItem.fixeOpt, " +
					"allowed is 'fix'.",
					e.getMessage());
		}
		assertEquals(valueOf(5.55, fix), i.getFixeOpt());
	}
	@Test public void testFixedConsistencyBrokenMulti()
	{
		final MoneyFieldItem i = fixeOpt(valueOf(5.55, fix));
		assertEquals(valueOf(5.55, fix), i.getFixeOpt());

		try
		{
			i.set(fixeOpt.map(valueOf(6.66, fixOther)));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(fixeOpt, e.getFeature());
			assertEquals(i, e.getItem());
			assertEquals(valueOf(6.66, fixOther), e.getValue());
			assertEquals(fix, e.getAllowed());
			assertEquals(
					"illegal currency at '6.66fixOther' " +
					"on " + i + " for MoneyFieldItem.fixeOpt, " +
					"allowed is 'fix'.",
					e.getMessage());
		}
		assertEquals(valueOf(5.55, fix), i.getFixeOpt());
	}
	@Test public void testFixedEnum()
	{
		final MoneyFieldItem i = fixeEnu(valueOf(5.55, eur));
		assertEquals(valueOf(5.55, eur), i.getFixeEnu());

		i.setFixeEnu(valueOf(6.66, eur));
		assertEquals(valueOf(6.66, eur), i.getFixeEnu());

		i.setFixeEnu(null);
		assertEquals(null, i.getFixeEnu());
	}
	@Test public void testSharedConsistencyOkSingle()
	{
		final MoneyFieldItem i = sharOpt(eur, valueOf(5.55, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharOpt());

		i.setSharOpt(valueOf(6.66, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getSharOpt());

		i.setSharOpt(null);
		assertEquals(eur , i.getCurrency());
		assertEquals(null, i.getSharOpt());
	}
	@Test public void testSharedConsistencyCreateNull()
	{
		final MoneyFieldItem i = sharOpt(eur, null);
		assertEquals(eur , i.getCurrency());
		assertEquals(null, i.getSharOpt());
	}
	@Test public void testSharedConsistencyOkMulti()
	{
		final MoneyFieldItem i = sharOpt(eur, valueOf(5.55, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharOpt());

		i.set(sharOpt.map(valueOf(6.66, eur)));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getSharOpt());

		i.set(sharOpt.map(null));
		assertEquals(eur , i.getCurrency());
		assertEquals(null, i.getSharOpt());
	}
	@Test public void testSharedConsistencyOkMultiWithCurrency()
	{
		final MoneyFieldItem i = sharOpt(eur, valueOf(5.55, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharOpt());

		i.set(sharOpt.map(valueOf(6.66, eur)), currency.map(eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getSharOpt());

		i.set(sharOpt.map(null), currency.map(eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(null, i.getSharOpt());
	}
	@Test public void testSharedConsistencyOkMultiWithOtherCurrency()
	{
		final MoneyFieldItem i = sharMan(eur, valueOf(15.55, eur), valueOf(25.55, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(15.55, eur), i.getSharOpt());
		assertEquals(valueOf(25.55, eur), i.getSharMan());

		i.set(
				sharOpt.map(valueOf(16.66, gbp)),
				sharMan.map(valueOf(26.66, gbp)),
				currency.map(gbp));
		assertEquals(gbp , i.getCurrency());
		assertEquals(valueOf(16.66, gbp), i.getSharOpt());
		assertEquals(valueOf(26.66, gbp), i.getSharMan());

		i.set(sharOpt.map(null), currency.map(gbp));
		assertEquals(gbp , i.getCurrency());
		assertEquals(null, i.getSharOpt());
	}

	@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
	@Test public void testSharedMandatorySingle()
	{
		final MoneyFieldItem i = sharMan(eur, valueOf(5.55, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharMan());

		i.setSharMan(valueOf(6.66, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getSharMan());

		try
		{
			i.setSharMan(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(i, e.getItem());
			assertEquals(sharMan, e.getFeature());
		}
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getSharMan());
	}
	@Test public void testSharedMandatoryMulti()
	{
		final MoneyFieldItem i = sharMan(eur, valueOf(5.55, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharMan());

		i.set(sharMan.map(valueOf(6.66, eur)));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getSharMan());

		try
		{
			i.set(sharMan.map(null));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(i, e.getItem());
			assertEquals(sharMan, e.getFeature());
		}
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getSharMan());
	}
	@Test public void testSharedConsistencyBrokenCreate()
	{
		try
		{
			sharOpt(eur, valueOf(5.55, gbp));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(sharOpt, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(valueOf(5.55, gbp), e.getValue());
			assertEquals(eur, e.getAllowed());
			assertEquals(
					"illegal currency at '5.55gbp' " +
					"for MoneyFieldItem.sharOpt, " +
					"allowed is 'eur'.",
					e.getMessage());
		}
	}
	@Test public void testSharedConsistencyBrokenSingle()
	{
		final MoneyFieldItem i = sharOpt(eur, valueOf(5.55, eur));
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharOpt());

		try
		{
			i.setSharOpt(valueOf(6.66, gbp));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(sharOpt, e.getFeature());
			assertEquals(i, e.getItem());
			assertEquals(valueOf(6.66, gbp), e.getValue());
			assertEquals(eur, e.getAllowed());
			assertEquals(
					"illegal currency at '6.66gbp' " +
					"on " + i + " for MoneyFieldItem.sharOpt, " +
					"allowed is 'eur'.",
					e.getMessage());
		}
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharOpt());
	}
	@Test public void testSharedConsistencyBrokenMulti()
	{
		final MoneyFieldItem i = sharOpt(eur, valueOf(5.55, eur));
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharOpt());

		try
		{
			i.set(sharOpt.map(valueOf(6.66, gbp)));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(sharOpt, e.getFeature());
			assertEquals(i, e.getItem());
			assertEquals(valueOf(6.66, gbp), e.getValue());
			assertEquals(eur, e.getAllowed());
			assertEquals(
					"illegal currency at '6.66gbp' " +
					"on " + i + " for MoneyFieldItem.sharOpt, " +
					"allowed is 'eur'.",
					e.getMessage());
		}
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharOpt());
	}
	@Test public void testSharedConsistencyBrokenMultiWithCurrency()
	{
		final MoneyFieldItem i = sharOpt(eur, valueOf(5.55, eur));
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharOpt());

		try
		{
			i.set(sharOpt.map(valueOf(6.66, gbp)), currency.map(eur));
			fail();
		}
		catch(final IllegalCurrencyException e)
		{
			assertEquals(sharOpt, e.getFeature());
			assertEquals(i, e.getItem());
			assertEquals(valueOf(6.66, gbp), e.getValue());
			assertEquals(eur, e.getAllowed());
			assertEquals(
					"illegal currency at '6.66gbp' " +
					"on " + i + " for MoneyFieldItem.sharOpt, " +
					"allowed is 'eur'.",
					e.getMessage());
		}
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getSharOpt());
	}
	@Test public void testExclusiveSingle()
	{
		final MoneyFieldItem i = exclOpt(valueOf(5.55, eur));
		assertEquals(valueOf(5.55, eur), i.getExclOpt());
		assertEquals(eur, i.getExclOptCurrency());

		i.setExclOpt(valueOf(6.66, eur));
		assertEquals(valueOf(6.66, eur), i.getExclOpt());
		assertEquals(eur, i.getExclOptCurrency());

		i.setExclOpt(valueOf(7.77, gbp));
		assertEquals(valueOf(7.77, gbp), i.getExclOpt());
		assertEquals(gbp, i.getExclOptCurrency());

		i.setExclOpt(null);
		assertEquals(null, i.getExclOpt());
		assertEquals(null, i.getExclOptCurrency());
	}
	@Test public void testExclusiveCreateNull()
	{
		final MoneyFieldItem i = exclOpt(null);
		assertEquals(null, i.getExclOpt());
		assertEquals(null, i.getExclOptCurrency());
	}
	@Test public void testExclusiveMulti()
	{
		final MoneyFieldItem i = exclOpt(valueOf(5.55, eur));
		assertEquals(valueOf(5.55, eur), i.getExclOpt());
		assertEquals(eur, i.getExclOptCurrency());

		i.set(exclOpt.map(valueOf(6.66, eur)));
		assertEquals(valueOf(6.66, eur), i.getExclOpt());
		assertEquals(eur, i.getExclOptCurrency());

		i.set(exclOpt.map(valueOf(7.77, gbp)));
		assertEquals(valueOf(7.77, gbp), i.getExclOpt());
		assertEquals(gbp, i.getExclOptCurrency());

		i.set(exclOpt.map(null));
		assertEquals(null, i.getExclOpt());
		assertEquals(null, i.getExclOptCurrency());
	}
	@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
	@Test public void testExclusiveMandatorySingle()
	{
		final MoneyFieldItem i = exclMan(valueOf(5.55, eur));
		assertEquals(valueOf(5.55, eur), i.getExclMan());
		assertEquals(eur, i.getExclManCurrency());

		i.setExclMan(valueOf(6.66, eur));
		assertEquals(valueOf(6.66, eur), i.getExclMan());
		assertEquals(eur, i.getExclManCurrency());

		i.setExclMan(valueOf(7.77, gbp));
		assertEquals(valueOf(7.77, gbp), i.getExclMan());
		assertEquals(gbp, i.getExclManCurrency());

		try
		{
			i.setExclMan(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + i + " for MoneyFieldItem.exclMan", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(exclMan, e.getFeature());
		}
		assertEquals(valueOf(7.77, gbp), i.getExclMan());
		assertEquals(gbp, i.getExclManCurrency());
	}
	@Test public void testExclusiveMandatoryCreateNull()
	{
		try
		{
			exclMan(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation for MoneyFieldItem.exclMan", e.getMessage());
			assertEquals(null, e.getItem());
			assertEquals(exclMan, e.getFeature());
		}
	}
	@Test public void testExclusiveMandatoryMulti()
	{
		final MoneyFieldItem i = exclMan(valueOf(5.55, eur));
		assertEquals(valueOf(5.55, eur), i.getExclMan());
		assertEquals(eur, i.getExclManCurrency());

		i.set(exclMan.map(valueOf(6.66, eur)));
		assertEquals(valueOf(6.66, eur), i.getExclMan());
		assertEquals(eur, i.getExclManCurrency());

		i.set(exclMan.map(valueOf(7.77, gbp)));
		assertEquals(valueOf(7.77, gbp), i.getExclMan());
		assertEquals(gbp, i.getExclManCurrency());

		try
		{
			i.set(exclMan.map(null));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + i + " for MoneyFieldItem.exclMan", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(exclMan, e.getFeature());
		}
		assertEquals(valueOf(7.77, gbp), i.getExclMan());
		assertEquals(gbp, i.getExclManCurrency());
	}
	@Test public void testByItem()
	{
		final CurrencyItem currency = new CurrencyItem();
		final MoneyFieldItem i = byItem(valueOf(5.55, currency));
		assertEquals(valueOf(5.55, currency), i.getByItem());
		i.setByItem(valueOf(6.66, currency));
		assertEquals(valueOf(6.66, currency), i.getByItem());
	}
}

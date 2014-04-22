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
import static com.exedio.cope.pattern.MoneyFieldItem.price;
import static com.exedio.cope.pattern.MoneyFieldItem.total;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.Model;
import com.exedio.cope.pattern.MoneyFieldItem.Currency;

public class MoneyFieldTest extends AbstractRuntimeModelTest
{
	private static final Model MODEL = new Model(TYPE);

	public MoneyFieldTest()
	{
		super(MODEL);
	}

	public void testNamesShared()
	{
		assertEquals("total",            total                     .getName());
		assertEquals("total-amount",     total.getAmount()         .getName());
		assertEquals("total-amount-int", total.getAmount().getInt().getName());
		assertEquals("currency",         total.getCurrency()       .getName());

		assertEquals("total_int", getColumnName(total.getAmount().getInt()));
		assertEquals("currency" , getColumnName(total.getCurrency()));
	}
	public void testNamesExclusive()
	{
		assertEquals("price",            price                     .getName());
		assertEquals("price-amount",     price.getAmount()         .getName());
		assertEquals("price-amount-int", price.getAmount().getInt().getName());
		assertEquals("price-currency",   price.getCurrency()       .getName());

		assertEquals("price_int",       getColumnName(price.getAmount().getInt()));
		assertEquals("price_currency" , getColumnName(price.getCurrency()));
	}
	public void testSharedConsistencyOk()
	{
		final Currency c = Currency.eur;
		final MoneyFieldItem o = new MoneyFieldItem(c, valueOf(5.55, c));
		assertEquals(c , o.getCurrency());
		assertEquals(valueOf(5.55, c), o.getTotal());
	}
	public void testSharedConsistencyBroken()
	{
		final Currency c1 = Currency.eur;
		final Currency c2 = Currency.gbp;
		final MoneyFieldItem o = new MoneyFieldItem(c1, valueOf(5.55, c2));
		// TODO currency ---------------------- !!!!!!!!!!!!!!!!!!!!
		assertEquals(c1 , o.getCurrency());
		assertEquals(valueOf(5.55, c1), o.getTotal());
	}
}

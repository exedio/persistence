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
import static com.exedio.cope.pattern.MoneyFieldItem.exclusive;
import static com.exedio.cope.pattern.MoneyFieldItem.shared;
import static com.exedio.cope.pattern.MoneyFieldItem.sharedMandatory;
import static com.exedio.cope.pattern.MoneyFieldItem.Currency.eur;
import static com.exedio.cope.pattern.MoneyFieldItem.Currency.gbp;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;

public class MoneyFieldTest extends AbstractRuntimeModelTest
{
	private static final Model MODEL = new Model(TYPE);

	public MoneyFieldTest()
	{
		super(MODEL);
	}

	public void testNames()
	{
		assertEquals("shared",            shared                     .getName());
		assertEquals("shared-amount",     shared.getAmount()         .getName());
		assertEquals("shared-amount-int", shared.getAmount().getInt().getName());
		assertEquals("currency",         shared.getCurrency()       .getName());

		assertEquals("shared_int", getColumnName(shared.getAmount().getInt()));
		assertEquals("currency" , getColumnName(shared.getCurrency()));

		assertEquals("exclusive",            exclusive                     .getName());
		assertEquals("exclusive-amount",     exclusive.getAmount()         .getName());
		assertEquals("exclusive-amount-int", exclusive.getAmount().getInt().getName());
		assertEquals("exclusive-currency",   exclusive.getCurrency()       .getName());

		assertEquals("exclusive_int",       getColumnName(exclusive.getAmount().getInt()));
		assertEquals("exclusive_currency" , getColumnName(exclusive.getCurrency()));
	}
	public void testSharedConsistencyOk()
	{
		final MoneyFieldItem i = new MoneyFieldItem(eur, valueOf(5.55, eur), valueOf(88.88, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getShared());

		i.setShared(valueOf(6.66, eur));
		assertEquals(eur , i.getCurrency());
		assertEquals(valueOf(6.66, eur), i.getShared());

		i.setShared(null);
		assertEquals(eur , i.getCurrency());
		assertEquals(null, i.getShared());
	}
	public void testSharedMandatory()
	{
		final MoneyFieldItem i = new MoneyFieldItem(eur, valueOf(88.88, eur), valueOf(5.55, eur));
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
	public void testSharedConsistencyBroken()
	{
		final MoneyFieldItem i = new MoneyFieldItem(eur, valueOf(5.55, gbp), valueOf(88.88, eur));
		// TODO currency ---------------------- !!!!!!!!!!!!!!!!!!!!
		assertEquals(eur, i.getCurrency());
		assertEquals(valueOf(5.55, eur), i.getShared());
	}
}

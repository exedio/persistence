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

import static com.exedio.cope.PrimaryKeyItem.TYPE;
import static com.exedio.cope.PrimaryKeyItem.next;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PrimaryKeyTest extends TestWithEnvironment
{
	/**
	 * Do not use this model in any other test.
	 * Otherwise problems may be hidden, because
	 * model has been connected before.
	 */
	private static final Model MODEL = new Model(TYPE);

	public PrimaryKeyTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private static void assertInfo(final Type<?> type, final int count, final int first, final int last, final SequenceInfo info, final int check)
	{
		SequenceInfoAssert.assertInfo(type, count, first, last, info);
		assertEquals("check", check, type.checkPrimaryKey());
	}

	private static void assertInfo(final IntegerField feature, final int count, final int first, final int last, final SequenceInfo info, final int check)
	{
		SequenceInfoAssert.assertInfo(feature, count, first, last, info);
		assertEquals("check", check, feature.checkDefaultToNext());
	}

	private static void assertInfo(final IntegerField feature, final SequenceInfo info, final int check)
	{
		SequenceInfoAssert.assertInfo(feature, info);
		assertEquals("check", check, feature.checkDefaultToNext());
	}

	private static final PrimaryKeyItem newPrimaryKeyItem(
			final String field,
			final int next)
	{
		try(TransactionTry tx = MODEL.startTransactionTry(PrimaryKeyTest.class.getName()))
		{
			return tx.commit(
					new PrimaryKeyItem(field, next)
			);
		}
	}

	private static final PrimaryKeyItem newPrimaryKeyItem(
			final String field)
	{
		try(TransactionTry tx = MODEL.startTransactionTry(PrimaryKeyTest.class.getName()))
		{
			return tx.commit(
					new PrimaryKeyItem(field)
			);
		}
	}

	@Test public void testMultipleTransactions()
	{
		SequenceInfoAssert.assertInfo(model.getSequenceInfo(), TYPE.getThis(), next);

		SequenceInfoAssert.assertInfo(TYPE, TYPE.getPrimaryKeyInfo());
		SequenceInfoAssert.assertInfo(next, next.getDefaultToNextInfo());

		newPrimaryKeyItem("first", 5);
		assertInfo(TYPE, 1, 0, 0, TYPE.getPrimaryKeyInfo(), 0);
		assertInfo(next, next.getDefaultToNextInfo(), (hsqldb||mysql)?6:5);

		newPrimaryKeyItem("second");
		assertInfo(TYPE, 2, 0, 1, TYPE.getPrimaryKeyInfo(), 0);
		assertInfo(next, 1, 0, 0, next.getDefaultToNextInfo(), !oracle?5:0);

		newPrimaryKeyItem("third");
		assertInfo(TYPE, 3, 0, 2, TYPE.getPrimaryKeyInfo(), 0);
		assertInfo(next, 2, 0, 1, next.getDefaultToNextInfo(), !oracle?4:0);
	}
}

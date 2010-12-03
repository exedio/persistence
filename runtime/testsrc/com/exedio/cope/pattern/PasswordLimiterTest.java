/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.PasswordLimiterItem.passwordLimited;
import static com.exedio.cope.pattern.PasswordLimiterItem.purgePasswordLimited;

import java.util.Date;
import java.util.List;

import com.exedio.cope.junit.CopeTest;
import com.exedio.cope.pattern.PasswordLimiter.Refusal;
import com.exedio.cope.util.Interrupter;

public class PasswordLimiterTest extends CopeTest
{
	public PasswordLimiterTest()
	{
		super(PasswordLimiterModelTest.MODEL);
	}

	PasswordLimiterItem i;
	PasswordLimiterItem i2;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		i = deleteOnTearDown(new PasswordLimiterItem(PASSWORD));
		i2 = deleteOnTearDown(new PasswordLimiterItem(PASSWORD2));
	}

	private static final String PASSWORD = "correctPassword8927365";
	private static final String PASSWORD2 = "correctPassword6576675";

	public void testIt() throws InterruptedException
	{
		assertTrue(i.checkPassword(PASSWORD));
		assertEquals(list(), getRefusals());
		assertTrue(i2.checkPassword(PASSWORD2));

		assertTrue(i.checkPasswordLimited(PASSWORD));
		assertEquals(list(), getRefusals());
		assertTrue(i2.checkPasswordLimited(PASSWORD2));

		final Refusal refusal1 = refuse();
		assertEquals(list(refusal1), getRefusals());
		assertTrue(i.checkPasswordLimited(PASSWORD));
		assertTrue(i2.checkPasswordLimited(PASSWORD2));

		final Refusal refusal2 = refuse();
		assertEquals(list(refusal1, refusal2), getRefusals());

		assertFalse(i.checkPasswordLimited(PASSWORD));
		assertEquals(list(refusal1, refusal2), getRefusals());
		assertTrue(i2.checkPasswordLimited(PASSWORD2));

		assertFalse(i.checkPasswordLimited("wrongpass"));
		assertEquals(list(refusal1, refusal2), getRefusals());

		assertEquals(0, purge());
		assertEquals(list(refusal1, refusal2), getRefusals());
		assertTrue(refusal1.existsCopeItem());
		assertTrue(refusal2.existsCopeItem());

		sleepLongerThan(passwordLimited.getPeriod());
		assertTrue(i.checkPasswordLimited(PASSWORD));

		final Refusal refusal3 = refuse();
		assertEquals(list(refusal1, refusal2, refusal3), getRefusals());
		assertTrue(i.checkPasswordLimited(PASSWORD));

		final Refusal refusal4 = refuse();
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		assertFalse(i.checkPasswordLimited(PASSWORD));
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		assertFalse(i.checkPasswordLimited("wrongpass"));
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		assertEquals(2, purge());
		assertEquals(list(refusal3, refusal4), getRefusals());
		assertFalse(refusal1.existsCopeItem());
		assertFalse(refusal2.existsCopeItem());
		assertTrue(refusal3.existsCopeItem());
		assertTrue(refusal4.existsCopeItem());

		assertEquals(0, purge());
		assertEquals(list(refusal3, refusal4), getRefusals());
		assertFalse(refusal1.existsCopeItem());
		assertFalse(refusal2.existsCopeItem());
		assertTrue(refusal3.existsCopeItem());
		assertTrue(refusal4.existsCopeItem());

		sleepLongerThan(passwordLimited.getPeriod());
		assertEquals(2, purge());
		assertEquals(list(), getRefusals());
		assertFalse(refusal1.existsCopeItem());
		assertFalse(refusal2.existsCopeItem());
		assertFalse(refusal3.existsCopeItem());
		assertFalse(refusal4.existsCopeItem());
	}

	private final Refusal refuse()
	{
		final List<Refusal> existing = getRefusals();
		final Date before = new Date();
		assertEquals(false, i.checkPasswordLimited("wrongpass"));
		final Date after = new Date();
		final Refusal result = passwordLimited.getRefusalType().searchSingletonStrict(passwordLimited.getRefusalType().getThis().in(existing).not());
		assertNotNull(result);
		assertWithin(before, after, result.getDate());
		return result;
	}

	private final List<Refusal> getRefusals()
	{
		return passwordLimited.getRefusalType().search(null, passwordLimited.getRefusalType().getThis(), true);
	}

	private final int purge()
	{
		model.commit();
		final int result = purgePasswordLimited((Interrupter)null);
		model.startTransaction("PasswordRecoveryTest");
		return result;
	}
}

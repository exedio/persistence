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

import static com.exedio.cope.pattern.PasswordLimiterItem.passwordLimited;
import static com.exedio.cope.pattern.PasswordLimiterItem.period0;
import static com.exedio.cope.pattern.PasswordLimiterItem.period1;
import static com.exedio.cope.pattern.PasswordLimiterItem.period1M;
import static com.exedio.cope.pattern.PasswordLimiterItem.period1P;
import static com.exedio.cope.pattern.PasswordLimiterItem.period2;
import static com.exedio.cope.pattern.PasswordLimiterItem.period2P;
import static com.exedio.cope.pattern.PasswordLimiterItem.purgePasswordLimited;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.misc.DeleteJobContext;
import com.exedio.cope.pattern.PasswordLimiter.ExceededException;
import com.exedio.cope.pattern.PasswordLimiter.Refusal;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class PasswordLimiterVerboseTest extends TestWithEnvironment
{
	public PasswordLimiterVerboseTest()
	{
		super(PasswordLimiterModelTest.MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	PasswordLimiterItem i;
	PasswordLimiterItem i2;

	@BeforeEach final void setUp()
	{
		i = new PasswordLimiterItem(PASSWORD);
		i2 = new PasswordLimiterItem(PASSWORD2);
		clockRule.override(clock);
	}

	private static final String PASSWORD = "correctPassword8927365";
	private static final String PASSWORD2 = "correctPassword6576675";

	@Test void testIt() throws ExceededException
	{
		assertTrue(i.checkPassword(PASSWORD));
		assertEquals(list(), getRefusals());
		assertTrue(i2.checkPassword(PASSWORD2));

		assertTrue(i.checkPasswordLimitedVerbosely(PASSWORD, clock, period0));
		assertEquals(list(), getRefusals());
		assertTrue(i2.checkPasswordLimitedVerbosely(PASSWORD2, clock, period0));

		final Refusal refusal1 = refuse(period0);
		assertEquals(list(refusal1), getRefusals());
		assertTrue(i.checkPasswordLimitedVerbosely(PASSWORD, clock, period0));
		assertTrue(i2.checkPasswordLimitedVerbosely(PASSWORD2, clock, period0));

		final Refusal refusal2 = refuse(period0);
		assertEquals(list(refusal1, refusal2), getRefusals());

		i.checkPasswordLimitedVerboselyFails(PASSWORD, clock, period0, period1);
		assertTrue(i2.checkPasswordLimitedVerbosely(PASSWORD2, clock, period0));
		assertEquals(list(refusal1, refusal2), getRefusals());

		i.checkPasswordLimitedVerboselyFails("wrongpass", clock, period0, period1);
		assertEquals(list(refusal1, refusal2), getRefusals());

		clock.add(period0); // start
		assertEquals(0, purge(), "fails spuriously with actual 2");
		clock.assertEmpty();
		assertEquals(list(refusal1, refusal2), getRefusals());
		assertTrue(refusal1.existsCopeItem());
		assertTrue(refusal2.existsCopeItem());

		i.checkPasswordLimitedVerboselyFails(PASSWORD, clock, period1M, period1);

		assertTrue(i.checkPasswordLimitedVerbosely(PASSWORD, clock, period1)); // refusal expires

		final Refusal refusal3 = refuse(period1);
		assertEquals(list(refusal1, refusal2, refusal3), getRefusals());
		assertTrue(i.checkPasswordLimitedVerbosely(PASSWORD, clock, period1));

		final Refusal refusal4 = refuse(period1);
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		i.checkPasswordLimitedVerboselyFails(PASSWORD, clock, period1, period2);
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		i.checkPasswordLimitedVerboselyFails("wrongpass", clock, period1, period2);
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		clock.add(period1);
		assertEquals(0, purge());
		clock.assertEmpty();
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());
		assertTrue(refusal1.existsCopeItem());
		assertTrue(refusal2.existsCopeItem());
		assertTrue(refusal3.existsCopeItem());
		assertTrue(refusal4.existsCopeItem());

		clock.add(period1P); // refusal expires
		assertEquals(2, purge());
		clock.assertEmpty();
		assertEquals(list(refusal3, refusal4), getRefusals());
		assertFalse(refusal1.existsCopeItem());
		assertFalse(refusal2.existsCopeItem());
		assertTrue(refusal3.existsCopeItem());
		assertTrue(refusal4.existsCopeItem());

		clock.add(period2);
		assertEquals(0, purge());
		clock.assertEmpty();
		assertEquals(list(refusal3, refusal4), getRefusals());
		assertFalse(refusal1.existsCopeItem());
		assertFalse(refusal2.existsCopeItem());
		assertTrue(refusal3.existsCopeItem());
		assertTrue(refusal4.existsCopeItem());

		clock.add(period2P); // refusal expires
		assertEquals(2, purge());
		clock.assertEmpty();
		assertEquals(list(), getRefusals());
		assertFalse(refusal1.existsCopeItem());
		assertFalse(refusal2.existsCopeItem());
		assertFalse(refusal3.existsCopeItem());
		assertFalse(refusal4.existsCopeItem());
	}

	private Refusal refuse(final String date) throws ExceededException
	{
		final List<Refusal> existing = getRefusals();
		assertEquals(false, i.checkPasswordLimitedVerbosely("wrongpass", clock, date));
		final Refusal result = passwordLimited.getRefusalType().searchSingletonStrict(passwordLimited.getRefusalType().getThis().in(existing).not());
		assertNotNull(result);
		assertEquals(i, result.getParent());
		assertEqualsDate(date, result.getDate());
		return result;
	}

	private static List<Refusal> getRefusals()
	{
		return passwordLimited.getRefusalType().search(null, passwordLimited.getRefusalType().getThis(), true);
	}

	private int purge()
	{
		final DeleteJobContext ctx = new DeleteJobContext(PasswordLimiterModelTest.MODEL);
		model.commit();
		purgePasswordLimited(ctx);
		model.startTransaction("PasswordRecoveryTest");
		return ctx.getProgress();
	}

	private void assertEqualsDate(final String expected, final Date actual)
	{
		clock.assertEqualsFormatted(expected, actual);
	}
}

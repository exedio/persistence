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

import static com.exedio.cope.SchemaInfoAssert.assertNoUpdateCounterColumn;
import static com.exedio.cope.pattern.PasswordLimiterItem.passwordLimited;
import static com.exedio.cope.pattern.PasswordLimiterItem.period0;
import static com.exedio.cope.pattern.PasswordLimiterItem.period1;
import static com.exedio.cope.pattern.PasswordLimiterItem.period1M;
import static com.exedio.cope.pattern.PasswordLimiterItem.period1P;
import static com.exedio.cope.pattern.PasswordLimiterItem.period2;
import static com.exedio.cope.pattern.PasswordLimiterItem.period2P;
import static com.exedio.cope.pattern.PasswordLimiterItem.purgePasswordLimited;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.pattern.PasswordLimiter.Refusal;
import com.exedio.cope.tojunit.ClockRule;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class PasswordLimiterTest extends TestWithEnvironment
{
	public PasswordLimiterTest()
	{
		super(PasswordLimiterModelTest.MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	@Rule public final RuleChain ruleChain = RuleChain.outerRule(clockRule);

	PasswordLimiterItem i;
	PasswordLimiterItem i2;

	@Before public final void setUp()
	{
		i = new PasswordLimiterItem(PASSWORD);
		i2 = new PasswordLimiterItem(PASSWORD2);
		clockRule.override(clock);
	}

	private static final String PASSWORD = "correctPassword8927365";
	private static final String PASSWORD2 = "correctPassword6576675";

	@Test public void testIt()
	{
		assertNoUpdateCounterColumn(passwordLimited.getRefusalType());

		assertTrue(i.checkPassword(PASSWORD));
		assertEquals(list(), getRefusals());
		assertTrue(i2.checkPassword(PASSWORD2));

		assertTrue(i.checkPasswordLimited(PASSWORD, clock, period0));
		assertEquals(list(), getRefusals());
		assertTrue(i2.checkPasswordLimited(PASSWORD2, clock, period0));

		final Refusal refusal1 = refuse(period0);
		assertEquals(list(refusal1), getRefusals());
		assertTrue(i.checkPasswordLimited(PASSWORD, clock, period0));
		assertTrue(i2.checkPasswordLimited(PASSWORD2, clock, period0));

		final Refusal refusal2 = refuse(period0);
		assertEquals(list(refusal1, refusal2), getRefusals());

		assertFalse(i.checkPasswordLimited(PASSWORD, clock, period0));
		assertEquals(list(refusal1, refusal2), getRefusals());
		assertTrue(i2.checkPasswordLimited(PASSWORD2, clock, period0));

		assertFalse(i.checkPasswordLimited("wrongpass", clock, period0));
		assertEquals(list(refusal1, refusal2), getRefusals());

		clock.add(period0);
		assertEquals(0, purge());
		clock.assertEmpty();
		assertEquals(list(refusal1, refusal2), getRefusals());
		assertTrue(refusal1.existsCopeItem());
		assertTrue(refusal2.existsCopeItem());

		clock.add(period1M);
		assertFalse(i.checkPasswordLimited(PASSWORD));
		clock.assertEmpty();

		clock.add(period1);
		assertTrue(i.checkPasswordLimited(PASSWORD));
		clock.assertEmpty();

		final Refusal refusal3 = refuse(period1);
		assertEquals(list(refusal1, refusal2, refusal3), getRefusals());
		assertTrue(i.checkPasswordLimited(PASSWORD, clock, period1));

		final Refusal refusal4 = refuse(period1);
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		assertFalse(i.checkPasswordLimited(PASSWORD, clock, period1));
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		assertFalse(i.checkPasswordLimited("wrongpass", clock, period1));
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

	@Test public void testReset()
	{
		final String now = period0;
		assertEquals(list(), getRefusals());
		assertEquals(true, i .checkPasswordLimited(PASSWORD , clock, now));
		assertEquals(true, i2.checkPasswordLimited(PASSWORD2, clock, now));

		final Refusal refusal1a = refuse(i, now);
		assertEquals(list(refusal1a), getRefusals());
		assertEquals(true, i .checkPasswordLimited(PASSWORD , clock, now));
		assertEquals(true, i2.checkPasswordLimited(PASSWORD2, clock, now));

		final Refusal refusal1b = refuse(i, now);
		assertEquals(list(refusal1a, refusal1b), getRefusals());
		assertEquals(false, i .checkPasswordLimited(PASSWORD , clock, now));
		assertEquals(true , i2.checkPasswordLimited(PASSWORD2, clock, now));

		final Refusal refusal2a = refuse(i2, now);
		assertEquals(list(refusal1a, refusal1b, refusal2a), getRefusals());
		assertEquals(false, i .checkPasswordLimited(PASSWORD , clock, now));
		assertEquals(true , i2.checkPasswordLimited(PASSWORD2, clock, now));

		final Refusal refusal2b = refuse(i2, now);
		assertEquals(list(refusal1a, refusal1b, refusal2a, refusal2b), getRefusals());
		assertEquals(false, i .checkPasswordLimited(PASSWORD , clock, now));
		assertEquals(false, i2.checkPasswordLimited(PASSWORD2, clock, now));

		i.resetPasswordLimited();
		assertEquals(list(refusal2a, refusal2b), getRefusals());
		assertEquals(true , i .checkPasswordLimited(PASSWORD , clock, now));
		assertEquals(false, i2.checkPasswordLimited(PASSWORD2, clock, now));
	}

	private final Refusal refuse(final String date)
	{
		return refuse(i, date);
	}

	private final Refusal refuse(final PasswordLimiterItem item, final String date)
	{
		final List<Refusal> existing = getRefusals();
		clock.add(date);
		assertEquals(false, item.checkPasswordLimited("wrongpass"));
		clock.assertEmpty();
		final Refusal result = passwordLimited.getRefusalType().searchSingletonStrict(passwordLimited.getRefusalType().getThis().in(existing).not());
		assertNotNull(result);
		assertEqualsDate(date, result.getDate());
		return result;
	}

	private static final List<Refusal> getRefusals()
	{
		return passwordLimited.getRefusalType().search(null, passwordLimited.getRefusalType().getThis(), true);
	}

	private final int purge()
	{
		final CountJobContext ctx = new CountJobContext();
		model.commit();
		purgePasswordLimited(ctx);
		model.startTransaction("PasswordRecoveryTest");
		return ctx.progress;
	}

	private void assertEqualsDate(final String expected, final Date actual)
	{
		clock.assertEqualsFormatted(expected, actual);
	}
}

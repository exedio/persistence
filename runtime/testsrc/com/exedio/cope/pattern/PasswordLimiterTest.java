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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.misc.DeleteJobContext;
import com.exedio.cope.pattern.PasswordLimiter.Refusal;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class PasswordLimiterTest extends TestWithEnvironment
{
	public PasswordLimiterTest()
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

	@Test void testIt()
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

		denyC.assertCount(0);
		assertFalse(i.checkPasswordLimited(PASSWORD, clock, period0));
		denyC.assertCount(1);
		assertEquals(list(refusal1, refusal2), getRefusals());
		assertTrue(i2.checkPasswordLimited(PASSWORD2, clock, period0));
		denyC.assertCount(0);
		assertEquals(list(refusal1, refusal2), getRefusals());

		assertFalse(i.checkPasswordLimited("wrongpass", clock, period0));
		denyC.assertCount(1);
		assertEquals(list(refusal1, refusal2), getRefusals());

		clock.add(period0);
		assertEquals(0, purge());
		clock.assertEmpty();
		assertEquals(list(refusal1, refusal2), getRefusals());
		assertTrue(refusal1.existsCopeItem());
		assertTrue(refusal2.existsCopeItem());

		assertFalse(i.checkPasswordLimited(PASSWORD, clock, period1M));
		denyC.assertCount(1);
		assertTrue (i.checkPasswordLimited(PASSWORD, clock, period1 ));
		denyC.assertCount(0);

		final Refusal refusal3 = refuse(period1);
		assertEquals(list(refusal1, refusal2, refusal3), getRefusals());
		assertTrue(i.checkPasswordLimited(PASSWORD, clock, period1));
		denyC.assertCount(0);

		final Refusal refusal4 = refuse(period1);
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		assertFalse(i.checkPasswordLimited(PASSWORD, clock, period1));
		denyC.assertCount(1);
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		assertFalse(i.checkPasswordLimited("wrongpass", clock, period1));
		denyC.assertCount(1);
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

		denyC.assertCount(0);
		denyOtherC.assertCount(0);
	}

	@Test void testReset()
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
		denyC.assertCount(0);
		assertEquals(false, i .checkPasswordLimited(PASSWORD , clock, now));
		denyC.assertCount(1);
		assertEquals(true , i2.checkPasswordLimited(PASSWORD2, clock, now));
		denyC.assertCount(0);

		final Refusal refusal2a = refuse(i2, now);
		assertEquals(list(refusal1a, refusal1b, refusal2a), getRefusals());
		assertEquals(false, i .checkPasswordLimited(PASSWORD , clock, now));
		denyC.assertCount(1);
		assertEquals(true , i2.checkPasswordLimited(PASSWORD2, clock, now));
		denyC.assertCount(0);

		final Refusal refusal2b = refuse(i2, now);
		assertEquals(list(refusal1a, refusal1b, refusal2a, refusal2b), getRefusals());
		assertEquals(false, i .checkPasswordLimited(PASSWORD , clock, now));
		denyC.assertCount(1);
		assertEquals(false, i2.checkPasswordLimited(PASSWORD2, clock, now));
		denyC.assertCount(1);

		i.resetPasswordLimited();
		assertEquals(list(refusal2a, refusal2b), getRefusals());
		assertEquals(true , i .checkPasswordLimited(PASSWORD , clock, now));
		denyC.assertCount(0);
		assertEquals(false, i2.checkPasswordLimited(PASSWORD2, clock, now));
		denyC.assertCount(1);

		denyC.assertCount(0);
		denyOtherC.assertCount(0);
	}

	private Refusal refuse(final String date)
	{
		return refuse(i, date);
	}

	private Refusal refuse(final PasswordLimiterItem item, final String date)
	{
		final List<Refusal> existing = getRefusals();
		denyC.assertCount(0);
		assertEquals(false, item.checkPasswordLimited("wrongpass", clock, date));
		denyC.assertCount(0);
		final Refusal result = passwordLimited.getRefusalType().searchSingletonStrict(passwordLimited.getRefusalType().getThis().in(existing).not());
		assertNotNull(result);
		assertEquals(item, result.getParent());
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

	private final FeatureCounterTester denyC       = new FeatureCounterTester(passwordLimited, "deny", "verbose", "no");
	private final FeatureCounterTester denyOtherC  = new FeatureCounterTester(passwordLimited, "deny", "verbose", "yes");
}

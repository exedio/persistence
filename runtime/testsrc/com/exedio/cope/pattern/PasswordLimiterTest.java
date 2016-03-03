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
import static com.exedio.cope.pattern.PasswordLimiterItem.purgePasswordLimited;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.PasswordLimiter.Refusal;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.RelativeMockClockStrategy;
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

	private final RelativeMockClockStrategy clock = new RelativeMockClockStrategy();
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

		assertTrue(i.checkPasswordLimited(PASSWORD, clock));
		assertEquals(list(), getRefusals());
		assertTrue(i2.checkPasswordLimited(PASSWORD2, clock));

		final Refusal refusal1 = refuse();
		assertEquals(list(refusal1), getRefusals());
		assertTrue(i.checkPasswordLimited(PASSWORD, clock));
		assertTrue(i2.checkPasswordLimited(PASSWORD2, clock));

		final Refusal refusal2 = refuse();
		assertEquals(list(refusal1, refusal2), getRefusals());

		assertFalse(i.checkPasswordLimited(PASSWORD, clock));
		assertEquals(list(refusal1, refusal2), getRefusals());
		assertTrue(i2.checkPasswordLimited(PASSWORD2, clock));

		assertFalse(i.checkPasswordLimited("wrongpass", clock));
		assertEquals(list(refusal1, refusal2), getRefusals());

		clock.addNow();
		assertEquals(0, purge());
		clock.assertEmpty();
		assertEquals(list(refusal1, refusal2), getRefusals());
		assertTrue(refusal1.existsCopeItem());
		assertTrue(refusal2.existsCopeItem());

		clock.addOffset(passwordLimited.getPeriod()-1);
		assertFalse(i.checkPasswordLimited(PASSWORD));
		clock.assertEmpty();

		clock.addOffset(1); // refusal expires
		assertTrue(i.checkPasswordLimited(PASSWORD));
		clock.assertEmpty();

		final Refusal refusal3 = refuse();
		assertEquals(list(refusal1, refusal2, refusal3), getRefusals());
		assertTrue(i.checkPasswordLimited(PASSWORD, clock));

		final Refusal refusal4 = refuse();
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		assertFalse(i.checkPasswordLimited(PASSWORD, clock));
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		assertFalse(i.checkPasswordLimited("wrongpass", clock));
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());

		clock.addNow();
		assertEquals(0, purge());
		assertEquals(list(refusal1, refusal2, refusal3, refusal4), getRefusals());
		assertTrue(refusal1.existsCopeItem());
		assertTrue(refusal2.existsCopeItem());
		assertTrue(refusal3.existsCopeItem());
		assertTrue(refusal4.existsCopeItem());

		clock.addOffset(1); // refusal expires
		assertEquals(2, purge());
		clock.assertEmpty();
		assertEquals(list(refusal3, refusal4), getRefusals());
		assertFalse(refusal1.existsCopeItem());
		assertFalse(refusal2.existsCopeItem());
		assertTrue(refusal3.existsCopeItem());
		assertTrue(refusal4.existsCopeItem());

		clock.addOffset(passwordLimited.getPeriod()-1);
		assertEquals(0, purge());
		clock.assertEmpty();
		assertEquals(list(refusal3, refusal4), getRefusals());
		assertFalse(refusal1.existsCopeItem());
		assertFalse(refusal2.existsCopeItem());
		assertTrue(refusal3.existsCopeItem());
		assertTrue(refusal4.existsCopeItem());

		clock.addOffset(1); // refusal expires
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
		assertEquals(list(), getRefusals());
		assertEquals(true, i .checkPasswordLimited(PASSWORD , clock));
		assertEquals(true, i2.checkPasswordLimited(PASSWORD2, clock));

		final Refusal refusal1a = refuse(i);
		assertEquals(list(refusal1a), getRefusals());
		assertEquals(true, i .checkPasswordLimited(PASSWORD , clock));
		assertEquals(true, i2.checkPasswordLimited(PASSWORD2, clock));

		final Refusal refusal1b = refuse(i);
		assertEquals(list(refusal1a, refusal1b), getRefusals());
		assertEquals(false, i .checkPasswordLimited(PASSWORD , clock));
		assertEquals(true , i2.checkPasswordLimited(PASSWORD2, clock));

		final Refusal refusal2a = refuse(i2);
		assertEquals(list(refusal1a, refusal1b, refusal2a), getRefusals());
		assertEquals(false, i .checkPasswordLimited(PASSWORD , clock));
		assertEquals(true , i2.checkPasswordLimited(PASSWORD2, clock));

		final Refusal refusal2b = refuse(i2);
		assertEquals(list(refusal1a, refusal1b, refusal2a, refusal2b), getRefusals());
		assertEquals(false, i .checkPasswordLimited(PASSWORD , clock));
		assertEquals(false, i2.checkPasswordLimited(PASSWORD2, clock));

		i.resetPasswordLimited();
		assertEquals(list(refusal2a, refusal2b), getRefusals());
		assertEquals(true , i .checkPasswordLimited(PASSWORD , clock));
		assertEquals(false, i2.checkPasswordLimited(PASSWORD2, clock));
	}

	private final Refusal refuse()
	{
		return refuse(i);
	}

	private final Refusal refuse(final PasswordLimiterItem item)
	{
		final List<Refusal> existing = getRefusals();
		final long f = clock.addNow();
		assertEquals(false, item.checkPasswordLimited("wrongpass"));
		clock.assertEmpty();
		final Refusal result = passwordLimited.getRefusalType().searchSingletonStrict(passwordLimited.getRefusalType().getThis().in(existing).not());
		assertNotNull(result);
		assertEquals(new Date(f), result.getDate());
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
}

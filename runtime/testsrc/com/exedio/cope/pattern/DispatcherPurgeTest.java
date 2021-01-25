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

import static java.time.Duration.ofDays;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Query;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.misc.DeleteJobContext;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.JobContexts;
import com.exedio.cope.util.Sources;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DispatcherPurgeTest extends TestWithEnvironment
{
	public DispatcherPurgeTest()
	{
		super(DispatcherModelTest.MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	DispatcherItem itemSuccessBefore;
	DispatcherItem itemSuccessAfter;
	DispatcherItem itemFailureBefore;
	DispatcherItem itemFailureAfter;

	@BeforeEach void setUp()
	{
		itemSuccessBefore = new DispatcherItem("successBefore", false);
		itemSuccessAfter  = new DispatcherItem("successAfter",  false);
		itemFailureBefore = new DispatcherItem("failureBefore", true );
		itemFailureAfter  = new DispatcherItem("failureAfter",  true );
		clockRule.override(clock);
	}


	@Test void testSame()
	{
		dispatch(8, 8);

		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND toTarget-unpend-date<'1970-01-09 00:00:00.555')",
				purge(555+20*day, 12, 12));

		assertPurged(false, false, 1);
	}

	@Test void testDifferent()
	{
		dispatch(8, 5);

		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND (" +
					"(toTarget-unpend-success='true' AND toTarget-unpend-date<'1970-01-09 00:00:00.555') OR " +
					"(toTarget-unpend-success='false' AND toTarget-unpend-date<'1970-01-06 00:00:00.555')))",
				purge(555+20*day, 12, 15));

		assertPurged(false, false, 1);
	}

	@Test void testNoPurge()
	{
		dispatch(8, 5);

		assertEquals(false, itemSuccessBefore.isToTargetNoPurge());
		assertEquals(false, itemFailureBefore.isToTargetNoPurge());
		itemSuccessBefore.setToTargetNoPurge(true);
		itemFailureBefore.setToTargetNoPurge(true);
		assertEquals(true, itemSuccessBefore.isToTargetNoPurge());
		assertEquals(true, itemFailureBefore.isToTargetNoPurge());

		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND (" +
					"(toTarget-unpend-success='true' AND toTarget-unpend-date<'1970-01-09 00:00:00.555') OR " +
					"(toTarget-unpend-success='false' AND toTarget-unpend-date<'1970-01-06 00:00:00.555')))",
				purge(555+20*day, 12, 15));

		assertPurged(true, true, 1);
	}

	@Test void testOmitSuccess()
	{
		dispatch(8, 8);

		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND toTarget-unpend-success='false' AND toTarget-unpend-date<'1970-01-09 00:00:00.555')",
				purge(555+20*day, 0, 12));

		assertPurged(true, false, 1);
	}

	@Test void testOmitFinalFailure()
	{
		dispatch(8, 8);

		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND toTarget-unpend-success='true' AND toTarget-unpend-date<'1970-01-09 00:00:00.555')",
				purge(555+20*day, 12, 0));

		assertPurged(false, true, 1);
	}

	@Test void testOmitBoth()
	{
		dispatch(8, 8);

		assertEquals(
				null,
				purge(null, 0, 0));

		assertPurged(true, true, 0);
	}

	@Test void testRestriction()
	{
		dispatch(8, 8);

		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND body='bodyMismatch' " +
				"AND toTarget-unpend-date<'1970-01-09 00:00:00.555')",
				purge(555+20*day, 12, 12, DispatcherItem.body.equal("bodyMismatch")));

		assertPurged(true, true, 1);

		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND toTarget-unpend-date<'1970-01-09 00:00:00.555')",
				purge(555+20*day, 12, 12));

		assertPurged(false, false, 1);
	}


	private static void assertIt(
			final boolean pending,
			final Boolean success,
			final Long date,
			final DispatcherItem item)
	{
		assertEquals(pending, item.isToTargetPending(),           "pending");
		assertEquals(success, item.getToTargetUnpendSuccess(),    "success");
		assertEquals(date,    item.getToTargetUnpendDateMillis(), "date");
	}

	private static final long day = 1000 * 60 * 60 * 24;

	private void dispatch(final int success, final int failure)
	{
		model.commit();
		clock.add(554+day*success);
		clock.add(555+day*success);
		clock.add(554+day*failure);
		clock.add(555+day*failure);
		DispatcherItem.dispatchToTarget(new Dispatcher.Config(1, 100), JobContexts.EMPTY);
		clock.assertEmpty();
		model.startTransaction("DispatcherPurgeTest");
		assertIt(false, true,  554+day*success, itemSuccessBefore);
		assertIt(false, true,  555+day*success, itemSuccessAfter );
		assertIt(false, false, 554+day*failure, itemFailureBefore);
		assertIt(false, false, 555+day*failure, itemFailureAfter );
	}

	private String purge(final Long now, final Integer success, final Integer failure)
	{
		return purge(now, success, failure, null);
	}

	private String purge(
			final Long now, final Integer success, final Integer failure,
			final Condition restriction)
	{
		model.commit();

		final Properties props = new Properties();
		if(success!=null)
			props.setProperty("retain.success",      ofDays(success).toString());
		if(failure!=null)
			props.setProperty("retain.finalFailure", ofDays(failure).toString());

		final DispatcherPurgeProperties purgeProps =
				DispatcherPurgeProperties.factory().create(Sources.view(props, "description"));

		if(now!=null)
			clock.add(now);
		final DeleteJobContext ctx = new DeleteJobContext(DispatcherModelTest.MODEL);
		if(restriction==null)
			DispatcherItem.purgeToTarget(purgeProps, ctx);
		else
			DispatcherItem.purgeToTarget(purgeProps, restriction, ctx);
		clock.assertEmpty();

		if(now!=null)
			clock.add(now);
		final Query<? extends Item> query = DispatcherItem.toTarget.purgeQuery(purgeProps, restriction!=null ? restriction : Condition.TRUE);
		clock.assertEmpty();

		model.startTransaction("DispatcherPurgeTest");
		return query!=null ? query.toString() : null;
	}

	private void assertPurged(final boolean success, final boolean failure, final int timerCount)
	{
		assertEquals(success, itemSuccessBefore.existsCopeItem());
		assertEquals(true,    itemSuccessAfter .existsCopeItem());
		assertEquals(failure, itemFailureBefore.existsCopeItem());
		assertEquals(true,    itemFailureAfter .existsCopeItem());
		timer.assertCount(timerCount);
	}

	private final FeatureTimerTester timer = new FeatureTimerTester(DispatcherItem.toTarget, "purge");

	@AfterEach void afterEach()
	{
		DispatcherItem.toTarget.reset();
	}
}

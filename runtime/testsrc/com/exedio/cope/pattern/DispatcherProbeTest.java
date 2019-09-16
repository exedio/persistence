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

import static com.exedio.cope.pattern.DispatcherItem.historyAdd;
import static com.exedio.cope.pattern.DispatcherItem.historyAssert;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.util.AssertionErrorJobContext;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DispatcherProbeTest extends TestWithEnvironment
{
	private static final Dispatcher.Config config = new Dispatcher.Config();

	public DispatcherProbeTest()
	{
		super(DispatcherModelTest.MODEL);
	}

	private final LogRule log = new LogRule(Dispatcher.class.getName() + '.' + DispatcherItem.toTarget.getID());

	@BeforeEach void setUp()
	{
		DispatcherItem.historyClear();
	}

	@Test void testEmpty()
	{
		DispatcherItem.toTarget.setProbeRequired(true);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		log.setLevelDebug();

		model.commit();
		historyAssert();
		DispatcherItem.dispatchToTarget(config, CTX);
		model.startTransaction("DispatcherTest");
		historyAssert(
				// probe must not appear here
				"ctx stop", "ctx defer", "dispatch " + item1, "ctx progress");
		// probe must not appear here
		log.assertDebug("dispatching " + item1);
		log.assertInfoMS("success for " + item1 + ", took XXms");
		log.assertEmpty();
		succeedT.assertCount(1);
		failT.   assertCount(0);
		probeT.  assertCount(0);
		assertEquals(false, item1.isToTargetPending());
	}

	@Test void testOkRequired()
	{
		final CountProbe probe = new CountProbe(1);
		DispatcherItem.toTarget.setProbeRequired(true);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		final DispatcherItem item2 = new DispatcherItem("item2", false);

		dispatch(probe);
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "dispatch " + item2, "ctx progress");
		succeedT.assertCount(2);
		failT.   assertCount(0);
		probeT.  assertCount(1);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(false, item2.isToTargetPending());

		final DispatcherItem item3 = new DispatcherItem("item3", false);
		dispatch(probe);
		historyAssert(
				// probe must not appear here
				"ctx stop", "ctx defer", "dispatch " + item3, "ctx progress");
		succeedT.assertCount(1);
		failT.   assertCount(0);
		probeT.  assertCount(0);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(false, item2.isToTargetPending());
		assertEquals(false, item3.isToTargetPending());
	}

	@Test void testOkNotRequired()
	{
		final CountProbe probe = new CountProbe(0);
		DispatcherItem.toTarget.setProbeRequired(false);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		final DispatcherItem item2 = new DispatcherItem("item2", false);

		dispatch(probe);
		historyAssert(
				"ctx stop", "ctx defer", "dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "dispatch " + item2, "ctx progress");
		succeedT.assertCount(2);
		failT.   assertCount(0);
		probeT.  assertCount(0);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(false, item2.isToTargetPending());
	}

	@Test void testFailInitial()
	{
		final CountProbe probe = new CountProbe(0);
		DispatcherItem.toTarget.setProbeRequired(true);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		final DispatcherItem item2 = new DispatcherItem("item2", false);

		dispatchFail(probe);
		historyAssert(
				"ctx stop", "ctx defer", "PROBE");
		succeedT.assertCount(0);
		failT.   assertCount(0);
		probeT.  assertCount(0);
		assertEquals(true, item1.isToTargetPending());
		assertEquals(true, item2.isToTargetPending());
	}

	@Test void testFailFirst()
	{
		final CountProbe probe = new CountProbe(0);
		DispatcherItem.toTarget.setProbeRequired(false);
		final DispatcherItem item1 = new DispatcherItem("item1", true);
		final DispatcherItem item2 = new DispatcherItem("item2", false);

		dispatchFail(probe);
		historyAssert(
				"ctx stop", "ctx defer", "dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "PROBE");
		succeedT.assertCount(0);
		failT.   assertCount(1);
		probeT.  assertCount(0);
		assertEquals(true, item1.isToTargetPending());
		assertEquals(true, item2.isToTargetPending());
	}

	@Test void testOkFailSecond()
	{
		final CountProbe probe = new CountProbe(0);
		DispatcherItem.toTarget.setProbeRequired(false);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		final DispatcherItem item2 = new DispatcherItem("item2", true);

		dispatch(probe);
		historyAssert(
				"ctx stop", "ctx defer", "dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "dispatch " + item2, "ctx progress");
		succeedT.assertCount(1);
		failT.   assertCount(1);
		probeT.  assertCount(0);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(true,  item2.isToTargetPending());
	}

	@Test void testFailSecond()
	{
		final CountProbe probe = new CountProbe(0);
		DispatcherItem.toTarget.setProbeRequired(false);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		final DispatcherItem item2 = new DispatcherItem("item2", true);
		final DispatcherItem item3 = new DispatcherItem("item3", false);

		dispatchFail(probe);
		historyAssert(
				"ctx stop", "ctx defer", "dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "dispatch " + item2, "ctx progress",
				"ctx stop", "ctx defer", "PROBE");
		succeedT.assertCount(1);
		failT.assertCount(1);
		probeT.assertCount(0);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(true,  item2.isToTargetPending());
		assertEquals(true,  item3.isToTargetPending());

		probe.setLimit(1); // is not enough
		dispatchFail(probe);
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "dispatch " + item2, "ctx progress",
				"ctx stop", "ctx defer", "PROBE");
		succeedT.assertCount(0);
		failT.   assertCount(1);
		probeT.  assertCount(1);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(true,  item2.isToTargetPending());
		assertEquals(true,  item3.isToTargetPending());

		probe.setLimit(2);
		dispatch(probe);
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "dispatch " + item2, "ctx progress",
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "dispatch " + item3, "ctx progress");
		succeedT.assertCount(1);
		failT.   assertCount(1);
		probeT.  assertCount(2);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(true,  item2.isToTargetPending());
		assertEquals(false, item3.isToTargetPending());
	}

	@Test void testProbeAtNextDispatch()
	{
		final CountProbe probe = new CountProbe(100);
		DispatcherItem.toTarget.setProbeRequired(false);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		final DispatcherItem item2 = new DispatcherItem("item2", true);

		dispatch(probe);
		historyAssert(
				"ctx stop", "ctx defer", "dispatch " + item1, "ctx progress",
				"ctx stop", "ctx defer", "dispatch " + item2, "ctx progress");
		succeedT.assertCount(1);
		failT.assertCount(1);
		probeT.assertCount(0);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(true,  item2.isToTargetPending());

		dispatch(probe);
		historyAssert(
				"ctx stop", "ctx defer", "probe", // cause by failure in previous dispatch
				"ctx stop", "ctx defer", "dispatch " + item2, "ctx progress");
		succeedT.assertCount(0);
		failT.assertCount(1);
		probeT.assertCount(1);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(true,  item2.isToTargetPending());
	}

	@Test void testLogging()
	{
		final CountProbe probe = new CountProbe(1);
		DispatcherItem.toTarget.setProbeRequired(true);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		log.setLevelDebug();

		dispatch(probe);
		historyAssert(
				"ctx stop", "ctx defer", "probe",
				"ctx stop", "ctx defer", "dispatch " + item1, "ctx progress");
		log.assertDebug("probing");
		log.assertInfoMS("probed, took XXms");
		log.assertDebug("dispatching " + item1);
		log.assertInfoMS("success for " + item1 + ", took XXms");
		log.assertEmpty();
		succeedT.assertCount(1);
		failT.   assertCount(0);
		probeT.  assertCount(1);
		assertEquals(false, item1.isToTargetPending());
	}


	private void dispatch(final Runnable probe)
	{
		model.commit();
		historyAssert();
		DispatcherItem.dispatchToTarget(config, probe, CTX);
		model.startTransaction("DispatcherTest");
	}

	private void dispatchFail(final Runnable probe)
	{
		model.commit();
		historyAssert();
		try
		{
			DispatcherItem.dispatchToTarget(config, probe, CTX);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("CountProbe", e.getMessage());
		}
		finally
		{
			model.startTransaction("DispatcherTest");
		}
	}

	private static final class CountProbe implements Runnable
	{
		private int limit;

		CountProbe(final int limit)
		{
			this.limit = limit;
		}

		void setLimit(final int limit)
		{
			this.limit = limit;
		}

		@Override
		public void run()
		{
			assertFalse(DispatcherModelTest.MODEL.hasCurrentTransaction());

			if(limit<=0)
			{
				historyAdd("PROBE");
				throw new IllegalStateException("CountProbe");
			}
			else
			{
				historyAdd("probe");
			}
			limit--;
		}
	}

	private static final AssertionErrorJobContext CTX = new AssertionErrorJobContext()
	{
		@Override public void stopIfRequested()
		{
			assertFalse(DispatcherModelTest.MODEL.hasCurrentTransaction());
			historyAdd("ctx stop");
		}
		@Override public Duration requestsDeferral()
		{
			assertFalse(DispatcherModelTest.MODEL.hasCurrentTransaction());
			historyAdd("ctx defer");
			return Duration.ZERO;
		}
		@Override public boolean supportsMessage()
		{
			assertFalse(DispatcherModelTest.MODEL.hasCurrentTransaction());
			return false;
		}
		@Override public void incrementProgress()
		{
			historyAdd("ctx progress");
		}
	};

	private final FeatureTimerTester succeedT = new FeatureTimerTester(DispatcherItem.toTarget, "dispatch", "result", "success");
	private final FeatureTimerTester failT    = new FeatureTimerTester(DispatcherItem.toTarget, "dispatch", "result", "failure");
	private final FeatureTimerTester probeT   = new FeatureTimerTester(DispatcherItem.toTarget, "probe");

	@AfterEach void afterEach()
	{
		DispatcherItem.toTarget.reset();
		DispatcherItem.historyClear();
	}
}

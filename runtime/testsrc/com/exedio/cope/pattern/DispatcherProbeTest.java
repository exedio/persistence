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

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.util.EmptyJobContext;
import org.junit.Test;

public class DispatcherProbeTest extends AbstractRuntimeModelTest
{
	private static final Dispatcher.Config config = new Dispatcher.Config();

	public DispatcherProbeTest()
	{
		super(DispatcherModelTest.MODEL);
	}

	@Test public void testOkRequired()
	{
		final CountProbe probe = new CountProbe(1);
		DispatcherItem.toTarget.setProbeRequired(true);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		final DispatcherItem item2 = new DispatcherItem("item2", false);

		dispatch(probe);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(false, item2.isToTargetPending());
	}

	@Test public void testOkNotRequired()
	{
		final CountProbe probe = new CountProbe(0);
		DispatcherItem.toTarget.setProbeRequired(false);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		final DispatcherItem item2 = new DispatcherItem("item2", false);

		dispatch(probe);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(false, item2.isToTargetPending());
	}

	@Test public void testFailInitial()
	{
		final CountProbe probe = new CountProbe(0);
		DispatcherItem.toTarget.setProbeRequired(true);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		final DispatcherItem item2 = new DispatcherItem("item2", false);

		dispatchFail(probe);
		assertEquals(true, item1.isToTargetPending());
		assertEquals(true, item2.isToTargetPending());
	}

	@Test public void testFailFirst()
	{
		final CountProbe probe = new CountProbe(0);
		DispatcherItem.toTarget.setProbeRequired(false);
		final DispatcherItem item1 = new DispatcherItem("item1", true);
		final DispatcherItem item2 = new DispatcherItem("item2", false);

		dispatchFail(probe);
		assertEquals(true, item1.isToTargetPending());
		assertEquals(true, item2.isToTargetPending());
	}

	@Test public void testOkFailSecond()
	{
		final CountProbe probe = new CountProbe(0);
		DispatcherItem.toTarget.setProbeRequired(false);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		final DispatcherItem item2 = new DispatcherItem("item2", true);

		dispatch(probe);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(true,  item2.isToTargetPending());
	}

	@Test public void testFailSecond()
	{
		final CountProbe probe = new CountProbe(0);
		DispatcherItem.toTarget.setProbeRequired(false);
		final DispatcherItem item1 = new DispatcherItem("item1", false);
		final DispatcherItem item2 = new DispatcherItem("item2", true);
		final DispatcherItem item3 = new DispatcherItem("item3", false);

		dispatchFail(probe);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(true,  item2.isToTargetPending());
		assertEquals(true,  item3.isToTargetPending());

		probe.setLimit(1); // is not enough
		dispatchFail(probe);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(true,  item2.isToTargetPending());
		assertEquals(true,  item3.isToTargetPending());

		probe.setLimit(2);
		dispatch(probe);
		assertEquals(false, item1.isToTargetPending());
		assertEquals(true,  item2.isToTargetPending());
		assertEquals(false, item3.isToTargetPending());
	}


	private void dispatch(final Runnable probe)
	{
		model.commit();
		DispatcherItem.dispatchToTarget(config, probe, new EmptyJobContext());
		model.startTransaction("DispatcherTest");
	}

	private void dispatchFail(final Runnable probe)
	{
		model.commit();
		try
		{
			DispatcherItem.dispatchToTarget(config, probe, new EmptyJobContext());
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

		public void run()
		{
			if(limit<=0)
				throw new IllegalStateException("CountProbe");
			limit--;
		}
	}
}

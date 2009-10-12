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

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.Dispatcher.Failure;
import com.exedio.cope.util.Interrupter;

public class DispatcherTest extends AbstractRuntimeTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(DispatcherItem.TYPE);
	
	public DispatcherTest()
	{
		super(MODEL);
	}

	DispatcherItem item;
	DispatcherItem item1;
	DispatcherItem item2;
	DispatcherItem item3;
	DispatcherItem item4;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = deleteOnTearDown(new DispatcherItem("item1", false));
		item2 = deleteOnTearDown(new DispatcherItem("item2", true));
		item3 = deleteOnTearDown(new DispatcherItem("item3", false));
		item4 = deleteOnTearDown(new DispatcherItem("item4", true));
	}
	
	public void testIt()
	{
		final Type<?> failureType = item.toTarget.getFailureType();
		
		// test model
		assertEqualsUnmodifiable(list(
				item.TYPE,
				failureType
			), model.getTypes());
		assertEqualsUnmodifiable(list(
				item.TYPE,
				failureType
			), model.getTypesSortedByHierarchy());
		assertEquals(DispatcherItem.class, item.TYPE.getJavaClass());
		assertEquals(true, item.TYPE.isBound());
		assertEquals(null, item.TYPE.getPattern());
		
		List<PartOf> partOfs = PartOf.getPartOfs(DispatcherItem.TYPE);
		assertEquals(1, partOfs.size());
		PartOf partOf = partOfs.get(0);
		assertSame(failureType, partOf.getType());
		assertEquals(DispatcherItem.TYPE, partOf.getContainer().getValueType());
		assertEqualsUnmodifiable(list(DispatcherItem.toTarget.failureType), DispatcherItem.toTarget.getSourceTypes());
		assertEquals(list(partOf), PartOf.getPartOfs(DispatcherItem.toTarget));

		assertEqualsUnmodifiable(list(
				item.TYPE.getThis(),
				item.body,
				item.dispatchCountCommitted,
				item.toTarget,
				item.toTarget.getPending(),
				item.toTarget.getSuccessDate(),
				item.toTarget.getSuccessElapsed()
			), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				failureType.getThis(),
				item.toTargetFailureParent(),
				item.toTarget.getFailureFailures(),
				item.toTarget.getFailureDate(),
				item.toTarget.getFailureElapsed(),
				item.toTarget.getFailureCause()
			), failureType.getFeatures());

		assertEquals(item.TYPE, item.toTarget.getType());
		assertEquals("toTarget", item.toTarget.getName());
		assertEquals(3, item.toTarget.getFailureLimit());
		assertEquals(2, item.toTarget.getSearchSize());

		assertEquals("DispatcherItem.toTargetFailure", failureType.getID());
		assertEquals(Dispatcher.Failure.class, failureType.getJavaClass());
		assertEquals(false, failureType.isBound());
		assertSame(DispatcherItem.toTarget, failureType.getPattern());
		assertEquals(null, failureType.getSupertype());
		assertEqualsUnmodifiable(list(), failureType.getSubtypes());
		assertEquals(false, failureType.isAbstract());
		assertEquals(Item.class, failureType.getThis().getValueClass().getSuperclass());
		assertEquals(failureType, failureType.getThis().getValueType());
		assertEquals(model, failureType.getModel());

		assertEquals(failureType, item.toTargetFailureParent().getType());
		assertEquals(failureType, item.toTarget.getFailureDate().getType());
		assertEquals(failureType, item.toTarget.getFailureCause().getType());

		assertEquals("parent", item.toTargetFailureParent().getName());
		assertEquals("date", item.toTarget.getFailureDate().getName());
		assertEquals("cause", item.toTarget.getFailureCause().getName());
		
		assertSame(DispatcherItem.class, item.toTargetFailureParent().getValueClass());
		assertSame(DispatcherItem.TYPE, item.toTargetFailureParent().getValueType());
		
		try
		{
			new Dispatcher(0, 0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("failureLimit must be greater zero, but was 0.", e.getMessage());
		}
		try
		{
			new Dispatcher(-10, 0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("failureLimit must be greater zero, but was -10.", e.getMessage());
		}
		try
		{
			new Dispatcher(1000, 0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("searchSize must be greater zero, but was 0.", e.getMessage());
		}
		try
		{
			new Dispatcher(1000, -10);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("searchSize must be greater zero, but was -10.", e.getMessage());
		}
		try
		{
			DispatcherNoneItem.newTypeAccessible(DispatcherNoneItem.class);
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals(
					"type of DispatcherNoneItem.wrong must implement " + Dispatchable.class +
					", but was " + DispatcherNoneItem.class.getName(),
					e.getMessage());
		}
		
		// test persistence
		assertPending(item1, 0, list());
		assertPending(item2, 0, list());
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());
		
		final DateRange d1 = dispatch(2);
		assertSuccess(item1, 1, d1, list());
		assertPending(item2, 0, list(d1));
		assertSuccess(item3, 1, d1, list());
		assertPending(item4, 0, list(d1));
		
		final DateRange d2 = dispatch(0);
		assertSuccess(item1, 1, d1, list());
		assertPending(item2, 0, list(d1, d2));
		assertSuccess(item3, 1, d1, list());
		assertPending(item4, 0, list(d1, d2));
		
		DispatcherItem.logs.get(item2).fail = false;
		final DateRange d3 = dispatch(1);
		assertSuccess(item1, 1, d1, list());
		assertSuccess(item2, 1, d3, list(d1, d2));
		assertSuccess(item3, 1, d1, list());
		assertFailed (item4, 0, list(d1, d2, d3));
		
		dispatch(0);
		assertSuccess(item1, 1, d1, list());
		assertSuccess(item2, 1, d3, list(d1, d2));
		assertSuccess(item3, 1, d1, list());
		assertFailed (item4, 0, list(d1, d2, d3));
		
		try
		{
			DispatcherItem.toTarget.dispatch(HashItem.class, null);
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected " + HashItem.class.getName() + ", but was " + DispatcherItem.class.getName(), e.getMessage());
		}
	}
	
	public void testInterrupt0()
	{
		dispatch(0, 0);
		assertPending(item1, 0, list());
		assertPending(item2, 0, list());
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());
	}
	
	public void testInterrupt1()
	{
		final DateRange d = dispatch(1, 1);
		assertSuccess(item1, 1, d, list());
		assertPending(item2, 0, list());
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());
	}
	
	public void testInterrupt2()
	{
		final DateRange d = dispatch(1, 2);
		assertSuccess(item1, 1, d, list());
		assertPending(item2, 0, list(d));
		assertPending(item3, 0, list());
		assertPending(item4, 0, list());
	}
	
	public void testInterrupt3()
	{
		final DateRange d = dispatch(2, 3);
		assertSuccess(item1, 1, d, list());
		assertPending(item2, 0, list(d));
		assertSuccess(item3, 1, d, list());
		assertPending(item4, 0, list());
	}
	
	public void testInterrupt4()
	{
		final DateRange d = dispatch(2, 4, 4);
		assertSuccess(item1, 1, d, list());
		assertPending(item2, 0, list(d));
		assertSuccess(item3, 1, d, list());
		assertPending(item4, 0, list(d));
	}
	
	public void testInterrupt5()
	{
		final DateRange d = dispatch(2, 5, 4);
		assertSuccess(item1, 1, d, list());
		assertPending(item2, 0, list(d));
		assertSuccess(item3, 1, d, list());
		assertPending(item4, 0, list(d));
	}
	
	private static class DateRange
	{
		final Date before;
		final Date after;
		
		DateRange(final Date before, final Date after)
		{
			this.before = before;
			this.after = after;
			assertTrue(!after.before(before));
		}
	}
	
	private DateRange dispatch(final int expectedResult)
	{
		return dispatch(expectedResult, null);
	}
	
	private DateRange dispatch(final int expectedResult, final Interrupter interrupter)
	{
		model.commit();
		final Date before = new Date();
		final int actualResult = item.dispatchToTarget(interrupter);
		final Date after = new Date();
		model.startTransaction("DispatcherTest");
		assertEquals(expectedResult, actualResult);
		return new DateRange(before, after);
	}
	
	private static class CountInterrupter implements Interrupter
	{
		final int callsWithoutInterrupt;
		int calls = 0;
		
		CountInterrupter(final int callsWithoutInterrupt)
		{
			this.callsWithoutInterrupt = callsWithoutInterrupt;
		}

		public boolean isRequested()
		{
			return (calls++)>=callsWithoutInterrupt;
		}
	}
	
	private DateRange dispatch(final int expectedResult, final int callsWithoutInterrupt)
	{
		return dispatch(expectedResult, callsWithoutInterrupt, callsWithoutInterrupt+1);
	}
	
	private DateRange dispatch(final int expectedResult, final int callsWithoutInterrupt, final int expectedCalls)
	{
		final CountInterrupter ci = new CountInterrupter(callsWithoutInterrupt);
		final DateRange result = dispatch(expectedResult, ci);
		assertEquals(expectedCalls, ci.calls);
		return result;
	}
	
	private static void assertSuccess(final DispatcherItem item, final int dispatchCountCommitted, final DateRange date, final List failures)
	{
		final DispatcherItem.Log log = DispatcherItem.logs.get(item);
		assertEquals(false, item.isToTargetPending());
		assertWithin(date.before, date.after, item.getToTargetSuccessDate());
		assertTrue(
				String.valueOf(item.getToTargetSuccessElapsed())+">="+log.dispatchLastSuccessElapsed,
				item.getToTargetSuccessElapsed()>=log.dispatchLastSuccessElapsed);
		assertIt(dispatchCountCommitted, failures.size()+1, failures, item, 0);
	}
	
	private static void assertPending(final DispatcherItem item, final int dispatchCountCommitted, final List failures)
	{
		assertTrue(item.isToTargetPending());
		assertNull(item.getToTargetSuccessDate());
		assertNull(item.getToTargetSuccessElapsed());
		assertIt(dispatchCountCommitted, failures.size(), failures, item, 0);
	}
	
	private static void assertFailed(final DispatcherItem item, final int dispatchCountCommitted, final List failures)
	{
		assertFalse(item.isToTargetPending());
		assertNull(item.getToTargetSuccessDate());
		assertNull(item.getToTargetSuccessElapsed());
		assertIt(dispatchCountCommitted, failures.size(), failures, item, 1);
	}
	
	private static void assertIt(final int dispatchCountCommitted, final int dispatchCount, final List failures, final DispatcherItem item, final int notifyFinalFailureCount)
	{
		assertEquals(dispatchCountCommitted, item.getDispatchCountCommitted());
		assertEquals(dispatchCount, DispatcherItem.logs.get(item).dispatchCount);
		
		final List<Failure> actualFailures = item.getToTargetFailures();
		assertTrue(actualFailures.size()<=3);
		assertEquals(failures.size(), actualFailures.size());
		
		final List<Integer> failuresElapsed = DispatcherItem.logs.get(item).dispatchFailureElapsed;
		assertEquals(failures.size(), failuresElapsed.size());
		final Iterator<Integer> failureElapsedIter = failuresElapsed.iterator();
		
		final Iterator expectedFailureIter = failures.iterator();
		for(final Failure actual : actualFailures)
		{
			final Integer failureElapsed = failureElapsedIter.next();
			final DateRange expected = (DateRange)expectedFailureIter.next();
			assertSame(item.toTarget, actual.getPattern());
			assertEquals(item, actual.getParent());
			assertWithin(expected.before, expected.after, actual.getDate());
			assertTrue(String.valueOf(actual.getElapsed())+">="+failureElapsed, actual.getElapsed()>=failureElapsed.intValue());
			assertTrue(actual.getCause(), actual.getCause().startsWith(IOException.class.getName()+": "+item.getBody()));
		}
		assertEquals(notifyFinalFailureCount, DispatcherItem.logs.get(item).notifyFinalFailureCount);
	}
}

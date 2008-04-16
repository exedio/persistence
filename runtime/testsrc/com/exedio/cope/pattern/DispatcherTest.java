/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.Dispatcher.Failure;

public class DispatcherTest extends AbstractLibTest
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
		final Type<?> failureType = item.upload.getFailureType();
		
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
		assertEquals(true, item.TYPE.hasUniqueJavaClass());
		assertEquals(null, item.TYPE.getPattern());

		assertEqualsUnmodifiable(list(
				item.TYPE.getThis(),
				item.body,
				item.fail,
				item.dispatchCount,
				item.upload,
				item.upload.getPending(),
				item.upload.getSuccessDate(),
				item.upload.getSuccessElapsed()
			), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				failureType.getThis(),
				item.uploadFailureParent(),
				item.upload.getFailureDate(),
				item.upload.getFailureElapsed(),
				item.upload.getFailureCause()
			), failureType.getFeatures());

		assertEquals(item.TYPE, item.upload.getType());
		assertEquals("upload", item.upload.getName());
		assertEquals(3, item.upload.getFailureLimit());
		assertEquals(2, item.upload.getSearchSize());

		assertEquals("DispatcherItem.uploadFailure", failureType.getID());
		assertEquals(Dispatcher.Failure.class, failureType.getJavaClass());
		assertEquals(false, failureType.hasUniqueJavaClass());
		assertSame(DispatcherItem.upload, failureType.getPattern());
		assertEquals(null, failureType.getSupertype());
		assertEqualsUnmodifiable(list(), failureType.getSubTypes());
		assertEquals(false, failureType.isAbstract());
		assertEquals(Item.class, failureType.getThis().getValueClass().getSuperclass());
		assertEquals(failureType, failureType.getThis().getValueType());
		assertEquals(model, failureType.getModel());

		assertEquals(failureType, item.uploadFailureParent().getType());
		assertEquals(failureType, item.upload.getFailureDate().getType());
		assertEquals(failureType, item.upload.getFailureCause().getType());

		assertEquals("parent", item.uploadFailureParent().getName());
		assertEquals("date", item.upload.getFailureDate().getName());
		assertEquals("cause", item.upload.getFailureCause().getName());
		
		assertSame(DispatcherItem.class, item.uploadFailureParent().getValueClass());
		assertSame(DispatcherItem.TYPE, item.uploadFailureParent().getValueType());
		
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
		assertNotDone(list(), item1);
		assertNotDone(list(), item2);
		assertNotDone(list(), item3);
		assertNotDone(list(), item4);
		
		final DateRange d1 = dispatch();
		assertDone(d1, list(), item1);
		assertNotDone(list(d1), item2);
		assertDone(d1, list(), item3);
		assertNotDone(list(d1), item4);
		
		final DateRange d2 = dispatch();
		assertDone(d1, list(), item1);
		assertNotDone(list(d1, d2), item2);
		assertDone(d1, list(), item3);
		assertNotDone(list(d1, d2), item4);
		
		item2.setFail(false);
		final DateRange d3 = dispatch();
		assertDone(d1, list(), item1);
		assertDone(d3, list(d1, d2), item2);
		assertDone(d1, list(), item3);
		assertNotDone(list(d1, d2, d3), item4);
		
		dispatch();
		assertDone(d1, list(), item1);
		assertDone(d3, list(d1, d2), item2);
		assertDone(d1, list(), item3);
		assertNotDone(list(d1, d2, d3), item4);
		
		try
		{
			DispatcherItem.upload.dispatch(HashItem.class);
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected " + HashItem.class.getName() + ", but was " + DispatcherItem.class.getName(), e.getMessage());
		}
	}
	
	private static class DateRange
	{
		final Date before;
		final Date after;
		
		DateRange(final Date before, final Date after)
		{
			this.before = before;
			this.after = after;
		}
	}
	
	private DateRange dispatch()
	{
		model.commit();
		final Date before = new Date();
		item.dispatchUpload();
		final Date after = new Date();
		model.startTransaction("DispatcherTest");
		return new DateRange(before, after);
	}
	
	private static void assertDone(final DateRange date, final List failures, final DispatcherItem item)
	{
		assertEquals(false, item.isUploadPending());
		assertWithin(date.before, date.after, item.getUploadSuccessDate());
		assertTrue(String.valueOf(item.getUploadSuccessElapsed()), item.getUploadSuccessElapsed()>=5);
		assertIt(failures.size()+1, failures, item);
	}
	
	
	private static void assertNotDone(final List failures, final DispatcherItem item)
	{
		assertEquals(failures.size()!=3, item.isUploadPending());
		assertNull(item.getUploadSuccessDate());
		assertNull(item.getUploadSuccessElapsed());
		assertIt(failures.size(), failures, item);
	}
	
	private static void assertIt(final int dispatchCount, final List failures, final DispatcherItem item)
	{
		assertEquals(dispatchCount, item.getDispatchCount());
		
		final List<Failure> actualFailures = item.getUploadFailures();
		assertTrue(actualFailures.size()<=3);
		assertEquals(failures.size(), actualFailures.size());
		final Iterator expectedFailureIter = failures.iterator();
		for(final Failure actual : actualFailures)
		{
			final DateRange expected = (DateRange)expectedFailureIter.next();
			assertSame(item.upload, actual.getPattern());
			assertEquals(item, actual.getParent());
			assertWithin(expected.before, expected.after, actual.getDate());
			assertTrue(String.valueOf(actual.getElapsed()), actual.getElapsed()>=5);
			assertTrue(actual.getCause(), actual.getCause().startsWith(IOException.class.getName()+": "+item.getBody()));
		}
	}
}

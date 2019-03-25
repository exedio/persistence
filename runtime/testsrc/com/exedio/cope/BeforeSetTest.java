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

package com.exedio.cope;

import static com.exedio.cope.BeforeSetItem.Action.addDuplicate;
import static com.exedio.cope.BeforeSetItem.Action.addField1;
import static com.exedio.cope.BeforeSetItem.Action.addField1ConstraintViolation;
import static com.exedio.cope.BeforeSetItem.Action.constraintViolation;
import static com.exedio.cope.BeforeSetItem.Action.replaceField1;
import static com.exedio.cope.BeforeSetItem.Action.returnEmpty;
import static com.exedio.cope.BeforeSetItem.Action.returnNull;
import static com.exedio.cope.BeforeSetItem.Action.runtimeException;
import static com.exedio.cope.BeforeSetItem.field1;
import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.BeforeSetItem.Action;
import org.junit.jupiter.api.Test;

public class BeforeSetTest extends TestWithEnvironment
{
	@SuppressWarnings("unused") // OK: Model that is never connected
	private static final Model MODEL = new Model(BeforeSetItem.TYPE);

	public BeforeSetTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		final BeforeSetItem item = new BeforeSetItem();
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(asList(), item.getCalls());

		item.setField1(12);
		assertEquals(12, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(asList(
				"[BeforeSetItem.field1=12]"),
				item.getCalls());

		item.setField2(22);
		assertEquals(12, item.getField1());
		assertEquals(22, item.getField2());
		assertEquals(asList(
				"[BeforeSetItem.field1=12]",
				"[BeforeSetItem.field2=22]"),
				item.getCalls());

		item.setFields(13, 23);
		assertEquals(13, item.getField1());
		assertEquals(23, item.getField2());
		assertEquals(asList(
				"[BeforeSetItem.field1=12]",
				"[BeforeSetItem.field2=22]",
				"[BeforeSetItem.field1=13, BeforeSetItem.field2=23]"),
				item.getCalls());
	}

	@Test void testActionConstraintViolation()
	{
		final BeforeSetItem item = new BeforeSetItem();
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList(), item.getCalls());

		try
		{
			item.setAction(constraintViolation);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(field1, e.getFeature());
			assertEquals(null, e.getItem());
		}
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList("[BeforeSetItem.action=" + constraintViolation + "]"), item.getCalls());
	}

	@Test void testActionRuntimeException()
	{
		final BeforeSetItem item = new BeforeSetItem();
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList(), item.getCalls());

		try
		{
			item.setAction(runtimeException);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals(Action.class.getName(), e.getMessage());
		}
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList("[BeforeSetItem.action=" + runtimeException + "]"), item.getCalls());
	}

	@Test void testActionAddField1()
	{
		final BeforeSetItem item = new BeforeSetItem();
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList(), item.getCalls());

		item.setAction(addField1);
		assertEquals(99, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(addField1, item.getAction());
		assertEquals(asList("[BeforeSetItem.action=" + addField1 + "]"), item.getCalls());
	}

	@Test void testActionAddField1ConstraintViolation()
	{
		final BeforeSetItem item = new BeforeSetItem();
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList(), item.getCalls());

		try
		{
			item.setAction(addField1ConstraintViolation);
			fail();
		}
		catch(final IntegerRangeViolationException e)
		{
			assertEquals(field1, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals(-1, e.getValue());
		}
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList("[BeforeSetItem.action=" + addField1ConstraintViolation + "]"), item.getCalls());
	}

	@Test void testActionReplaceField1()
	{
		final BeforeSetItem item = new BeforeSetItem();
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList(), item.getCalls());

		item.setFieldsAndAction(12, 22, replaceField1);
		assertEquals(99, item.getField1());
		assertEquals(22, item.getField2());
		assertEquals(replaceField1, item.getAction());
		assertEquals(asList("[" +
				"BeforeSetItem.field1=12, " +
				"BeforeSetItem.field2=22, " +
				"BeforeSetItem.action=" + replaceField1 + "]"),
				item.getCalls());
	}

	@Test void testActionAddDuplicate()
	{
		final BeforeSetItem item = new BeforeSetItem();
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList(), item.getCalls());

		try
		{
			item.setAction(addDuplicate);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("SetValues contain duplicate settable BeforeSetItem.field1", e.getMessage());
		}
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList("[BeforeSetItem.action=" + addDuplicate + "]"), item.getCalls());
	}

	@Test void testSetNull()
	{
		final BeforeSetItem item = new BeforeSetItem();
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList(), item.getCalls());

		item.setFail();
		try
		{
			item.set((SetValue[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("setValues", e.getMessage());
		}
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList(), item.getCalls());
	}

	@Test void testActionReturnNull()
	{
		final BeforeSetItem item = new BeforeSetItem();
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList(), item.getCalls());

		try
		{
			item.setAction(returnNull);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("setValues after beforeSetCopeItem", e.getMessage());
		}
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList("[BeforeSetItem.action=" + returnNull + "]"), item.getCalls());
	}

	@Test void testSetEmpty()
	{
		final BeforeSetItem item = new BeforeSetItem();
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList(), item.getCalls());

		item.setFail();
		item.set(new SetValue<?>[]{});
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList(), item.getCalls());
	}

	@Test void testActionReturnEmpty()
	{
		final BeforeSetItem item = new BeforeSetItem();
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList(), item.getCalls());

		item.setAction(returnEmpty);
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(null, item.getAction());
		assertEquals(asList("[BeforeSetItem.action=" + returnEmpty + "]"), item.getCalls());
	}

	@Test void testConstraintViolation()
	{
		final BeforeSetItem item = new BeforeSetItem();
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(asList(), item.getCalls());

		try
		{
			item.setField1(-1);
			fail();
		}
		catch(final IntegerRangeViolationException e)
		{
			assertEquals(field1, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals(-1, e.getValue());
		}
		assertEquals(11, item.getField1());
		assertEquals(21, item.getField2());
		assertEquals(asList("[BeforeSetItem.field1=-1]"), item.getCalls());
	}
}

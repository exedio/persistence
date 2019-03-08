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

import static com.exedio.cope.AbstractRuntimeTest.assertDeleteFails;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.testmodel.AttributeItem.TYPE;
import static com.exedio.cope.testmodel.AttributeItem.someItem;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullItem;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;
import com.exedio.cope.testmodel.EmptyItem2;
import com.exedio.cope.testmodel.FinalItem;
import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerTargetItem;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

public class FieldItemTest extends FieldTest
{
	@Test void testSomeItem()
	{
		assertEquals(TYPE, someItem.getType());
		assertEquals(EmptyItem.TYPE, someItem.getValueType());
		assertEquals(EmptyItem.class, someItem.getValueClass());
		assertEquals(DeletePolicy.FORBID, someItem.getDeletePolicy());
		assertEqualsUnmodifiable(list(), FinalItem.TYPE.getDeclaredReferences());
		assertEqualsUnmodifiable(list(), FinalItem.TYPE.getReferences());
		assertSerializedSame(someItem, 377);

		assertEquals(null, item.getSomeItem());
		item.setSomeItem(emptyItem);
		assertEquals(emptyItem, item.getSomeItem());

		assertContains(item,
				TYPE.search(someItem.equal(emptyItem)));
		assertContains(item2,
				TYPE.search(someItem.equal((EmptyItem)null)));
		assertContains(
				TYPE.search(someItem.notEqual(emptyItem)));
		assertContains(item,
				TYPE.search(someItem.notEqual((EmptyItem)null)));

		assertContains(emptyItem, null, search(someItem));
		assertContains(emptyItem, search(someItem, someItem.equal(emptyItem)));

		restartTransaction();
		assertEquals(emptyItem, item.getSomeItem());
		item.setSomeItem(null);
		assertEquals(null, item.getSomeItem());
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	@Test void testUnchecked()
	{
		try
		{
			item.set((FunctionField)someItem, Integer.valueOf(10));
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + EmptyItem.class.getName() + ", but was a " + Integer.class.getName() + " for " + someItem + '.', e.getMessage());
		}

		final EmptyItem2 wrongItem = new EmptyItem2();
		try
		{
			item.set((FunctionField)someItem, wrongItem);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + EmptyItem.class.getName() + ", but was a " + EmptyItem2.class.getName() + " for " + someItem + '.', e.getMessage());
		}
	}

	@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
	@Test void testSomeNotNullItem()
		throws MandatoryViolationException
	{
		assertEquals(TYPE, someNotNullItem.getType());
		assertEquals(
			EmptyItem.TYPE,
			someNotNullItem.getValueType());
		assertEquals(DeletePolicy.FORBID, someNotNullItem.getDeletePolicy());
		assertEquals(emptyItem, item.getSomeNotNullItem());

		item.setSomeNotNullItem(emptyItem2);
		assertEquals(emptyItem2, item.getSomeNotNullItem());

		restartTransaction();
		assertEquals(emptyItem2, item.getSomeNotNullItem());
		try
		{
			item.setSomeNotNullItem(null);
			fail();
		}
		catch (final MandatoryViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(someNotNullItem, e.getFeature());
			assertEquals(someNotNullItem, e.getFeature());
			assertEquals("mandatory violation on " + item + " for " + someNotNullItem, e.getMessage());
		}
		assertEquals(emptyItem2, item.getSomeNotNullItem());
		assertDeleteFails(emptyItem2, someNotNullItem, 2);

		try
		{
			new AttributeItem("someString", 5, 6l, 2.2, true, null, AttributeItem.SomeEnum.enumValue1);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(someNotNullItem, e.getFeature());
			assertEquals(someNotNullItem, e.getFeature());
			assertEquals("mandatory violation for " + someNotNullItem, e.getMessage());
		}
	}

	@Test void testIntegrity()
	{
		final EmptyItem2 target = new EmptyItem2();
		final PointerTargetItem pointer2 = new PointerTargetItem("pointer2");
		final PointerItem source = new PointerItem("source", pointer2);
		source.setEmpty2(target);

		assertDeleteFails(target, PointerItem.empty2);
	}

}

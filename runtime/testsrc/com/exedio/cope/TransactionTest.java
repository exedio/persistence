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

import static com.exedio.cope.testmodel.AttributeItem.TYPE;
import static com.exedio.cope.testmodel.AttributeItem.someNotNullString;
import static com.exedio.cope.testmodel.AttributeItem.someString;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TransactionTest extends TestmodelTest
{
	protected EmptyItem someItem;
	protected AttributeItem item;

	private AttributeItem newItem(final String code)
	{
		return new AttributeItem(code, 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnum.enumValue1);
	}

	@BeforeEach final void setUp()
	{
		someItem = new EmptyItem();
		item = newItem("someString");
	}

	private Transaction createTransaction(final String name)
	{
		return model.startTransaction(name);
	}

	private void rollback()
	{
		model.rollback();
	}

	private static void assertSomeString(final AttributeItem actualItem, final String someStringValue)
	{
		assertEquals(someStringValue, actualItem.getSomeString());
		assertTrue(TYPE.search(someString.equal(someStringValue)).contains(actualItem));
		assertFalse(TYPE.search(someString.equal("X"+someStringValue)).contains(actualItem));
	}

	private void assertSomeString(final String someString)
	{
		assertSomeString(item, someString);
	}

	@SuppressWarnings({"EqualsWithItself", "ResultOfMethodCallIgnored"})
	private static void assertNotExists(final AttributeItem actualItem)
	{
		assertTrue(!actualItem.existsCopeItem());
		try
		{
			actualItem.getSomeNotNullString();
			fail();
		}
		catch(final NoSuchItemException e)
		{
			assertSame(actualItem, e.getItem());
			assertEquals(actualItem.getCopeID(), e.getMessage());
		}
		try
		{
			actualItem.setSomeNotNullString("hallo");
			fail();
		}
		catch(final NoSuchItemException e)
		{
			assertSame(actualItem, e.getItem());
			assertEquals(actualItem.getCopeID(), e.getMessage());
		}
		try
		{
			actualItem.deleteCopeItem();
			fail();
		}
		catch(final NoSuchItemException e)
		{
			assertSame(actualItem, e.getItem());
			assertEquals(actualItem.getCopeID(), e.getMessage());
		}
		assertNotNull(actualItem.getCopeID());
		assertTrue(actualItem.equals(actualItem));
		actualItem.hashCode(); // test, that hashCode works
	}

	@Test void testCommitChange()
	{
		assertSomeString(null);
		item.setSomeString("someString");
		assertSomeString("someString");
		commit();

		createTransaction("testCommitChange1");
		assertSomeString("someString");
		item.setSomeString("someString2");
		assertSomeString("someString2");
		item.setSomeString(null);
		commit();

		createTransaction("testCommitChange2");
		assertSomeString(null);
	}

	@Test void testCommitCreate()
	{
		item.setSomeString("someString");
		final AttributeItem itemx = newItem("someStringX");
		assertSomeString(itemx, null);
		assertTrue(itemx.existsCopeItem());
		commit();

		createTransaction("testCommitCreate1");
		assertSomeString(itemx, null);
		assertTrue(itemx.existsCopeItem());
		final AttributeItem itemy = newItem("someStringY");
		assertSomeString(itemx, null);
		assertSomeString(itemy, null);
		assertTrue(itemy.existsCopeItem());
		itemx.setSomeString("someStringX");
		itemy.setSomeString("someStringY");
		assertSomeString(itemx, "someStringX");
		assertSomeString(itemy, "someStringY");
		commit();

		createTransaction("testCommitCreate2");
		assertSomeString(itemx, "someStringX");
		assertSomeString(itemy, "someStringY");
		assertTrue(itemx.existsCopeItem());
		assertTrue(itemy.existsCopeItem());
	}

	@Test void testCommitDelete()
	{
		final AttributeItem itemx = newItem("someStringX");
		assertTrue(itemx.existsCopeItem());
		assertEquals("someStringX", itemx.getSomeNotNullString());
		itemx.deleteCopeItem();
		assertNotExists(itemx);
		commit();

		createTransaction("testCommitDelete1");
		assertNotExists(itemx);
		final AttributeItem itemy = newItem("someStringY");
		assertTrue(itemy.existsCopeItem());
		assertEquals("someStringY", itemy.getSomeNotNullString());
		commit();

		createTransaction("testCommitDelete2");
		assertNotExists(itemx);
		assertTrue(itemy.existsCopeItem());
		assertEquals("someStringY", itemy.getSomeNotNullString());
		itemy.deleteCopeItem();
		assertNotExists(itemy);
		commit();

		createTransaction("testCommitDelete3");
		assertNotExists(itemx);
		assertNotExists(itemy);
	}

	@Test void testRollbackChange()
	{
		commit();

		createTransaction("testRollbackChange1");
		assertSomeString(null);
		item.setSomeString("someString");
		assertSomeString("someString");
		rollback();

		createTransaction("testRollbackChange2");
		assertSomeString(null);
		item.setSomeString("someString2");
		assertSomeString("someString2");
		commit();

		createTransaction("testRollbackChange3");
		assertSomeString("someString2");
		item.setSomeString("someString3");
		assertSomeString("someString3");
		rollback();

		createTransaction("testRollbackChange4");
		assertSomeString("someString2");
	}

	@Test void testRollbackCreate()
	{
		commit();

		createTransaction("testRollbackCreate1");
		assertContains(TYPE.search(someNotNullString.equal("someStringX")));
		assertContains(TYPE.search(someNotNullString.equal("someStringY")));
		final AttributeItem itemx = newItem("someStringX");
		assertSomeString(itemx, null);
		assertContains(itemx, TYPE.search(someNotNullString.equal("someStringX")));
		assertContains(TYPE.search(someNotNullString.equal("someStringY")));
		assertTrue(itemx.existsCopeItem());
		rollback();

		createTransaction("testRollbackCreate2");
		assertContains(TYPE.search(someNotNullString.equal("someStringX")));
		assertContains(TYPE.search(someNotNullString.equal("someStringY")));
		assertTrue(!itemx.existsCopeItem());
		final AttributeItem itemy = newItem("someStringY");
		assertNotEqualsAndHash(itemx, itemy);
		assertSomeString(itemy, null);
		assertContains(TYPE.search(someNotNullString.equal("someStringX")));
		assertContains(itemy, TYPE.search(someNotNullString.equal("someStringY")));
		assertTrue(itemy.existsCopeItem());
		itemy.setSomeString("someStringYY");
		assertSomeString(itemy, "someStringYY");
		assertContains(TYPE.search(someNotNullString.equal("someStringX")));
		assertContains(itemy, TYPE.search(someNotNullString.equal("someStringY")));
		rollback();

		createTransaction("testRollbackCreate3");
		assertContains(TYPE.search(someNotNullString.equal("someStringX")));
		assertContains(TYPE.search(someNotNullString.equal("someStringY")));
		assertNotEqualsAndHash(itemx, itemy);
		assertTrue(!itemx.existsCopeItem());
		assertTrue(!itemy.existsCopeItem());
	}

	@Test void testRollbackDelete()
	{
		final AttributeItem itemx = newItem("someStringX");
		commit();

		createTransaction("testRollbackDelete1");
		assertTrue(itemx.existsCopeItem());
		assertEquals("someStringX", itemx.getSomeNotNullString());
		itemx.deleteCopeItem();
		assertNotExists(itemx);
		rollback();

		createTransaction("testRollbackDelete2");
		assertTrue(itemx.existsCopeItem());
		assertEquals("someString", item.getSomeNotNullString());
		item.setSomeNotNullString("someString2");
		assertTrue(item.existsCopeItem());
		assertEquals("someString2", item.getSomeNotNullString());
		item.deleteCopeItem();
		assertNotExists(item);
		rollback();

		createTransaction("testRollbackDelete3");
		assertTrue(item.existsCopeItem());
		assertEquals("someString", item.getSomeNotNullString());
	}

	@Disabled // TODO enable
	@Test void testIsolation()
	{
		model.commit();
		final Transaction t1 = createTransaction("testIsolation1");
		model.leaveTransaction();
		final Transaction t2 = createTransaction("testIsolation2");

		activate(t1);
		assertSomeString(null);

		activate(t2);
		assertSomeString(null);

		activate(t1);
		assertSomeString(null);
		item.setSomeString("someString");
		assertSomeString("someString");

		activate(t2);
		assertSomeString(null);
		item.setSomeString("someString2");
		assertSomeString("someString2");

		activate(t1);
		assertSomeString("someString");
		item.setSomeString(null);
		assertSomeString(null);

		activate(t2);
		assertSomeString("someString2");
		item.setSomeString(null);
		assertSomeString(null);

		// TODO: test item creation/deletion
	}

	private void activate(final Transaction transaction)
	{
		model.leaveTransaction();
		model.joinTransaction( transaction );
	}
}

/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.EmptyItem;


public class TransactionTest extends TestmodelTest
{
	protected EmptyItem someItem;
	protected AttributeItem item;

	private final AttributeItem newItem(final String code)
	{
		return new AttributeItem(code, 5, 6l, 2.2, true, someItem, AttributeItem.SomeEnum.enumValue1);
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		someItem = deleteOnTearDown(new EmptyItem());
		item = deleteOnTearDown(newItem("someString"));
	}
	
	private Transaction createTransaction(final String name)
	{
		return model.startTransaction(name);
	}
	
	private void commit()
	{
		model.commit();
	}
	
	private void rollback()
	{
		model.rollback();
	}
	
	private void assertSomeString(final AttributeItem actualItem, final String someString)
	{
		assertEquals(someString, actualItem.getSomeString());
		assertTrue(actualItem.TYPE.search(actualItem.someString.equal(someString)).contains(actualItem));
		assertFalse(actualItem.TYPE.search(actualItem.someString.equal("X"+someString)).contains(actualItem));
	}
	
	private void assertSomeString(final String someString)
	{
		assertSomeString(item, someString);
	}
	
	private void assertNotExists(final AttributeItem actualItem)
	{
		assertTrue(!actualItem.existsCopeItem());
		try
		{
			actualItem.getSomeNotNullString();
			fail();
		}
		catch(NoSuchItemException e)
		{
			assertSame(actualItem, e.getItem());
		}
		try
		{
			actualItem.setSomeNotNullString("hallo");
			fail();
		}
		catch(NoSuchItemException e)
		{
			assertSame(actualItem, e.getItem());
		}
		try
		{
			actualItem.deleteCopeItem();
			fail();
		}
		catch(NoSuchItemException e)
		{
			assertSame(actualItem, e.getItem());
		}
		assertNotNull(actualItem.getCopeID());
		assertTrue(actualItem.equals(actualItem));
		actualItem.hashCode(); // test, that hashCode works
	}
	
	public void testCommitChange()
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
	
	public void testCommitCreate()
	{
		item.setSomeString("someString");
		final AttributeItem itemx = deleteOnTearDown(newItem("someStringX"));
		assertSomeString(itemx, null);
		assertTrue(itemx.existsCopeItem());
		commit();
		
		createTransaction("testCommitCreate1");
		assertSomeString(itemx, null);
		assertTrue(itemx.existsCopeItem());
		final AttributeItem itemy = deleteOnTearDown(newItem("someStringY"));
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
	
	public void testCommitDelete()
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
	
	public void testRollbackChange()
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

	public void testRollbackCreate()
	{
		commit();
		
		createTransaction("testRollbackCreate1");
		assertContains(item.TYPE.search(item.someNotNullString.equal("someStringX")));
		assertContains(item.TYPE.search(item.someNotNullString.equal("someStringY")));
		final AttributeItem itemx = newItem("someStringX");
		assertSomeString(itemx, null);
		assertContains(itemx, itemx.TYPE.search(itemx.someNotNullString.equal("someStringX")));
		assertContains(item.TYPE.search(item.someNotNullString.equal("someStringY")));
		assertTrue(itemx.existsCopeItem());
		rollback();
		
		createTransaction("testRollbackCreate2");
		assertContains(itemx.TYPE.search(itemx.someNotNullString.equal("someStringX")));
		assertContains(item.TYPE.search(item.someNotNullString.equal("someStringY")));
		assertTrue(!itemx.existsCopeItem());
		final AttributeItem itemy = newItem("someStringY");
		assertNotEquals(itemx, itemy);
		assertSomeString(itemy, null);
		assertContains(itemx.TYPE.search(itemx.someNotNullString.equal("someStringX")));
		assertContains(itemy, itemy.TYPE.search(itemx.someNotNullString.equal("someStringY")));
		assertTrue(itemy.existsCopeItem());
		itemy.setSomeString("someStringYY");
		assertSomeString(itemy, "someStringYY");
		assertContains(itemx.TYPE.search(itemx.someNotNullString.equal("someStringX")));
		assertContains(itemy, itemy.TYPE.search(itemx.someNotNullString.equal("someStringY")));
		rollback();
		
		createTransaction("testRollbackCreate3");
		assertContains(itemx.TYPE.search(itemx.someNotNullString.equal("someStringX")));
		assertContains(itemy.TYPE.search(itemx.someNotNullString.equal("someStringY")));
		assertNotEquals(itemx, itemy);
		assertTrue(!itemx.existsCopeItem());
		assertTrue(!itemy.existsCopeItem());
	}

	public void testRollbackDelete()
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
		deleteOnTearDown(itemx);
		assertTrue(item.existsCopeItem());
		assertEquals("someString", item.getSomeNotNullString());
	}
	
	public void xxtestIsolation() // TODO enable testIsolation
	{
		if ( ! model.supportsReadCommitted() ) return;
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

}

/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.lib;

import com.exedio.cope.testmodel.ItemWithDoubleUnique;
import com.exedio.cope.testmodel.ItemWithSingleUnique;
import com.exedio.cope.testmodel.ItemWithSingleUniqueNotNull;
import com.exedio.cope.testmodel.ItemWithSingleUniqueReadOnly;


public class UniqueItemTest extends DatabaseLibTest
{

	public void testItemWithSingleUnique()
			throws IntegrityViolationException, UniqueViolationException
	{
		assertEquals(null, ItemWithSingleUnique.findByUniqueString("uniqueString"));

		final ItemWithSingleUnique item = new ItemWithSingleUnique();
		assertEquals(null, item.getUniqueString());
		assertEquals(null, item.findByUniqueString("uniqueString"));

		item.setUniqueString("uniqueString");
		assertEquals("uniqueString", item.getUniqueString());
		assertEquals(item, ItemWithSingleUnique.findByUniqueString("uniqueString"));

		// test unique violation
		{
			final ItemWithSingleUnique item2 = new ItemWithSingleUnique();
			item2.setUniqueString("uniqueString2");
			try
			{
				item2.setUniqueString("uniqueString");
				fail("should have thrown UniqueViolationException");
			}
			catch(UniqueViolationException e)
			{
				assertTrue(!mysql);
				assertEquals(item2.uniqueString.getSingleUniqueConstraint(), e.getConstraint());
				assertEquals("uniqueString2", item2.getUniqueString());
			}
			catch(NestingRuntimeException e)
			{
				assertTrue(mysql);
				assertEquals("Duplicate entry 'uniqueString' for key 2", e.getNestedCause().getMessage());
				assertEquals("uniqueString", item2.getUniqueString());
			}
			assertEquals(item2, ItemWithSingleUnique.findByUniqueString("uniqueString2"));

			assertDelete(item2);
		}

		item.passivateItem();
		assertTrue(!item.isActiveCopeItem());
		assertEquals("uniqueString", item.getUniqueString());
		assertTrue(item.isActiveCopeItem());

		final ItemWithSingleUnique firstFoundItem;
		{
			item.passivateItem();
			assertTrue(!item.isActiveCopeItem());
			final ItemWithSingleUnique foundItem = ItemWithSingleUnique.findByUniqueString("uniqueString");
			assertEquals(item, foundItem);
			assertEquals(item.getCopeID(), foundItem.getCopeID());
			assertEquals(item.hashCode(), foundItem.hashCode());
			assertNotSame(item, foundItem);
			assertTrue(!item.isActiveCopeItem());
			assertTrue(!foundItem.isActiveCopeItem());

			assertSame(item, item.activeCopeItem());
			assertTrue(item.isActiveCopeItem());
			assertTrue(!foundItem.isActiveCopeItem());

			assertSame(item, foundItem.activeCopeItem());
			assertTrue(item.isActiveCopeItem());
			assertTrue(!foundItem.isActiveCopeItem());

			firstFoundItem = foundItem;
		}
		{
			item.passivateItem();
			assertTrue(!item.isActiveCopeItem());
			final ItemWithSingleUnique foundItem = ItemWithSingleUnique.findByUniqueString("uniqueString");
			assertEquals("uniqueString", foundItem.getUniqueString());
			assertEquals("uniqueString", item.getUniqueString());
			assertEquals(item, foundItem);
			assertEquals(item.getCopeID(), foundItem.getCopeID());
			assertEquals(item.hashCode(), foundItem.hashCode());
			assertNotSame(item, foundItem);
			assertNotSame(item, firstFoundItem);
			assertNotSame(foundItem, firstFoundItem);
			assertTrue(!item.isActiveCopeItem());
			assertTrue(foundItem.isActiveCopeItem());
			assertSame(foundItem, item.activeCopeItem());
			assertSame(foundItem, foundItem.activeCopeItem());
		}
		assertDelete(item);
	}

	public void testItemWithSingleUniqueReadOnly()
			throws ConstraintViolationException
	{
		assertEquals(null, ItemWithSingleUniqueReadOnly.findByUniqueReadOnlyString("uniqueString"));

		final ItemWithSingleUniqueReadOnly item = new ItemWithSingleUniqueReadOnly("uniqueString");
		assertEquals("uniqueString", item.getUniqueReadOnlyString());
		assertEquals(item, ItemWithSingleUniqueReadOnly.findByUniqueReadOnlyString("uniqueString"));

		try
		{
			item.setAttribute(item.uniqueReadOnlyString, "zapp");
			fail("should have thrown ReadOnlyViolationException");
		}
		catch(ReadOnlyViolationException e)
		{
			assertEquals(item.uniqueReadOnlyString, e.getReadOnlyAttribute());
			assertEquals(item, e.getItem());
		}
		assertEquals("uniqueString", item.getUniqueReadOnlyString());
		assertEquals(item, ItemWithSingleUniqueReadOnly.findByUniqueReadOnlyString("uniqueString"));

		assertDelete(item);
	}

	public void testItemWithSingleUniqueNotNull()
			throws IntegrityViolationException, UniqueViolationException, NotNullViolationException
	{
		assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString"));
		assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString2"));

		final ItemWithSingleUniqueNotNull item = new ItemWithSingleUniqueNotNull("uniqueString");
		assertEquals("uniqueString", item.getUniqueNotNullString());
		assertEquals(item, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString"));
		assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString2"));

		item.setUniqueNotNullString("uniqueString2");
		assertEquals("uniqueString2", item.getUniqueNotNullString());
		assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString"));
		assertEquals(item, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString2"));

		try
		{
			item.setUniqueNotNullString(null);
			fail("should have thrown NotNullViolationException");
		}
		catch(NotNullViolationException e)
		{
			assertEquals(item.uniqueNotNullString, e.getNotNullAttribute());
			assertEquals(item, e.getItem());
		}
		assertEquals("uniqueString2", item.getUniqueNotNullString());
		assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString"));
		assertEquals(item, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString2"));

		assertDelete(item);
	}
	
	public void testDoubleUnique()
		throws ConstraintViolationException
	{
		assertEquals("doubleUnique", ItemWithDoubleUnique.doubleUnique.getID());
		assertEquals(ItemWithDoubleUnique.TYPE, ItemWithDoubleUnique.doubleUnique.getType());
		assertEquals(
			list(ItemWithDoubleUnique.string, ItemWithDoubleUnique.integer),
			ItemWithDoubleUnique.doubleUnique.getUniqueAttributes());

		assertEquals(null, ItemWithDoubleUnique.findByDoubleUnique("a", 1));
		
		final ItemWithDoubleUnique a1 = new ItemWithDoubleUnique("a", 1);
		assertEquals(a1, ItemWithDoubleUnique.findByDoubleUnique("a", 1));
		
		final ItemWithDoubleUnique a2 = new ItemWithDoubleUnique("a", 2);
		assertEquals(a2, ItemWithDoubleUnique.findByDoubleUnique("a", 2));
		
		final ItemWithDoubleUnique b1 = new ItemWithDoubleUnique("b", 1);
		assertEquals(b1, ItemWithDoubleUnique.findByDoubleUnique("b", 1));
		
		final ItemWithDoubleUnique b2 = new ItemWithDoubleUnique("b", 2);
		assertEquals(b2, ItemWithDoubleUnique.findByDoubleUnique("b", 2));

		try
		{		
			new ItemWithDoubleUnique("b", 1);
			fail("should have thrown UniqueViolationException");
		}
		catch(UniqueViolationException e)
		{
			assertTrue(!mysql);
			assertEquals(a1.doubleUnique, e.getConstraint());
		}
		catch(NestingRuntimeException e)
		{
			assertTrue(mysql);
			assertEquals("Duplicate entry 'b-1' for key 2", e.getNestedCause().getMessage());
		}
		assertEquals(b1, ItemWithDoubleUnique.findByDoubleUnique("b", 1));
		
		assertDelete(b2);
		assertDelete(b1);

		final ItemWithDoubleUnique b1X = new ItemWithDoubleUnique("b", 1);
		assertEquals(b1X, ItemWithDoubleUnique.findByDoubleUnique("b", 1));

		assertDelete(a2);
		assertDelete(a1);
		assertDelete(b1X);
	}

}

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

package com.exedio.cope;

import com.exedio.cope.testmodel.ItemWithDoubleUnique;
import com.exedio.cope.testmodel.ItemWithSingleUnique;
import com.exedio.cope.testmodel.ItemWithSingleUniqueNotNull;
import com.exedio.cope.testmodel.UniqueFinal;


public class UniqueItemTest extends TestmodelTest
{

	public void testItemWithSingleUnique()
			throws IntegrityViolationException, UniqueViolationException, NoSuchIDException
	{
		assertEqualsUnmodifiable(
			list(
				ItemWithSingleUnique.uniqueString,
				ItemWithSingleUnique.uniqueString.getImplicitUniqueConstraint(),
				ItemWithSingleUnique.otherString
			),
			ItemWithSingleUnique.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				ItemWithSingleUnique.uniqueString,
				ItemWithSingleUnique.uniqueString.getImplicitUniqueConstraint(),
				ItemWithSingleUnique.otherString
			),
			ItemWithSingleUnique.TYPE.getFeatures());
		assertEqualsUnmodifiable(
			list(ItemWithSingleUnique.uniqueString),
			ItemWithSingleUnique.uniqueString.getImplicitUniqueConstraint().getUniqueAttributes());
		assertEqualsUnmodifiable(
			list(ItemWithSingleUnique.uniqueString.getImplicitUniqueConstraint()),
			ItemWithSingleUnique.uniqueString.getUniqueConstraints());
		
		assertEqualsUnmodifiable(
			list(UniqueFinal.uniqueReadOnlyString),
			UniqueFinal.uniqueReadOnlyString.getImplicitUniqueConstraint().getUniqueAttributes());
		assertEqualsUnmodifiable(
			list(UniqueFinal.uniqueReadOnlyString.getImplicitUniqueConstraint()),
			UniqueFinal.uniqueReadOnlyString.getUniqueConstraints());
		
		assertEqualsUnmodifiable(
			list(ItemWithSingleUniqueNotNull.uniqueNotNullString),
			ItemWithSingleUniqueNotNull.uniqueNotNullString.getImplicitUniqueConstraint().getUniqueAttributes());
		assertEqualsUnmodifiable(
			list(ItemWithSingleUniqueNotNull.uniqueNotNullString.getImplicitUniqueConstraint()),
			ItemWithSingleUniqueNotNull.uniqueNotNullString.getUniqueConstraints());

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
				fail();
			}
			catch(UniqueViolationException e)
			{
				assertEquals(item2.uniqueString.getImplicitUniqueConstraint(), e.getConstraint());
				assertEquals("unique violation for "+item2.uniqueString.getImplicitUniqueConstraint().toString(), e.getMessage());
			}
			assertEquals("uniqueString2", item2.getUniqueString());
			assertEquals(item2, ItemWithSingleUnique.findByUniqueString("uniqueString2"));

			assertDelete(item2);
		}

		restartTransaction();
		assertTrue(!item.isActiveCopeItem());
		Item otheritem = model.findByID(item.getCopeID());
		assertNotSame(item, otheritem);
		assertTrue(otheritem.isActiveCopeItem());
		assertTrue(!item.isActiveCopeItem());
		assertEquals("uniqueString", item.getUniqueString());
		assertTrue(otheritem.isActiveCopeItem());
		assertTrue(!item.isActiveCopeItem());
		
		final ItemWithSingleUnique firstFoundItem;
		{
			restartTransaction();
			assertTrue(!item.isActiveCopeItem());
			final ItemWithSingleUnique foundItem = ItemWithSingleUnique.findByUniqueString("uniqueString");
			assertEquals(item, foundItem);
			assertEquals(item.getCopeID(), foundItem.getCopeID());
			assertEquals(item.hashCode(), foundItem.hashCode());
			assertNotSame(item, foundItem);
			assertTrue(!item.isActiveCopeItem());
			assertTrue(!foundItem.isActiveCopeItem());

			assertSameCache(otheritem, item.activeCopeItem());
			assertEquals(!cache, item.isActiveCopeItem());
			assertTrue(!foundItem.isActiveCopeItem());
			assertEquals(cache, otheritem.isActiveCopeItem());

			assertSameCache(otheritem, foundItem.activeCopeItem());
			assertEquals(cache, !item.isActiveCopeItem());
			assertTrue(!foundItem.isActiveCopeItem());
			assertEquals(cache, otheritem.isActiveCopeItem());

			firstFoundItem = foundItem;
		}
		{
			restartTransaction();
			assertTrue(!item.isActiveCopeItem());
			final ItemWithSingleUnique foundItem = ItemWithSingleUnique.findByUniqueString("uniqueString");
			assertEquals("uniqueString", foundItem.getUniqueString());
			assertEquals("uniqueString", item.getUniqueString());
			assertEquals(item, foundItem);
			assertEquals(item.getCopeID(), foundItem.getCopeID());
			assertEquals(item.hashCode(), foundItem.hashCode());
			assertNotSame(item, foundItem);
			assertNotSame(item, firstFoundItem);
			if ( model.getProperties().getCacheQueryLimit()>0 )
			{
				assertSame(foundItem, firstFoundItem);
			}
			else
			{
				assertNotSame(foundItem, firstFoundItem);
			}
			assertTrue(!item.isActiveCopeItem());
			assertEquals(cache, !foundItem.isActiveCopeItem());
			assertEquals(cache, otheritem.isActiveCopeItem());
			assertSameCache(otheritem, item.activeCopeItem());
			assertSameCache(otheritem, foundItem.activeCopeItem());
		}
		assertDelete(item);
	}
	
	public void testMultipleSet() throws ConstraintViolationException
	{
		final ItemWithSingleUnique item1 = new ItemWithSingleUnique();
		deleteOnTearDown(item1);
		final ItemWithSingleUnique item2 = new ItemWithSingleUnique();
		deleteOnTearDown(item2);

		item1.set(new AttributeValue[]{
				new AttributeValue(item1.uniqueString, "uniqueString1"),
				new AttributeValue(item1.otherString, "otherString1"),
		});
		assertEquals("uniqueString1", item1.getUniqueString());
		assertEquals("otherString1", item1.getOtherString());

		item2.set(new AttributeValue[]{
				new AttributeValue(item1.uniqueString, "uniqueString2"),
				new AttributeValue(item1.otherString, "otherString2"),
		});
		assertEquals("uniqueString2", item2.getUniqueString());
		assertEquals("otherString2", item2.getOtherString());

		// test unique violation
		try
		{
			item2.set(new AttributeValue[]{
					new AttributeValue(item1.uniqueString, "uniqueString1"),
					new AttributeValue(item1.otherString, "otherString1"),
			});
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(item2.uniqueString.getImplicitUniqueConstraint(), e.getConstraint());
			assertEquals("unique violation for "+item2.uniqueString.getImplicitUniqueConstraint().toString(), e.getMessage());
		}
		assertEquals("uniqueString2", item2.getUniqueString());
		assertEquals("otherString2", item2.getOtherString());
	}

	public void testItemWithSingleUniqueReadOnly()
			throws ConstraintViolationException
	{
		assertEquals(null, UniqueFinal.findByUniqueReadOnlyString("uniqueString"));

		final UniqueFinal item = new UniqueFinal("uniqueString");
		assertEquals("uniqueString", item.getUniqueReadOnlyString());
		assertEquals(item, UniqueFinal.findByUniqueReadOnlyString("uniqueString"));

		try
		{
			item.set(item.uniqueReadOnlyString, "zapp");
			fail("should have thrown FinalViolationException");
		}
		catch(FinalViolationException e)
		{
			assertEquals(item.uniqueReadOnlyString, e.getFinalAttribute());
			assertEquals(item, e.getItem());
			assertEquals("final violation on " + item + " for UniqueFinal#uniqueReadOnlyString", e.getMessage());
		}
		assertEquals("uniqueString", item.getUniqueReadOnlyString());
		assertEquals(item, UniqueFinal.findByUniqueReadOnlyString("uniqueString"));

		assertDelete(item);
	}

	public void testItemWithSingleUniqueNotNull()
			throws IntegrityViolationException, UniqueViolationException, MandatoryViolationException
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
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.uniqueNotNullString, e.getMandatoryAttribute());
			assertEquals(item, e.getItem());
			assertEquals("mandatory violation on " + item + " for ItemWithSingleUniqueNotNull#uniqueNotNullString", e.getMessage());
		}
		assertEquals("uniqueString2", item.getUniqueNotNullString());
		assertEquals(null, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString"));
		assertEquals(item, ItemWithSingleUniqueNotNull.findByUniqueNotNullString("uniqueString2"));

		assertContains(item, item.TYPE.search(null));
		try
		{
			new ItemWithSingleUniqueNotNull(null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.uniqueNotNullString, e.getMandatoryAttribute());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation on a newly created item for ItemWithSingleUniqueNotNull#uniqueNotNullString", e.getMessage());
		}
		assertContains(item, item.TYPE.search(null));

		assertDelete(item);
	}
	
	public void testDoubleUnique()
		throws ConstraintViolationException
	{
		assertEqualsUnmodifiable(
			list(
				ItemWithDoubleUnique.string,
				ItemWithDoubleUnique.integer,
				ItemWithDoubleUnique.doubleUnique
			),
			ItemWithDoubleUnique.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				ItemWithDoubleUnique.string,
				ItemWithDoubleUnique.integer,
				ItemWithDoubleUnique.doubleUnique
			),
			ItemWithDoubleUnique.TYPE.getFeatures());
		assertEquals("doubleUnique", ItemWithDoubleUnique.doubleUnique.getName());
		assertEquals(ItemWithDoubleUnique.TYPE, ItemWithDoubleUnique.doubleUnique.getType());
		assertEquals(
			list(ItemWithDoubleUnique.string, ItemWithDoubleUnique.integer),
			ItemWithDoubleUnique.doubleUnique.getUniqueAttributes());
		assertEquals(
			list(ItemWithDoubleUnique.doubleUnique),
			ItemWithDoubleUnique.string.getUniqueConstraints());
		assertEquals(
			list(ItemWithDoubleUnique.doubleUnique),
			ItemWithDoubleUnique.integer.getUniqueConstraints());

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
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(a1.doubleUnique, e.getConstraint());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for ItemWithDoubleUnique#doubleUnique", e.getMessage());
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

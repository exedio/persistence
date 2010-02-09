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
		// test model
		assertEqualsUnmodifiable(
			list(
				ItemWithSingleUnique.TYPE.getThis(),
				ItemWithSingleUnique.uniqueString,
				ItemWithSingleUnique.uniqueString.getImplicitUniqueConstraint(),
				ItemWithSingleUnique.otherString
			),
			ItemWithSingleUnique.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				ItemWithSingleUnique.TYPE.getThis(),
				ItemWithSingleUnique.uniqueString,
				ItemWithSingleUnique.uniqueString.getImplicitUniqueConstraint(),
				ItemWithSingleUnique.otherString
			),
			ItemWithSingleUnique.TYPE.getFeatures());
		assertEqualsUnmodifiable(
			list(ItemWithSingleUnique.uniqueString),
			ItemWithSingleUnique.uniqueString.getImplicitUniqueConstraint().getFields());
		assertEqualsUnmodifiable(
			list(ItemWithSingleUnique.uniqueString.getImplicitUniqueConstraint()),
			ItemWithSingleUnique.uniqueString.getUniqueConstraints());
		
		assertEqualsUnmodifiable(
			list(UniqueFinal.uniqueFinalString),
			UniqueFinal.uniqueFinalString.getImplicitUniqueConstraint().getFields());
		assertEqualsUnmodifiable(
			list(UniqueFinal.uniqueFinalString.getImplicitUniqueConstraint()),
			UniqueFinal.uniqueFinalString.getUniqueConstraints());
		
		assertEqualsUnmodifiable(
			list(ItemWithSingleUniqueNotNull.uniqueNotNullString),
			ItemWithSingleUniqueNotNull.uniqueNotNullString.getImplicitUniqueConstraint().getFields());
		assertEqualsUnmodifiable(
			list(ItemWithSingleUniqueNotNull.uniqueNotNullString.getImplicitUniqueConstraint()),
			ItemWithSingleUniqueNotNull.uniqueNotNullString.getUniqueConstraints());
		
		assertSerializedSame(ItemWithSingleUnique.uniqueString.getImplicitUniqueConstraint(), 402);

		// test persistence
		assertEquals(null, ItemWithSingleUnique.forUniqueString("uniqueString"));

		// create two items with null, that must not interfere with uniqueness
		final ItemWithSingleUnique nullItem1 = deleteOnTearDown(new ItemWithSingleUnique());
		assertEquals(null, nullItem1.getUniqueString());
		assertEquals(null, nullItem1.forUniqueString("uniqueString"));
		try
		{
			nullItem1.forUniqueString(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on ItemWithSingleUnique.uniqueString", e.getMessage());
		}
		
		final ItemWithSingleUnique nullItem2 = deleteOnTearDown(new ItemWithSingleUnique());
		assertEquals(null, nullItem2.getUniqueString());
		assertEquals(null, nullItem2.forUniqueString("uniqueString"));
		try
		{
			nullItem2.forUniqueString(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on ItemWithSingleUnique.uniqueString", e.getMessage());
		}
		
		nullItem2.setUniqueString("uniqueString");
		assertEquals("uniqueString", nullItem2.getUniqueString());
		assertEquals(nullItem2, ItemWithSingleUnique.forUniqueString("uniqueString"));
		try
		{
			nullItem2.forUniqueString(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on ItemWithSingleUnique.uniqueString", e.getMessage());
		}
		
		nullItem2.setUniqueString(null);
		assertEquals(null, nullItem2.getUniqueString());
		assertEquals(null, nullItem2.forUniqueString("uniqueString"));
		try
		{
			nullItem2.forUniqueString(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on ItemWithSingleUnique.uniqueString", e.getMessage());
		}
		
		// test non-null values
		final ItemWithSingleUnique item = new ItemWithSingleUnique();
		assertEquals(null, item.getUniqueString());
		assertEquals(null, item.forUniqueString("uniqueString"));

		item.setUniqueString("uniqueString");
		assertEquals("uniqueString", item.getUniqueString());
		assertEquals(item, ItemWithSingleUnique.forUniqueString("uniqueString"));

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
				assertEquals(item2.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
				assertEquals(item2, e.getItem());
				assertEquals("unique violation on " + item2 + " for " + item2.uniqueString.getImplicitUniqueConstraint().toString(), e.getMessage());
			}
			assertEquals("uniqueString2", item2.getUniqueString());
			assertEquals(item2, ItemWithSingleUnique.forUniqueString("uniqueString2"));

			assertDelete(item2);
		}
		
		try
		{
			new ItemWithSingleUnique("uniqueString");
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(item.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + item.uniqueString.getImplicitUniqueConstraint().toString(), e.getMessage());
		}
		assertEquals(item, ItemWithSingleUnique.forUniqueString("uniqueString"));

		try
		{
			new ItemWithSingleUnique("uniqueString", "otherString");
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(item.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + item.uniqueString.getImplicitUniqueConstraint().toString(), e.getMessage());
		}
		assertEquals(item, ItemWithSingleUnique.forUniqueString("uniqueString"));

		try
		{
			new ItemWithSingleUnique("uniqueString", null);
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(item.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + item.uniqueString.getImplicitUniqueConstraint().toString(), e.getMessage());
		}
		assertEquals(item, ItemWithSingleUnique.forUniqueString("uniqueString"));

		restartTransaction();
		assertTrue(!item.isActiveCopeItem());
		Item otheritem = model.getItem(item.getCopeID());
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
			final ItemWithSingleUnique foundItem = ItemWithSingleUnique.forUniqueString("uniqueString");
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
			final ItemWithSingleUnique foundItem = ItemWithSingleUnique.forUniqueString("uniqueString");
			assertEquals("uniqueString", foundItem.getUniqueString());
			assertEquals("uniqueString", item.getUniqueString());
			assertEquals(item, foundItem);
			assertEquals(item.getCopeID(), foundItem.getCopeID());
			assertEquals(item.hashCode(), foundItem.hashCode());
			assertNotSame(item, foundItem);
			assertNotSame(item, firstFoundItem);
			if(model.getConnectProperties().getQueryCacheLimit()>0)
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
	
	public void testMultipleSet()
	{
		final ItemWithSingleUnique item1 = deleteOnTearDown(new ItemWithSingleUnique());
		final ItemWithSingleUnique item2 = deleteOnTearDown(new ItemWithSingleUnique());

		item1.set(
				item1.uniqueString.map("uniqueString1"),
				item1.otherString.map("otherString1")
		);
		assertEquals("uniqueString1", item1.getUniqueString());
		assertEquals("otherString1", item1.getOtherString());

		item2.set(
				item1.uniqueString.map("uniqueString2"),
				item1.otherString.map("otherString2")
		);
		assertEquals("uniqueString2", item2.getUniqueString());
		assertEquals("otherString2", item2.getOtherString());

		// test unique violation
		try
		{
			item2.set(
					item1.uniqueString.map("uniqueString1"),
					item1.otherString.map("otherString1")
			);
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(item2.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(item2, e.getItem());
			assertEquals("unique violation on " + item2 + " for " + item2.uniqueString.getImplicitUniqueConstraint().toString(), e.getMessage());
		}
		assertEquals("uniqueString2", item2.getUniqueString());
		assertEquals("otherString2", item2.getOtherString());

		// test setting the value already set
		item2.set(
				item1.uniqueString.map("uniqueString2"),
				item1.otherString.map("otherString1")
		);
		assertEquals("uniqueString2", item2.getUniqueString());
		assertEquals("otherString1", item2.getOtherString());
	}

	public void testUniqueFinal()
	{
		assertEquals(null, UniqueFinal.forUniqueFinalString("uniqueString"));

		final UniqueFinal item = new UniqueFinal("uniqueString");
		assertEquals("uniqueString", item.getUniqueFinalString());
		assertEquals(item, UniqueFinal.forUniqueFinalString("uniqueString"));

		try
		{
			item.set(item.uniqueFinalString, "zapp");
			fail();
		}
		catch(FinalViolationException e)
		{
			assertEquals(item.uniqueFinalString, e.getFeature());
			assertEquals(item.uniqueFinalString, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("final violation on " + item + " for " + item.uniqueFinalString, e.getMessage());
		}
		assertEquals("uniqueString", item.getUniqueFinalString());
		assertEquals(item, UniqueFinal.forUniqueFinalString("uniqueString"));

		assertDelete(item);
	}

	public void testItemWithSingleUniqueNotNull()
	{
		assertEquals(null, ItemWithSingleUniqueNotNull.forUniqueNotNullString("uniqueString"));
		assertEquals(null, ItemWithSingleUniqueNotNull.forUniqueNotNullString("uniqueString2"));

		final ItemWithSingleUniqueNotNull item = new ItemWithSingleUniqueNotNull("uniqueString");
		assertEquals("uniqueString", item.getUniqueNotNullString());
		assertEquals(item, ItemWithSingleUniqueNotNull.forUniqueNotNullString("uniqueString"));
		assertEquals(null, ItemWithSingleUniqueNotNull.forUniqueNotNullString("uniqueString2"));

		item.setUniqueNotNullString("uniqueString2");
		assertEquals("uniqueString2", item.getUniqueNotNullString());
		assertEquals(null, ItemWithSingleUniqueNotNull.forUniqueNotNullString("uniqueString"));
		assertEquals(item, ItemWithSingleUniqueNotNull.forUniqueNotNullString("uniqueString2"));

		try
		{
			item.setUniqueNotNullString(null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.uniqueNotNullString, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("mandatory violation on " + item + " for " + item.uniqueNotNullString, e.getMessage());
		}
		assertEquals("uniqueString2", item.getUniqueNotNullString());
		assertEquals(null, ItemWithSingleUniqueNotNull.forUniqueNotNullString("uniqueString"));
		assertEquals(item, ItemWithSingleUniqueNotNull.forUniqueNotNullString("uniqueString2"));

		assertContains(item, item.TYPE.search(null));
		try
		{
			new ItemWithSingleUniqueNotNull(null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.uniqueNotNullString, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation for " + item.uniqueNotNullString, e.getMessage());
		}
		assertContains(item, item.TYPE.search(null));
		try
		{
			ItemWithSingleUniqueNotNull.TYPE.newItem(item.uniqueNotNullString.map(null));
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.uniqueNotNullString, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation for " + item.uniqueNotNullString, e.getMessage());
		}
		assertContains(item, item.TYPE.search(null));

		assertDelete(item);
	}
	
	public void testDoubleUnique()
	{
		assertEqualsUnmodifiable(
			list(
				ItemWithDoubleUnique.TYPE.getThis(),
				ItemWithDoubleUnique.string,
				ItemWithDoubleUnique.integer,
				ItemWithDoubleUnique.doubleUnique
			),
			ItemWithDoubleUnique.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				ItemWithDoubleUnique.TYPE.getThis(),
				ItemWithDoubleUnique.string,
				ItemWithDoubleUnique.integer,
				ItemWithDoubleUnique.doubleUnique
			),
			ItemWithDoubleUnique.TYPE.getFeatures());
		assertEquals("doubleUnique", ItemWithDoubleUnique.doubleUnique.getName());
		assertEquals(ItemWithDoubleUnique.TYPE, ItemWithDoubleUnique.doubleUnique.getType());
		assertEquals(
			list(ItemWithDoubleUnique.string, ItemWithDoubleUnique.integer),
			ItemWithDoubleUnique.doubleUnique.getFields());
		assertEquals(
			list(ItemWithDoubleUnique.doubleUnique),
			ItemWithDoubleUnique.string.getUniqueConstraints());
		assertEquals(
			list(ItemWithDoubleUnique.doubleUnique),
			ItemWithDoubleUnique.integer.getUniqueConstraints());

		assertSerializedSame(ItemWithDoubleUnique.doubleUnique, 388);
		
		assertEquals(null, ItemWithDoubleUnique.forDoubleUnique("a", 1));
		
		final ItemWithDoubleUnique a1 = new ItemWithDoubleUnique("a", 1);
		assertEquals(a1, ItemWithDoubleUnique.forDoubleUnique("a", 1));
		
		final ItemWithDoubleUnique a2 = new ItemWithDoubleUnique("a", 2);
		assertEquals(a2, ItemWithDoubleUnique.forDoubleUnique("a", 2));
		
		final ItemWithDoubleUnique b1 = new ItemWithDoubleUnique("b", 1);
		assertEquals(b1, ItemWithDoubleUnique.forDoubleUnique("b", 1));
		
		final ItemWithDoubleUnique b2 = new ItemWithDoubleUnique("b", 2);
		assertEquals(b2, ItemWithDoubleUnique.forDoubleUnique("b", 2));

		assertEquals(b1, ItemWithDoubleUnique.forDoubleUnique("b", 1));
		try
		{
			new ItemWithDoubleUnique("b", 1);
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(a1.doubleUnique, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + a1.doubleUnique, e.getMessage());
		}
		assertEquals(b1, ItemWithDoubleUnique.forDoubleUnique("b", 1));
		try
		{
			ItemWithDoubleUnique.TYPE.newItem(
					ItemWithDoubleUnique.string.map("b"),
					ItemWithDoubleUnique.integer.map(1)
				);
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(a1.doubleUnique, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + a1.doubleUnique, e.getMessage());
		}
		assertEquals(b1, ItemWithDoubleUnique.forDoubleUnique("b", 1));
		
		try
		{
			b2.setInteger(1);
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(a1.doubleUnique, e.getFeature());
			assertEquals(b2, e.getItem());
			assertEquals("unique violation on " + b2 + " for " + a1.doubleUnique, e.getMessage());
		}
		assertEquals(2, b2.getInteger());

		try
		{
			b2.set(b2.integer.map(1));
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(a1.doubleUnique, e.getFeature());
			assertEquals(b2, e.getItem());
			assertEquals("unique violation on " + b2 + " for " + a1.doubleUnique, e.getMessage());
		}
		assertEquals(2, b2.getInteger());

		// test setting the value already set
		b2.setInteger(2);
		assertEquals(2, b2.getInteger());

		assertDelete(b2);
		assertDelete(b1);

		final ItemWithDoubleUnique b1X = new ItemWithDoubleUnique("b", 1);
		assertEquals(b1X, ItemWithDoubleUnique.forDoubleUnique("b", 1));

		assertDelete(a2);
		assertDelete(a1);
		assertDelete(b1X);
	}

}

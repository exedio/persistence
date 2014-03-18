/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;

public class UniqueTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(
			UniqueSingleItem.TYPE,
			UniqueSingleNotNullItem.TYPE,
			UniqueFinalItem.TYPE);

	static
	{
		MODEL.enableSerialization(UniqueTest.class, "MODEL");
	}

	public UniqueTest()
	{
		super(MODEL);
	}

	public void testItemWithSingleUnique()
			throws IntegrityViolationException, UniqueViolationException, NoSuchIDException
	{
		// test model
		assertEqualsUnmodifiable(
			list(
				UniqueSingleItem.TYPE.getThis(),
				UniqueSingleItem.uniqueString,
				UniqueSingleItem.uniqueString.getImplicitUniqueConstraint(),
				UniqueSingleItem.otherString
			),
			UniqueSingleItem.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				UniqueSingleItem.TYPE.getThis(),
				UniqueSingleItem.uniqueString,
				UniqueSingleItem.uniqueString.getImplicitUniqueConstraint(),
				UniqueSingleItem.otherString
			),
			UniqueSingleItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(
			list(UniqueSingleItem.uniqueString),
			UniqueSingleItem.uniqueString.getImplicitUniqueConstraint().getFields());
		assertEqualsUnmodifiable(
			list(UniqueSingleItem.uniqueString.getImplicitUniqueConstraint()),
			UniqueSingleItem.uniqueString.getUniqueConstraints());

		assertEqualsUnmodifiable(
			list(UniqueFinalItem.uniqueFinalString),
			UniqueFinalItem.uniqueFinalString.getImplicitUniqueConstraint().getFields());
		assertEqualsUnmodifiable(
			list(UniqueFinalItem.uniqueFinalString.getImplicitUniqueConstraint()),
			UniqueFinalItem.uniqueFinalString.getUniqueConstraints());

		assertEqualsUnmodifiable(
			list(UniqueSingleNotNullItem.uniqueNotNullString),
			UniqueSingleNotNullItem.uniqueNotNullString.getImplicitUniqueConstraint().getFields());
		assertEqualsUnmodifiable(
			list(UniqueSingleNotNullItem.uniqueNotNullString.getImplicitUniqueConstraint()),
			UniqueSingleNotNullItem.uniqueNotNullString.getUniqueConstraints());

		assertSerializedSame(UniqueSingleItem.uniqueString.getImplicitUniqueConstraint(), 394);

		// test persistence
		assertEquals(null, UniqueSingleItem.forUniqueString("uniqueString"));

		// create two items with null, that must not interfere with uniqueness
		final UniqueSingleItem nullItem1 = deleteOnTearDown(new UniqueSingleItem());
		assertEquals(null, nullItem1.getUniqueString());
		assertEquals(null, UniqueSingleItem.forUniqueString("uniqueString"));
		try
		{
			UniqueSingleItem.forUniqueString(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on UniqueSingleItem.uniqueString", e.getMessage());
		}

		final UniqueSingleItem nullItem2 = deleteOnTearDown(new UniqueSingleItem());
		assertEquals(null, nullItem2.getUniqueString());
		assertEquals(null, UniqueSingleItem.forUniqueString("uniqueString"));
		try
		{
			UniqueSingleItem.forUniqueString(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on UniqueSingleItem.uniqueString", e.getMessage());
		}

		nullItem2.setUniqueString("uniqueString");
		assertEquals("uniqueString", nullItem2.getUniqueString());
		assertEquals(nullItem2, UniqueSingleItem.forUniqueString("uniqueString"));
		try
		{
			UniqueSingleItem.forUniqueString(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on UniqueSingleItem.uniqueString", e.getMessage());
		}

		nullItem2.setUniqueString(null);
		assertEquals(null, nullItem2.getUniqueString());
		assertEquals(null, UniqueSingleItem.forUniqueString("uniqueString"));
		try
		{
			UniqueSingleItem.forUniqueString(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on UniqueSingleItem.uniqueString", e.getMessage());
		}

		// test non-null values
		final UniqueSingleItem item = new UniqueSingleItem();
		assertEquals(null, item.getUniqueString());
		assertEquals(null, UniqueSingleItem.forUniqueString("uniqueString"));

		item.setUniqueString("uniqueString");
		assertEquals("uniqueString", item.getUniqueString());
		assertEquals(item, UniqueSingleItem.forUniqueString("uniqueString"));

		// test unique violation
		{
			final UniqueSingleItem item2 = new UniqueSingleItem();
			item2.setUniqueString("uniqueString2");
			try
			{
				item2.setUniqueString("uniqueString");
				fail();
			}
			catch(final UniqueViolationException e)
			{
				assertEquals(UniqueSingleItem.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
				assertEquals(item2, e.getItem());
				assertEquals("unique violation on " + item2 + " for " + UniqueSingleItem.uniqueString.getImplicitUniqueConstraint().toString(), e.getMessage());
				assertCause(e);
			}
			assertEquals("uniqueString2", item2.getUniqueString());
			assertEquals(item2, UniqueSingleItem.forUniqueString("uniqueString2"));

			assertDelete(item2);
		}

		try
		{
			new UniqueSingleItem("uniqueString");
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(UniqueSingleItem.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + UniqueSingleItem.uniqueString.getImplicitUniqueConstraint().toString(), e.getMessage());
			assertCause(e);
		}
		assertEquals(item, UniqueSingleItem.forUniqueString("uniqueString"));

		try
		{
			new UniqueSingleItem("uniqueString", "otherString");
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(UniqueSingleItem.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + UniqueSingleItem.uniqueString.getImplicitUniqueConstraint().toString(), e.getMessage());
			assertCause(e);
		}
		assertEquals(item, UniqueSingleItem.forUniqueString("uniqueString"));

		try
		{
			new UniqueSingleItem("uniqueString", null);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(UniqueSingleItem.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + UniqueSingleItem.uniqueString.getImplicitUniqueConstraint().toString(), e.getMessage());
			assertCause(e);
		}
		assertEquals(item, UniqueSingleItem.forUniqueString("uniqueString"));

		restartTransaction();
		assertTrue(!item.isActiveCopeItem());
		final Item otheritem = model.getItem(item.getCopeID());
		assertNotSame(item, otheritem);
		assertTrue(otheritem.isActiveCopeItem());
		assertTrue(!item.isActiveCopeItem());
		assertEquals("uniqueString", item.getUniqueString());
		assertTrue(otheritem.isActiveCopeItem());
		assertTrue(!item.isActiveCopeItem());

		final UniqueSingleItem firstFoundItem;
		{
			restartTransaction();
			assertTrue(!item.isActiveCopeItem());
			final UniqueSingleItem foundItem = UniqueSingleItem.forUniqueString("uniqueString");
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
			final UniqueSingleItem foundItem = UniqueSingleItem.forUniqueString("uniqueString");
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
		final UniqueSingleItem item1 = deleteOnTearDown(new UniqueSingleItem());
		final UniqueSingleItem item2 = deleteOnTearDown(new UniqueSingleItem());

		item1.set(
				UniqueSingleItem.uniqueString.map("uniqueString1"),
				UniqueSingleItem.otherString.map("otherString1")
		);
		assertEquals("uniqueString1", item1.getUniqueString());
		assertEquals("otherString1", item1.getOtherString());

		item2.set(
				UniqueSingleItem.uniqueString.map("uniqueString2"),
				UniqueSingleItem.otherString.map("otherString2")
		);
		assertEquals("uniqueString2", item2.getUniqueString());
		assertEquals("otherString2", item2.getOtherString());

		// test unique violation
		try
		{
			item2.set(
					UniqueSingleItem.uniqueString.map("uniqueString1"),
					UniqueSingleItem.otherString.map("otherString1")
			);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(UniqueSingleItem.uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(item2, e.getItem());
			assertEquals("unique violation on " + item2 + " for " + UniqueSingleItem.uniqueString.getImplicitUniqueConstraint().toString(), e.getMessage());
			assertCause(e);
		}
		assertEquals("uniqueString2", item2.getUniqueString());
		assertEquals("otherString2", item2.getOtherString());

		// test setting the value already set
		item2.set(
				UniqueSingleItem.uniqueString.map("uniqueString2"),
				UniqueSingleItem.otherString.map("otherString1")
		);
		assertEquals("uniqueString2", item2.getUniqueString());
		assertEquals("otherString1", item2.getOtherString());
	}

	public void testUniqueFinal()
	{
		assertEquals(null, UniqueFinalItem.forUniqueFinalString("uniqueString"));

		final UniqueFinalItem item = new UniqueFinalItem("uniqueString");
		assertEquals("uniqueString", item.getUniqueFinalString());
		assertEquals(item, UniqueFinalItem.forUniqueFinalString("uniqueString"));

		try
		{
			item.set(UniqueFinalItem.uniqueFinalString, "zapp");
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(UniqueFinalItem.uniqueFinalString, e.getFeature());
			assertEquals(UniqueFinalItem.uniqueFinalString, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("final violation on " + item + " for " + UniqueFinalItem.uniqueFinalString, e.getMessage());
		}
		assertEquals("uniqueString", item.getUniqueFinalString());
		assertEquals(item, UniqueFinalItem.forUniqueFinalString("uniqueString"));

		assertDelete(item);
	}

	public void testItemWithSingleUniqueNotNull()
	{
		assertEquals(null, UniqueSingleNotNullItem.forUniqueNotNullString("uniqueString"));
		assertEquals(null, UniqueSingleNotNullItem.forUniqueNotNullString("uniqueString2"));

		final UniqueSingleNotNullItem item = new UniqueSingleNotNullItem("uniqueString");
		assertEquals("uniqueString", item.getUniqueNotNullString());
		assertEquals(item, UniqueSingleNotNullItem.forUniqueNotNullString("uniqueString"));
		assertEquals(null, UniqueSingleNotNullItem.forUniqueNotNullString("uniqueString2"));

		item.setUniqueNotNullString("uniqueString2");
		assertEquals("uniqueString2", item.getUniqueNotNullString());
		assertEquals(null, UniqueSingleNotNullItem.forUniqueNotNullString("uniqueString"));
		assertEquals(item, UniqueSingleNotNullItem.forUniqueNotNullString("uniqueString2"));

		try
		{
			item.setUniqueNotNullString(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(UniqueSingleNotNullItem.uniqueNotNullString, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("mandatory violation on " + item + " for " + UniqueSingleNotNullItem.uniqueNotNullString, e.getMessage());
		}
		assertEquals("uniqueString2", item.getUniqueNotNullString());
		assertEquals(null, UniqueSingleNotNullItem.forUniqueNotNullString("uniqueString"));
		assertEquals(item, UniqueSingleNotNullItem.forUniqueNotNullString("uniqueString2"));

		assertContains(item, UniqueSingleNotNullItem.TYPE.search(null));
		try
		{
			new UniqueSingleNotNullItem(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(UniqueSingleNotNullItem.uniqueNotNullString, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation for " + UniqueSingleNotNullItem.uniqueNotNullString, e.getMessage());
		}
		assertContains(item, UniqueSingleNotNullItem.TYPE.search(null));
		try
		{
			UniqueSingleNotNullItem.TYPE.newItem(UniqueSingleNotNullItem.uniqueNotNullString.map(null));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(UniqueSingleNotNullItem.uniqueNotNullString, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation for " + UniqueSingleNotNullItem.uniqueNotNullString, e.getMessage());
		}
		assertContains(item, UniqueSingleNotNullItem.TYPE.search(null));

		assertDelete(item);
	}
}

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

import static com.exedio.cope.AbstractRuntimeTest.assertDelete;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.SchemaInfo.getConstraintName;
import static com.exedio.cope.UniqueFinalItem.forUniqueFinalString;
import static com.exedio.cope.UniqueFinalItem.uniqueFinalString;
import static com.exedio.cope.UniqueSingleItem.forUniqueString;
import static com.exedio.cope.UniqueSingleItem.forUniqueStringStrict;
import static com.exedio.cope.UniqueSingleItem.otherString;
import static com.exedio.cope.UniqueSingleItem.uniqueString;
import static com.exedio.cope.UniqueSingleNotNullItem.forUniqueNotNullString;
import static com.exedio.cope.UniqueSingleNotNullItem.uniqueNotNullString;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class UniqueTest extends TestWithEnvironment
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

	@Test void testItemWithSingleUnique()
			throws IntegrityViolationException, UniqueViolationException, NoSuchIDException
	{
		// test model
		assertEqualsUnmodifiable(
			list(
				UniqueSingleItem.TYPE.getThis(),
				uniqueString,
				uniqueString.getImplicitUniqueConstraint(),
				otherString
			),
			UniqueSingleItem.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				UniqueSingleItem.TYPE.getThis(),
				uniqueString,
				uniqueString.getImplicitUniqueConstraint(),
				otherString
			),
			UniqueSingleItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(
			list(uniqueString),
			uniqueString.getImplicitUniqueConstraint().getFields());
		assertEqualsUnmodifiable(
			list(uniqueString.getImplicitUniqueConstraint()),
			uniqueString.getUniqueConstraints());

		assertEqualsUnmodifiable(
			list(uniqueFinalString),
			uniqueFinalString.getImplicitUniqueConstraint().getFields());
		assertEqualsUnmodifiable(
			list(uniqueFinalString.getImplicitUniqueConstraint()),
			uniqueFinalString.getUniqueConstraints());

		assertEqualsUnmodifiable(
			list(uniqueNotNullString),
			uniqueNotNullString.getImplicitUniqueConstraint().getFields());
		assertEqualsUnmodifiable(
			list(uniqueNotNullString.getImplicitUniqueConstraint()),
			uniqueNotNullString.getUniqueConstraints());

		assertSerializedSame(uniqueString.getImplicitUniqueConstraint(), 394);

		// test persistence
		assertEquals(
				mysql && !propertiesLongConstraintNames()
				? "UniquSingItem_uniStri_Unq"
				: "UniqueSingleItem_uniqueString_Unq",
				getConstraintName(uniqueString.getImplicitUniqueConstraint()));

		assertEquals(null, forUniqueString("uniqueString"));
		assertEquals(null, uniqueString.searchUnique("uniqueString"));
		try
		{
			forUniqueStringStrict("uniqueString");
			fail();
		}
		catch (final IllegalArgumentException e)
		{
			assertEquals("expected result of size one, but was empty for query: select this from UniqueSingleItem where uniqueString='uniqueString'", e.getMessage());
		}

		// create two items with null, that must not interfere with uniqueness
		final UniqueSingleItem nullItem1 = new UniqueSingleItem();
		assertEquals(null, nullItem1.getUniqueString());
		assertEquals(null, forUniqueString("uniqueString"));
		assertEquals(null, uniqueString.searchUnique("uniqueString"));
		try
		{
			forUniqueString(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on UniqueSingleItem.uniqueString", e.getMessage());
		}

		final UniqueSingleItem nullItem2 = new UniqueSingleItem();
		assertEquals(null, nullItem2.getUniqueString());
		assertEquals(null, forUniqueString("uniqueString"));
		assertEquals(null, uniqueString.searchUnique("uniqueString"));
		try
		{
			forUniqueString(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on UniqueSingleItem.uniqueString", e.getMessage());
		}

		nullItem2.setUniqueString("uniqueString");
		assertEquals("uniqueString", nullItem2.getUniqueString());
		assertEquals(nullItem2, forUniqueString("uniqueString"));
		assertEquals(nullItem2, uniqueString.searchUnique("uniqueString"));
		try
		{
			forUniqueString(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on UniqueSingleItem.uniqueString", e.getMessage());
		}

		nullItem2.setUniqueString(null);
		assertEquals(null, nullItem2.getUniqueString());
		assertEquals(null, forUniqueString("uniqueString"));
		try
		{
			forUniqueString(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("cannot search uniquely for null on UniqueSingleItem.uniqueString", e.getMessage());
		}

		// test non-null values
		final UniqueSingleItem item = new UniqueSingleItem();
		assertEquals(null, item.getUniqueString());
		assertEquals(null, forUniqueString("uniqueString"));

		item.setUniqueString("uniqueString");
		assertEquals("uniqueString", item.getUniqueString());
		assertEquals(item, forUniqueString("uniqueString"));
		assertEquals(item, forUniqueStringStrict("uniqueString"));

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
				assertEquals(uniqueString.getImplicitUniqueConstraint(), e.getFeature());
				assertEquals(uniqueString, e.getFeatureForDescription());
				assertEqualsUnmodifiable(asList(uniqueString), e.getFields());
				assertEquals(item2, e.getItem());
				assertEquals("unique violation on " + item2 + " for " + uniqueString.getImplicitUniqueConstraint(), e.getMessage());
				assertCause(e);
			}
			assertEquals("uniqueString2", item2.getUniqueString());
			assertEquals(item2, forUniqueString("uniqueString2"));

			assertDelete(item2);
		}

		try
		{
			new UniqueSingleItem("uniqueString");
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + uniqueString.getImplicitUniqueConstraint(), e.getMessage());
			assertCause(e);
		}
		assertEquals(item, forUniqueString("uniqueString"));

		try
		{
			new UniqueSingleItem("uniqueString", "otherString");
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + uniqueString.getImplicitUniqueConstraint(), e.getMessage());
			assertCause(e);
		}
		assertEquals(item, forUniqueString("uniqueString"));

		try
		{
			new UniqueSingleItem("uniqueString", null);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("unique violation for " + uniqueString.getImplicitUniqueConstraint(), e.getMessage());
			assertCause(e);
		}
		assertEquals(item, forUniqueString("uniqueString"));

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
			final UniqueSingleItem foundItem = forUniqueString("uniqueString");
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
			final UniqueSingleItem foundItem = forUniqueString("uniqueString");
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

	@Test void testMultipleSet()
	{
		final UniqueSingleItem item1 = new UniqueSingleItem();
		final UniqueSingleItem item2 = new UniqueSingleItem();

		item1.set(
				uniqueString.map("uniqueString1"),
				otherString.map("otherString1")
		);
		assertEquals("uniqueString1", item1.getUniqueString());
		assertEquals("otherString1", item1.getOtherString());

		item2.set(
				uniqueString.map("uniqueString2"),
				otherString.map("otherString2")
		);
		assertEquals("uniqueString2", item2.getUniqueString());
		assertEquals("otherString2", item2.getOtherString());

		// test unique violation
		try
		{
			item2.set(
					uniqueString.map("uniqueString1"),
					otherString.map("otherString1")
			);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals(uniqueString.getImplicitUniqueConstraint(), e.getFeature());
			assertEquals(item2, e.getItem());
			assertEquals("unique violation on " + item2 + " for " + uniqueString.getImplicitUniqueConstraint(), e.getMessage());
			assertCause(e);
		}
		assertEquals("uniqueString2", item2.getUniqueString());
		assertEquals("otherString2", item2.getOtherString());

		// test setting the value already set
		item2.set(
				uniqueString.map("uniqueString2"),
				otherString.map("otherString1")
		);
		assertEquals("uniqueString2", item2.getUniqueString());
		assertEquals("otherString1", item2.getOtherString());
	}

	@Test void testUniqueFinal()
	{
		assertEquals(null, forUniqueFinalString("uniqueString"));

		final UniqueFinalItem item = new UniqueFinalItem("uniqueString");
		assertEquals("uniqueString", item.getUniqueFinalString());
		assertEquals(item, forUniqueFinalString("uniqueString"));

		try
		{
			item.set(uniqueFinalString, "zapp");
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals(uniqueFinalString, e.getFeature());
			assertEquals(uniqueFinalString, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("final violation on " + item + " for " + uniqueFinalString, e.getMessage());
		}
		assertEquals("uniqueString", item.getUniqueFinalString());
		assertEquals(item, forUniqueFinalString("uniqueString"));

		assertDelete(item);
	}

	@Test void testItemWithSingleUniqueNotNull()
	{
		assertEquals(null, forUniqueNotNullString("uniqueString"));
		assertEquals(null, forUniqueNotNullString("uniqueString2"));

		final UniqueSingleNotNullItem item = new UniqueSingleNotNullItem("uniqueString");
		assertEquals("uniqueString", item.getUniqueNotNullString());
		assertEquals(item, forUniqueNotNullString("uniqueString"));
		assertEquals(null, forUniqueNotNullString("uniqueString2"));

		item.setUniqueNotNullString("uniqueString2");
		assertEquals("uniqueString2", item.getUniqueNotNullString());
		assertEquals(null, forUniqueNotNullString("uniqueString"));
		assertEquals(item, forUniqueNotNullString("uniqueString2"));

		try
		{
			item.setUniqueNotNullString(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(uniqueNotNullString, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("mandatory violation on " + item + " for " + uniqueNotNullString, e.getMessage());
		}
		assertEquals("uniqueString2", item.getUniqueNotNullString());
		assertEquals(null, forUniqueNotNullString("uniqueString"));
		assertEquals(item, forUniqueNotNullString("uniqueString2"));

		assertContains(item, UniqueSingleNotNullItem.TYPE.search(null));
		try
		{
			new UniqueSingleNotNullItem(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(uniqueNotNullString, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation for " + uniqueNotNullString, e.getMessage());
		}
		assertContains(item, UniqueSingleNotNullItem.TYPE.search(null));
		try
		{
			UniqueSingleNotNullItem.TYPE.newItem(uniqueNotNullString.map(null));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(uniqueNotNullString, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("mandatory violation for " + uniqueNotNullString, e.getMessage());
		}
		assertContains(item, UniqueSingleNotNullItem.TYPE.search(null));

		assertDelete(item);
	}
}

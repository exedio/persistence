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

package com.exedio.cope.pattern;

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;

public class PartOfModelTest extends CopeAssert
{
	static final Model MODEL = new Model(PartOfItem.TYPE, PartOfOrderedItem.TYPE, PartOfContainerItem.TYPE);

	static
	{
		MODEL.enableSerialization(PartOfModelTest.class, "MODEL");
	}

	public void testTypes()
	{
		assertEqualsUnmodifiable(list(
				PartOfItem.TYPE,
				PartOfOrderedItem.TYPE,
				PartOfContainerItem.TYPE
			), MODEL.getTypes());
		assertEqualsUnmodifiable(list(
				PartOfItem.TYPE,
				PartOfOrderedItem.TYPE,
				PartOfContainerItem.TYPE
			), MODEL.getTypesSortedByHierarchy());
	}

	public void testFeatures()
	{
		assertEqualsUnmodifiable(list(
				PartOfItem.TYPE.getThis(),
				PartOfItem.container,
				PartOfItem.unordered,
				PartOfItem.partString,
				PartOfItem.partInteger
			), PartOfItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				PartOfOrderedItem.TYPE.getThis(),
				PartOfOrderedItem.container,
				PartOfOrderedItem.order,
				PartOfOrderedItem.partsOrdered,
				PartOfOrderedItem.partString,
				PartOfOrderedItem.partInteger
			), PartOfOrderedItem.TYPE.getFeatures());
	}

	public void testPattern()
	{
		assertEquals(PartOfItem.TYPE, PartOfItem.unordered.getType());
		assertEquals("unordered", PartOfItem.unordered.getName());

		assertSame(PartOfItem.container, PartOfItem.unordered.getContainer());
		assertSame(null, PartOfItem.unordered.getOrder());
		assertSame(PartOfItem.unordered, PartOfItem.container.getPattern());
		assertEqualsUnmodifiable(list(PartOfItem.container), PartOfItem.unordered.getSourceFeatures());

		assertSame(PartOfOrderedItem.container, PartOfOrderedItem.partsOrdered.getContainer());
		assertSame(PartOfOrderedItem.order, PartOfOrderedItem.partsOrdered.getOrder());
		assertSame(PartOfOrderedItem.partsOrdered, PartOfOrderedItem.container.getPattern());
		assertSame(PartOfOrderedItem.partsOrdered, PartOfOrderedItem.order.getPattern());
		assertEqualsUnmodifiable(list(PartOfOrderedItem.container, PartOfOrderedItem.order), PartOfOrderedItem.partsOrdered.getSourceFeatures());
	}

	public void testGetPartOfs()
	{
		assertEqualsUnmodifiable(list(), PartOf.getDeclaredPartOfs(PartOfItem.TYPE));
		assertEqualsUnmodifiable(list(), PartOf.getPartOfs(PartOfItem.TYPE));
		assertEqualsUnmodifiable(list(PartOfItem.unordered, PartOfOrderedItem.partsOrdered), PartOf.getDeclaredPartOfs(PartOfContainerItem.TYPE));
		assertEqualsUnmodifiable(list(PartOfItem.unordered, PartOfOrderedItem.partsOrdered), PartOf.getPartOfs(PartOfContainerItem.TYPE));
		assertEquals(list(), PartOf.getPartOfs(PartOfItem.unordered));
	}

	public void testSerialization()
	{
		assertSerializedSame(PartOfItem.unordered, 384);
	}

	public void testContainerNull()
	{
		try
		{
			PartOf.create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("container", e.getMessage());
		}
	}

	public void testContainerNullWithOrder()
	{
		try
		{
			PartOf.create(null, new IntegerField());
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("container", e.getMessage());
		}
	}

	public void testOrderNull()
	{
		try
		{
			PartOf.create(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("order", e.getMessage());
		}
	}
}

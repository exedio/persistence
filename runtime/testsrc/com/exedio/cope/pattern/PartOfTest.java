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

package com.exedio.cope.pattern;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;

public class PartOfTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(PartOfItem.TYPE, PartOfOrderedItem.TYPE, PartOfContainerItem.TYPE);
	
	static
	{
		MODEL.enableSerialization(PartOfTest.class, "MODEL");
	}
	
	public PartOfTest()
	{
		super(MODEL);
	}

	PartOfContainerItem container;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		container = deleteOnTearDown(new PartOfContainerItem("container"));
	}
	
	public void testIt()
	{
		// test model
		assertEqualsUnmodifiable(list(
				PartOfItem.TYPE,
				PartOfOrderedItem.TYPE,
				PartOfContainerItem.TYPE
			), model.getTypes());
		assertEqualsUnmodifiable(list(
				PartOfItem.TYPE,
				PartOfOrderedItem.TYPE,
				PartOfContainerItem.TYPE
			), model.getTypesSortedByHierarchy());

		assertEqualsUnmodifiable(list(
				PartOfItem.TYPE.getThis(),
				PartOfItem.container,
				PartOfItem.parts,
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

		assertEquals(PartOfItem.TYPE, PartOfItem.parts.getType());
		assertEquals("parts", PartOfItem.parts.getName());

		assertSame(PartOfItem.container, PartOfItem.parts.getContainer());
		assertSame(null, PartOfItem.parts.getOrder());
		assertEquals(PartOfItem.parts, PartOfItem.container.getPattern());
		assertEqualsUnmodifiable(list(PartOfItem.container), PartOfItem.parts.getSourceFeatures());

		assertSame(PartOfOrderedItem.container, PartOfOrderedItem.partsOrdered.getContainer());
		assertSame(PartOfOrderedItem.order, PartOfOrderedItem.partsOrdered.getOrder());
		assertEquals(PartOfOrderedItem.partsOrdered, PartOfOrderedItem.container.getPattern());
		assertEquals(PartOfOrderedItem.partsOrdered, PartOfOrderedItem.order.getPattern());
		assertEqualsUnmodifiable(list(PartOfOrderedItem.container, PartOfOrderedItem.order), PartOfOrderedItem.partsOrdered.getSourceFeatures());
		
		assertEquals(list(), PartOf.getDeclaredPartOfs(PartOfItem.TYPE));
		assertEquals(list(), PartOf.getPartOfs(PartOfItem.TYPE));
		assertEquals(list(PartOfItem.parts, PartOfOrderedItem.partsOrdered), PartOf.getDeclaredPartOfs(PartOfContainerItem.TYPE));
		assertEquals(list(PartOfItem.parts, PartOfOrderedItem.partsOrdered), PartOf.getPartOfs(PartOfContainerItem.TYPE));
		assertEquals(list(), PartOf.getPartOfs(PartOfItem.parts));
		assertSerializedSame(PartOfItem.parts, 375);

		try
		{
			PartOf.newPartOf(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("feature", e.getMessage());
		}
		try
		{
			PartOf.newPartOf(null, new IntegerField());
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("feature", e.getMessage());
		}
		try
		{
			PartOf.newPartOf(null, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("order", e.getMessage());
		}

		// test persistence
		assertContains(container.getParts());
		
		final PartOfItem part1 = container.addToParts("part1", 1);
		assertEquals(container, part1.getPartsContainer());
		assertContains(part1, container.getParts());
		assertContains(part1, PartOfItem.parts.getPartsAndCast(container));
		
		final PartOfItem part2 = container.addToParts("part2", 2);
		assertEquals(container, part1.getPartsContainer());
		assertEquals(container, part2.getPartsContainer());
		assertContains(part1, part2, container.getParts());
		assertContains(part1, part2, PartOfItem.parts.getPartsAndCast(container));
		
		
		assertEquals(list(), container.getPartsOrdered());
		
		final PartOfOrderedItem partOrdered1 = container.addToPartsOrdered(2, "part1", 1);
		assertEquals(container, partOrdered1.getPartsOrderedContainer());
		assertEquals(list(partOrdered1), container.getPartsOrdered());
		assertEquals(list(partOrdered1), PartOfOrderedItem.partsOrdered.getPartsAndCast(container));
		
		final PartOfOrderedItem partOrdered2 = container.addToPartsOrdered(1, "part2", 2);
		assertEquals(container, partOrdered1.getPartsOrderedContainer());
		assertEquals(container, partOrdered2.getPartsOrderedContainer());
		assertEquals(list(partOrdered2, partOrdered1), container.getPartsOrdered());
		assertEquals(list(partOrdered2, partOrdered1), PartOfOrderedItem.partsOrdered.getPartsAndCast(container));
	}
}

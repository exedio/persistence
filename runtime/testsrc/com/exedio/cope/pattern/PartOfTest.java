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

import com.exedio.cope.AbstractRuntimeTest;

public class PartOfTest extends AbstractRuntimeTest
{
	public PartOfTest()
	{
		super(PartOfModelTest.MODEL);
	}

	PartOfContainerItem container;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		container = deleteOnTearDown(new PartOfContainerItem("container"));
	}

	public void testUnordered()
	{
		assertEquals(list(), container.getParts());

		final PartOfItem part1 = container.addToParts("part1", 1);
		assertEquals(container, part1.getPartsContainer());
		assertEquals(list(part1), container.getParts());
		assertEquals(list(part1), PartOfItem.parts.getParts(container));

		final PartOfItem part2 = container.addToParts("part2", 2);
		assertEquals(container, part1.getPartsContainer());
		assertEquals(container, part2.getPartsContainer());
		assertEquals(list(part1, part2), container.getParts());
		assertEquals(list(part1, part2), PartOfItem.parts.getParts(container));

		// parts condition
		assertEquals(list(part1, part2), container.getParts(null));
		assertEquals(list(part1       ), container.getParts(PartOfItem.partString.equal("part1")));
	}

	public void testOrdered()
	{
		assertEquals(list(), container.getPartsOrdered());

		final PartOfOrderedItem partOrdered1 = container.addToPartsOrdered(2, "part1", 1);
		assertEquals(container, partOrdered1.getPartsOrderedContainer());
		assertEquals(list(partOrdered1), container.getPartsOrdered());
		assertEquals(list(partOrdered1), PartOfOrderedItem.partsOrdered.getParts(container));

		final PartOfOrderedItem partOrdered2 = container.addToPartsOrdered(1, "part2", 2);
		assertEquals(container, partOrdered1.getPartsOrderedContainer());
		assertEquals(container, partOrdered2.getPartsOrderedContainer());
		assertEquals(list(partOrdered2, partOrdered1), container.getPartsOrdered());
		assertEquals(list(partOrdered2, partOrdered1), PartOfOrderedItem.partsOrdered.getParts(container));

		// parts condition
		assertEquals(list(partOrdered2, partOrdered1), container.getPartsOrdered(null));
		assertEquals(list(partOrdered1              ), container.getPartsOrdered(PartOfOrderedItem.partString.equal("part1")));
	}
}

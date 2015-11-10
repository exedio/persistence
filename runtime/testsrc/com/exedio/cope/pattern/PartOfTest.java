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
		assertEquals(list(), container.getUnordered());

		final PartOfItem part1 = container.addToUnordered("part1", 1);
		assertEquals(container, part1.getUnorderedContainer());
		assertEquals(list(part1), container.getUnordered());
		assertEquals(list(part1), PartOfItem.unordered.getParts(container));

		final PartOfItem part2 = container.addToUnordered("part2", 2);
		assertEquals(container, part1.getUnorderedContainer());
		assertEquals(container, part2.getUnorderedContainer());
		assertEquals(list(part1, part2), container.getUnordered());
		assertEquals(list(part1, part2), PartOfItem.unordered.getParts(container));

		// parts condition
		assertEquals(list(part1, part2), container.getUnordered(null));
		assertEquals(list(part1       ), container.getUnordered(PartOfItem.partString.equal("part1")));
	}

	public void testOrdered()
	{
		assertEquals(list(), container.getPartsOrdered());

		final PartOfOrderedItem part1 = container.addToPartsOrdered(2, "part1", 1);
		assertEquals(container, part1.getPartsOrderedContainer());
		assertEquals(list(part1), container.getPartsOrdered());
		assertEquals(list(part1), PartOfOrderedItem.partsOrdered.getParts(container));

		final PartOfOrderedItem part2 = container.addToPartsOrdered(1, "part2", 2);
		assertEquals(container, part1.getPartsOrderedContainer());
		assertEquals(container, part2.getPartsOrderedContainer());
		assertEquals(list(part2, part1), container.getPartsOrdered());
		assertEquals(list(part2, part1), PartOfOrderedItem.partsOrdered.getParts(container));

		final PartOfOrderedItem part3 = container.addToPartsOrdered(3, "part3", 3);
		assertEquals(container, part1.getPartsOrderedContainer());
		assertEquals(container, part2.getPartsOrderedContainer());
		assertEquals(container, part3.getPartsOrderedContainer());
		assertEquals(list(part2, part1, part3), container.getPartsOrdered());
		assertEquals(list(part2, part1, part3), PartOfOrderedItem.partsOrdered.getParts(container));

		// parts condition
		assertEquals(list(part2, part1, part3), container.getPartsOrdered(null));
		assertEquals(list(part1              ), container.getPartsOrdered(PartOfOrderedItem.partString.equal("part1")));
	}
}

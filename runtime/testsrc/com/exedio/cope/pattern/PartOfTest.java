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

import com.exedio.cope.AbstractRuntimeModelTest;

public class PartOfTest extends AbstractRuntimeModelTest
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
		container = new PartOfContainerItem("container");
	}

	@Test public void testUnordered()
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

	@Test public void testOrdered()
	{
		assertEquals(list(), container.getOrdered());

		final PartOfItem part1 = container.addToOrdered(2, "part1", 1);
		assertEquals(container, part1.getOrderedContainer());
		assertEquals(list(part1), container.getOrdered());
		assertEquals(list(part1), PartOfItem.ordered.getParts(container));

		final PartOfItem part2 = container.addToOrdered(1, "part2", 2);
		assertEquals(container, part1.getOrderedContainer());
		assertEquals(container, part2.getOrderedContainer());
		assertEquals(list(part2, part1), container.getOrdered());
		assertEquals(list(part2, part1), PartOfItem.ordered.getParts(container));

		final PartOfItem part3 = container.addToOrdered(3, "part3", 3);
		assertEquals(container, part1.getOrderedContainer());
		assertEquals(container, part2.getOrderedContainer());
		assertEquals(container, part3.getOrderedContainer());
		assertEquals(list(part2, part1, part3), container.getOrdered());
		assertEquals(list(part2, part1, part3), PartOfItem.ordered.getParts(container));

		// parts condition
		assertEquals(list(part2, part1, part3), container.getOrdered(null));
		assertEquals(list(part1              ), container.getOrdered(PartOfItem.partString.equal("part1")));
	}
}

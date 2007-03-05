/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Model;

public class PartOfTest extends AbstractLibTest
{
	private static final Model MODEL = new Model(PartOfItem.TYPE, PartOfContainerItem.TYPE);
	
	public PartOfTest()
	{
		super(MODEL);
	}

	PartOfContainerItem container;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(container = new PartOfContainerItem("container"));
	}
	
	public void testIt()
	{
		// test model
		assertEqualsUnmodifiable(list(
				PartOfItem.TYPE,
				PartOfContainerItem.TYPE
			), model.getTypes());
		assertEqualsUnmodifiable(list(
				PartOfItem.TYPE,
				PartOfContainerItem.TYPE
			), model.getTypesSortedByHierarchy());

		assertEqualsUnmodifiable(list(
				PartOfItem.TYPE.getThis(),
				PartOfItem.container,
				PartOfItem.parts,
				PartOfItem.partString,
				PartOfItem.partInteger
			), PartOfItem.TYPE.getFeatures());

		assertEquals(PartOfItem.TYPE, PartOfItem.parts.getType());
		assertEquals("parts", PartOfItem.parts.getName());

		assertSame(PartOfItem.container, PartOfItem.parts.getContainer());
		assertEquals(list(PartOfItem.parts), PartOfItem.container.getPatterns());

		try
		{
			PartOf.newPartOf(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}

		// test persistence
		assertContains(container.getParts());
		
		final PartOfItem part1 = container.addToParts("part1", 1);
		assertEquals(container, part1.getPartsContainer());
		assertContains(part1, container.getParts());
		
		final PartOfItem part2 = container.addToParts("part2", 2);
		assertEquals(container, part1.getPartsContainer());
		assertEquals(container, part2.getPartsContainer());
		assertContains(part1, part2, container.getParts());
	}
}

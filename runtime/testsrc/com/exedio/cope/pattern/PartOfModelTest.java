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
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import org.junit.jupiter.api.Test;

public class PartOfModelTest
{
	static final Model MODEL = new Model(PartOfItem.TYPE, PartOfContainerItem.TYPE);

	static
	{
		MODEL.enableSerialization(PartOfModelTest.class, "MODEL");
	}

	@Test void testTypes()
	{
		assertEqualsUnmodifiable(list(
				PartOfItem.TYPE,
				PartOfContainerItem.TYPE
			), MODEL.getTypes());
		assertEqualsUnmodifiable(list(
				PartOfItem.TYPE,
				PartOfContainerItem.TYPE
			), MODEL.getTypesSortedByHierarchy());
	}

	@Test void testFeatures()
	{
		assertEqualsUnmodifiable(list(
				PartOfItem.TYPE.getThis(),
				PartOfItem.container,
				PartOfItem.order,
				PartOfItem.unordered,
				PartOfItem.ordered,
				PartOfItem.partString,
				PartOfItem.partInteger
			), PartOfItem.TYPE.getFeatures());
	}

	@Test void testPattern()
	{
		assertEquals(PartOfItem.TYPE, PartOfItem.unordered.getType());
		assertEquals("unordered", PartOfItem.unordered.getName());

		assertSame(PartOfItem.container, PartOfItem.unordered.getContainer());
		assertSame(null, PartOfItem.unordered.getOrder());
		assertSame(PartOfItem.unordered, PartOfItem.container.getPattern());
		assertEqualsUnmodifiable(list(PartOfItem.container), PartOfItem.unordered.getSourceFeatures());

		assertSame(PartOfItem.container, PartOfItem.ordered.getContainer());
		assertSame(PartOfItem.order, PartOfItem.ordered.getOrder());
		assertSame(PartOfItem.unordered, PartOfItem.container.getPattern());
		assertSame(null, PartOfItem.order.getPattern());
		assertEqualsUnmodifiable(list(), PartOfItem.ordered.getSourceFeatures());
	}

	@Test void testGetPartOfs()
	{
		assertEqualsUnmodifiable(list(), PartOf.getDeclaredPartOfs(PartOfItem.TYPE));
		assertEqualsUnmodifiable(list(), PartOf.getPartOfs(PartOfItem.TYPE));
		assertEqualsUnmodifiable(list(PartOfItem.unordered, PartOfItem.ordered), PartOf.getDeclaredPartOfs(PartOfContainerItem.TYPE));
		assertEqualsUnmodifiable(list(PartOfItem.unordered, PartOfItem.ordered), PartOf.getPartOfs(PartOfContainerItem.TYPE));
		assertEquals(list(), PartOf.getPartOfs(PartOfItem.unordered));
	}

	@Test void testSerialize()
	{
		assertSerializedSame(PartOfItem.unordered, 384);
	}

	@Test void testContainerNull()
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

	@Test void testContainerNullWithOrder()
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

	@Test void testOrderNull()
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

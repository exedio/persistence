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
import static com.exedio.cope.pattern.PartOf.orderBy;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
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
		assertEqualsUnmodifiable(asList(), PartOfItem.unordered.getOrders());
		assertSame(PartOfItem.unordered, PartOfItem.container.getPattern());
		assertEqualsUnmodifiable(list(PartOfItem.container), PartOfItem.unordered.getSourceFeatures());

		assertSame(PartOfItem.container, PartOfItem.ordered.getContainer());
		assertEqualsUnmodifiable(asList(orderBy(PartOfItem.order)), PartOfItem.ordered.getOrders());
		assertSame(PartOfItem.unordered, PartOfItem.container.getPattern());
		assertSame(null, PartOfItem.order.getPattern());
		assertEqualsUnmodifiable(list(), PartOfItem.ordered.getSourceFeatures());
	}

	@Deprecated // OK: test deprecated api
	@Test void testOrderByDeprecated()
	{
		assertSame(null, PartOfItem.unordered.getOrder());
		assertSame(PartOfItem.order, PartOfItem.ordered.getOrder());
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
		assertFails(
				() -> PartOf.create(null),
				NullPointerException.class,
				"container");
	}

	@Test void testContainerNullWithOrder()
	{
		assertFails(
				() -> PartOf.create(null, new IntegerField()),
				NullPointerException.class,
				"container");
	}

	@Test void testOrderNull()
	{
		assertFails(
				() -> PartOf.create(null, (FunctionField)null),
				NullPointerException.class,
				"order");
	}

	@Test void testOrdersNull()
	{
		assertFails(
				() -> PartOf.create(null, (PartOf.OrderBy[])null),
				NullPointerException.class,
				"orders");
	}

	@Test void testOrdersEmpty()
	{
		final ItemField<?> container = ItemField.create(PartOfContainerItem.class);
		final PartOf<?> partOf = PartOf.create(container, new PartOf.OrderBy[]{});
		assertEquals(container, partOf.getContainer());
		assertEqualsUnmodifiable(asList(), partOf.getOrders());
	}

	@Test void testOrdersContainsNull()
	{
		final ItemField<?> container = ItemField.create(PartOfContainerItem.class);
		final PartOf.OrderBy order0 = orderBy(new IntegerField());
		final PartOf.OrderBy order2 = orderBy(new IntegerField());
		assertFails(
				() -> PartOf.create(container, order0, null, order2),
				NullPointerException.class,
				"orders[1]");
	}
}

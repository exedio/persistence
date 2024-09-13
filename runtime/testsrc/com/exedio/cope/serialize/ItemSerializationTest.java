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

package com.exedio.cope.serialize;
// Must not be in package com.exedio.cope,
// otherwise bugs could be hidden, where empty constructor
// needed for deserialization is not public.

import static com.exedio.cope.serialize.ItemSerializationItem.TYPE;
import static com.exedio.cope.serialize.ItemSerializationItem.list;
import static com.exedio.cope.serialize.ItemSerializationItem.listParent;
import static com.exedio.cope.tojunit.Assert.reserialize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItemSerializationTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE, ItemSerializationItem2.TYPE);

	static
	{
		MODEL.enableSerialization(ItemSerializationTest.class, "MODEL");
	}

	public ItemSerializationTest()
	{
		super(MODEL);
	}

	private ItemSerializationItem item;

	@BeforeEach final void setUp()
	{
		// need this to let item have pk==1 but not the default pk==0, which could hide bugs
		new ItemSerializationItem("nullus");
		item = new ItemSerializationItem("eins");
	}

	@Test void testItem()
	{
		final String id = item.getCopeID();
		assertSame(TYPE, item.getCopeType());
		assertEquals("eins", item.getName());

		final ItemSerializationItem readItem = reserialize(item, 116);
		assertEquals(id, readItem.getCopeID());
		assertSame(TYPE, readItem.getCopeType());
		assertEquals("eins", readItem.getName());
		assertEquals(item, readItem);
		assertEquals(item.hashCode(), readItem.hashCode());
		assertNotSame(item, readItem);
		assertSame(item, item.activeCopeItem());
		assertSame(item, readItem.activeCopeItem());

		readItem.setName("zwei");
		assertEquals("zwei", readItem.getName());
		assertEquals("zwei", item.getName());
	}

	@Test void testUnboundItem()
	{
		item.setList(Arrays.asList("zack"));
		final Item unboundItem =
			list.getEntryType().searchSingleton(listParent().equal(item));
		final String id = unboundItem.getCopeID();
		assertEquals(list.getEntryType(), unboundItem.getCopeType());

		final Item readItem = reserialize(unboundItem, 394);
		assertEquals(id, readItem.getCopeID());
		assertSame(list.getEntryType(), readItem.getCopeType());
		assertEquals("zack", list.getElement().get(readItem));
		assertEquals(unboundItem, readItem);
		assertEquals(unboundItem.hashCode(), readItem.hashCode());
		assertNotSame(unboundItem, readItem);
		assertSame(unboundItem, unboundItem.activeCopeItem());
		assertSame(unboundItem, readItem.activeCopeItem());
	}
}

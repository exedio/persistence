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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.Query;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.ListField;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SerializationSizeTest extends TestWithEnvironment
{
	public SerializationSizeTest()
	{
		super(ItemSerializationTest.MODEL);
	}

	ArrayList<Serializable> accu = null;
	int previousSize;

	@BeforeEach final void setUp()
	{
		accu = new ArrayList<>();
		previousSize = accuSize();
	}

	@Test void testModel()
	{
		final ItemSerializationItem itemA = new ItemSerializationItem("nameA");
		final ItemSerializationItem itemB = new ItemSerializationItem("nameB");
		final ItemSerializationItem2 item2A = new ItemSerializationItem2();
		final ItemSerializationItem2 item2B = new ItemSerializationItem2();

		assertAccu(176, ItemSerializationTest.MODEL);
		assertAccu(  5, ItemSerializationTest.MODEL);
		assertAccu(  5, ItemSerializationTest.MODEL);

		assertAccu(121, ItemSerializationItem.TYPE);
		assertAccu(  5, ItemSerializationItem.TYPE);
		assertAccu(  5, ItemSerializationItem.TYPE);
		assertAccu( 36, ItemSerializationItem2.TYPE);
		assertAccu(  5, ItemSerializationItem2.TYPE);
		assertAccu(  5, ItemSerializationItem2.TYPE);

		assertAccu(115, ItemSerializationItem.name);
		assertAccu(  5, ItemSerializationItem.name);
		assertAccu(  5, ItemSerializationItem.name);
		assertAccu( 19, ItemSerializationItem.name2);
		assertAccu(  5, ItemSerializationItem.name2);
		assertAccu(  5, ItemSerializationItem.name2);

		assertAccu(112, itemA);
		assertAccu(  5, itemA);
		assertAccu(  5, itemA);
		assertAccu( 14, itemB);
		assertAccu(  5, itemB);
		assertAccu(  5, itemB);
		assertAccu( 77, item2A);
		assertAccu(  5, item2A);
		assertAccu(  5, item2A);
		assertAccu( 14, item2B);
		assertAccu(  5, item2B);
		assertAccu(  5, item2B);
	}

	@Test void testTypes()
	{
		assertAccu(292, ItemSerializationItem.TYPE);
		assertAccu(  5, ItemSerializationItem.TYPE);
		assertAccu(  5, ItemSerializationItem.TYPE);
		assertAccu( 36, ItemSerializationItem2.TYPE);
		assertAccu(  5, ItemSerializationItem2.TYPE);
		assertAccu(  5, ItemSerializationItem2.TYPE);
	}

	@Test void testFeatures()
	{
		assertAccu(402, ItemSerializationItem.name);
		assertAccu(  5, ItemSerializationItem.name);
		assertAccu(  5, ItemSerializationItem.name);
		assertAccu( 19, ItemSerializationItem.name2);
		assertAccu(  5, ItemSerializationItem.name2);
		assertAccu(  5, ItemSerializationItem.name2);
	}

	@Test void testFeaturesFromDifferentTypes()
	{
		assertAccu(402, ItemSerializationItem.name);
		assertAccu(  5, ItemSerializationItem.name);
		assertAccu(  5, ItemSerializationItem.name);
		assertAccu( 50, ItemSerializationItem2.name2);
		assertAccu(  5, ItemSerializationItem2.name2);
		assertAccu(  5, ItemSerializationItem2.name2);
	}

	@Test void testItems()
	{
		assertAccu(112, new ItemSerializationItem("name"));
		assertAccu( 14, new ItemSerializationItem("name"));
		assertAccu( 14, new ItemSerializationItem("name"));
		assertAccu( 77, new ItemSerializationItem2());
		assertAccu( 14, new ItemSerializationItem2());
	}

	@Test void testUnboundItems()
	{
		final ItemSerializationItem  item1 = new ItemSerializationItem("name");
		final ItemSerializationItem2 item2 = new ItemSerializationItem2();
		item1.addToList("listItem0");
		item1.addToList("listItem1");
		item1.addToList("listItem2");
		item2.addToList("listItem0");
		item2.addToList("listItem1");
		final List<? extends Item> list1 = getItems(ItemSerializationItem .list, item1);
		final List<? extends Item> list2 = getItems(ItemSerializationItem2.list, item2);

		assertAccu(390, list1.get(0));
		assertAccu( 19, list1.get(1));
		assertAccu( 19, list1.get(2));
		assertAccu( 55, list2.get(0));
		assertAccu( 19, list2.get(1));
	}

	private static List<? extends Item> getItems(final ListField<?> f, final Item parent)
	{
		final Query<? extends Item> q = new Query<>(f.getEntryType().getThis(), f.getParent().isCasted(parent));
		q.setOrderBy(f.getOrder(), true);
		return q.search();
	}

	private void assertAccu(final int expectedSize, final Serializable value)
	{
		if(accu==null)
			throw new NullPointerException();

		accu.add(value);
		final int size = accuSize();
		final int actualSize = size - previousSize;
		previousSize = size;
		assertEquals(expectedSize, actualSize);
	}

	private int accuSize()
	{
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try(ObjectOutputStream oos = new ObjectOutputStream(bos))
		{
			oos.writeObject(accu);
		}
		catch(final IOException e)
		{
			throw new RuntimeException(e);
		}
		return bos.size();
	}
}

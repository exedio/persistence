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

package com.exedio.cope.serialize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Cope;
import com.exedio.cope.Item;
import com.exedio.cope.Query;
import com.exedio.cope.pattern.ListField;

public class SerializationSizeTest extends AbstractRuntimeTest
{
	public SerializationSizeTest()
	{
		super(ItemSerializationTest.MODEL);
	}
	
	ArrayList<Serializable> accu = null;
	int previousSize;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		accu = new ArrayList<Serializable>();
		previousSize = accuSize();
	}
	
	public void testModel()
	{
		final ItemSerializationItem itemA = deleteOnTearDown(new ItemSerializationItem("nameA"));
		final ItemSerializationItem itemB = deleteOnTearDown(new ItemSerializationItem("nameB"));
		final ItemSerializationItem2 item2A = deleteOnTearDown(new ItemSerializationItem2());
		final ItemSerializationItem2 item2B = deleteOnTearDown(new ItemSerializationItem2());
		
		assertAccu(176, ItemSerializationTest.MODEL);
		assertAccu(  5, ItemSerializationTest.MODEL);
		assertAccu(  5, ItemSerializationTest.MODEL);
		
		assertAccu(121, ItemSerializationItem.TYPE);
		assertAccu(  5, ItemSerializationItem.TYPE);
		assertAccu(  5, ItemSerializationItem.TYPE);
		assertAccu( 36, ItemSerializationItem2.TYPE);
		assertAccu(  5, ItemSerializationItem2.TYPE);
		assertAccu(  5, ItemSerializationItem2.TYPE);
		
		assertAccu(107, ItemSerializationItem.name);
		assertAccu(  5, ItemSerializationItem.name);
		assertAccu(  5, ItemSerializationItem.name);
		assertAccu( 19, ItemSerializationItem.name2);
		assertAccu(  5, ItemSerializationItem.name2);
		assertAccu(  5, ItemSerializationItem.name2);
		
		assertAccu(108, itemA);
		assertAccu(  5, itemA);
		assertAccu(  5, itemA);
		assertAccu( 10, itemB);
		assertAccu(  5, itemB);
		assertAccu(  5, itemB);
		assertAccu( 73, item2A);
		assertAccu(  5, item2A);
		assertAccu(  5, item2A);
		assertAccu( 10, item2B);
		assertAccu(  5, item2B);
		assertAccu(  5, item2B);
	}
	
	public void testTypes()
	{
		assertAccu(292, ItemSerializationItem.TYPE);
		assertAccu(  5, ItemSerializationItem.TYPE);
		assertAccu(  5, ItemSerializationItem.TYPE);
		assertAccu( 36, ItemSerializationItem2.TYPE);
		assertAccu(  5, ItemSerializationItem2.TYPE);
		assertAccu(  5, ItemSerializationItem2.TYPE);
	}
	
	public void testFeatures()
	{
		assertAccu(394, ItemSerializationItem.name);
		assertAccu(  5, ItemSerializationItem.name);
		assertAccu(  5, ItemSerializationItem.name);
		assertAccu( 19, ItemSerializationItem.name2);
		assertAccu(  5, ItemSerializationItem.name2);
		assertAccu(  5, ItemSerializationItem.name2);
	}
	
	public void testFeaturesFromDifferentTypes()
	{
		assertAccu(394, ItemSerializationItem.name);
		assertAccu(  5, ItemSerializationItem.name);
		assertAccu(  5, ItemSerializationItem.name);
		assertAccu( 50, ItemSerializationItem2.name2);
		assertAccu(  5, ItemSerializationItem2.name2);
		assertAccu(  5, ItemSerializationItem2.name2);
	}
	
	public void testItems()
	{
		assertAccu(108, deleteOnTearDown(new ItemSerializationItem("name")));
		assertAccu( 10, deleteOnTearDown(new ItemSerializationItem("name")));
		assertAccu( 10, deleteOnTearDown(new ItemSerializationItem("name")));
		assertAccu( 73, deleteOnTearDown(new ItemSerializationItem2()));
		assertAccu( 10, deleteOnTearDown(new ItemSerializationItem2()));
	}
	
	public void testUnboundItems()
	{
		final ItemSerializationItem  item1 = deleteOnTearDown(new ItemSerializationItem("name"));
		final ItemSerializationItem2 item2 = deleteOnTearDown(new ItemSerializationItem2());
		item1.addToList("listItem0");
		item1.addToList("listItem1");
		item1.addToList("listItem2");
		item2.addToList("listItem0");
		item2.addToList("listItem1");
		final List<? extends Item> list1 = getItems(item1.list, item1);
		final List<? extends Item> list2 = getItems(item2.list, item2);
		
		assertAccu(386, list1.get(0));
		assertAccu( 15, list1.get(1));
		assertAccu( 15, list1.get(2));
		assertAccu( 51, list2.get(0));
		assertAccu( 15, list2.get(1));
	}
	
	private static List<? extends Item> getItems(final ListField<?> f, final Item parent)
	{
		final Query<? extends Item> q = new Query<Item>(f.getRelationType().getThis(), Cope.equalAndCast(f.getParent(), parent));
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
		try
		{
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(accu);
			oos.close();
			
			return bos.size();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}

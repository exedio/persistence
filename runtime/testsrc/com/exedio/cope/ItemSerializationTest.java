/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

package com.exedio.cope;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ItemSerializationTest extends AbstractLibTest
{
	static final Model MODEL = new Model(ItemSerializationItem.TYPE);

	public ItemSerializationTest()
	{
		super(MODEL);
	}
	
	private ItemSerializationItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		deleteOnTearDown(item = new ItemSerializationItem("eins"));
	}
	
	public void testItemSerialization() throws IOException, ClassNotFoundException
	{
		final String id = item.getCopeID();
		assertSame(item.TYPE, item.getCopeType());
		assertEquals("eins", item.getName());
		
		final byte[] buf;
		{
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(item);
			oos.close();
			buf = bos.toByteArray();
		}
		
		final ItemSerializationItem readItem;
		{
			final ByteArrayInputStream bis = new ByteArrayInputStream(buf);
			final ObjectInputStream ois = new ObjectInputStream(bis);
			readItem = (ItemSerializationItem)ois.readObject();
			ois.close();
		}
		
		assertEquals(id, readItem.getCopeID());
		assertSame(item.TYPE, readItem.getCopeType());
		assertEquals("eins", readItem.getName());
		assertEquals(item, readItem);
		assertNotSame(item, readItem);
		assertSame(item, item.activeCopeItem());
		assertSame(item, readItem.activeCopeItem());
		assertTrue(String.valueOf(buf.length), buf.length<100);
		
		readItem.setName("zwei");
		assertEquals("zwei", readItem.getName());
		assertEquals("zwei", item.getName());
	}
	
}

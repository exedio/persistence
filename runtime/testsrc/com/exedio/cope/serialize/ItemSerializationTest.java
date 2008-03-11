/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
// See http://www.jguru.com/faq/view.jsp?EID=251942

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Model;

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
		
		// need this to let item have pk==1 but not the default pk==0, which could hide bugs
		deleteOnTearDown(new ItemSerializationItem("nullus"));
		item = deleteOnTearDown(new ItemSerializationItem("eins"));
	}
	
	public void testItemSerialization()
	{
		final String id = item.getCopeID();
		assertSame(item.TYPE, item.getCopeType());
		assertEquals("eins", item.getName());
		
		final ItemSerializationItem readItem = reserialize(item, 150);
		assertEquals(id, readItem.getCopeID());
		assertSame(item.TYPE, readItem.getCopeType());
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
}

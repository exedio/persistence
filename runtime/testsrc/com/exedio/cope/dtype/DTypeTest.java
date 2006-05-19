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

package com.exedio.cope.dtype;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Main;

public class DTypeTest extends AbstractLibTest
{
	public DTypeTest()
	{
		super(Main.dtypeModel);
	}
	
	DTypeItem item, item2;
	
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new DTypeItem("item1"));
		deleteOnTearDown(item2 = new DTypeItem("item2"));
	}
	
	public void testIt()
	{
		assertEquals(item.TYPE, item.features.getType());
		assertEquals("features", item.features.getName());
		
		assertContains(item.features.getTypes());
		
		final DType cellPhone = item.features.createType("cellPhone");
		deleteOnTearDown(cellPhone);
		assertContains(cellPhone, item.features.getTypes());
		assertContains(cellPhone.getAttributes());

		final DAttribute akkuTime = cellPhone.addIntegerAttribute("akkuTime");
		deleteOnTearDown(akkuTime);
		assertEquals(0, akkuTime.getPosition());
		assertEquals(list(akkuTime), cellPhone.getAttributes());

		final DAttribute memory = cellPhone.addStringAttribute("memory");
		deleteOnTearDown(memory);
		assertEquals(1, memory.getPosition());
		assertEquals(list(akkuTime, memory), cellPhone.getAttributes());
		
		assertEquals(null, item.features.getType(item));
		
		item.features.setType(item, cellPhone);
		assertEquals(cellPhone, item.features.getType(item));
		
		assertEquals(null, akkuTime.get(item));
		assertEquals(null, memory.get(item));
		
		akkuTime.set(item, 5);
		assertEquals(5, akkuTime.get(item));
		assertEquals(null, memory.get(item));
		
		akkuTime.set(item, 80);
		assertEquals(80, akkuTime.get(item));
		assertEquals(null, memory.get(item));
		
		memory.set(item, "80TB");
		assertEquals(80, akkuTime.get(item));
		assertEquals("80TB", memory.get(item));

		item.features.setType(item, null);
		assertEquals(null, item.features.getType(item));
	}
	
}

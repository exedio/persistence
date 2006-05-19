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
		assertEquals(item.TYPE, cellPhone.getParentType());
		assertEquals(item.features, cellPhone.getDtypeSystem());
		assertEquals("cellPhone", cellPhone.getCode());
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
		
		assertEquals(null, item.getFeaturesType());
		
		item.setFeaturesType(cellPhone);
		assertEquals(cellPhone, item.getFeaturesType());
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
		
		final DType organizer = item.features.createType("organizer");
		deleteOnTearDown(organizer);
		assertEquals(item.TYPE, organizer.getParentType());
		assertEquals(item.features, organizer.getDtypeSystem());
		assertEquals("organizer", organizer.getCode());
		assertContains(cellPhone, organizer, item.features.getTypes());

		final DAttribute weight = organizer.addIntegerAttribute("weight");
		deleteOnTearDown(weight);
		assertEquals(0, akkuTime.getPosition());
		assertEquals(list(weight), organizer.getAttributes());
		
		item2.setFeaturesType(organizer);
		assertEquals(organizer, item2.getFeaturesType());
		assertEquals(null, weight.get(item2));

		weight.set(item2, 500);
		assertEquals(500, weight.get(item2));
		
		// wrong value type
		try
		{
			weight.set(item2, "510");
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("java.lang.String", e.getMessage()); // TODO SOON better message
		}
		assertEquals(500, weight.get(item2));

		// wrong dtype
		try
		{
			weight.get(item);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("dynamic type mismatch: attribute has type organizer, but item has cellPhone", e.getMessage());
		}
		try
		{
			weight.set(item, 510);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("dynamic type mismatch: attribute has type organizer, but item has cellPhone", e.getMessage());
		}
		assertEquals(500, weight.get(item2));
		
		// test cleaning of attributes on setting type
		item2.setFeaturesType(cellPhone);
		assertEquals(null, akkuTime.get(item2)); // must not be 500 left from weight
		assertEquals(null, memory.get(item2));

		item.setFeaturesType(null);
		assertEquals(null, item.getFeaturesType());
		item2.setFeaturesType(null);
		assertEquals(null, item2.getFeaturesType());
	}
	
}

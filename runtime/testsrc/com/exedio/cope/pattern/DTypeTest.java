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

package com.exedio.cope.pattern;

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
		assertEquals(null, item.getFeatures(akkuTime));
		assertEquals(null, item.getFeatures(memory));
		
		item.setFeatures(akkuTime, 5);
		assertEquals(5, item.getFeatures(akkuTime));
		assertEquals(5, akkuTime.get(item));
		assertEquals(null, item.getFeatures(memory));
		
		akkuTime.set(item, 10);
		assertEquals(10, item.getFeatures(akkuTime));
		assertEquals(10, akkuTime.get(item));
		assertEquals(null, item.getFeatures(memory));
		
		item.setFeatures(akkuTime, 80);
		assertEquals(80, item.getFeatures(akkuTime));
		assertEquals(null, item.getFeatures(memory));
		
		item.setFeatures(memory, "80TB");
		assertEquals(80, item.getFeatures(akkuTime));
		assertEquals(80, akkuTime.get(item));
		assertEquals("80TB", item.getFeatures(memory));
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
		assertEquals(null, item2.getFeatures(weight));

		item2.setFeatures(weight, 500);
		assertEquals(500, item2.getFeatures(weight));
		
		// wrong value type
		try
		{
			item2.setFeatures(weight, "510");
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + Integer.class.getName() + ", but was a " + String.class.getName(), e.getMessage());
		}
		assertEquals(500, item2.getFeatures(weight));

		// wrong dtype
		try
		{
			item.getFeatures(weight);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("dynamic type mismatch: attribute has type organizer, but item has cellPhone", e.getMessage());
		}
		try
		{
			item.setFeatures(weight, 510);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("dynamic type mismatch: attribute has type organizer, but item has cellPhone", e.getMessage());
		}
		assertEquals(500, item2.getFeatures(weight));
		
		assertContains(akkuTime, memory, cellPhone.getAttributes());
		try
		{
			cellPhone.addStringAttribute("tooMuch");
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("capacity for STRING exceeded, 1 available, but tried to allocate 2", e.getMessage());
		}
		assertContains(akkuTime, memory, cellPhone.getAttributes());
		
		// test cleaning of attributes on setting type
		item2.setFeaturesType(cellPhone);
		assertEquals(null, item2.getFeatures(akkuTime)); // must not be 500 left from weight
		assertEquals(null, item2.getFeatures(memory));

		item.setFeaturesType(null);
		assertEquals(null, item.getFeaturesType());
		item2.setFeaturesType(null);
		assertEquals(null, item2.getFeaturesType());
	}
	
}

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

public class AttributeMapLimitedTest extends AbstractLibTest
{
	private static final AttributeMapLimitedItem.Language DE = AttributeMapLimitedItem.Language.DE;
	private static final AttributeMapLimitedItem.Language EN = AttributeMapLimitedItem.Language.EN;
	private static final AttributeMapLimitedItem.Language PL = AttributeMapLimitedItem.Language.PL;
	
	public AttributeMapLimitedTest()
	{
		super(Main.attributeMapLimitedModel);
	}
	
	AttributeMapLimitedItem item, itemX;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new AttributeMapLimitedItem());
		deleteOnTearDown(itemX = new AttributeMapLimitedItem());
	}
	
	public void testIt()
	{
		// test model
		assertEquals(item.TYPE, item.name.getType());
		assertEquals("name", item.name.getName());

		assertEquals(AttributeMapLimitedItem.Language.class, item.name.getKeyClass());

		assertEquals(String.class, item.name.getAttribute(DE).getValueClass());
		assertEquals("nameDE", item.name.getAttribute(DE).getName());
		assertSame(item.TYPE, item.name.getAttribute(DE).getType());
		assertEqualsUnmodifiable(list(item.name), item.name.getAttribute(DE).getPatterns());

		assertEqualsUnmodifiable(
				list(
						item.TYPE.getThis(),
						item.name, item.name.getAttribute(DE), item.name.getAttribute(EN), item.name.getAttribute(PL),
						item.nameLength, item.nameLength.getAttribute(DE), item.nameLength.getAttribute(EN), item.nameLength.getAttribute(PL)),
				item.TYPE.getFeatures());
		assertEqualsUnmodifiable(
				list(
						item.name.getAttribute(DE), item.name.getAttribute(EN), item.name.getAttribute(PL),
						item.nameLength.getAttribute(DE), item.nameLength.getAttribute(EN), item.nameLength.getAttribute(PL)),
				item.TYPE.getAttributes());

		assertEqualsUnmodifiable(list(item.TYPE), model.getTypes());

		// test persistence
		assertEquals(null, item.getName(DE));
		assertEquals(null, item.getNameLength(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getNameLength(EN));

		item.setName(DE, "nameDE");
		assertEquals("nameDE", item.getName(DE));
		assertEquals(null, item.getNameLength(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));
		
		item.setNameLength(DE, 5);
		assertEquals("nameDE", item.getName(DE));
		assertEquals(new Integer(5), item.getNameLength(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));
		
		item.setNameLength(DE, 6);
		assertEquals("nameDE", item.getName(DE));
		assertEquals(new Integer(6), item.getNameLength(DE));
		assertEquals(null, item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));
		
		item.setName(EN, "nameEN");
		assertEquals("nameDE", item.getName(DE));
		assertEquals(new Integer(6), item.getNameLength(DE));
		assertEquals("nameEN", item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));
		
		item.setName(DE, null);
		assertEquals(null, item.getName(DE));
		assertEquals(new Integer(6), item.getNameLength(DE));
		assertEquals("nameEN", item.getName(EN));
		assertEquals(null, item.getNameLength(EN));
		assertEquals(null, itemX.getName(DE));
	}
}

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

public class FieldMapLimitedTest extends AbstractLibTest
{
	private static final FieldMapLimitedItem.Language DE = FieldMapLimitedItem.Language.DE;
	private static final FieldMapLimitedItem.Language EN = FieldMapLimitedItem.Language.EN;
	private static final FieldMapLimitedItem.Language PL = FieldMapLimitedItem.Language.PL;
	
	public FieldMapLimitedTest()
	{
		super(Main.attributeMapLimitedModel);
	}
	
	FieldMapLimitedItem item, itemX;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new FieldMapLimitedItem());
		deleteOnTearDown(itemX = new FieldMapLimitedItem());
	}
	
	public void testIt()
	{
		// test model
		assertEquals(item.TYPE, item.name.getType());
		assertEquals("name", item.name.getName());

		assertEquals(FieldMapLimitedItem.Language.class, item.name.getKeyClass());

		assertEquals(String.class, item.name.getField(DE).getValueClass());
		assertEquals("nameDE", item.name.getField(DE).getName());
		assertSame(item.TYPE, item.name.getField(DE).getType());
		assertEqualsUnmodifiable(list(item.name), item.name.getField(DE).getPatterns());

		assertEqualsUnmodifiable(
				list(
						item.TYPE.getThis(),
						item.name, item.name.getField(DE), item.name.getField(EN), item.name.getField(PL),
						item.nameLength, item.nameLength.getField(DE), item.nameLength.getField(EN), item.nameLength.getField(PL)),
				item.TYPE.getFeatures());
		assertEqualsUnmodifiable(
				list(
						item.name.getField(DE), item.name.getField(EN), item.name.getField(PL),
						item.nameLength.getField(DE), item.nameLength.getField(EN), item.nameLength.getField(PL)),
				item.TYPE.getFields());

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

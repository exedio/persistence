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
import com.exedio.cope.EnumAttribute;
import com.exedio.cope.Main;
import com.exedio.cope.ItemAttribute.DeletePolicy;

public class AttributeMapTest extends AbstractLibTest
{
	private static final AttributeMapItem.Language DE = AttributeMapItem.Language.DE;
	private static final AttributeMapItem.Language EN = AttributeMapItem.Language.EN;
	private static final AttributeMapItem.Language PL = AttributeMapItem.Language.PL;
	
	public AttributeMapTest()
	{
		super(Main.attributeMapModel);
	}
	
	AttributeMapItem item, itemX;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new AttributeMapItem());
		deleteOnTearDown(itemX = new AttributeMapItem());
	}
	
	public void testIt()
	{
		// test model
		assertEquals(item.TYPE, item.name.getType());
		assertEquals("name", item.name.getName());

		assertEquals(item.TYPE, item.name.getParent().getValueType());
		assertEquals("parent", item.name.getParent().getName());
		assertEquals(DeletePolicy.CASCADE, item.name.getParent().getDeletePolicy());
		assertSame(item.name.getRelationType(), item.name.getParent().getType());
		assertEqualsUnmodifiable(list(), item.name.getParent().getPatterns());

		assertEquals(AttributeMapItem.Language.class, ((EnumAttribute)item.name.getKey()).getValueClass());
		assertEquals("key", item.name.getKey().getName());
		assertSame(item.name.getRelationType(), item.name.getKey().getType());
		assertEqualsUnmodifiable(list(), item.name.getKey().getPatterns());

		assertEqualsUnmodifiable(list(item.name.getParent(), item.name.getKey()), item.name.getUniqueConstraint().getUniqueAttributes());
		assertEquals("uniqueConstraint", item.name.getUniqueConstraint().getName());
		assertSame(item.name.getRelationType(), item.name.getUniqueConstraint().getType());
		assertEquals(list(item.name.getParent(), item.name.getKey()), item.name.getUniqueConstraint().getUniqueAttributes());

		assertEquals(String.class, item.name.getValue().getValueClass());
		assertEquals("value", item.name.getValue().getName());
		assertSame(item.name.getRelationType(), item.name.getValue().getType());
		assertEqualsUnmodifiable(list(), item.name.getValue().getPatterns());

		assertEquals("AttributeMapItem.name", item.name.getRelationType().getID());
		assertEquals(null, item.name.getRelationType().getJavaClass());
		assertEquals(null, item.name.getRelationType().getSupertype());
		assertEquals(list(), item.name.getRelationType().getSubTypes());
		assertEqualsUnmodifiable(
				list(
						item.name.getRelationType().getThis(),
						item.name.getParent(), item.name.getKey(), item.name.getUniqueConstraint(),
						item.name.getValue()),
				item.name.getRelationType().getFeatures());

		assertEqualsUnmodifiable(list(item.TYPE.getThis(), item.name, item.nameLength, item.string, item.integer), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(list(item.TYPE, item.name.getRelationType(), item.nameLength.getRelationType(), item.string.getRelationType(), item.integer.getRelationType()), model.getTypes());

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

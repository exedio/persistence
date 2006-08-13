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
		assertEquals(item.TYPE, item.language.getType());
		assertEquals("language", item.language.getName());

		assertEquals(item.TYPE, item.language.getParent().getValueType());
		assertEquals("parent", item.language.getParent().getName());
		assertEquals(DeletePolicy.CASCADE, item.language.getParent().getDeletePolicy());
		assertSame(item.language.getRelationType(), item.language.getParent().getType());
		assertEqualsUnmodifiable(list(), item.language.getParent().getPatterns());

		assertEquals(AttributeMapItem.Language.class, ((EnumAttribute)item.language.getKey()).getValueClass());
		assertEquals("key", item.language.getKey().getName());
		assertSame(item.language.getRelationType(), item.language.getKey().getType());
		assertEqualsUnmodifiable(list(), item.language.getKey().getPatterns());

		assertEqualsUnmodifiable(list(item.language.getParent(), item.language.getKey()), item.language.getUniqueConstraint().getUniqueAttributes());
		assertEquals("uniqueConstraint", item.language.getUniqueConstraint().getName());
		assertSame(item.language.getRelationType(), item.language.getUniqueConstraint().getType());

		assertEquals("AttributeMapItem.language", item.language.getRelationType().getID());
		assertEquals("com.exedio.cope.ItemWithoutJavaClass", item.language.getRelationType().getJavaClass().getName());
		assertEquals(null, item.language.getRelationType().getSupertype());
		assertEquals(list(), item.language.getRelationType().getSubTypes());
		assertEqualsUnmodifiable(
				list(
						item.language.getRelationType().getThis(),
						item.language.getParent(), item.language.getKey(), item.language.getUniqueConstraint(),
						AttributeMapItem.LanguageEntry.name, AttributeMapItem.LanguageEntry.nameLength),
				AttributeMapItem.language.getRelationType().getFeatures());
		assertEqualsUnmodifiable(list(AttributeMapItem.LanguageEntry.name, AttributeMapItem.LanguageEntry.nameLength), item.language.getFeatures());
		assertEqualsUnmodifiable(list(AttributeMapItem.LanguageEntry.name, AttributeMapItem.LanguageEntry.nameLength), item.language.getAttributes());

		assertEqualsUnmodifiable(list(item.TYPE.getThis(), item.language, item.code), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(list(item.TYPE, item.language.getRelationType(), item.code.getRelationType()), model.getTypes());

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

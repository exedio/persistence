/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.ItemField.DeletePolicy;

public class MapFieldTest extends AbstractLibTest
{
	static final Model MODEL = new Model(MapFieldItem.TYPE);

	private static final MapFieldItem.Language DE = MapFieldItem.Language.DE;
	private static final MapFieldItem.Language EN = MapFieldItem.Language.EN;
	
	public MapFieldTest()
	{
		super(MODEL);
	}
	
	MapFieldItem item, itemX;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new MapFieldItem());
		itemX = deleteOnTearDown(new MapFieldItem());
	}
	
	public void testIt()
	{
		// test model
		assertEquals(item.TYPE, item.name.getType());
		assertEquals("name", item.name.getName());
		assertEquals(MapFieldItem.class, item.TYPE.getJavaClass());
		assertEquals(true, item.TYPE.hasUniqueJavaClass());

		assertEquals(item.TYPE, item.nameParent().getValueType());
		assertEquals("parent", item.nameParent().getName());
		assertEquals(DeletePolicy.CASCADE, item.nameParent().getDeletePolicy());
		assertSame(item.name.getRelationType(), item.nameParent().getType());
		assertEqualsUnmodifiable(list(), item.nameParent().getPatterns());

		assertEquals(MapFieldItem.Language.class, ((EnumField<MapFieldItem.Language>)item.name.getKey()).getValueClass());
		assertEquals("key", item.name.getKey().getName());
		assertSame(item.name.getRelationType(), item.name.getKey().getType());
		assertEqualsUnmodifiable(list(), item.name.getKey().getPatterns());

		assertEqualsUnmodifiable(list(item.nameParent(), item.name.getKey()), item.name.getUniqueConstraint().getFields());
		assertEquals("uniqueConstraint", item.name.getUniqueConstraint().getName());
		assertSame(item.name.getRelationType(), item.name.getUniqueConstraint().getType());
		assertEquals(list(item.nameParent(), item.name.getKey()), item.name.getUniqueConstraint().getFields());

		assertEquals(String.class, item.name.getValue().getValueClass());
		assertEquals("value", item.name.getValue().getName());
		assertSame(item.name.getRelationType(), item.name.getValue().getType());
		assertEqualsUnmodifiable(list(), item.name.getValue().getPatterns());

		assertEquals("MapFieldItem.name", item.name.getRelationType().getID());
		assertEquals(Item.class, item.name.getRelationType().getJavaClass().getSuperclass());
		assertEquals(false, item.name.getRelationType().hasUniqueJavaClass());
		assertEquals(null, item.name.getRelationType().getSupertype());
		assertEquals(list(), item.name.getRelationType().getSubTypes());
		assertEqualsUnmodifiable(
				list(
						item.name.getRelationType().getThis(),
						item.nameParent(), item.name.getKey(), item.name.getUniqueConstraint(),
						item.name.getValue()),
				item.name.getRelationType().getFeatures());
		assertEquals(model, item.name.getRelationType().getModel());

		assertEqualsUnmodifiable(list(item.TYPE.getThis(), item.name, item.nameLength, item.string, item.integer), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(list(item.TYPE, item.name.getRelationType(), item.nameLength.getRelationType(), item.string.getRelationType(), item.integer.getRelationType()), model.getTypes());
		assertEqualsUnmodifiable(list(item.TYPE, item.name.getRelationType(), item.nameLength.getRelationType(), item.string.getRelationType(), item.integer.getRelationType()), model.getTypesSortedByHierarchy());
		
		assertEquals("MapFieldItem.name", item.name.getRelationType().getID());
		assertEquals("MapFieldItem.name.parent", item.name.getParent(MapFieldItem.class).getID());
		assertEquals("MapFieldItem.name.key", item.name.getKey().getID());
		assertEquals("MapFieldItem.name.value", item.name.getValue().getID());
		assertSame(item.name.getRelationType(), model.findTypeByID("MapFieldItem.name"));
		assertSame(item.name.getParent(MapFieldItem.class), model.findFeatureByID("MapFieldItem.name.parent"));
		assertSame(item.name.getKey(), model.findFeatureByID("MapFieldItem.name.key"));
		assertSame(item.name.getValue(), model.findFeatureByID("MapFieldItem.name.value"));

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
		{
			final Query<MapFieldItem> q = item.TYPE.newQuery(item.name.getValue().equal("nameDE"));
			item.name.join(q, DE);
			assertContains(item, q.search());
		}
		{
			final Query<MapFieldItem> q = item.TYPE.newQuery(item.name.getValue().equal("nameEN"));
			item.name.join(q, DE);
			assertContains(q.search());
		}
		{
			final Query<MapFieldItem> q = item.TYPE.newQuery(item.name.getValue().equal("nameDE"));
			item.name.join(q, EN);
			assertContains(q.search());
		}
		
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
		
		try
		{
			item.name.getParent(Item.class);
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + ItemField.class.getName() + "<" + Item.class.getName() + ">, but was a " + ItemField.class.getName() + "<" + item.getClass().getName() + ">", e.getMessage());
		}
	}
}

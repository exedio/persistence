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

import java.util.Date;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Item;
import com.exedio.cope.Main;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Query;
import com.exedio.cope.junit.CopeAssert;

public class AttributeListTest extends AbstractLibTest
{
	
	static final Date date1 = new Date(918756915152l);
	static final Date date2 = new Date(918756915153l);
	
	public AttributeListTest()
	{
		super(Main.attributeListModel);
	}

	AttributeListItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new AttributeListItem());
	}
	
	public void testIt()
	{
		// test model
		assertEquals(list(
				item.TYPE,
				item.strings.getRelationType(),
				item.dates.getRelationType()
			), model.getTypes());

		assertEquals(list(
				item.TYPE.getThis(),
				item.strings,
				item.dates
			), item.TYPE.getFeatures());
		assertEquals(list(
				item.strings.getRelationType().getThis(),
				item.strings.getParent(),
				item.strings.getOrder(),
				item.strings.getUniqueConstraint(),
				item.strings.getElement()
			), item.strings.getRelationType().getFeatures());
		assertEquals(list(
				item.dates.getRelationType().getThis(),
				item.dates.getParent(),
				item.dates.getOrder(),
				item.dates.getUniqueConstraint(),
				item.dates.getElement()
			), item.dates.getRelationType().getFeatures());

		assertEquals(item.TYPE, item.strings.getType());
		assertEquals("strings", item.strings.getName());
		assertEquals(item.TYPE, item.dates.getType());
		assertEquals("dates", item.dates.getName());

		assertEquals("AttributeListItem.strings", item.strings.getRelationType().getID());
		assertEquals(null, item.dates.getRelationType().getJavaClass());
		assertEquals(null, item.strings.getRelationType().getSupertype());
		assertEquals(list(), item.strings.getRelationType().getSubTypes());
		assertEquals(false, item.strings.getRelationType().isAbstract());
		assertEquals(Item.class, item.strings.getRelationType().getThis().getValueClass());
		assertEquals(item.strings.getRelationType(), item.strings.getRelationType().getThis().getValueType());

		assertEquals("AttributeListItem.dates", item.dates.getRelationType().getID());
		assertEquals(null, item.dates.getRelationType().getJavaClass());
		assertEquals(null, item.dates.getRelationType().getSupertype());
		assertEquals(list(), item.dates.getRelationType().getSubTypes());
		assertEquals(false, item.dates.getRelationType().isAbstract());
		assertEquals(Item.class, item.dates.getRelationType().getThis().getValueClass());
		assertEquals(item.dates.getRelationType(), item.dates.getRelationType().getThis().getValueType());

		assertEquals(item.strings.getRelationType(), item.strings.getParent().getType());
		assertEquals(item.strings.getRelationType(), item.strings.getOrder().getType());
		assertEquals(item.strings.getRelationType(), item.strings.getUniqueConstraint().getType());
		assertEquals(item.strings.getRelationType(), item.strings.getElement().getType());
		assertEquals(item.dates.getRelationType(), item.dates.getParent().getType());
		assertEquals(item.dates.getRelationType(), item.dates.getOrder().getType());
		assertEquals(item.dates.getRelationType(), item.dates.getUniqueConstraint().getType());
		assertEquals(item.dates.getRelationType(), item.dates.getElement().getType());

		assertEquals("parent", item.strings.getParent().getName());
		assertEquals("order", item.strings.getOrder().getName());
		assertEquals("uniqueConstraint", item.strings.getUniqueConstraint().getName());
		assertEquals("element", item.strings.getElement().getName());
		assertEquals("parent", item.dates.getParent().getName());
		assertEquals("order", item.dates.getOrder().getName());
		assertEquals("uniqueConstraint", item.dates.getUniqueConstraint().getName());
		assertEquals("element", item.dates.getElement().getName());

		assertEquals(list(item.strings.getParent(), item.strings.getOrder()), item.strings.getUniqueConstraint().getFields());
		assertEquals(list(item.dates.getParent(), item.dates.getOrder()), item.dates.getUniqueConstraint().getFields());

		assertTrue(item.strings.getRelationType().isAssignableFrom(item.strings.getRelationType()));
		assertTrue(!item.strings.getRelationType().isAssignableFrom(item.dates.getRelationType()));
		assertTrue(!item.TYPE.isAssignableFrom(item.strings.getRelationType()));
		assertTrue(!item.strings.getRelationType().isAssignableFrom(item.TYPE));

		// test persistence
		// test searching
		final Query<AttributeListItem> q = item.TYPE.newQuery();
		q.join(item.strings.getRelationType(), item.strings.getParent().equalTarget());
		assertEquals(list(), q.search());
		
		q.setCondition(item.strings.getElement().equal("zack"));
		assertEquals(list(), q.search());
		
		q.setCondition(item.dates.getElement().equal(new Date()));
		try
		{
			q.search();
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals(
					"function AttributeListItem.dates.element " +
					"belongs to type AttributeListItem.dates, " +
					"which is not a type of the query: " +
					"AttributeListItem, " +
					"[inner join AttributeListItem.strings on AttributeListItem.strings.parent=AttributeListItem.this]",
					e.getMessage());
		}

		// strings
		assertEquals(list(), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item.getDistinctParentsOfStrings("hallo"));
		assertContains(item.getDistinctParentsOfStrings("bello"));
		assertEquals(0, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(listg("hallo", "bello"));
		assertEquals(list("hallo", "bello"), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item, item.getDistinctParentsOfStrings("hallo"));
		assertContains(item, item.getDistinctParentsOfStrings("bello"));
		assertContains(item.getDistinctParentsOfStrings("zack1"));
		assertEquals(2, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(listg("zack1", "zack2", "zack3"));
		assertEquals(list("zack1", "zack2", "zack3"), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item, item.getDistinctParentsOfStrings("zack1"));
		assertContains(item, item.getDistinctParentsOfStrings("zack2"));
		assertContains(item, item.getDistinctParentsOfStrings("zack3"));
		assertContains(item.getDistinctParentsOfStrings("zackx"));
		assertEquals(3, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(listg("null1", null, "null3", "null4"));
		assertEquals(list("null1", null, "null3", "null4"), item.getStrings());
		assertContains(item, item.getDistinctParentsOfStrings(null));
		assertContains(item, item.getDistinctParentsOfStrings("null1"));
		assertContains(      item.getDistinctParentsOfStrings("null2"));
		assertContains(item, item.getDistinctParentsOfStrings("null3"));
		assertContains(item, item.getDistinctParentsOfStrings("null4"));
		assertEquals(4, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(listg("dup1", "dup2", "dup1"));
		assertEquals(list("dup1", "dup2", "dup1"), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item, item.getDistinctParentsOfStrings("dup1"));
		assertContains(item, item.getDistinctParentsOfStrings("dup2"));
		assertContains(      item.getDistinctParentsOfStrings("dup3"));
		assertEquals(3, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(CopeAssert.<String>listg());
		assertEquals(list(), item.getStrings());
		assertContains(item.getDistinctParentsOfStrings(null));
		assertContains(item.getDistinctParentsOfStrings("null1"));
		assertEquals(0, item.strings.getRelationType().newQuery(null).search().size());

		// dates
		assertEquals(list(), item.getDates());
		assertContains(item.getDistinctParentsOfDates(date1));
		assertContains(item.getDistinctParentsOfDates(date2));
		assertEquals(0, item.dates.getRelationType().newQuery(null).search().size());

		item.setDates(listg(date1, date2));
		assertEquals(list(date1, date2), item.getDates());
		assertContains(item, item.getDistinctParentsOfDates(date1));
		assertContains(item, item.getDistinctParentsOfDates(date2));
		assertEquals(2, item.dates.getRelationType().newQuery(null).search().size());

		try
		{
			item.setDates(listg(date1, null, date2));
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.dates.getElement(), e.getFeature());
		}
		assertEquals(list(date1), item.getDates()); // TODO should be list(date1, date2)
		assertContains(item, item.getDistinctParentsOfDates(date1));
		assertContains(item.getDistinctParentsOfDates(date2)); // TODO should contain item
		assertEquals(1, item.dates.getRelationType().newQuery(null).search().size());
	}
	
}

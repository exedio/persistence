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
import com.exedio.cope.Main;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.junit.CopeAssert;

public class AttributeListTest extends AbstractLibTest
{
	
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

		assertEquals("AttributeListItem.dates", item.dates.getRelationType().getID());
		assertEquals(null, item.dates.getRelationType().getJavaClass());
		assertEquals(null, item.dates.getRelationType().getSupertype());
		assertEquals(list(), item.dates.getRelationType().getSubTypes());

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

		// test persistence
		assertEquals(list(), item.getStrings());
		assertEquals(0, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(listg("hallo", "bello"));
		assertEquals(list("hallo", "bello"), item.getStrings());
		assertEquals(2, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(listg("zack1", "zack2", "zack3"));
		assertEquals(list("zack1", "zack2", "zack3"), item.getStrings());
		assertEquals(3, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(listg("null1", null, "null3", "null4"));
		assertEquals(list("null1", null, "null3", "null4"), item.getStrings());
		assertEquals(4, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(CopeAssert.<String>listg());
		assertEquals(list(), item.getStrings());
		assertEquals(0, item.strings.getRelationType().newQuery(null).search().size());

		assertEquals(list(), item.getDates());
		assertEquals(0, item.dates.getRelationType().newQuery(null).search().size());

		final Date date1 = new Date(918756915152l);
		final Date date2 = new Date(918756915153l);
		item.setDates(listg(date1, date2));
		assertEquals(list(date1, date2), item.getDates());
		assertEquals(2, item.dates.getRelationType().newQuery(null).search().size());

		try
		{
			item.setDates(listg(date1, null, date2));
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertEquals(item.dates.getElement(), e.getMandatoryAttribute());
		}
		assertEquals(list(date1), item.getDates()); // TODO should be list(date1, date2)
		assertEquals(1, item.dates.getRelationType().newQuery(null).search().size());
	}
	
}

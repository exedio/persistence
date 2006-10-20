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
import com.exedio.cope.junit.CopeAssert;

public class AttributeSetTest extends AbstractLibTest
{
	
	public AttributeSetTest()
	{
		super(Main.attributeSetModel);
	}

	FieldSetItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new FieldSetItem());
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
				item.strings.getElement(),
				item.strings.getUniqueConstraint()
			), item.strings.getRelationType().getFeatures());
		assertEquals(list(
				item.dates.getRelationType().getThis(),
				item.dates.getParent(),
				item.dates.getElement(),
				item.dates.getUniqueConstraint()
			), item.dates.getRelationType().getFeatures());

		assertEquals(item.TYPE, item.strings.getType());
		assertEquals("strings", item.strings.getName());
		assertEquals(item.TYPE, item.dates.getType());
		assertEquals("dates", item.dates.getName());

		assertEquals("AttributeSetItem.strings", item.strings.getRelationType().getID());
		assertEquals(null, item.dates.getRelationType().getJavaClass());
		assertEquals(null, item.strings.getRelationType().getSupertype());
		assertEquals(list(), item.strings.getRelationType().getSubTypes());
		assertEquals(false, item.strings.getRelationType().isAbstract());
		assertEquals(Item.class, item.strings.getRelationType().getThis().getValueClass());
		assertEquals(item.strings.getRelationType(), item.strings.getRelationType().getThis().getValueType());

		assertEquals("AttributeSetItem.dates", item.dates.getRelationType().getID());
		assertEquals(null, item.dates.getRelationType().getJavaClass());
		assertEquals(null, item.dates.getRelationType().getSupertype());
		assertEquals(list(), item.dates.getRelationType().getSubTypes());
		assertEquals(false, item.dates.getRelationType().isAbstract());
		assertEquals(Item.class, item.dates.getRelationType().getThis().getValueClass());
		assertEquals(item.dates.getRelationType(), item.dates.getRelationType().getThis().getValueType());

		assertEquals(item.strings.getRelationType(), item.strings.getParent().getType());
		assertEquals(item.strings.getRelationType(), item.strings.getElement().getType());
		assertEquals(item.strings.getRelationType(), item.strings.getUniqueConstraint().getType());
		assertEquals(item.dates.getRelationType(), item.dates.getParent().getType());
		assertEquals(item.dates.getRelationType(), item.dates.getElement().getType());
		assertEquals(item.dates.getRelationType(), item.dates.getUniqueConstraint().getType());

		assertEquals("parent", item.strings.getParent().getName());
		assertEquals("element", item.strings.getElement().getName());
		assertEquals("uniqueConstraint", item.strings.getUniqueConstraint().getName());
		assertEquals("parent", item.dates.getParent().getName());
		assertEquals("element", item.dates.getElement().getName());
		assertEquals("uniqueConstraint", item.dates.getUniqueConstraint().getName());

		assertEquals(list(item.strings.getParent(), item.strings.getElement()), item.strings.getUniqueConstraint().getFields());
		assertEquals(list(item.dates.getParent(), item.dates.getElement()), item.dates.getUniqueConstraint().getFields());

		assertTrue(item.strings.getRelationType().isAssignableFrom(item.strings.getRelationType()));
		assertTrue(!item.strings.getRelationType().isAssignableFrom(item.dates.getRelationType()));
		assertTrue(!item.TYPE.isAssignableFrom(item.strings.getRelationType()));
		assertTrue(!item.strings.getRelationType().isAssignableFrom(item.TYPE));

		// test persistence
		assertContains(item.getStrings());
		assertEquals(0, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(listg("hallo", "bello"));
		assertContains("hallo", "bello", item.getStrings());
		assertEquals(2, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(listg("zack1", "zack2", "zack3"));
		assertContains("zack1", "zack2", "zack3", item.getStrings());
		assertEquals(3, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(listg("null1", null, "null3", "null4"));
		assertContains("null1", null, "null3", "null4", item.getStrings());
		assertEquals(4, item.strings.getRelationType().newQuery(null).search().size());

		item.setStrings(CopeAssert.<String>listg());
		assertContains(item.getStrings());
		assertEquals(0, item.strings.getRelationType().newQuery(null).search().size());

		assertContains(item.getDates());
		assertEquals(0, item.dates.getRelationType().newQuery(null).search().size());

		final Date date1 = new Date(918756915152l);
		final Date date2 = new Date(918756915153l);
		item.setDates(listg(date1, date2));
		assertContains(date1, date2, item.getDates());
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
		assertContains(date1, item.getDates()); // TODO should be list(date1, date2)
		assertEquals(1, item.dates.getRelationType().newQuery(null).search().size());
	}
	
}

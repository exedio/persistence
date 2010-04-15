/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope;

import static com.exedio.cope.CompareConditionItem.TYPE;

import java.util.ArrayList;
import java.util.List;

public class RandomTest extends AbstractRuntimeTest
{
	public RandomTest()
	{
		super(CompareConditionTest.MODEL);
	}
	
	CompareConditionItem item1, item2, item3, item4, item5, itemX;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = deleteOnTearDown(new CompareConditionItem("string1", 1, 11l, 2.1, null, null, null));
		item2 = deleteOnTearDown(new CompareConditionItem("string2", 2, 12l, 2.2, null, null, null));
		item3 = deleteOnTearDown(new CompareConditionItem("string3", 3, 13l, 2.3, null, null, null));
		item4 = deleteOnTearDown(new CompareConditionItem("string4", 4, 14l, 2.4, null, null, null));
		item5 = deleteOnTearDown(new CompareConditionItem("string5", 5, 15l, 2.5, null, null, null));
	}
	
	public void testModel()
	{
		// test equals/hashCode
		assertEquals(TYPE.random(5), TYPE.random(5));
		assertNotEquals(TYPE.random(5), new Random(CompareFunctionConditionItem.TYPE, 5));
		assertNotEquals(TYPE.random(5), TYPE.random(6));
		
		// test toString
		assertEquals("CompareConditionItem.rand(5)", TYPE.random(5).toString());
		
		try
		{
			new Random(null, 5);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("type", e.getMessage());
		}
		
		// start new transaction, otherwise query cache will not work,
		// because type is invalidated.
		restartTransaction();
		
		{
			final Query<Double> q = new Query<Double>(TYPE.random(5));
			q.setOrderBy(TYPE.getThis(), true);
			assertEquals("select rand(5) from CompareConditionItem order by this", q.toString());
			if(mysql)
			{
				final List<Long> expected = listg(
						406135974830l,
						874543935874l,
						154311785618l,
						147927151199l,
						276700429876l);
				assertEquals(expected, toLong(q.search()));
				assertEquals(expected, toLong(q.search()));
				model.clearCache();
				assertEquals(expected, toLong(q.search()));
			}
			q.setSelect(TYPE.random(6));
			assertEquals("select rand(6) from CompareConditionItem order by this", q.toString());
			if(mysql)
			{
				final List<Long> expected = listg(
						656319084257l,
						125276374747l,
						657424651698l,
						911294164984l,
						584196900561l);
				assertEquals(expected, toLong(q.search()));
				assertEquals(expected, toLong(q.search()));
				model.clearCache();
				assertEquals(expected, toLong(q.search()));
			}
		}
		{
			final Query<CompareConditionItem> q = TYPE.newQuery();
			q.setOrderBy(TYPE.random(6), true);
			assertEquals("select this from CompareConditionItem order by rand(6)", q.toString());
			if(mysql)
			{
				final List expected = list(item2, item5, item1, item3, item4);
				assertEquals(expected, q.search());
				assertEquals(expected, q.search());
				model.clearCache();
				assertEquals(expected, q.search());
			}
		}
	}
	
	private static final List<Long> toLong(final List<Double> l)
	{
		final ArrayList<Long> result = new ArrayList<Long>(l.size());
		for(final Double d : l)
			result.add(Math.round(Math.floor(d.doubleValue()*1000000000000d)));
		return result;
	}
}

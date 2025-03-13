/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.RuntimeTester.assertFieldsCovered;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RandomTest extends TestWithEnvironment
{
	public RandomTest()
	{
		super(CompareConditionTest.MODEL);
	}

	CompareConditionItem item1, item2, item3, item4, item5;
	List<Long> expected5, expected6;
	List<?> expected6Sort;

	@BeforeEach final void setUp()
	{
		item1 = new CompareConditionItem("string1", 1, 11l, 2.1, null, null, null);
		item2 = new CompareConditionItem("string2", 2, 12l, 2.2, null, null, null);
		item3 = new CompareConditionItem("string3", 3, 13l, 2.3, null, null, null);
		item4 = new CompareConditionItem("string4", 4, 14l, 2.4, null, null, null);
		item5 = new CompareConditionItem("string5", 5, 15l, 2.5, null, null, null);
		expected5 = asList(
				406135974830l,
				874543935874l,
				154311785618l,
				147927151199l,
				276700429876l);
		expected6 = asList(
				656319084257l,
				125276374747l,
				657424651698l,
				911294164984l,
				584196900561l);
		expected6Sort = list(item2, item5, item1, item3, item4);
	}

	@Test void testModel()
	{
		// test acceptFieldsCovered
		assertFieldsCovered(asList(), TYPE.random(5));
		// test equals/hashCode
		assertEqualsAndHash(TYPE.random(5), TYPE.random(5));
		assertNotEqualsAndHash(
				TYPE.random(5),
				new Random(CompareFunctionConditionItem.TYPE, 5),
				TYPE.random(6));

		// test toString
		assertEquals("CompareConditionItem.rand(5)", TYPE.random(5).toString());

		assertFails(
				() -> new Random(null, 5),
				NullPointerException.class,
				"type");
		{
			final Condition c = TYPE.random(5).is(6.6);
			assertFails(
					() -> new CheckConstraint(c),
					IllegalArgumentException.class,
					"check constraint condition contains unsupported function: " + TYPE.random(5));
		}

		// start new transaction, otherwise query cache will not work,
		// because type is invalidated.
		restartTransaction();

		{
			final Query<Double> q = new Query<>(TYPE.random(5));
			q.setOrderBy(TYPE.getThis(), true);
			assertEquals("select rand(5) from CompareConditionItem order by this", q.toString());
			if(model.supportsRandom())
			{
				assertEquals(expected5, toLong(q.search()));
				assertEquals(expected5, toLong(q.search()));
				model.clearCache();
				assertEquals(expected5, toLong(q.search()));
				restartTransaction();
				model.clearCache();
				assertEquals(expected5, toLong(q.search()));
			}
			else
				assertNotSupported(q);

			q.setSelect(TYPE.random(6));
			assertEquals("select rand(6) from CompareConditionItem order by this", q.toString());
			if(model.supportsRandom())
			{
				assertEquals(expected6, toLong(q.search()));
				assertEquals(expected6, toLong(q.search()));
				model.clearCache();
				assertEquals(expected6, toLong(q.search()));
				restartTransaction();
				model.clearCache();
				assertEquals(expected6, toLong(q.search()));
			}
			else
				assertNotSupported(q);
		}
		{
			final Query<CompareConditionItem> q = TYPE.newQuery();
			q.setOrderBy(TYPE.random(6), true);
			assertEquals("select this from CompareConditionItem order by rand(6)", q.toString());
			if(model.supportsRandom())
			{
				assertEquals(expected6Sort, q.search());
				assertEquals(expected6Sort, q.search());
				model.clearCache();
				assertEquals(expected6Sort, q.search());
				restartTransaction();
				model.clearCache();
				assertEquals(expected6Sort, q.search());
			}
			else
				assertNotSupported(q);
		}

		assertSeed(Integer.MIN_VALUE);
		assertSeed(Integer.MIN_VALUE+1);
		assertSeed(Integer.MIN_VALUE+2);
		assertSeed(Integer.MIN_VALUE+3);
		assertSeed(-4);
		assertSeed(-3);
		assertSeed(-2);
		assertSeed(-1);
		assertSeed( 0);
		assertSeed(+1);
		assertSeed(+2);
		assertSeed(+3);
		assertSeed(+4);
		assertSeed(Integer.MAX_VALUE-3);
		assertSeed(Integer.MAX_VALUE-2);
		assertSeed(Integer.MAX_VALUE-1);
		assertSeed(Integer.MAX_VALUE);
	}

	private static List<Long> toLong(final List<Double> l)
	{
		final ArrayList<Long> result = new ArrayList<>(l.size());
		for(final Double d : l)
			result.add(Math.round(Math.floor(d*1000000000000d)));
		return result;
	}

	private void assertSeed(final int seed)
	{
		final Query<Double> q = new Query<>(TYPE.random(seed));
		q.setOrderBy(TYPE.getThis(), true);
		assertEquals("select rand(" + seed + ") from CompareConditionItem order by this", q.toString());
		if(!model.supportsRandom())
		{
			assertNotSupported(q);
			return;
		}

		final List<Double> result = q.search();
		//System.out.println("random " + result + " seed " + seed);
		for(final Double d : result)
		{
			assertNotNull(d);
			assertTrue(0.0<=d, String.valueOf(d));
			assertTrue(d<=1.0, String.valueOf(d));
		}
		final HashSet<Double> set = new HashSet<>(result);
		assertEquals(result.size(), set.size(), result.toString());

		assertEquals(result, q.search());

		model.clearCache();
		assertEquals(result, q.search());

		restartTransaction();
		model.clearCache();
		assertEquals(result, q.search());
	}

	private static void assertNotSupported(final Query<?> q)
	{
		assertFails(
				q::search,
				IllegalArgumentException.class,
				"random not supported by this dialect");
	}
}

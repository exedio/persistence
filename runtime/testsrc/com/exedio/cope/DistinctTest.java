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

import static com.exedio.cope.PlusIntegerItem.TYPE;
import static com.exedio.cope.PlusIntegerItem.numA;
import static com.exedio.cope.PlusIntegerItem.numB;
import static com.exedio.cope.Query.newQuery;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Query.Mode;
import com.exedio.cope.tojunit.SI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DistinctTest extends TestWithEnvironment
{
	public DistinctTest()
	{
		super(PlusIntegerTest.MODEL);
	}

	PlusIntegerItem item1, item2, item5;

	@BeforeEach final void setUp()
	{
		item1 = new PlusIntegerItem(1, 2, 0);
		item2 = new PlusIntegerItem(1, 3, 0);
		        new PlusIntegerItem(1, 4, 0);
		        new PlusIntegerItem(1, 4, 0);
		item5 = new PlusIntegerItem(2, 4, 0);
	}

	@Test void testDistinctSingle()
	{
		final Query<Integer> q = new Query<>(numB, TYPE, null);
		assertEquals("select numB from PlusIntegerItem", q.toString(true, Mode.SEARCH));
		assertEquals("select count(*) from PlusIntegerItem", q.toString(true, Mode.TOTAL));
		assertEquals("select exists( select count(*) from PlusIntegerItem )", q.toString(true, Mode.EXISTS));
		assertEquals("SELECT " + SI.col(numB) + " FROM " + SI.tab(TYPE), SchemaInfo.search(q));
		assertEquals("SELECT COUNT(*) FROM " + SI.tab(TYPE), SchemaInfo.total(q));
		assertEquals(
				switch(dialect) {
					case hsqldb     -> "SELECT COUNT(*) FROM (SELECT " + SI.col(numB) + " FROM " + SI.tab(TYPE) + " LIMIT 1)";
					case mysql      -> "SELECT EXISTS " +   "(SELECT " + SI.col(numB) + " FROM " + SI.tab(TYPE) +         ") AS cope_exists";
					case postgresql -> "SELECT COUNT(*) FROM (SELECT " + SI.col(numB) + " FROM " + SI.tab(TYPE) + " LIMIT 1) AS cope_exists";
				},
				SchemaInfo.exists(q));

		assertContains(2, 3, 4, 4, 4, q.search());
		assertEquals(5, q.total());
		assertTrue(q.exists());

		q.setDistinct(true);
		assertEquals("select distinct numB from PlusIntegerItem", q.toString(true, Mode.SEARCH));
		assertEquals("select distinct count(*) from PlusIntegerItem", q.toString(true, Mode.TOTAL)); // TODO bug: must mention numB, causes wrong hits in query cache
		assertEquals("select exists( select count(*) from PlusIntegerItem )", q.toString(true, Mode.EXISTS));
		assertEquals("SELECT DISTINCT " + SI.col(numB) + " FROM " + SI.tab(TYPE), SchemaInfo.search(q));
		assertEquals("SELECT COUNT(*) FROM ( SELECT DISTINCT " + SI.col(numB) + " FROM " + SI.tab(TYPE) + " )" + (hsqldb?"":" AS cope_total_distinct"), SchemaInfo.total(q));
		assertEquals(
				switch(dialect) {
					case hsqldb     -> "SELECT COUNT(*) FROM (SELECT " + SI.col(numB) + " FROM " + SI.tab(TYPE) + " LIMIT 1)";
					case mysql      -> "SELECT EXISTS " +   "(SELECT " + SI.col(numB) + " FROM " + SI.tab(TYPE) +         ") AS cope_exists";
					case postgresql -> "SELECT COUNT(*) FROM (SELECT " + SI.col(numB) + " FROM " + SI.tab(TYPE) + " LIMIT 1) AS cope_exists";
				},
				SchemaInfo.exists(q));

		assertContains(2, 3, 4, q.search());
		assertEquals(3, q.total());
		assertTrue(q.exists());

		item1.setNumB(null);
		q.setDistinct(false);
		assertContains(null, 3, 4, 4, 4, q.search());
		assertEquals(5, q.total());
		assertTrue(q.exists());

		q.setDistinct(true);
		assertContains(null, 3, 4, q.search());
		assertEquals(3, q.total());
		assertTrue(q.exists());

		item5.setNumB(null);
		q.setDistinct(false);
		assertContains(null, 3, 4, 4, null, q.search());
		assertEquals(5, q.total());
		assertTrue(q.exists());

		q.setDistinct(true);
		assertContains(null, 3, 4, q.search());
		assertEquals(3, q.total());
		assertTrue(q.exists());

		item2.setNumB(null);
		q.setDistinct(false);
		assertContains(null, null, 4, 4, null, q.search());
		assertEquals(5, q.total());
		assertTrue(q.exists());

		q.setDistinct(true);
		assertContains(null, 4, q.search());
		assertEquals(2, q.total());
		assertTrue(q.exists());
	}

	@Test void testDistinctMulti()
	{
		final Query<List<Object>> q = newQuery(new Function<?>[]{numA, numB}, TYPE, null);
		assertContains(
				list(1, 2),
				list(1, 3),
				list(1, 4),
				list(1, 4),
				list(2, 4),
			q.search());
		assertEquals(5, q.total());
		assertTrue(q.exists());

		q.setDistinct(true);
		assertContains(
				list(1, 2),
				list(1, 3),
				list(1, 4),
				list(2, 4),
			q.search());
		assertEquals(4, q.total());
		assertTrue(q.exists());

		item1.setNumA(null);
		q.setDistinct(false);
		assertContains(
				list(null, 2),
				list(1, 3),
				list(1, 4),
				list(1, 4),
				list(2, 4),
			q.search());
		assertEquals(5, q.total());
		assertTrue(q.exists());

		q.setDistinct(true);
		assertContains(
				list(null, 2),
				list(1, 3),
				list(1, 4),
				list(2, 4),
			q.search());
		assertEquals(4, q.total());
		assertTrue(q.exists());

		item5.setNumA(null);
		q.setDistinct(false);
		assertContains(
				list(null, 2),
				list(1, 3),
				list(1, 4),
				list(1, 4),
				list(null, 4),
			q.search());
		assertEquals(5, q.total());
		assertTrue(q.exists());

		q.setDistinct(true);
		assertContains(
				list(null, 2),
				list(1, 3),
				list(1, 4),
				list(null, 4),
			q.search());
		assertEquals(4, q.total());
		assertTrue(q.exists());

		item1.setNumB(null);
		q.setDistinct(false);
		assertContains(
				list(null, null),
				list(1, 3),
				list(1, 4),
				list(1, 4),
				list(null, 4),
			q.search());
		assertEquals(5, q.total());
		assertTrue(q.exists());

		q.setDistinct(true);
		assertContains(
				list(null, null),
				list(1, 3),
				list(1, 4),
				list(null, 4),
			q.search());
		assertEquals(4, q.total());
		assertTrue(q.exists());

		item5.setNumB(null);
		q.setDistinct(false);
		assertContains(
				list(null, null),
				list(1, 3),
				list(1, 4),
				list(1, 4),
				list(null, null),
			q.search());
		assertEquals(5, q.total());
		assertTrue(q.exists());

		q.setDistinct(true);
		assertContains(
				list(null, null),
				list(1, 3),
				list(1, 4),
			q.search());
		assertEquals(3, q.total());
		assertTrue(q.exists());
	}

	@Test void testDistinctDuplicateColumns()
	{
		final Query<List<Object>> q = newQuery(new Function<?>[]{numA, numA}, TYPE, null);
		assertContains(
				list(1, 1),
				list(1, 1),
				list(1, 1),
				list(1, 1),
				list(2, 2),
			q.search());
		assertEquals(5, q.total());
		assertTrue(q.exists());

		q.setDistinct(true);
		assertContains(
				list(1, 1),
				list(2, 2),
			q.search());

		// Triggers special handling for total together with distinct
		// which may cause problems.
		// On MySQL the error message read:
		//    Duplicate column name 'numA'
		assertEquals(2, q.total());
		assertTrue(q.exists());
	}
}

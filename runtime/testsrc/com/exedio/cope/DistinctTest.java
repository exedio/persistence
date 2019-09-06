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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class DistinctTest extends TestWithEnvironment
{
	public DistinctTest()
	{
		super(PlusIntegerTest.MODEL);
	}

	PlusIntegerItem item1, item5;

	@BeforeEach final void setUp()
	{
		item1 = new PlusIntegerItem(1, 2, 0);
		        new PlusIntegerItem(1, 3, 0);
		        new PlusIntegerItem(1, 4, 0);
		        new PlusIntegerItem(1, 4, 0);
		item5 = new PlusIntegerItem(2, 4, 0);
	}

	@Test void testDistinctSingle()
	{
		final Query<Integer> q = new Query<>(numB, TYPE, null);
		assertContains(2, 3, 4, 4, 4, q.search());
		assertEquals(5, q.total());
		assertTrue(q.exists());

		q.setDistinct(true);
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

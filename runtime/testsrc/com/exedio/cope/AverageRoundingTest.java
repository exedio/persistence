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

import static com.exedio.cope.CompareConditionItem.doublex;
import static com.exedio.cope.CompareConditionItem.intx;
import static com.exedio.cope.CompareConditionItem.longx;
import static com.exedio.cope.CompareConditionTest.MODEL;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AverageRoundingTest extends TestWithEnvironment
{
	public AverageRoundingTest()
	{
		super(MODEL);
	}

	@Test public void testHalf()
	{
		create(11, 21l, 31.1);
		create(12, 22l, 31.2);
		assertIt(
				11, // 11.5
				21, // 21.5
				31.15);
	}

	@Test public void testBelowHalf()
	{
		create(11, 21l, 31.1);
		create(11, 21l, 31.1);
		create(12, 22l, 31.2);
		assertIt(
				11, // 11.33
				21, // 21.33
				31.1333333);
	}

	@Test public void testAboveHalf()
	{
		create(11, 21l, 31.1);
		create(12, 22l, 31.2);
		create(12, 22l, 31.2);
		assertIt(
				11, // 11.66
				21, // 21.66
				31.1666666);
	}

	@Test public void testExact()
	{
		create(11, 21l, 31.1);
		create(11, 21l, 31.1);
		assertIt(
				11, // 11
				21, // 21
				31.1);
	}


	private static final void create(final int intx, final long longx, final double doublex)
	{
		new CompareConditionItem(null, intx, longx, doublex, null, null, null);
	}

	private static final void assertIt(
			final int expectedInt,
			final long expectedLong,
			final double expectedDouble)
	{
		assertEquals("int",    expectedInt,    new Query<>(intx   .average()).searchSingleton().intValue());
		assertEquals("long",   expectedLong,   new Query<>(longx  .average()).searchSingleton().longValue());
		assertEquals("double", expectedDouble, new Query<>(doublex.average()).searchSingleton().doubleValue(), 0.0000005);
	}
}

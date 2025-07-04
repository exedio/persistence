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

import static com.exedio.cope.DivideDoubleItem.TYPE;
import static com.exedio.cope.DivideDoubleItem.divideAB;
import static com.exedio.cope.DivideDoubleItem.divideAC;
import static com.exedio.cope.DivideDoubleItem.divideBC;
import static com.exedio.cope.DivideDoubleItem.numA;
import static com.exedio.cope.DivideDoubleItem.numB;
import static com.exedio.cope.DivideDoubleItem.numC;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DivideDoubleTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(DivideDoubleTest.class, "MODEL");
	}

	public DivideDoubleTest()
	{
		super(MODEL);
	}

	DivideDoubleItem item;

	@BeforeEach final void setUp()
	{
		item = new DivideDoubleItem(8.5, 2.5, 4.0);
		       new DivideDoubleItem(30.3, 4.4, 5.5);
	}

	private static final double EPS = 0.000000000000005d;
	private static final Double nA = Double.valueOf(8.5);
	private static final Double nB = Double.valueOf(2.5);
	private static final Double nC = Double.valueOf(4.0);
	private static final Double dAB = Double.valueOf(3.4);
	private static final Double dAC = Double.valueOf(2.125);
	private static final Double dBC = Double.valueOf(0.625);

	@Test void testSum()
	{
		// test model
		assertEquals(TYPE, divideAB.getType());
		assertEquals(TYPE, divideAC.getType());
		assertEquals(TYPE, divideBC.getType());
		assertEquals("divideAB", divideAB.getName());
		assertEquals("divideAC", divideAC.getName());
		assertEquals("divideBC", divideBC.getName());
		assertEqualsUnmodifiable(list(numA, numB), divideAB.getSources());
		assertEqualsUnmodifiable(list(numA, numC), divideAC.getSources());
		assertEqualsUnmodifiable(list(numB, numC), divideBC.getSources());

		// test equals/hashCode
		assertEqualsAndHash(divideAB, numA.divide(numB));
		assertNotEqualsAndHash(
				divideAB,
				numA.plus(numB),
				numA.multiply(numB),
				numB.divide(numA));

		// serialization
		assertSerializedSame(divideAB, 382);
		assertSerializedSame(divideAC, 382);
		assertSerializedSame(divideBC, 382);

		// exceptions
		try
		{
			DivideView.divide(null, numA);
		}
		catch(final NullPointerException e)
		{
			assertEquals("Cannot invoke \"com.exedio.cope.Function.getValueClass()\" because \"dividend\" is null", e.getMessage());
		}
		try
		{
			DivideView.divide(numA, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("sources[1]", e.getMessage());
		}

		// test normal operation
		assertEquals(nA, item.getNumA());
		assertEquals(nB, item.getNumB());
		assertEquals(nC, item.getNumC());
		assertContains(item, TYPE.search(numA.is(nA)));
		assertContains(item, TYPE.search(numB.is(nB)));
		assertContains(item, TYPE.search(numC.is(nC)));

		assertEquals(dAB, item.getDivideAB());
		assertEquals(dAC, item.getDivideAC());
		assertEquals(dBC, item.getDivideBC());
		assertContains(item, TYPE.search(divideAB.is(dAB)));
		assertContains(item, TYPE.search(divideAC.between(dAC-EPS, dAC+EPS)));
		assertContains(item, TYPE.search(divideBC.is(dBC)));

		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(nB, item.getNumB());
		assertEquals(nC, item.getNumC());
		assertContains(item, TYPE.search(numA.is((Double)null)));
		assertContains(item, TYPE.search(numB.is(nB)));
		assertContains(item, TYPE.search(numC.is(nC)));

		assertEquals(null, item.getDivideAB());
		assertEquals(null, item.getDivideAC());
		assertEquals(dBC, item.getDivideBC());
		assertContains(item, TYPE.search(divideAB.is((Double)null)));
		assertContains(item, TYPE.search(divideAC.is((Double)null)));
		assertContains(item, TYPE.search(divideBC.is(dBC)));
		assertContains(item, TYPE.search(numA.divide(numB).is((Double)null)));
	}
}

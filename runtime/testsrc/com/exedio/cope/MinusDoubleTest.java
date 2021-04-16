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

import static com.exedio.cope.MinusDoubleItem.TYPE;
import static com.exedio.cope.MinusDoubleItem.numA;
import static com.exedio.cope.MinusDoubleItem.numB;
import static com.exedio.cope.MinusDoubleItem.numC;
import static com.exedio.cope.MinusDoubleItem.viewAB;
import static com.exedio.cope.MinusDoubleItem.viewAC;
import static com.exedio.cope.MinusDoubleItem.viewBC;
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

public class MinusDoubleTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(MinusDoubleTest.class, "MODEL");
	}

	public MinusDoubleTest()
	{
		super(MODEL);
	}

	MinusDoubleItem item;

	@BeforeEach final void setUp()
	{
		item = new MinusDoubleItem(8.5, 2.5, 4.0);
		       new MinusDoubleItem(30.3, 4.4, 5.5);
	}

	private static final double EPS = 0.000000000000005d;
	private static final Double nA = Double.valueOf(8.5);
	private static final Double nB = Double.valueOf(2.5);
	private static final Double nC = Double.valueOf(4.0);
	private static final Double dAB = Double.valueOf(6.0);
	private static final Double dAC = Double.valueOf(4.5);
	private static final Double dBC = Double.valueOf(-1.5);

	@Test void testSum()
	{
		// test model
		assertEquals(TYPE, viewAB.getType());
		assertEquals(TYPE, viewAC.getType());
		assertEquals(TYPE, viewBC.getType());
		assertEquals("viewAB", viewAB.getName());
		assertEquals("viewAC", viewAC.getName());
		assertEquals("viewBC", viewBC.getName());
		assertEqualsUnmodifiable(list(numA, numB), viewAB.getSources());
		assertEqualsUnmodifiable(list(numA, numC), viewAC.getSources());
		assertEqualsUnmodifiable(list(numB, numC), viewBC.getSources());

		// test equals/hashCode
		assertEqualsAndHash(viewAB, numA.minus(numB));
		assertNotEqualsAndHash(
				viewAB,
				numA.plus(numB),
				numA.multiply(numB),
				numB.minus(numA));

		// serialization
		assertSerializedSame(viewAB, 378);
		assertSerializedSame(viewAC, 378);
		assertSerializedSame(viewBC, 378);

		// exceptions
		try
		{
			MinusView.minus(null, numA);
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			MinusView.minus(numA, null);
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
		assertContains(item, TYPE.search(numA.equal(nA)));
		assertContains(item, TYPE.search(numB.equal(nB)));
		assertContains(item, TYPE.search(numC.equal(nC)));

		assertEquals(dAB, item.getViewAB());
		assertEquals(dAC, item.getViewAC());
		assertEquals(dBC, item.getViewBC());
		assertContains(item, TYPE.search(viewAB.equal(dAB)));
		assertContains(item, TYPE.search(viewAC.equal(dAC)));
		assertContains(item, TYPE.search(viewBC.between(dBC-EPS,dBC+EPS)));

		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(nB, item.getNumB());
		assertEquals(nC, item.getNumC());
		assertContains(item, TYPE.search(numA.equal((Double)null)));
		assertContains(item, TYPE.search(numB.equal(nB)));
		assertContains(item, TYPE.search(numC.equal(nC)));

		assertEquals(null, item.getViewAB());
		assertEquals(null, item.getViewAC());
		assertEquals(dBC, item.getViewBC());
		assertContains(item, TYPE.search(viewAB.equal((Double)null)));
		assertContains(item, TYPE.search(viewAC.equal((Double)null)));
		assertContains(item, TYPE.search(viewBC.between(dBC-EPS,dBC+EPS)));
		assertContains(item, TYPE.search(numA.divide(numB).equal((Double)null)));
	}
}

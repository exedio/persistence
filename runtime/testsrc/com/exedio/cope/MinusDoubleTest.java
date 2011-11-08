/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.MinusDoubleItem.divideAB;
import static com.exedio.cope.MinusDoubleItem.divideAC;
import static com.exedio.cope.MinusDoubleItem.divideBC;
import static com.exedio.cope.MinusDoubleItem.numA;
import static com.exedio.cope.MinusDoubleItem.numB;
import static com.exedio.cope.MinusDoubleItem.numC;

public class MinusDoubleTest extends AbstractRuntimeTest
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
	MinusDoubleItem item2;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new MinusDoubleItem(8.5, 2.5, 4.0));
		item2 = deleteOnTearDown(new MinusDoubleItem(30.3, 4.4, 5.5));
	}

	private static final double EPS = 0.000000000000005d;
	private static final Double nA = Double.valueOf(8.5);
	private static final Double nB = Double.valueOf(2.5);
	private static final Double nC = Double.valueOf(4.0);
	private static final Double dAB = Double.valueOf(6.0);
	private static final Double dAC = Double.valueOf(4.5);
	private static final Double dBC = Double.valueOf(-1.5);

	public void testSum()
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
		assertEquals(divideAB, divideAB);
		assertEquals(divideAB, numA.minus(numB));
		assertNotEquals(divideAB, numA.plus(numB));
		assertNotEquals(divideAB, numA.multiply(numB));
		assertNotEquals(divideAB, numB.minus(numA));

		// serialization
		assertSerializedSame(divideAB, 380);
		assertSerializedSame(divideAC, 380);
		assertSerializedSame(divideBC, 380);

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

		assertEquals(dAB, item.getDivideAB());
		assertEquals(dAC, item.getDivideAC());
		assertEquals(dBC, item.getDivideBC());
		assertContains(item, TYPE.search(divideAB.equal(dAB)));
		assertContains(item, TYPE.search(divideAC.equal(dAC)));
		assertContains(item, TYPE.search(divideBC.between(dBC-EPS,dBC+EPS)));

		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(nB, item.getNumB());
		assertEquals(nC, item.getNumC());
		assertContains(item, TYPE.search(numA.equal((Double)null)));
		assertContains(item, TYPE.search(numB.equal(nB)));
		assertContains(item, TYPE.search(numC.equal(nC)));

		assertEquals(null, item.getDivideAB());
		assertEquals(null, item.getDivideAC());
		assertEquals(dBC, item.getDivideBC());
		assertContains(item, TYPE.search(divideAB.equal((Double)null)));
		assertContains(item, TYPE.search(divideAC.equal((Double)null)));
		assertContains(item, TYPE.search(divideBC.between(dBC-EPS,dBC+EPS)));
		assertContains(item, TYPE.search(numA.divide(numB).equal((Double)null)));
	}
}

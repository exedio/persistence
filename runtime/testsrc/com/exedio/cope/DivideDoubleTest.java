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

import static com.exedio.cope.DivideDoubleItem.TYPE;
import static com.exedio.cope.DivideDoubleItem.divideAB;
import static com.exedio.cope.DivideDoubleItem.divideAC;
import static com.exedio.cope.DivideDoubleItem.divideBC;
import static com.exedio.cope.DivideDoubleItem.numA;
import static com.exedio.cope.DivideDoubleItem.numB;
import static com.exedio.cope.DivideDoubleItem.numC;

public class DivideDoubleTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(TYPE);
	
	public DivideDoubleTest()
	{
		super(MODEL);
	}
	
	DivideDoubleItem item;
	DivideDoubleItem item2;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new DivideDoubleItem(8.5, 2.5, 4.0));
		item2 = deleteOnTearDown(new DivideDoubleItem(30.3, 4.4, 5.5));
	}
	
	private static final double EPS = 0.000000000000005d;
	private static final Double nA = Double.valueOf(8.5);
	private static final Double nB = Double.valueOf(2.5);
	private static final Double nC = Double.valueOf(4.0);
	private static final Double dAB = Double.valueOf(3.4);
	private static final Double dAC = Double.valueOf(2.125);
	private static final Double dBC = Double.valueOf(0.625);
	
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
		assertEquals(divideAB, numA.divide(numB));
		assertNotEquals(divideAB, numA.plus(numB));
		assertNotEquals(divideAB, numA.multiply(numB));
		assertNotEquals(divideAB, numB.divide(numA));
		
		// exceptions
		try
		{
			new DivideView<Double>(null, numA);
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			new DivideView<Double>(numA, null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("divisor", e.getMessage());
		}

		// test normal operation
		assertEquals(nA, item.getNumA());
		assertEquals(nB, item.getNumB());
		assertEquals(nC, item.getNumC());
		assertContains(item, TYPE.search(numA.equal(8.5)));
		assertContains(item, TYPE.search(numB.equal(2.5)));
		assertContains(item, TYPE.search(numC.equal(4.0)));

		assertEquals(dAB, item.getDivideAB());
		assertEquals(dAC, item.getDivideAC());
		assertEquals(dBC, item.getDivideBC());
		assertContains(item, TYPE.search(divideAB.equal(3.4)));
		assertContains(item, TYPE.search(divideAC.between(2.125-EPS, 2.125+EPS)));
		assertContains(item, TYPE.search(divideBC.equal(0.625)));
		
		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(nB, item.getNumB());
		assertEquals(nC, item.getNumC());
		assertContains(item, TYPE.search(numA.equal((Double)null)));
		assertContains(item, TYPE.search(numB.equal(2.5)));
		assertContains(item, TYPE.search(numC.equal(4.0)));

		assertEquals(null, item.getDivideAB());
		assertEquals(null, item.getDivideAC());
		assertEquals(dBC, item.getDivideBC());
		assertContains(item, TYPE.search(divideAB.equal((Double)null)));
		assertContains(item, TYPE.search(divideAC.equal((Double)null)));
		assertContains(item, TYPE.search(divideBC.equal(0.625)));
		assertContains(item, TYPE.search(numA.divide(numB).equal((Double)null)));
	}
}

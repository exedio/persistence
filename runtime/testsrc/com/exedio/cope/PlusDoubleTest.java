/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

public class PlusDoubleTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(PlusDoubleItem.TYPE);
	
	public PlusDoubleTest()
	{
		super(MODEL);
	}
	
	PlusDoubleItem item;
	PlusDoubleItem item2;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new PlusDoubleItem(1.1, 2.2, 3.3));
		item2 = deleteOnTearDown(new PlusDoubleItem(3.3, 4.4, 5.5));
	}
	
	private static final double EPS = 0.000000000000001d;
	private static final Double d6x = Double.valueOf(d2.doubleValue() * d3.doubleValue());
	
	public void testSum()
	{
		// test model
		assertEquals(item.TYPE, item.plusAB.getType());
		assertEquals(item.TYPE, item.plusAC.getType());
		assertEquals(item.TYPE, item.plusBC.getType());
		assertEquals(item.TYPE, item.plusABC.getType());
		assertEquals(item.TYPE, item.plusABaC.getType());
		assertEquals(item.TYPE, item.multiplyBC.getType());
		assertEquals("plusAB", item.plusAB.getName());
		assertEquals("plusAC", item.plusAC.getName());
		assertEquals("plusBC", item.plusBC.getName());
		assertEquals("plusABC", item.plusABC.getName());
		assertEquals("plusABaC", item.plusABaC.getName());
		assertEquals("multiplyBC", item.multiplyBC.getName());
		assertEqualsUnmodifiable(list(item.numA, item.numB), item.plusAB.getSources());
		assertEqualsUnmodifiable(list(item.numA, item.numC), item.plusAC.getSources());
		assertEqualsUnmodifiable(list(item.numB, item.numC), item.plusBC.getSources());
		assertEqualsUnmodifiable(list(item.numA, item.numB, item.numC), item.plusABC.getSources());
		assertEqualsUnmodifiable(list(item.plusAB, item.numC), item.plusABaC.getSources());
		assertEqualsUnmodifiable(list(item.numB, item.numC), item.multiplyBC.getSources());
		
		// test equals/hashCode
		assertEquals(item.plusAB, item.plusAB);
		assertEquals(item.plusAB, item.numA.plus(item.numB));
		assertNotEquals(item.plusAB, item.numB.plus(item.numA));
		assertNotEquals(item.plusAB, item.plusBC);
		assertNotEquals(item.plusAB, item.numA);
		assertNotEquals(item.plusBC, item.multiplyBC);

		// test normal operation
		assertEquals(d1, item.getNumA());
		assertEquals(d2, item.getNumB());
		assertEquals(d3, item.getNumC());
		assertContains(item, item.TYPE.search(item.numA.equal(1.1)));
		assertContains(item, item.TYPE.search(item.numB.equal(2.2)));
		assertContains(item, item.TYPE.search(item.numC.equal(3.3)));

		assertEquals(d3.doubleValue(), item.getPlusAB().doubleValue(), EPS);
		assertEquals(d4, item.getPlusAC());
		assertEquals(d5, item.getPlusBC());
		assertEquals(d6, item.getPlusABC());
		assertEquals(d6x,item.getMultiplyBC());
		assertContains(item, item.TYPE.search(item.plusAB.between(3.3-EPS, 3.3+EPS)));
		assertContains(item, item.TYPE.search(item.plusAC.equal(4.4)));
		assertContains(item, item.TYPE.search(item.plusBC.equal(5.5)));
		assertContains(item, item.TYPE.search(item.plusABC.equal(6.6)));
		assertContains(item, item.TYPE.search(item.plusABaC.equal(6.6)));
		assertContains(item, item.TYPE.search(item.multiplyBC.equal(d6x)));
		assertContains(item, item.TYPE.search(item.numA.plus(item.numB).between(3.3-EPS, 3.3+EPS)));
		assertContains(item, item.TYPE.search(item.numB.multiply(item.numC).equal(d6x)));
		
		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(d2, item.getNumB());
		assertEquals(d3, item.getNumC());
		assertContains(item, item.TYPE.search(item.numA.equal((Double)null)));
		assertContains(item, item.TYPE.search(item.numB.equal(2.2)));
		assertContains(item, item.TYPE.search(item.numC.equal(3.3)));

		assertEquals(null, item.getPlusAB());
		assertEquals(null, item.getPlusAC());
		assertEquals(d5, item.getPlusBC());
		assertEquals(null, item.getPlusABC());
		assertContains(item, item.TYPE.search(item.plusAB.equal((Double)null)));
		assertContains(item, item.TYPE.search(item.plusAC.equal((Double)null)));
		assertContains(item, item.TYPE.search(item.plusBC.equal(5.5)));
		assertContains(item, item.TYPE.search(item.plusABC.equal((Double)null)));
		assertContains(item, item.TYPE.search(item.plusABaC.equal((Double)null)));
		assertContains(item, item.TYPE.search(item.numA.multiply(item.numB).equal((Double)null)));
	}
}

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

public class PlusLongTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(PlusLongItem.TYPE);
	
	public PlusLongTest()
	{
		super(MODEL);
	}
	
	PlusLongItem item;
	PlusLongItem item2;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new PlusLongItem(1l, 2l, 3l));
		item2 = deleteOnTearDown(new PlusLongItem(3l, 4l, 5l));
	}
	
	public void testSum()
	{
		// test model
		assertEquals(item.TYPE, item.plusA9.getType());
		assertEquals(item.TYPE, item.plusAB.getType());
		assertEquals(item.TYPE, item.plusAC.getType());
		assertEquals(item.TYPE, item.plusBC.getType());
		assertEquals(item.TYPE, item.plusABC.getType());
		assertEquals(item.TYPE, item.plusABaC.getType());
		assertEquals(item.TYPE, item.multiplyB9.getType());
		assertEquals(item.TYPE, item.multiplyBC.getType());
		assertEquals("plusA9", item.plusA9.getName());
		assertEquals("plusAB", item.plusAB.getName());
		assertEquals("plusAC", item.plusAC.getName());
		assertEquals("plusBC", item.plusBC.getName());
		assertEquals("plusABC", item.plusABC.getName());
		assertEquals("plusABaC", item.plusABaC.getName());
		assertEquals("multiplyB9", item.multiplyB9.getName());
		assertEquals("multiplyBC", item.multiplyBC.getName());
		assertEqualsUnmodifiable(list(item.numA), item.plusA9.getSources());
		assertEqualsUnmodifiable(list(item.numA, item.numB), item.plusAB.getSources());
		assertEqualsUnmodifiable(list(item.numA, item.numC), item.plusAC.getSources());
		assertEqualsUnmodifiable(list(item.numB, item.numC), item.plusBC.getSources());
		assertEqualsUnmodifiable(list(item.numA, item.numB, item.numC), item.plusABC.getSources());
		assertEqualsUnmodifiable(list(item.plusAB, item.numC), item.plusABaC.getSources());
		assertEqualsUnmodifiable(list(item.numB), item.multiplyB9.getSources());
		assertEqualsUnmodifiable(list(item.numB, item.numC), item.multiplyBC.getSources());
		
		// test equals/hashCode
		assertEquals(item.plusA9, item.plusA9);
		assertEquals(item.plusAB, item.plusAB);
		assertEquals(item.plusAB, item.numA.plus(item.numB));
		assertEquals(item.multiplyB9, item.multiplyB9);
		assertNotEquals(item.plusAB, item.numB.plus(item.numA));
		assertNotEquals(item.plusAB, item.plusBC);
		assertNotEquals(item.plusAB, item.numA);
		assertNotEquals(item.plusBC, item.multiplyBC);

		// test normal operation
		assertEquals(l1, item.getNumA());
		assertEquals(l2, item.getNumB());
		assertEquals(l3, item.getNumC());
		assertContains(item, item.TYPE.search(item.numA.equal(1l)));
		assertContains(item, item.TYPE.search(item.numB.equal(2l)));
		assertContains(item, item.TYPE.search(item.numC.equal(3l)));

		assertEquals(l10,item.getPlusA9());
		assertEquals(l3, item.getPlusAB());
		assertEquals(l4, item.getPlusAC());
		assertEquals(l5, item.getPlusBC());
		assertEquals(l6, item.getPlusABC());
		assertEquals(l18,item.getMultiplyB9());
		assertEquals(l6, item.getMultiplyBC());
		assertContains(item, item.TYPE.search(item.plusA9.equal(10l)));
		assertContains(item, item.TYPE.search(item.plusAB.equal(3l)));
		assertContains(item, item.TYPE.search(item.plusAC.equal(4l)));
		assertContains(item, item.TYPE.search(item.plusBC.equal(5l)));
		assertContains(item, item.TYPE.search(item.plusABC.equal(6l)));
		assertContains(item, item.TYPE.search(item.plusABaC.equal(6l)));
		assertContains(item, item.TYPE.search(item.multiplyB9.equal(18l)));
		assertContains(item, item.TYPE.search(item.multiplyBC.equal(6l)));
		assertContains(item, item.TYPE.search(item.numA.plus(9l       ).equal(10l)));
		assertContains(item, item.TYPE.search(item.numA.plus(item.numB).equal(3l)));
		assertContains(item, item.TYPE.search(item.numB.multiply(9l        ).equal(18l)));
		assertContains(item, item.TYPE.search(item.numB.multiply(item.numC).equal(6l)));
		
		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(l2, item.getNumB());
		assertEquals(l3, item.getNumC());
		assertContains(item, item.TYPE.search(item.numA.equal((Long)null)));
		assertContains(item, item.TYPE.search(item.numB.equal(2l)));
		assertContains(item, item.TYPE.search(item.numC.equal(3l)));

		assertEquals(null, item.getPlusA9());
		assertEquals(null, item.getPlusAB());
		assertEquals(null, item.getPlusAC());
		assertEquals(l5, item.getPlusBC());
		assertEquals(null, item.getPlusABC());
		assertContains(item, item.TYPE.search(item.plusA9.equal((Long)null)));
		assertContains(item, item.TYPE.search(item.plusAB.equal((Long)null)));
		assertContains(item, item.TYPE.search(item.plusAC.equal((Long)null)));
		assertContains(item, item.TYPE.search(item.plusBC.equal(5l)));
		assertContains(item, item.TYPE.search(item.plusABC.equal((Long)null)));
		assertContains(item, item.TYPE.search(item.plusABaC.equal((Long)null)));
		assertContains(item, item.TYPE.search(item.numA.multiply(9l       ).equal((Long)null)));
		assertContains(item, item.TYPE.search(item.numA.multiply(item.numB).equal((Long)null)));
	}
}

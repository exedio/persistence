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

import static com.exedio.cope.PlusLongItem.TYPE;
import static com.exedio.cope.PlusLongItem.multiplyB9;
import static com.exedio.cope.PlusLongItem.multiplyBC;
import static com.exedio.cope.PlusLongItem.numA;
import static com.exedio.cope.PlusLongItem.numB;
import static com.exedio.cope.PlusLongItem.numC;
import static com.exedio.cope.PlusLongItem.plusA9;
import static com.exedio.cope.PlusLongItem.plusAB;
import static com.exedio.cope.PlusLongItem.plusABC;
import static com.exedio.cope.PlusLongItem.plusABaC;
import static com.exedio.cope.PlusLongItem.plusAC;
import static com.exedio.cope.PlusLongItem.plusBC;

public class PlusLongTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(TYPE);
	
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
		assertEquals(TYPE, plusA9.getType());
		assertEquals(TYPE, plusAB.getType());
		assertEquals(TYPE, plusAC.getType());
		assertEquals(TYPE, plusBC.getType());
		assertEquals(TYPE, plusABC.getType());
		assertEquals(TYPE, plusABaC.getType());
		assertEquals(TYPE, multiplyB9.getType());
		assertEquals(TYPE, multiplyBC.getType());
		assertEquals("plusA9", plusA9.getName());
		assertEquals("plusAB", plusAB.getName());
		assertEquals("plusAC", plusAC.getName());
		assertEquals("plusBC", plusBC.getName());
		assertEquals("plusABC", plusABC.getName());
		assertEquals("plusABaC", plusABaC.getName());
		assertEquals("multiplyB9", multiplyB9.getName());
		assertEquals("multiplyBC", multiplyBC.getName());
		assertEqualsUnmodifiable(list(numA), plusA9.getSources());
		assertEqualsUnmodifiable(list(numA, numB), plusAB.getSources());
		assertEqualsUnmodifiable(list(numA, numC), plusAC.getSources());
		assertEqualsUnmodifiable(list(numB, numC), plusBC.getSources());
		assertEqualsUnmodifiable(list(numA, numB, numC), plusABC.getSources());
		assertEqualsUnmodifiable(list(plusAB, numC), plusABaC.getSources());
		assertEqualsUnmodifiable(list(numB), multiplyB9.getSources());
		assertEqualsUnmodifiable(list(numB, numC), multiplyBC.getSources());
		
		// test equals/hashCode
		assertEquals(plusA9, plusA9);
		assertEquals(plusAB, plusAB);
		assertEquals(plusAB, numA.plus(numB));
		assertEquals(multiplyB9, multiplyB9);
		assertNotEquals(plusAB, numB.plus(numA));
		assertNotEquals(plusAB, plusBC);
		assertNotEquals(plusAB, numA);
		assertNotEquals(plusBC, multiplyBC);

		// test normal operation
		assertEquals(l1, item.getNumA());
		assertEquals(l2, item.getNumB());
		assertEquals(l3, item.getNumC());
		assertContains(item, TYPE.search(numA.equal(1l)));
		assertContains(item, TYPE.search(numB.equal(2l)));
		assertContains(item, TYPE.search(numC.equal(3l)));

		assertEquals(l10,item.getPlusA9());
		assertEquals(l3, item.getPlusAB());
		assertEquals(l4, item.getPlusAC());
		assertEquals(l5, item.getPlusBC());
		assertEquals(l6, item.getPlusABC());
		assertEquals(l18,item.getMultiplyB9());
		assertEquals(l6, item.getMultiplyBC());
		assertContains(item, TYPE.search(plusA9.equal(10l)));
		assertContains(item, TYPE.search(plusAB.equal(3l)));
		assertContains(item, TYPE.search(plusAC.equal(4l)));
		assertContains(item, TYPE.search(plusBC.equal(5l)));
		assertContains(item, TYPE.search(plusABC.equal(6l)));
		assertContains(item, TYPE.search(plusABaC.equal(6l)));
		assertContains(item, TYPE.search(multiplyB9.equal(18l)));
		assertContains(item, TYPE.search(multiplyBC.equal(6l)));
		assertContains(item, TYPE.search(numA.plus(9l  ).equal(10l)));
		assertContains(item, TYPE.search(numA.plus(numB).equal(3l)));
		assertContains(item, TYPE.search(numB.multiply(9l  ).equal(18l)));
		assertContains(item, TYPE.search(numB.multiply(numC).equal(6l)));
		
		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(l2, item.getNumB());
		assertEquals(l3, item.getNumC());
		assertContains(item, TYPE.search(numA.equal((Long)null)));
		assertContains(item, TYPE.search(numB.equal(2l)));
		assertContains(item, TYPE.search(numC.equal(3l)));

		assertEquals(null, item.getPlusA9());
		assertEquals(null, item.getPlusAB());
		assertEquals(null, item.getPlusAC());
		assertEquals(l5, item.getPlusBC());
		assertEquals(null, item.getPlusABC());
		assertContains(item, TYPE.search(plusA9.equal((Long)null)));
		assertContains(item, TYPE.search(plusAB.equal((Long)null)));
		assertContains(item, TYPE.search(plusAC.equal((Long)null)));
		assertContains(item, TYPE.search(plusBC.equal(5l)));
		assertContains(item, TYPE.search(plusABC.equal((Long)null)));
		assertContains(item, TYPE.search(plusABaC.equal((Long)null)));
		assertContains(item, TYPE.search(numA.multiply(9l  ).equal((Long)null)));
		assertContains(item, TYPE.search(numA.multiply(numB).equal((Long)null)));
	}
}

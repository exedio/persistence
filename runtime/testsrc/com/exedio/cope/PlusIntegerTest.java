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

import static com.exedio.cope.PlusIntegerItem.TYPE;
import static com.exedio.cope.PlusIntegerItem.multiplyB9;
import static com.exedio.cope.PlusIntegerItem.multiplyBC;
import static com.exedio.cope.PlusIntegerItem.numA;
import static com.exedio.cope.PlusIntegerItem.numB;
import static com.exedio.cope.PlusIntegerItem.numC;
import static com.exedio.cope.PlusIntegerItem.plusA9;
import static com.exedio.cope.PlusIntegerItem.plusAB;
import static com.exedio.cope.PlusIntegerItem.plusABC;
import static com.exedio.cope.PlusIntegerItem.plusABaC;
import static com.exedio.cope.PlusIntegerItem.plusAC;
import static com.exedio.cope.PlusIntegerItem.plusBC;

public class PlusIntegerTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(TYPE);
	
	public PlusIntegerTest()
	{
		super(MODEL);
	}
	
	PlusIntegerItem item;
	PlusIntegerItem item2;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new PlusIntegerItem(1, 2, 3));
		item2 = deleteOnTearDown(new PlusIntegerItem(3, 4, 5));
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
		assertEquals(i1, item.getNumA());
		assertEquals(i2, item.getNumB());
		assertEquals(i3, item.getNumC());
		assertContains(item, TYPE.search(numA.equal(1)));
		assertContains(item, TYPE.search(numB.equal(2)));
		assertContains(item, TYPE.search(numC.equal(3)));

		assertEquals(i10,item.getPlusA9());
		assertEquals(i3, item.getPlusAB());
		assertEquals(i4, item.getPlusAC());
		assertEquals(i5, item.getPlusBC());
		assertEquals(i6, item.getPlusABC());
		assertEquals(i18,item.getMultiplyB9());
		assertEquals(i6, item.getMultiplyBC());
		assertContains(item, TYPE.search(plusA9.equal(10)));
		assertContains(item, TYPE.search(plusAB.equal(3)));
		assertContains(item, TYPE.search(plusAC.equal(4)));
		assertContains(item, TYPE.search(plusBC.equal(5)));
		assertContains(item, TYPE.search(plusABC.equal(6)));
		assertContains(item, TYPE.search(plusABaC.equal(6)));
		assertContains(item, TYPE.search(multiplyB9.equal(18)));
		assertContains(item, TYPE.search(multiplyBC.equal(6)));
		assertContains(item, TYPE.search(numA.plus(9   ).equal(10)));
		assertContains(item, TYPE.search(numA.plus(numB).equal(3)));
		assertContains(item, TYPE.search(numB.multiply(9   ).equal(18)));
		assertContains(item, TYPE.search(numB.multiply(numC).equal(6)));
		
		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(i2, item.getNumB());
		assertEquals(i3, item.getNumC());
		assertContains(item, TYPE.search(numA.equal((Integer)null)));
		assertContains(item, TYPE.search(numB.equal(2)));
		assertContains(item, TYPE.search(numC.equal(3)));

		assertEquals(null, item.getPlusA9());
		assertEquals(null, item.getPlusAB());
		assertEquals(null, item.getPlusAC());
		assertEquals(i5, item.getPlusBC());
		assertEquals(null, item.getPlusABC());
		assertContains(item, TYPE.search(plusA9.equal((Integer)null)));
		assertContains(item, TYPE.search(plusAB.equal((Integer)null)));
		assertContains(item, TYPE.search(plusAC.equal((Integer)null)));
		assertContains(item, TYPE.search(plusBC.equal(5)));
		assertContains(item, TYPE.search(plusABC.equal((Integer)null)));
		assertContains(item, TYPE.search(plusABaC.equal((Integer)null)));
		assertContains(item, TYPE.search(numA.multiply(9   ).equal((Integer)null)));
		assertContains(item, TYPE.search(numA.multiply(numB).equal((Integer)null)));
	}
}

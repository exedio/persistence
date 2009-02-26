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

import static com.exedio.cope.DivideIntegerItem.TYPE;
import static com.exedio.cope.DivideIntegerItem.divideAB;
import static com.exedio.cope.DivideIntegerItem.divideAC;
import static com.exedio.cope.DivideIntegerItem.divideBC;
import static com.exedio.cope.DivideIntegerItem.numA;
import static com.exedio.cope.DivideIntegerItem.numB;
import static com.exedio.cope.DivideIntegerItem.numC;

public class DivideIntegerTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(TYPE);
	
	public DivideIntegerTest()
	{
		super(MODEL);
	}
	
	DivideIntegerItem item;
	DivideIntegerItem item2;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new DivideIntegerItem(7, 9, 3));
		item2 = deleteOnTearDown(new DivideIntegerItem(30, 4, 5));
	}
	
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

		// test normal operation
		assertEquals(i7, item.getNumA());
		assertEquals(i9, item.getNumB());
		assertEquals(i3, item.getNumC());
		assertContains(item, TYPE.search(numA.equal(7)));
		assertContains(item, TYPE.search(numB.equal(9)));
		assertContains(item, TYPE.search(numC.equal(3)));

		assertEquals(i0, item.getDivideAB());
		assertEquals(i2, item.getDivideAC());
		assertEquals(i3, item.getDivideBC());
		assertContains(item, TYPE.search(divideAB.equal(0)));
		assertContains(item, TYPE.search(divideAC.equal(2)));
		assertContains(item, TYPE.search(divideBC.equal(3)));
		
		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(i9, item.getNumB());
		assertEquals(i3, item.getNumC());
		assertContains(item, TYPE.search(numA.equal((Integer)null)));
		assertContains(item, TYPE.search(numB.equal(9)));
		assertContains(item, TYPE.search(numC.equal(3)));

		assertEquals(null, item.getDivideAB());
		assertEquals(null, item.getDivideAC());
		assertEquals(i3, item.getDivideBC());
		assertContains(item, TYPE.search(divideAB.equal((Integer)null)));
		assertContains(item, TYPE.search(divideAC.equal((Integer)null)));
		assertContains(item, TYPE.search(divideBC.equal(3)));
		assertContains(item, TYPE.search(numA.divide(numB).equal((Integer)null)));
	}
}

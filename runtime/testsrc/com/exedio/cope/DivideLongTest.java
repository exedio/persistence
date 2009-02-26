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

import static com.exedio.cope.DivideLongItem.TYPE;
import static com.exedio.cope.DivideLongItem.divideAB;
import static com.exedio.cope.DivideLongItem.divideAC;
import static com.exedio.cope.DivideLongItem.divideBC;
import static com.exedio.cope.DivideLongItem.numA;
import static com.exedio.cope.DivideLongItem.numB;
import static com.exedio.cope.DivideLongItem.numC;

public class DivideLongTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(TYPE);
	
	public DivideLongTest()
	{
		super(MODEL);
	}
	
	DivideLongItem item;
	DivideLongItem item2;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new DivideLongItem(7, 9, 3));
		item2 = deleteOnTearDown(new DivideLongItem(30, 4, 5));
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
		assertEquals(l7, item.getNumA());
		assertEquals(l9, item.getNumB());
		assertEquals(l3, item.getNumC());
		assertContains(item, TYPE.search(numA.equal(7l)));
		assertContains(item, TYPE.search(numB.equal(9l)));
		assertContains(item, TYPE.search(numC.equal(3l)));

		assertEquals(l0, item.getDivideAB());
		assertEquals(l2, item.getDivideAC());
		assertEquals(l3, item.getDivideBC());
		assertContains(item, TYPE.search(divideAB.equal(0l)));
		assertContains(item, TYPE.search(divideAC.equal(2l)));
		assertContains(item, TYPE.search(divideBC.equal(3l)));
		
		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(l9, item.getNumB());
		assertEquals(l3, item.getNumC());
		assertContains(item, TYPE.search(numA.equal((Long)null)));
		assertContains(item, TYPE.search(numB.equal(9l)));
		assertContains(item, TYPE.search(numC.equal(3l)));

		assertEquals(null, item.getDivideAB());
		assertEquals(null, item.getDivideAC());
		assertEquals(l3, item.getDivideBC());
		assertContains(item, TYPE.search(divideAB.equal((Long)null)));
		assertContains(item, TYPE.search(divideAC.equal((Long)null)));
		assertContains(item, TYPE.search(divideBC.equal(3l)));
		assertContains(item, TYPE.search(numA.divide(numB).equal((Long)null)));
	}
}

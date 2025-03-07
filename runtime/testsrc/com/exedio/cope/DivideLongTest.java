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

import static com.exedio.cope.AbstractRuntimeTest.l0;
import static com.exedio.cope.AbstractRuntimeTest.l2;
import static com.exedio.cope.AbstractRuntimeTest.l3;
import static com.exedio.cope.AbstractRuntimeTest.l7;
import static com.exedio.cope.AbstractRuntimeTest.l9;
import static com.exedio.cope.DivideLongItem.TYPE;
import static com.exedio.cope.DivideLongItem.divideAB;
import static com.exedio.cope.DivideLongItem.divideAC;
import static com.exedio.cope.DivideLongItem.divideBC;
import static com.exedio.cope.DivideLongItem.numA;
import static com.exedio.cope.DivideLongItem.numB;
import static com.exedio.cope.DivideLongItem.numC;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DivideLongTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	public DivideLongTest()
	{
		super(MODEL);
	}

	DivideLongItem item;

	@BeforeEach final void setUp()
	{
		item = new DivideLongItem(7, 9, 3);
		new DivideLongItem(30, 4, 5);
	}

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

		// test normal operation
		assertEquals(l7, item.getNumA());
		assertEquals(l9, item.getNumB());
		assertEquals(l3, item.getNumC());
		assertContains(item, TYPE.search(numA.is(7l)));
		assertContains(item, TYPE.search(numB.is(9l)));
		assertContains(item, TYPE.search(numC.is(3l)));

		assertEquals(l0, item.getDivideAB());
		assertEquals(l2, item.getDivideAC());
		assertEquals(l3, item.getDivideBC());
		assertContains(item, TYPE.search(divideAB.is(0l)));
		assertContains(item, TYPE.search(divideAC.is(2l)));
		assertContains(item, TYPE.search(divideBC.is(3l)));

		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(l9, item.getNumB());
		assertEquals(l3, item.getNumC());
		assertContains(item, TYPE.search(numA.is((Long)null)));
		assertContains(item, TYPE.search(numB.is(9l)));
		assertContains(item, TYPE.search(numC.is(3l)));

		assertEquals(null, item.getDivideAB());
		assertEquals(null, item.getDivideAC());
		assertEquals(l3, item.getDivideBC());
		assertContains(item, TYPE.search(divideAB.is((Long)null)));
		assertContains(item, TYPE.search(divideAC.is((Long)null)));
		assertContains(item, TYPE.search(divideBC.is(3l)));
		assertContains(item, TYPE.search(numA.divide(numB).is((Long)null)));
	}
}

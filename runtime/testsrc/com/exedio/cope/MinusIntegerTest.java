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

import static com.exedio.cope.MinusIntegerItem.TYPE;
import static com.exedio.cope.MinusIntegerItem.numA;
import static com.exedio.cope.MinusIntegerItem.numB;
import static com.exedio.cope.MinusIntegerItem.numC;
import static com.exedio.cope.MinusIntegerItem.viewAB;
import static com.exedio.cope.MinusIntegerItem.viewAC;
import static com.exedio.cope.MinusIntegerItem.viewBC;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.lang.Integer.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MinusIntegerTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	public MinusIntegerTest()
	{
		super(MODEL);
	}

	MinusIntegerItem item;

	@BeforeEach final void setUp()
	{
		item = new MinusIntegerItem(7, 9, 3);
		new MinusIntegerItem(30, 4, 5);
	}

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

		// test normal operation
		assertEquals(valueOf(7), item.getNumA());
		assertEquals(valueOf(9), item.getNumB());
		assertEquals(valueOf(3), item.getNumC());
		assertContains(item, TYPE.search(numA.is(7)));
		assertContains(item, TYPE.search(numB.is(9)));
		assertContains(item, TYPE.search(numC.is(3)));

		assertEquals(valueOf(-2), item.getViewAB());
		assertEquals(valueOf( 4), item.getViewAC());
		assertEquals(valueOf( 6), item.getViewBC());
		assertContains(item, TYPE.search(viewAB.is(-2)));
		assertContains(item, TYPE.search(viewAC.is( 4)));
		assertContains(item, TYPE.search(viewBC.is( 6)));

		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(valueOf(9), item.getNumB());
		assertEquals(valueOf(3), item.getNumC());
		assertContains(item, TYPE.search(numA.is((Integer)null)));
		assertContains(item, TYPE.search(numB.is(9)));
		assertContains(item, TYPE.search(numC.is(3)));

		assertEquals(null, item.getViewAB());
		assertEquals(null, item.getViewAC());
		assertEquals(valueOf(6), item.getViewBC());
		assertContains(item, TYPE.search(viewAB.is((Integer)null)));
		assertContains(item, TYPE.search(viewAC.is((Integer)null)));
		assertContains(item, TYPE.search(viewBC.is(6)));
		assertContains(item, TYPE.search(numA.divide(numB).is((Integer)null)));
	}
}

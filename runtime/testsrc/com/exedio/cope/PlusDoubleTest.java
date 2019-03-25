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

import static com.exedio.cope.AbstractRuntimeTest.d1;
import static com.exedio.cope.AbstractRuntimeTest.d2;
import static com.exedio.cope.AbstractRuntimeTest.d3;
import static com.exedio.cope.AbstractRuntimeTest.d4;
import static com.exedio.cope.AbstractRuntimeTest.d5;
import static com.exedio.cope.AbstractRuntimeTest.d6;
import static com.exedio.cope.PlusDoubleItem.TYPE;
import static com.exedio.cope.PlusDoubleItem.multiplyB9;
import static com.exedio.cope.PlusDoubleItem.multiplyBC;
import static com.exedio.cope.PlusDoubleItem.numA;
import static com.exedio.cope.PlusDoubleItem.numB;
import static com.exedio.cope.PlusDoubleItem.numC;
import static com.exedio.cope.PlusDoubleItem.plusA9;
import static com.exedio.cope.PlusDoubleItem.plusAB;
import static com.exedio.cope.PlusDoubleItem.plusABC;
import static com.exedio.cope.PlusDoubleItem.plusABaC;
import static com.exedio.cope.PlusDoubleItem.plusAC;
import static com.exedio.cope.PlusDoubleItem.plusBC;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlusDoubleTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(PlusDoubleTest.class, "MODEL");
	}

	public PlusDoubleTest()
	{
		super(MODEL);
	}

	PlusDoubleItem item;

	@BeforeEach final void setUp()
	{
		item = new PlusDoubleItem(1.1, 2.2, 3.3);
		       new PlusDoubleItem(3.3, 4.4, 5.5);
	}

	private static final double EPS = 0.000000000000005d;
	private static final Double d6x = Double.valueOf(d2 * d3);
	private static final Double d10= Double.valueOf(10.4);
	private static final Double d18= Double.valueOf(20.46);

	@Test void testSum()
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
		assertEqualsAndHash(plusA9, numA.plus(9.3));
		assertEqualsAndHash(plusAB, numA.plus(numB));
		assertEqualsAndHash(multiplyB9, numB.multiply(9.3));
		assertNotEqualsAndHash(plusAB, numB.plus(numA), plusBC);
		assertNotEqualsAndHash(plusAB, numA, plusBC, multiplyBC);

		// toString
		assertEquals("("+numB+"+5.5)", numB.plus(5.5).toString());
		assertEquals("plus("+numB+","+numA+")", numB.plus(numA).toString());
		assertEquals("("+numB+"*5.5)", numB.multiply(5.5).toString());
		assertEquals("multiply("+numB+","+numA+")", numB.multiply(numA).toString());

		// serialization
		assertSerializedSame(plusA9    , 376);
		assertSerializedSame(plusAB    , 376);
		assertSerializedSame(plusAC    , 376);
		assertSerializedSame(plusBC    , 376);
		assertSerializedSame(plusABC   , 377);
		assertSerializedSame(plusABaC  , 378);
		assertSerializedSame(multiplyBC, 380);
		assertSerializedSame(multiplyB9, 380);

		// test normal operation
		assertEquals(d1, item.getNumA());
		assertEquals(d2, item.getNumB());
		assertEquals(d3, item.getNumC());
		assertContains(item, TYPE.search(numA.equal(1.1)));
		assertContains(item, TYPE.search(numB.equal(2.2)));
		assertContains(item, TYPE.search(numC.equal(3.3)));

		assertEquals(d10,item.getPlusA9());
		assertEquals(d3, item.getPlusAB(), EPS);
		assertEquals(d4, item.getPlusAC());
		assertEquals(d5, item.getPlusBC());
		assertEquals(d6, item.getPlusABC());
		assertEquals(d18, item.getMultiplyB9(), EPS);
		assertEquals(d6x,item.getMultiplyBC());
		assertContains(item, TYPE.search(plusA9.between(10.4-EPS, 10.4+EPS)));
		assertContains(item, TYPE.search(plusAB.between(3.3-EPS, 3.3+EPS)));
		assertContains(item, TYPE.search(plusAC.equal(4.4)));
		assertContains(item, TYPE.search(plusBC.equal(5.5)));
		assertContains(item, TYPE.search(plusABC.equal(6.6)));
		assertContains(item, TYPE.search(plusABaC.equal(6.6)));
		assertContains(item, TYPE.search(multiplyB9.between(d18-EPS, d18+EPS)));
		assertContains(item, TYPE.search(multiplyBC.equal(d6x)));
		assertContains(item, TYPE.search(numA.plus(9.3 ).between(10.4-EPS, 10.4+EPS)));
		assertContains(item, TYPE.search(numA.plus(numB).between(3.3-EPS, 3.3+EPS)));
		assertContains(item, TYPE.search(numB.multiply(9.3 ).between(d18-EPS, d18+EPS)));
		assertContains(item, TYPE.search(numB.multiply(numC).equal(d6x)));

		// test null propagation
		item.setNumA(null);

		assertEquals(null, item.getNumA());
		assertEquals(d2, item.getNumB());
		assertEquals(d3, item.getNumC());
		assertContains(item, TYPE.search(numA.equal((Double)null)));
		assertContains(item, TYPE.search(numB.equal(2.2)));
		assertContains(item, TYPE.search(numC.equal(3.3)));

		assertEquals(null, item.getPlusA9());
		assertEquals(null, item.getPlusAB());
		assertEquals(null, item.getPlusAC());
		assertEquals(d5, item.getPlusBC());
		assertEquals(null, item.getPlusABC());
		assertContains(item, TYPE.search(plusA9.equal((Double)null)));
		assertContains(item, TYPE.search(plusAB.equal((Double)null)));
		assertContains(item, TYPE.search(plusAC.equal((Double)null)));
		assertContains(item, TYPE.search(plusBC.equal(5.5)));
		assertContains(item, TYPE.search(plusABC.equal((Double)null)));
		assertContains(item, TYPE.search(plusABaC.equal((Double)null)));
		assertContains(item, TYPE.search(numA.multiply(9.9 ).equal((Double)null)));
		assertContains(item, TYPE.search(numA.multiply(numB).equal((Double)null)));
	}
}

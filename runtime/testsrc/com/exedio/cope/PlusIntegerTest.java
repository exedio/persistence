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
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.lang.Integer.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlusIntegerTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	public PlusIntegerTest()
	{
		super(MODEL);
	}

	PlusIntegerItem item;

	@BeforeEach final void setUp()
	{
		item  = new PlusIntegerItem(1, 2, 3);
		        new PlusIntegerItem(3, 4, 5);
	}

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
		assertEqualsAndHash(plusA9, numA.plus(9));
		assertEqualsAndHash(plusAB, numA.plus(numB));
		assertEqualsAndHash(multiplyB9, numB.multiply(9));
		assertNotEqualsAndHash(plusAB, numB.plus(numA), plusBC, numA, multiplyBC);

		// toString
		assertEquals("("+numB+"+5)", numB.plus(5).toString());
		assertEquals("plus("+numB+","+numA+")", numB.plus(numA).toString());
		assertEquals("("+numB+"*5)", numB.multiply(5).toString());
		assertEquals("multiply("+numB+","+numA+")", numB.multiply(numA).toString());
		assertEquals("select ("+numB.getName()+"+5) from "+TYPE, new Query<>(numB.plus(5)).toString());
		assertEquals("select plus("+numB.getName()+","+numA.getName()+") from "+TYPE, new Query<>(numB.plus(numA)).toString());
		assertEquals("select ("+numB.getName()+"*5) from "+TYPE, new Query<>(numB.multiply(5)).toString());
		assertEquals("select multiply("+numB.getName()+","+numA.getName()+") from "+TYPE, new Query<>(numB.multiply(numA)).toString());

		// exceptions
		try
		{
			PlusLiteralView.plus(null, 6);
		}
		catch(final NullPointerException e)
		{
			assertEquals("Cannot invoke \"com.exedio.cope.Function.getValueClass()\" because \"left\" is null", e.getMessage());
		}
		try
		{
			PlusLiteralView.plus(numA, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("right", e.getMessage());
		}
		try
		{
			MultiplyLiteralView.multiply(null, 6);
		}
		catch(final NullPointerException e)
		{
			assertEquals("Cannot invoke \"com.exedio.cope.Function.getValueClass()\" because \"left\" is null", e.getMessage());
		}
		try
		{
			MultiplyLiteralView.multiply(numA, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("right", e.getMessage());
		}

		// test normal operation
		assertEquals(valueOf(1), item.getNumA());
		assertEquals(valueOf(2), item.getNumB());
		assertEquals(valueOf(3), item.getNumC());
		assertContains(item, TYPE.search(numA.equal(1)));
		assertContains(item, TYPE.search(numB.equal(2)));
		assertContains(item, TYPE.search(numC.equal(3)));

		assertEquals(valueOf(10), item.getPlusA9());
		assertEquals(valueOf( 3), item.getPlusAB());
		assertEquals(valueOf( 4), item.getPlusAC());
		assertEquals(valueOf( 5), item.getPlusBC());
		assertEquals(valueOf( 6), item.getPlusABC());
		assertEquals(valueOf(18), item.getMultiplyB9());
		assertEquals(valueOf( 6), item.getMultiplyBC());
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

		assertEquals(null,       item.getNumA());
		assertEquals(valueOf(2), item.getNumB());
		assertEquals(valueOf(3), item.getNumC());
		assertContains(item, TYPE.search(numA.equal((Integer)null)));
		assertContains(item, TYPE.search(numB.equal(2)));
		assertContains(item, TYPE.search(numC.equal(3)));

		assertEquals(null,       item.getPlusA9());
		assertEquals(null,       item.getPlusAB());
		assertEquals(null,       item.getPlusAC());
		assertEquals(valueOf(5), item.getPlusBC());
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

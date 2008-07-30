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
		assertEquals(item.TYPE, item.plus12.getType());
		assertEquals(item.TYPE, item.plus13.getType());
		assertEquals(item.TYPE, item.plus23.getType());
		assertEquals(item.TYPE, item.plus123.getType());
		assertEquals(item.TYPE, item.plus12a3.getType());
		assertEquals("plus12", item.plus12.getName());
		assertEquals("plus13", item.plus13.getName());
		assertEquals("plus23", item.plus23.getName());
		assertEquals("plus123", item.plus123.getName());
		assertEquals("plus12a3", item.plus12a3.getName());
		assertEqualsUnmodifiable(list(item.num1, item.num2), item.plus12.getSources());
		assertEqualsUnmodifiable(list(item.num1, item.num3), item.plus13.getSources());
		assertEqualsUnmodifiable(list(item.num2, item.num3), item.plus23.getSources());
		assertEqualsUnmodifiable(list(item.num1, item.num2, item.num3), item.plus123.getSources());
		assertEqualsUnmodifiable(list(item.plus12, item.num3), item.plus12a3.getSources());
		
		// test equals/hashCode
		assertEquals(item.plus12, item.plus12);
		assertEquals(item.plus12, item.num1.plus(item.num2));
		assertNotEquals(item.plus12, item.num2.plus(item.num1));
		assertNotEquals(item.plus12, item.plus23);
		assertNotEquals(item.plus12, item.num1);

		// test normal operation
		assertEquals(l1, item.getNum1());
		assertEquals(l2, item.getNum2());
		assertEquals(l3, item.getNum3());
		assertContains(item, item.TYPE.search(item.num1.equal(1l)));
		assertContains(item, item.TYPE.search(item.num2.equal(2l)));
		assertContains(item, item.TYPE.search(item.num3.equal(3l)));

		assertEquals(l3, item.getPlus12());
		assertEquals(l4, item.getPlus13());
		assertEquals(l5, item.getPlus23());
		assertEquals(l6, item.getPlus123());
		assertContains(item, item.TYPE.search(item.plus12.equal(3l)));
		assertContains(item, item.TYPE.search(item.plus13.equal(4l)));
		assertContains(item, item.TYPE.search(item.plus23.equal(5l)));
		assertContains(item, item.TYPE.search(item.plus123.equal(6l)));
		assertContains(item, item.TYPE.search(item.plus12a3.equal(6l)));
		assertContains(item, item.TYPE.search(item.num1.plus(item.num2).equal(3l)));
		
		// test null propagation
		item.setNum1(null);

		assertEquals(null, item.getNum1());
		assertEquals(l2, item.getNum2());
		assertEquals(l3, item.getNum3());
		assertContains(item, item.TYPE.search(item.num1.equal((Long)null)));
		assertContains(item, item.TYPE.search(item.num2.equal(2l)));
		assertContains(item, item.TYPE.search(item.num3.equal(3l)));

		assertEquals(null, item.getPlus12());
		assertEquals(null, item.getPlus13());
		assertEquals(l5, item.getPlus23());
		assertEquals(null, item.getPlus123());
		assertContains(item, item.TYPE.search(item.plus12.equal((Long)null)));
		assertContains(item, item.TYPE.search(item.plus13.equal((Long)null)));
		assertContains(item, item.TYPE.search(item.plus23.equal(5l)));
		assertContains(item, item.TYPE.search(item.plus123.equal((Long)null)));
		assertContains(item, item.TYPE.search(item.plus12a3.equal((Long)null)));
	}
}

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

public class PlusTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(PlusItem.TYPE);
	
	public PlusTest()
	{
		super(MODEL);
	}
	
	PlusItem item;
	PlusItem item2;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new PlusItem(1, 2, 3));
		item2 = deleteOnTearDown(new PlusItem(3, 4, 5));
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
		assertEquals(i1, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i3, item.getNum3());
		assertContains(item, item.TYPE.search(item.num1.equal(1)));
		assertContains(item, item.TYPE.search(item.num2.equal(2)));
		assertContains(item, item.TYPE.search(item.num3.equal(3)));

		assertEquals(i3, item.getPlus12());
		assertEquals(i4, item.getPlus13());
		assertEquals(i5, item.getPlus23());
		assertEquals(i6, item.getPlus123());
		assertContains(item, item.TYPE.search(item.plus12.equal(3)));
		assertContains(item, item.TYPE.search(item.plus13.equal(4)));
		assertContains(item, item.TYPE.search(item.plus23.equal(5)));
		assertContains(item, item.TYPE.search(item.plus123.equal(6)));
		assertContains(item, item.TYPE.search(item.plus12a3.equal(6)));
		assertContains(item, item.TYPE.search(item.num1.plus(item.num2).equal(3)));
		
		// test null propagation
		item.setNum1(null);

		assertEquals(null, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i3, item.getNum3());
		assertContains(item, item.TYPE.search(item.num1.equal((Integer)null)));
		assertContains(item, item.TYPE.search(item.num2.equal(2)));
		assertContains(item, item.TYPE.search(item.num3.equal(3)));

		assertEquals(null, item.getPlus12());
		assertEquals(null, item.getPlus13());
		assertEquals(i5, item.getPlus23());
		assertEquals(null, item.getPlus123());
		assertContains(item, item.TYPE.search(item.plus12.equal((Integer)null)));
		assertContains(item, item.TYPE.search(item.plus13.equal((Integer)null)));
		assertContains(item, item.TYPE.search(item.plus23.equal(5)));
		assertContains(item, item.TYPE.search(item.plus123.equal((Integer)null)));
		assertContains(item, item.TYPE.search(item.plus12a3.equal((Integer)null)));
	}
}

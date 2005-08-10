/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.testmodel.SumItem;


public class SumTest extends TestmodelTest
{
	SumItem item;
	SumItem item2;
	
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new SumItem(1, 2, 3));
		deleteOnTearDown(item2 = new SumItem(3, 4, 5));
	}
	
	public void testSum()
	{
		// test model
		assertEquals(item.TYPE, item.sum12.getType());
		assertEquals(item.TYPE, item.sum13.getType());
		assertEquals(item.TYPE, item.sum23.getType());
		assertEquals(item.TYPE, item.sum123.getType());
		assertEquals(item.TYPE, item.sum12a3.getType());
		assertEquals("sum12", item.sum12.getName());
		assertEquals("sum13", item.sum13.getName());
		assertEquals("sum23", item.sum23.getName());
		assertEquals("sum123", item.sum123.getName());
		assertEquals("sum12a3", item.sum12a3.getName());
		assertEquals(list(item.num1, item.num2), item.sum12.getSources());
		assertEquals(list(item.num1, item.num3), item.sum13.getSources());
		assertEquals(list(item.num2, item.num3), item.sum23.getSources());
		assertEquals(list(item.num1, item.num2, item.num3), item.sum123.getSources());
		assertEquals(list(item.sum12, item.num3), item.sum12a3.getSources());
		assertUnmodifiable(item.sum12.getSources());
		assertUnmodifiable(item.sum13.getSources());
		assertUnmodifiable(item.sum23.getSources());
		assertUnmodifiable(item.sum123.getSources());
		assertUnmodifiable(item.sum12a3.getSources());

		// test normal operation
		assertEquals(i1, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i3, item.getNum3());
		assertContains(item, item.TYPE.search(item.num1.equal(1)));
		assertContains(item, item.TYPE.search(item.num2.equal(2)));
		assertContains(item, item.TYPE.search(item.num3.equal(3)));

		assertEquals(i3, item.getSum12());
		assertEquals(i4, item.getSum13());
		assertEquals(i5, item.getSum23());
		assertEquals(i6, item.getSum123());
		assertContains(item, item.TYPE.search(item.sum12.equal(3)));
		assertContains(item, item.TYPE.search(item.sum13.equal(4)));
		assertContains(item, item.TYPE.search(item.sum23.equal(5)));
		assertContains(item, item.TYPE.search(item.sum123.equal(6)));
		assertContains(item, item.TYPE.search(item.sum12a3.equal(6)));
		assertContains(item, item.TYPE.search(item.num1.sum(item.num2).equal(3)));
		
		// test null propagation
		item.setNum1(null);

		assertEquals(null, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i3, item.getNum3());
		assertContains(item, item.TYPE.search(item.num1.equal(null)));
		assertContains(item, item.TYPE.search(item.num2.equal(2)));
		assertContains(item, item.TYPE.search(item.num3.equal(3)));

		assertEquals(null, item.getSum12());
		assertEquals(null, item.getSum13());
		assertEquals(i5, item.getSum23());
		assertEquals(null, item.getSum123());
		assertContains(item, item.TYPE.search(item.sum12.equal(null)));
		assertContains(item, item.TYPE.search(item.sum13.equal(null)));
		assertContains(item, item.TYPE.search(item.sum23.equal(5)));
		assertContains(item, item.TYPE.search(item.sum123.equal(null)));
		assertContains(item, item.TYPE.search(item.sum12a3.equal(null)));
	}

}

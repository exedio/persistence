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

package com.exedio.cope.pattern;

import java.util.Iterator;
import java.util.List;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.StringAttribute;
import com.exedio.cope.testmodel.Main;
import com.exedio.cope.testmodel.VectorItem;


public class VectorTest extends AbstractLibTest
{
	
	public VectorTest()
	{
		super(Main.vectorModel);
	}

	VectorItem item;
	
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new VectorItem(1, 2, 3));
	}
	
	public void testNums() throws ConstraintViolationException
	{
		// test model
		assertEquals(list(item.nums, item.strings), item.TYPE.getPatterns());

		assertEquals(item.TYPE, item.nums.getType());
		assertEquals("nums", item.nums.getName());
		assertEquals(list(item.num1, item.num2, item.num3), item.nums.getSources());
		assertUnmodifiable(item.nums.getSources());

		assertEquals(item.TYPE, item.strings.getType());
		assertEquals("strings", item.strings.getName());
		final List stringSources = item.strings.getSources();
		assertEquals(4, stringSources.size());
		assertUnmodifiable(stringSources);
		final Iterator stringSourcesIterator = stringSources.iterator();
		final StringAttribute string1 = assertString(stringSourcesIterator, 1);
		final StringAttribute string2 = assertString(stringSourcesIterator, 2);
		final StringAttribute string3 = assertString(stringSourcesIterator, 3);
		final StringAttribute string4 = assertString(stringSourcesIterator, 4);
		assertTrue(!stringSourcesIterator.hasNext());
		assertEquals(
				list(item.num1, item.num2, item.num3, string1, string2, string3, string4),
				item.TYPE.getDeclaredAttributes());

		assertEquals(i1, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i3, item.getNum3());

		item.setNums(list(i3, i2, i1));
		assertEquals(i3, item.getNum1());
		assertEquals(i2, item.getNum2());
		assertEquals(i1, item.getNum3());

		item.setNums(list(i2, i1));
		assertEquals(i2, item.getNum1());
		assertEquals(i1, item.getNum2());
		assertEquals(null, item.getNum3());

		item.setNums(list());
		assertEquals(null, item.getNum1());
		assertEquals(null, item.getNum2());
		assertEquals(null, item.getNum3());
		
		item.setNum1(i1);
		item.setNum2(i2);
		item.setNum3(i3);
		assertEquals(list(i1, i2, i3), item.getNums());
		
		item.setNum1(null);
		item.setNum2(i2);
		item.setNum3(i3);
		assertEquals(list(i2, i3), item.getNums());
		
		item.setNum1(i1);
		item.setNum2(null);
		item.setNum3(i3);
		assertEquals(list(i1, i3), item.getNums());
		
		item.setNum1(null);
		item.setNum2(null);
		item.setNum3(null);
		assertEquals(list(), item.getNums());
		
		item.setStrings(list("hallo", "bello"));
		assertEquals(list("hallo", "bello"), item.getStrings());
		assertEquals("hallo", item.get(string1));
		assertEquals("bello", item.get(string2));
		assertEquals(null, item.get(string3));
		assertEquals(null, item.get(string4));
	}
	
	private final StringAttribute assertString(final Iterator i, final int num)
	{
		final StringAttribute string = (StringAttribute)i.next();
		assertEquals(item.TYPE, string.getType());
		assertEquals("strings"+num, string.getName());
		assertEquals(false, string.isNotNull());
		assertEquals(false, string.isReadOnly());
		assertEquals(false, string.isLengthConstrained());
		assertEquals(0, string.getMinimumLength());
		assertEquals(Integer.MAX_VALUE, string.getMaximumLength());
		return string;
	}

}

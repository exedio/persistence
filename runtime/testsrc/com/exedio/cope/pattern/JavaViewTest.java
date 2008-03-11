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

package com.exedio.cope.pattern;

import java.util.Arrays;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;

public class JavaViewTest extends AbstractLibTest
{
	private static final Model MODEL = new Model(JavaViewItem.TYPE);
	private static final Double d2 = new Double(2.25d);
	
	public JavaViewTest()
	{
		super(MODEL);
	}
	
	JavaViewItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new JavaViewItem());
	}
	
	public void testNumber()
	{
		assertEquals(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
				item.numberString,
				item.number,
			}), item.TYPE.getDeclaredFeatures());
		assertEquals(item.TYPE.getDeclaredFeatures(), item.TYPE.getFeatures());

		assertEquals(item.TYPE, item.number.getType());
		assertEquals("number", item.number.getName());
		assertEqualsUnmodifiable(list(), item.numberString.getPatterns());
		assertEquals(Double.class, item.number.getValueType());
		assertEquals(Double.class, item.number.getValueGenericType());

		assertNull(item.getNumberString());
		assertNull(item.getNumber());
		assertNull(item.number.get(item));
		
		item.setNumberString("2.25");
		assertEquals("2.25", item.getNumberString());
		assertEquals(d2, item.getNumber());
		assertEquals(d2, item.number.get(item));
		
		item.setNumberString(null);
		assertNull(item.getNumberString());
		assertNull(item.getNumber());
		assertNull(item.number.get(item));
	}
	
}

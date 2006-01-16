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

import java.io.IOException;
import java.util.Arrays;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.ConstraintViolationException;
import com.exedio.cope.Feature;
import com.exedio.cope.Main;

public class CustomTest extends AbstractLibTest
{
	public CustomTest()
	{
		super(Main.customModel);
	}
	
	CustomItem item;
	
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new CustomItem());
	}
	
	static final Integer i20 = new Integer(20);
	static final Integer i44 = new Integer(44);
	static final Integer im2 = new Integer(-2);
	
	public void testNumber() throws ConstraintViolationException, IOException, CustomAttributeException
	{
		assertEquals(Arrays.asList(new Feature[]{
				item.numberString,
				item.number,
				item.element1,
				item.element2,
				item.element3,
				item.elements,
			}), item.TYPE.getDeclaredFeatures());
		assertEquals(item.TYPE.getDeclaredFeatures(), item.TYPE.getFeatures());

		assertEquals(item.TYPE, item.number.getType());
		assertEquals("number", item.number.getName());
		assertEqualsUnmodifiable(list(item.numberString), item.number.getStorages());
		assertEqualsUnmodifiable(list(item.number), item.numberString.getPatterns());
		assertEquals(Integer.class, item.number.getValueType());

		assertNull(item.getNumberString());
		assertNull(item.getNumber());
		assertNull(item.number.get(item));
		
		item.setNumber(i2);
		assertEquals("2", item.getNumberString());
		assertEquals(i2, item.getNumber());
		assertEquals(i2, item.number.get(item));
		
		item.setNumber(i20);
		assertEquals("20", item.getNumberString());
		assertEquals(i20, item.getNumber());
		assertEquals(i20, item.number.get(item));
		
		item.number.set(item, i44);
		assertEquals("44", item.getNumberString());
		assertEquals(i44, item.getNumber());
		assertEquals(i44, item.number.get(item));
		
		try
		{
			item.setNumber(im2);
			fail();
		}
		catch(IOException e)
		{
			assertEquals("test exception:-2", e.getMessage());
		}
		assertEquals("44", item.getNumberString());
		
		try
		{
			item.number.set(item, im2);
			fail();
		}
		catch(CustomAttributeException e)
		{
			assertSame(item.number, e.getAttribute());
			assertEquals("test exception:-2", e.getCause().getMessage());
			assertEquals(IOException.class, e.getCause().getClass());
		}
		assertEquals("44", item.getNumberString());
		
		item.setNumber(null);
		assertNull(item.getNumberString());
		assertNull(item.getNumber());
		assertNull(item.number.get(item));
		assertEquals(list(null, null, null), item.getElements());
		
		item.setElements(list(i2, i4, i8));
		assertEquals(list(i2, i4, i8), item.getElements());
	}
	
}

/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.Feature;
import com.exedio.cope.LengthViolationException;
import com.exedio.cope.Model;

public class CustomTest extends AbstractLibTest
{
	private static final Model MODEL = new Model(CustomItem.TYPE);

	public CustomTest()
	{
		super(MODEL);
	}
	
	CustomItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new CustomItem());
	}
	
	static final Integer i20 = Integer.valueOf(20);
	static final Integer i44 = Integer.valueOf(44);
	static final Integer i55 = Integer.valueOf(55);
	static final Integer i56 = Integer.valueOf(56);
	static final Integer im2 = Integer.valueOf(-2);
	
	public void testNumber() throws IOException
	{
		assertEquals(Arrays.asList(new Feature[]{
				item.TYPE.getThis(),
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
		assertEquals(Integer.class, item.number.getValueClass());
		assertEquals(false, item.number.isInitial());
		assertEquals(false, item.number.isFinal());
		assertContains(LengthViolationException.class, item.number.getSetterExceptions());

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
			assertSame(item.number, e.getFeature());
			assertSame(item.number, e.getFeature());
			assertEquals(item, e.getItem());
			assertEquals("test exception:-2", e.getCause().getMessage());
			assertEquals(IOException.class, e.getCause().getClass());
		}
		assertEquals("44", item.getNumberString());
		
		assertContains(item, item.TYPE.search(null));
		try
		{
			new CustomItem(im2);
			fail();
		}
		catch(CustomAttributeException e)
		{
			assertSame(item.number, e.getFeature());
			assertSame(item.number, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("java.io.IOException: test exception:-2", e.getMessage());
			assertEquals("test exception:-2", e.getCause().getMessage());
			assertEquals(IOException.class, e.getCause().getClass());
		}
		assertContains(item, item.TYPE.search(null));
		try
		{
			CustomItem.TYPE.newItem(item.number.map(im2));
			fail();
		}
		catch(CustomAttributeException e)
		{
			assertSame(item.number, e.getFeature());
			assertSame(item.number, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals("test exception:-2", e.getCause().getMessage());
			assertEquals(IOException.class, e.getCause().getClass());
		}
		assertContains(item, item.TYPE.search(null));
		
		item.setNumber(null);
		assertNull(item.getNumberString());
		assertNull(item.getNumber());
		assertNull(item.number.get(item));
		assertEquals(list(null, null, null), item.getElements());

		{
			final CustomItem numberItem55 = deleteOnTearDown(new CustomItem(i55));
			assertEquals("55", numberItem55.getNumberString());
			assertEquals(i55, numberItem55.getNumber());
			assertEquals(i55, numberItem55.number.get(numberItem55));
		}
		{
			final CustomItem numberItem56 =
				deleteOnTearDown(CustomItem.TYPE.newItem(CustomItem.number.map(i56)));
			assertEquals("56", numberItem56.getNumberString());
			assertEquals(i56, numberItem56.getNumber());
			assertEquals(i56, numberItem56.number.get(numberItem56));
		}
		
		assertEquals(false, item.elements.isInitial());
		assertEquals(false, item.elements.isFinal());
		assertContains(item.elements.getSetterExceptions());
		
		item.setElements(listg(i2, i4, i8));
		assertEquals(list(i2, i4, i8), item.getElements());
		assertEquals(i2, item.getElement1());
		assertEquals(i4, item.getElement2());
		assertEquals(i8, item.getElement3());
		
		{
			final CustomItem elementsItem3 = deleteOnTearDown(new CustomItem(listg(i3, i4, i5)));
			assertEquals(list(i3, i4, i5), elementsItem3.getElements());
			assertEquals(list(i3, i4, i5), elementsItem3.elements.get(elementsItem3));
			assertEquals(i3, elementsItem3.getElement1());
			assertEquals(i4, elementsItem3.getElement2());
			assertEquals(i5, elementsItem3.getElement3());
		}
		{
			final CustomItem elementsItem7 =
				deleteOnTearDown(CustomItem.TYPE.newItem(CustomItem.elements.map(listg(i7, i8, i9))));
			assertEquals(list(i7, i8, i9), elementsItem7.getElements());
			assertEquals(list(i7, i8, i9), elementsItem7.elements.get(elementsItem7));
			assertEquals(i7, elementsItem7.getElement1());
			assertEquals(i8, elementsItem7.getElement2());
			assertEquals(i9, elementsItem7.getElement3());
		}
	}
	
}

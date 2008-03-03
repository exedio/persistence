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

import java.util.Arrays;
import java.util.Iterator;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Type;

/**
 * Tests complicated situations for the setter.
 */
public class ListFieldTest2 extends AbstractLibTest
{
	public ListFieldTest2()
	{
		super(ListFieldTest.MODEL);
	}

	ListFieldItem item;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new ListFieldItem());
	}
	
	public void testIt()
	{
		final Type<?> type = item.strings.getRelationType();
		final IntegerField order = item.strings.getOrder();
		final FunctionField<String> element = item.strings.getElement();
		
		item.setStrings(Arrays.asList("0zero", "1one", "2two"));
		assertEquals(list("0zero", "1one", "2two"), item.getStrings());
		final Item r0;
		final Item r1;
		final Item r2;
		{
			final Iterator<? extends Item> i = type.search(null, order, true).iterator();
			r0 = i.next();
			r1 = i.next();
			r2 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("0zero", r0.get(element));
		assertEquals("1one",  r1.get(element));
		assertEquals("2two",  r2.get(element));
		assertEquals(0, r0.get(order).intValue());
		assertEquals(1, r1.get(order).intValue());
		assertEquals(2, r2.get(order).intValue());
		
		r1.deleteCopeItem();
		assertEquals(list("0zero", "2two"), item.getStrings());
		
		item.setStrings(Arrays.asList("0zero", "1one", "2two"));
		assertEquals(list("0zero", "1one", "2two"), item.getStrings());
		final Item r3;
		{
			final Iterator<? extends Item> i = type.search(null, order, true).iterator();
			assertEquals(r0, i.next());
			assertEquals(r2, i.next());
			r3 = i.next();
			assertFalse(i.hasNext());
		}
		assertEquals("0zero", r0.get(element));
		assertEquals("1one",  r2.get(element));
		assertEquals("2two",  r3.get(element));
		assertEquals(0, r0.get(order).intValue());
		assertEquals(2, r2.get(order).intValue());
		assertEquals(3, r3.get(order).intValue());
		assertFalse(r1.existsCopeItem());
	}
}

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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.ListFieldItem.strings;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.Type;
import java.util.Iterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests complicated situations for the setter.
 */
public class ListFieldTest2 extends TestWithEnvironment
{
	public ListFieldTest2()
	{
		super(ListFieldTest.MODEL);
	}

	ListFieldItem item;

	@BeforeEach final void setUp()
	{
		item = new ListFieldItem();
	}

	@Test void testIt()
	{
		final Type<?> type = strings.getRelationType();
		final IntegerField order = strings.getOrder();
		final FunctionField<String> element = strings.getElement();

		item.setStrings(asList("0zero", "1one", "2two"));
		assertEqualsUnmodifiable(list("0zero", "1one", "2two"), item.getStrings());
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

		r1.deleteCopeItem(); // could happen, if element was a CASCADE ItemField
		assertEquals(list("0zero", "2two"), item.getStrings());

		item.setStrings(asList("0zero", "1one", "2two"));
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

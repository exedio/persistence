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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.pattern.PartOf.orderBy;
import static com.exedio.cope.pattern.PartOf.orderByDesc;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.reserialize;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.PartOf.OrderBy;
import org.junit.jupiter.api.Test;

public class PartOfOrderByTest
{
	@Test void testCreate()
	{
		final OrderBy o = orderBy(MyItem.field);
		assertSame(MyItem.field, o.getField());
		assertEquals(true, o.isAscending());
		assertEquals(MyItem.field + " asc", o.toString());
		assertEquals(o, reserialize(o, 488));
	}

	@Test void testCreateDescending()
	{
		final OrderBy o = orderByDesc(MyItem.field);
		assertSame(MyItem.field, o.getField());
		assertEquals(false, o.isAscending());
		assertEquals(MyItem.field + " desc", o.toString());
		assertEquals(o, reserialize(o, 488));
	}

	@Test void testCreateBoolAscending()
	{
		final OrderBy o = orderBy(MyItem.field, true);
		assertSame(MyItem.field, o.getField());
		assertEquals(true, o.isAscending());
		assertEquals(MyItem.field + " asc", o.toString());
		assertEquals(o, reserialize(o, 488));
	}

	@Test void testCreateBoolDescending()
	{
		final OrderBy o = orderBy(MyItem.field, false);
		assertSame(MyItem.field, o.getField());
		assertEquals(false, o.isAscending());
		assertEquals(MyItem.field + " desc", o.toString());
		assertEquals(o, reserialize(o, 488));
	}

	@Test void testCreateNullDefault()
	{
		assertFails(
				() -> orderBy(null),
				NullPointerException.class,
				"order");
	}

	@Test void testCreateNullDescending()
	{
		assertFails(
				() -> orderByDesc(null),
				NullPointerException.class,
				"order");
	}

	@Test void testCreateNullBool()
	{
		assertFails(
				() -> orderBy(null, true),
				NullPointerException.class,
				"order");
	}

	@Test void testEquals()
	{
		assertEqualsAndHash(orderBy(MyItem.field), orderBy(MyItem.field));
		assertEqualsAndHash(orderBy(MyItem.field), orderBy(MyItem.field, true));
		assertEqualsAndHash(orderByDesc(MyItem.field), orderBy(MyItem.field, false));

		assertNotEqualsAndHash(
				orderBy(MyItem.field), orderByDesc(MyItem.field),
				orderBy(MyItem.field2), orderByDesc(MyItem.field2));
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@WrapperIgnore static final StringField field = new StringField();
		@WrapperIgnore static final StringField field2 = new StringField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE);

	static
	{
		MODEL.enableSerialization(PartOfOrderByTest.class, "MODEL");
	}
}

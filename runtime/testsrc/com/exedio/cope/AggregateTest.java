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

package com.exedio.cope;

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.serialize;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

class AggregateTest
{
	@Test void testMin()
	{
		final ExtremumAggregate<Integer> a = MyItem.field.min();
		assertSame(MyItem.field, a.getSource());
		assertEquals("min", a.getName());
		assertSame(Integer.class, a.getValueClass());
		assertSame(SimpleSelectType.INTEGER, a.getValueType());
		assertSame(MyItem.TYPE, a.getType());
		assertEquals("min(MyItem.field)", a.toString());
		assertEqualsAndHash(a, MyItem.field.min());

		assertNotEqualsAndHash(a,
				MyItem.field.max(),
				MyItem.field.sum(),
				MyItem.field.average(),
				MyItem.field.any(),
				new IntegerField().min());

		assertFails( // TODO
				() -> serialize(a),
				RuntimeException.class,
				"java.io.NotSerializableException: com.exedio.cope.SimpleSelectType");
	}

	@Test void testMax()
	{
		final ExtremumAggregate<Integer> a = MyItem.field.max();
		assertSame(MyItem.field, a.getSource());
		assertEquals("max", a.getName());
		assertSame(Integer.class, a.getValueClass());
		assertSame(SimpleSelectType.INTEGER, a.getValueType());
		assertSame(MyItem.TYPE, a.getType());
		assertEquals("max(MyItem.field)", a.toString());
		assertEqualsAndHash(a, MyItem.field.max());

		assertFails( // TODO
				() -> serialize(a),
				RuntimeException.class,
				"java.io.NotSerializableException: com.exedio.cope.SimpleSelectType");
	}

	@Test void testSum()
	{
		final SumAggregate<Integer> a = MyItem.field.sum();
		assertSame(MyItem.field, a.getSource());
		assertEquals("sum", a.getName());
		assertSame(Integer.class, a.getValueClass());
		assertSame(SimpleSelectType.INTEGER, a.getValueType());
		assertSame(MyItem.TYPE, a.getType());
		assertEquals("sum(MyItem.field)", a.toString());
		assertEqualsAndHash(a, MyItem.field.sum());

		assertFails( // TODO
				() -> serialize(a),
				RuntimeException.class,
				"java.io.NotSerializableException: com.exedio.cope.SimpleSelectType");
	}

	@Test void testAverage()
	{
		final AverageAggregate a = MyItem.field.average();
		assertSame(MyItem.field, a.getSource());
		assertEquals("avg", a.getName());
		assertSame(Double.class, a.getValueClass());
		assertSame(SimpleSelectType.DOUBLE, a.getValueType());
		assertSame(MyItem.TYPE, a.getType());
		assertEquals("avg(MyItem.field)", a.toString());
		assertEqualsAndHash(a, MyItem.field.average());

		assertFails( // TODO
				() -> serialize(a),
				RuntimeException.class,
				"java.io.NotSerializableException: com.exedio.cope.SimpleSelectType");
	}

	@Test void testAny()
	{
		final Aggregate<Integer> a = MyItem.field.any();
		assertSame(MyItem.field, a.getSource());
		assertEquals("any", a.getName());
		assertSame(Integer.class, a.getValueClass());
		assertSame(SimpleSelectType.INTEGER, a.getValueType());
		assertSame(MyItem.TYPE, a.getType());
		assertEquals("any(MyItem.field)", a.toString());
		assertEqualsAndHash(a, MyItem.field.any());

		assertFails( // TODO
				() -> serialize(a),
				RuntimeException.class,
				"java.io.NotSerializableException: com.exedio.cope.SimpleSelectType");
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		@WrapperIgnore
		static final IntegerField field = new IntegerField();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	static
	{
		MODEL.enableSerialization(AggregateTest.class, "MODEL");
	}
}

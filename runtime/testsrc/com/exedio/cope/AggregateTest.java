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

import static com.exedio.cope.RuntimeTester.assertFieldsCovered;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.reserialize;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.util.Arrays.asList;
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
		assertFieldsCovered(asList(MyItem.field), a);
		assertEquals("min(MyItem.field)", a.toString());
		assertEqualsAndHash(a, MyItem.field.min());

		assertNotEqualsAndHash(a,
				MyItem.field.max(),
				MyItem.field.sum(),
				MyItem.field.average(),
				MyItem.field.any(),
				new IntegerField().min());

		assertFails(
				() -> a.get((Item)null),
				UnsupportedGetException.class,
				"min(MyItem.field)");

		final ExtremumAggregate<Integer> as = reserialize(a, 740);
		assertSame(MyItem.field, as.getSource());
		assertEquals("min", as.getName());
		assertSame(Integer.class, as.getValueClass());
		assertSame(SimpleSelectType.INTEGER, as.getValueType());
		assertSame(MyItem.TYPE, as.getType());
		assertEquals("min(MyItem.field)", as.toString());
		assertEqualsAndHash(a, as);
	}

	@Test void testMax()
	{
		final ExtremumAggregate<Integer> a = MyItem.field.max();
		assertSame(MyItem.field, a.getSource());
		assertEquals("max", a.getName());
		assertSame(Integer.class, a.getValueClass());
		assertSame(SimpleSelectType.INTEGER, a.getValueType());
		assertSame(MyItem.TYPE, a.getType());
		assertFieldsCovered(asList(MyItem.field), a);
		assertEquals("max(MyItem.field)", a.toString());
		assertEqualsAndHash(a, MyItem.field.max());

		assertFails(
				() -> a.get((Item)null),
				UnsupportedGetException.class,
				"max(MyItem.field)");

		final ExtremumAggregate<Integer> as = reserialize(a, 740);
		assertSame(MyItem.field, as.getSource());
		assertEquals("max", as.getName());
		assertSame(Integer.class, as.getValueClass());
		assertSame(SimpleSelectType.INTEGER, as.getValueType());
		assertSame(MyItem.TYPE, as.getType());
		assertEquals("max(MyItem.field)", as.toString());
		assertEqualsAndHash(a, as);
	}

	@Test void testSum()
	{
		final SumAggregate<Integer> a = MyItem.field.sum();
		assertSame(MyItem.field, a.getSource());
		assertEquals("sum", a.getName());
		assertSame(Integer.class, a.getValueClass());
		assertSame(SimpleSelectType.INTEGER, a.getValueType());
		assertSame(MyItem.TYPE, a.getType());
		assertFieldsCovered(asList(MyItem.field), a);
		assertEquals("sum(MyItem.field)", a.toString());
		assertEqualsAndHash(a, MyItem.field.sum());

		assertFails(
				() -> a.get((Item)null),
				UnsupportedGetException.class,
				"sum(MyItem.field)");

		final SumAggregate<Integer> as = reserialize(a, 724);
		assertSame(MyItem.field, as.getSource());
		assertEquals("sum", as.getName());
		assertSame(Integer.class, as.getValueClass());
		assertSame(SimpleSelectType.INTEGER, as.getValueType());
		assertSame(MyItem.TYPE, as.getType());
		assertEquals("sum(MyItem.field)", as.toString());
		assertEqualsAndHash(a, as);
	}

	@Test void testAverage()
	{
		final AverageAggregate a = MyItem.field.average();
		assertSame(MyItem.field, a.getSource());
		assertEquals("avg", a.getName());
		assertSame(Double.class, a.getValueClass());
		assertSame(SimpleSelectType.DOUBLE, a.getValueType());
		assertSame(MyItem.TYPE, a.getType());
		assertFieldsCovered(asList(MyItem.field), a);
		assertEquals("avg(MyItem.field)", a.toString());
		assertEqualsAndHash(a, MyItem.field.average());

		assertFails(
				() -> a.get((Item)null),
				UnsupportedGetException.class,
				"avg(MyItem.field)");

		final AverageAggregate as = reserialize(a, 727);
		assertSame(MyItem.field, as.getSource());
		assertEquals("avg", as.getName());
		assertSame(Double.class, as.getValueClass());
		assertSame(SimpleSelectType.DOUBLE, as.getValueType());
		assertSame(MyItem.TYPE, as.getType());
		assertEquals("avg(MyItem.field)", as.toString());
		assertEqualsAndHash(a, as);
	}

	@Test void testAny()
	{
		final Aggregate<Integer> a = MyItem.field.any();
		assertSame(MyItem.field, a.getSource());
		assertEquals("any", a.getName());
		assertSame(Integer.class, a.getValueClass());
		assertSame(SimpleSelectType.INTEGER, a.getValueType());
		assertSame(MyItem.TYPE, a.getType());
		assertFieldsCovered(asList(MyItem.field), a);
		assertEquals("any(MyItem.field)", a.toString());
		assertEqualsAndHash(a, MyItem.field.any());

		assertFails(
				() -> a.get((Item)null),
				UnsupportedGetException.class,
				"any(MyItem.field)");

		final Aggregate<Integer> as = reserialize(a, 730);
		assertSame(MyItem.field, as.getSource());
		assertEquals("any", as.getName());
		assertSame(Integer.class, as.getValueClass());
		assertSame(SimpleSelectType.INTEGER, as.getValueType());
		assertSame(MyItem.TYPE, as.getType());
		assertEquals("any(MyItem.field)", as.toString());
		assertEqualsAndHash(a, as);
	}

	@Test void testDistinct()
	{
		final Aggregate<Integer> a = MyItem.field.distinct();
		assertSame(MyItem.field, a.getSource());
		assertEquals("distinct", a.getName());
		assertSame(Integer.class, a.getValueClass());
		assertSame(SimpleSelectType.INTEGER, a.getValueType());
		assertSame(MyItem.TYPE, a.getType());
		assertFieldsCovered(asList(MyItem.field), a);
		assertEquals("distinct(MyItem.field)", a.toString());
		assertEqualsAndHash(a, MyItem.field.distinct());

		assertFails(
				() -> a.get((Item)null),
				UnsupportedGetException.class,
				"distinct(MyItem.field)");

		final Aggregate<Integer> as = reserialize(a, 730);
		assertSame(MyItem.field, as.getSource());
		assertEquals("distinct", as.getName());
		assertSame(Integer.class, as.getValueClass());
		assertSame(SimpleSelectType.INTEGER, as.getValueType());
		assertSame(MyItem.TYPE, as.getType());
		assertEquals("distinct(MyItem.field)", as.toString());
		assertEqualsAndHash(a, as);
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@WrapperIgnore
		static final IntegerField field = new IntegerField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	static
	{
		MODEL.enableSerialization(AggregateTest.class, "MODEL");
	}
}

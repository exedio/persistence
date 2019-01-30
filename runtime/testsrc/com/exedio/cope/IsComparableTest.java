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

import static com.exedio.cope.IsComparableTest.MyItem.boolA;
import static com.exedio.cope.IsComparableTest.MyItem.boolB;
import static com.exedio.cope.IsComparableTest.MyItem.dateA;
import static com.exedio.cope.IsComparableTest.MyItem.dateB;
import static com.exedio.cope.IsComparableTest.MyItem.dayA;
import static com.exedio.cope.IsComparableTest.MyItem.dayB;
import static com.exedio.cope.IsComparableTest.MyItem.doubleA;
import static com.exedio.cope.IsComparableTest.MyItem.doubleB;
import static com.exedio.cope.IsComparableTest.MyItem.enumA;
import static com.exedio.cope.IsComparableTest.MyItem.enumB;
import static com.exedio.cope.IsComparableTest.MyItem.enumOther;
import static com.exedio.cope.IsComparableTest.MyItem.intA;
import static com.exedio.cope.IsComparableTest.MyItem.intB;
import static com.exedio.cope.IsComparableTest.MyItem.itemA;
import static com.exedio.cope.IsComparableTest.MyItem.itemB;
import static com.exedio.cope.IsComparableTest.MyItem.itemBrother;
import static com.exedio.cope.IsComparableTest.MyItem.itemOther;
import static com.exedio.cope.IsComparableTest.MyItem.itemSub;
import static com.exedio.cope.IsComparableTest.MyItem.itemSuper;
import static com.exedio.cope.IsComparableTest.MyItem.longA;
import static com.exedio.cope.IsComparableTest.MyItem.longB;
import static com.exedio.cope.IsComparableTest.MyItem.stringA;
import static com.exedio.cope.IsComparableTest.MyItem.stringB;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.Day;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class IsComparableTest
{
	@Test void testString()
	{
		assertEquals(
				stringA + "='123-45'",
				stringA.equal("123-45").toString());
		assertEquals(
				stringA + "=" + stringB,
				stringA.equal(stringB).toString());
	}
	@Test void testStringInt()
	{
		final Function<Integer> stringAInt = cast(stringA);
		assertFails(
				() -> stringAInt.equal(12345),
				IllegalArgumentException.class,
				stringA + " not comparable to '12345' (java.lang.Integer)");
		assertFails(
				() -> stringAInt.equal(intA),
				IllegalArgumentException.class,
				stringA + " not comparable to " + intA);
	}
	@Test void testStringEnum()
	{
		final Function<AnEnum> stringAEnum = cast(stringA);
		assertFails(
				() -> stringAEnum.equal(AnEnum.V4),
				IllegalArgumentException.class,
				stringA + " not comparable to 'V4' (com.exedio.cope.IsComparableTest$AnEnum$1)");
		assertFails(
				() -> stringAEnum.equal(enumA),
				IllegalArgumentException.class,
				stringA + " not comparable to " + enumA);
	}
	@Test void testStringItem()
	{
		final Function<AnItem> stringAItem = cast(stringA);
		assertFails(
				() -> stringAItem.equal(AnItem.TYPE.activate(12345)),
				IllegalArgumentException.class,
				stringA + " not comparable to 'AnItem-12345' (com.exedio.cope.IsComparableTest$AnItem)");
		assertFails(
				() -> stringAItem.equal(itemA),
				IllegalArgumentException.class,
				stringA + " not comparable to " + itemA);
	}

	@Test void testBool()
	{
		assertEquals(
				boolA + "='true'",
				boolA.equal(true).toString());
		assertEquals(
				boolA + "=" + boolB,
				boolA.equal(boolB).toString());
	}
	@Test void testBoolInt()
	{
		final Function<Integer> boolAInt = cast(boolA);
		assertFails(
				() -> boolAInt.equal(12345),
				IllegalArgumentException.class,
				boolA + " not comparable to '12345' (java.lang.Integer)");
		assertFails(
				() -> boolAInt.equal(intA),
				IllegalArgumentException.class,
				boolA + " not comparable to " + intA);
	}

	@Test void testInt()
	{
		assertEquals(
				intA + "='12345'",
				intA.equal(12345).toString());
		assertEquals(
				intA + "=" + intB,
				intA.equal(intB).toString());
	}
	@Test void testIntLong()
	{
		final Function<Long> intALong = cast(intA);
		assertFails(
				() -> intALong.less(12345l), // equal has special implementation
				IllegalArgumentException.class,
				intA + " not comparable to '12345' (java.lang.Long)");
		assertFails(
				() -> intALong.equal(longA),
				IllegalArgumentException.class,
				intA + " not comparable to " + longA);
	}

	@Test void testLong()
	{
		assertEquals(
				longA + "='12345'",
				longA.equal(12345l).toString());
		assertEquals(
				longA + "=" + longB,
				longA.equal(longB).toString());
	}
	@Test void testLongInt()
	{
		final Function<Integer> longAInt = cast(longA);
		assertFails(
				() -> longAInt.equal(12345),
				IllegalArgumentException.class,
				longA + " not comparable to '12345' (java.lang.Integer)");
		assertFails(
				() -> longAInt.equal(intA),
				IllegalArgumentException.class,
				longA + " not comparable to " + intA);
	}

	@Test void testDouble()
	{
		assertEquals(
				doubleA + "='123.45'",
				doubleA.equal(123.45).toString());
		assertEquals(
				doubleA + "=" + doubleB,
				doubleA.equal(doubleB).toString());
	}
	@Test void testDoubleInt()
	{
		final Function<Integer> doubleAInt = cast(doubleA);
		assertFails(
				() -> doubleAInt.less(12345), // equal has special implementation
				IllegalArgumentException.class,
				doubleA + " not comparable to '12345' (java.lang.Integer)");
		assertFails(
				() -> doubleAInt.equal(intA),
				IllegalArgumentException.class,
				doubleA + " not comparable to " + intA);
	}

	@Test void testDate()
	{
		assertEquals(
				dateA + "='1970-01-01 00:00:12.345'",
				dateA.equal(new Date(12345)).toString());
		assertEquals(
				dateA + "=" + dateB,
				dateA.equal(dateB).toString());
	}
	@Test void testDateInt()
	{
		final Function<Integer> dateAInt = cast(dateA);
		assertFails(
				() -> dateAInt.equal(12345),
				IllegalArgumentException.class,
				dateA + " not comparable to '12345' (java.lang.Integer)");
		assertFails(
				() -> dateAInt.equal(intA),
				IllegalArgumentException.class,
				dateA + " not comparable to " + intA);
	}
	@Test void testDateDay()
	{
		final Function<Day> dateADay = cast(dateA);
		assertFails(
				() -> dateADay.equal(new Day(8123, 4, 5)),
				IllegalArgumentException.class,
				dateA + " not comparable to '8123/4/5' (com.exedio.cope.util.Day)");
		assertFails(
				() -> dateADay.equal(dayA),
				IllegalArgumentException.class,
				dateA + " not comparable to " + dayA);
	}

	@Test void testDay()
	{
		assertEquals(
				dayA + "='8123/4/5'",
				dayA.equal(new Day(8123, 4, 5)).toString());
		assertEquals(
				dayA + "=" + dayB,
				dayA.equal(dayB).toString());
	}
	@Test void testDayInt()
	{
		final Function<Integer> dayAInt = cast(dayA);
		assertFails(
				() -> dayAInt.equal(12345),
				IllegalArgumentException.class,
				dayA + " not comparable to '12345' (java.lang.Integer)");
		assertFails(
				() -> dayAInt.equal(intA),
				IllegalArgumentException.class,
				dayA + " not comparable to " + intA);
	}
	@Test void testDayDate()
	{
		final Function<Date> dayADate = cast(dayA);
		assertFails(
				() -> dayADate.equal(new Date(12345)),
				IllegalArgumentException.class,
				dayA + " not comparable to '1970-01-01 00:00:12.345' (java.util.Date)");
		assertFails(
				() -> dayADate.equal(dateA),
				IllegalArgumentException.class,
				dayA + " not comparable to " + dateA);
	}

	@Test void testEnum()
	{
		assertEquals(
				enumA + "='V2'",
				enumA.equal(AnEnum.V2).toString());
		assertEquals(
				enumA + "='V4'",
				enumA.equal(AnEnum.V4).toString());
		assertEquals(
				enumA + "=" + enumB,
				enumA.equal(enumB).toString());
	}
	enum AnEnum
	{
		V2,
		V4
		{
			// test whether Enum#getDeclaringClass() instead of just Object#getClass()
		}
	}
	@Test void testEnumInt()
	{
		final Function<Integer> enumAInt = cast(enumA);
		assertFails(
				() -> enumAInt.equal(12345),
				IllegalArgumentException.class,
				enumA + " not comparable to '12345' (java.lang.Integer)");
		assertFails(
				() -> enumAInt.equal(intA),
				IllegalArgumentException.class,
				enumA + " not comparable to " + intA);
	}
	@Test void testEnumOther()
	{
		final Function<AnEnumOther> enumAOther = cast(enumA);
		assertFails(
				() -> enumAOther.equal(AnEnumOther.V2),
				IllegalArgumentException.class,
				enumA + " not comparable to 'V2' (com.exedio.cope.IsComparableTest$AnEnumOther)");
		assertFails(
				() -> enumAOther.equal(AnEnumOther.V4),
				IllegalArgumentException.class,
				enumA + " not comparable to 'V4' (com.exedio.cope.IsComparableTest$AnEnumOther$1)");
		assertFails(
				() -> enumAOther.equal(enumOther),
				IllegalArgumentException.class,
				enumA + " not comparable to " + enumOther);
	}
	enum AnEnumOther
	{
		V2,
		V4
		{
			// test whether Enum#getDeclaringClass() instead of just Object#getClass()
		}
	}

	@Test void testItem()
	{
		assertEquals(
				itemA + "='AnItem-12345'",
				itemA.equal(AnItem.TYPE.activate(12345)).toString());
		assertEquals(
				itemA + "=" + itemB,
				itemA.equal(itemB).toString());
	}
	@Test void testItemSuper()
	{
		final Function<AnItemSuper> itemASuper = cast(itemA);
		assertEquals(
				itemA + "='AnItemSuper-12345'",
				itemASuper.equal(AnItemSuper.TYPE.activate(12345)).toString());
		assertEquals(
				itemA + "=" + itemSuper,
				itemASuper.equal(itemSuper).toString());
	}
	@Test void testItemBrother()
	{
		final Function<AnItemBrother> itemABrother = cast(itemA);
		assertEquals(
				itemA + "='AnItemBrother-12345'",
				itemABrother.equal(AnItemBrother.TYPE.activate(12345)).toString());
		assertEquals(
				itemA + "=" + itemBrother,
				itemABrother.equal(itemBrother).toString());
	}
	@Test void testItemSub()
	{
		assertEquals(
				itemA + "='AnItemSub-12345'",
				itemA.equal(AnItemSub.TYPE.activate(12345)).toString());
		assertEquals(
				itemA + "=" + itemSub,
				itemA.equal(itemSub).toString());
	}
	@Test void testItemInt()
	{
		final Function<Integer> itemAInt = cast(itemA);
		assertFails(
				() -> itemAInt.equal(12345),
				IllegalArgumentException.class,
				itemA + " not comparable to '12345' (java.lang.Integer)");
		assertFails(
				() -> itemAInt.equal(intA),
				IllegalArgumentException.class,
				itemA + " not comparable to " + intA);
	}
	@Test void testItemOther()
	{
		final Function<AnItemOther> itemAOther = cast(itemA);
		assertFails(
				() -> itemAOther.equal(AnItemOther.TYPE.activate(12345)),
				IllegalArgumentException.class,
				itemA + " not comparable to 'AnItemOther-12345' (com.exedio.cope.IsComparableTest$AnItemOther)");
		assertFails(
				() -> itemAOther.equal(itemOther),
				IllegalArgumentException.class,
				itemA + " not comparable to " + itemOther);
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class AnItemSuper extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItemSuper> TYPE = com.exedio.cope.TypesBound.newType(AnItemSuper.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected AnItemSuper(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class AnItem extends AnItemSuper
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class AnItemBrother extends AnItemSuper
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItemBrother> TYPE = com.exedio.cope.TypesBound.newType(AnItemBrother.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected AnItemBrother(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class AnItemSub extends AnItem
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItemSub> TYPE = com.exedio.cope.TypesBound.newType(AnItemSub.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnItemSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class AnItemOther extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItemOther> TYPE = com.exedio.cope.TypesBound.newType(AnItemOther.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnItemOther(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@com.exedio.cope.instrument.WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		@WrapperIgnore static final StringField stringA = new StringField();
		@WrapperIgnore static final StringField stringB = new StringField();

		@WrapperIgnore static final BooleanField boolA = new BooleanField();
		@WrapperIgnore static final BooleanField boolB = new BooleanField();

		@WrapperIgnore static final IntegerField intA = new IntegerField();
		@WrapperIgnore static final IntegerField intB = new IntegerField();

		@WrapperIgnore static final LongField longA = new LongField();
		@WrapperIgnore static final LongField longB = new LongField();

		@WrapperIgnore static final DoubleField doubleA = new DoubleField();
		@WrapperIgnore static final DoubleField doubleB = new DoubleField();

		@WrapperIgnore static final DateField dateA = new DateField();
		@WrapperIgnore static final DateField dateB = new DateField();

		@WrapperIgnore static final DayField dayA = new DayField();
		@WrapperIgnore static final DayField dayB = new DayField();

		@WrapperIgnore static final EnumField<AnEnum     > enumA     = EnumField.create(AnEnum     .class);
		@WrapperIgnore static final EnumField<AnEnum     > enumB     = EnumField.create(AnEnum     .class);
		@WrapperIgnore static final EnumField<AnEnumOther> enumOther = EnumField.create(AnEnumOther.class);

		@WrapperIgnore static final ItemField<AnItem       > itemA       = ItemField.create(AnItem       .class);
		@WrapperIgnore static final ItemField<AnItem       > itemB       = ItemField.create(AnItem       .class);
		@WrapperIgnore static final ItemField<AnItemSuper  > itemSuper   = ItemField.create(AnItemSuper  .class);
		@WrapperIgnore static final ItemField<AnItemBrother> itemBrother = ItemField.create(AnItemBrother.class);
		@WrapperIgnore static final ItemField<AnItemSub    > itemSub     = ItemField.create(AnItemSub    .class);
		@WrapperIgnore static final ItemField<AnItemOther  > itemOther   = ItemField.create(AnItemOther  .class);


		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@SuppressWarnings("unchecked")
	static <E> Function<E> cast(final Function<?> x)
	{
		return (Function<E>)(Function)x;
	}
}

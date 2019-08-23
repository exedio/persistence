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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.Day;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class UniqueImplicitTest extends TestWithEnvironment
{
	private static final Day day1 = new Day(2019, 8, 1);
	private static final Day day2 = new Day(2019, 8, 2);
	private static final Day day3 = new Day(2019, 8, 3);

	public UniqueImplicitTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		final MyItem iN1 = new MyItem(null, null, null, null, null, null, null, null, null);
		final MyItem iN2 = new MyItem(null, null, null, null, null, null, null, null, null);
		final MyItem iN3 = new MyItem(null, null, null, null, null, null, null, null, null);

		final Date date1 = new Date(4441);
		final Date date2 = new Date(4442);
		final Date date3 = new Date(4443);
		final MyItem i1 = new MyItem(false, 1, 11l, 1.1, "s1", date1, day1, MyEnum.F1, iN1);
		assertEquals(null, MyItem.forBooleanField(true));
		assertFailsStrict(() -> MyItem.forBooleanFieldStrict(true));
		final MyItem i2 = new MyItem(true,  2, 22l, 2.2, "s2", date2, day2, MyEnum.F2, iN2);

		assertEquals(i1, MyItem.forBooleanField(false));
		assertEquals(i1, MyItem.forIntegerField(1));
		assertEquals(i1, MyItem.forLongField(11l));
		assertEquals(i1, MyItem.forDoubleField(1.1));
		assertEquals(i1, MyItem.forStringField("s1"));
		assertEquals(i1, MyItem.forDateField(date1));
		assertEquals(i1, MyItem.forDayField(day1));
		assertEquals(i1, MyItem.forEnumField(MyEnum.F1));
		assertEquals(i1, MyItem.forItemField(iN1));

		assertEquals(i1, MyItem.forBooleanFieldStrict(false));
		assertEquals(i1, MyItem.forIntegerFieldStrict(1));
		assertEquals(i1, MyItem.forLongFieldStrict(11l));
		assertEquals(i1, MyItem.forDoubleFieldStrict(1.1));
		assertEquals(i1, MyItem.forStringFieldStrict("s1"));
		assertEquals(i1, MyItem.forDateFieldStrict(date1));
		assertEquals(i1, MyItem.forDayFieldStrict(day1));
		assertEquals(i1, MyItem.forEnumFieldStrict(MyEnum.F1));
		assertEquals(i1, MyItem.forItemFieldStrict(iN1));

		assertEquals(i2, MyItem.forBooleanField(true));
		assertEquals(i2, MyItem.forIntegerField(2));
		assertEquals(i2, MyItem.forLongField(22l));
		assertEquals(i2, MyItem.forDoubleField(2.2));
		assertEquals(i2, MyItem.forStringField("s2"));
		assertEquals(i2, MyItem.forDateField(date2));
		assertEquals(i2, MyItem.forDayField(day2));
		assertEquals(i2, MyItem.forEnumField(MyEnum.F2));
		assertEquals(i2, MyItem.forItemField(iN2));

		assertEquals(i2, MyItem.forBooleanFieldStrict(true));
		assertEquals(i2, MyItem.forIntegerFieldStrict(2));
		assertEquals(i2, MyItem.forLongFieldStrict(22l));
		assertEquals(i2, MyItem.forDoubleFieldStrict(2.2));
		assertEquals(i2, MyItem.forStringFieldStrict("s2"));
		assertEquals(i2, MyItem.forDateFieldStrict(date2));
		assertEquals(i2, MyItem.forDayFieldStrict(day2));
		assertEquals(i2, MyItem.forEnumFieldStrict(MyEnum.F2));
		assertEquals(i2, MyItem.forItemFieldStrict(iN2));

		assertEquals(null, MyItem.forIntegerField(3));
		assertEquals(null, MyItem.forLongField(33l));
		assertEquals(null, MyItem.forDoubleField(3.3));
		assertEquals(null, MyItem.forStringField("s3"));
		assertEquals(null, MyItem.forDateField(date3));
		assertEquals(null, MyItem.forDayField(day3));
		assertEquals(null, MyItem.forEnumField(MyEnum.F3));
		assertEquals(null, MyItem.forItemField(iN3));

		assertFailsStrict(() -> MyItem.forIntegerFieldStrict(3));
		assertFailsStrict(() -> MyItem.forLongFieldStrict(33l));
		assertFailsStrict(() -> MyItem.forDoubleFieldStrict(3.3));
		assertFailsStrict(() -> MyItem.forStringFieldStrict("s3"));
		assertFailsStrict(() -> MyItem.forDateFieldStrict(date3));
		assertFailsStrict(() -> MyItem.forDayFieldStrict(day3));
		assertFailsStrict(() -> MyItem.forEnumFieldStrict(MyEnum.F3));
		assertFailsStrict(() -> MyItem.forItemFieldStrict(iN3));

		assertFailsUnique(() -> new MyItem(false, 1,   11l,  1.1,  "s1", date1, day1, MyEnum.F1, iN1), MyItem.booleanField);
		assertFailsUnique(() -> new MyItem(null,  1,   11l,  1.1,  "s1", date1, day1, MyEnum.F1, iN1), MyItem.integerField);
		assertFailsUnique(() -> new MyItem(null, null, 11l,  1.1,  "s1", date1, day1, MyEnum.F1, iN1), MyItem.   longField);
		assertFailsUnique(() -> new MyItem(null, null, null, 1.1,  "s1", date1, day1, MyEnum.F1, iN1), MyItem. doubleField);
		assertFailsUnique(() -> new MyItem(null, null, null, null, "s1", date1, day1, MyEnum.F1, iN1), MyItem. stringField);
		assertFailsUnique(() -> new MyItem(null, null, null, null, null, date1, day1, MyEnum.F1, iN1), MyItem.   dateField);
		assertFailsUnique(() -> new MyItem(null, null, null, null, null, null,  day1, MyEnum.F1, iN1), MyItem.    dayField);
		assertFailsUnique(() -> new MyItem(null, null, null, null, null, null,  null, MyEnum.F1, iN1), MyItem.   enumField);
		assertFailsUnique(() -> new MyItem(null, null, null, null, null, null,  null, null,      iN1), MyItem.   itemField);
	}

	private static void assertFailsStrict(final Executable executable)
	{
		final IllegalArgumentException e =
				assertThrows(IllegalArgumentException.class, executable);
		assertTrue(
				e.getMessage().startsWith("expected result of size one, but was empty for query: "),
				e.getMessage());
	}

	private static void assertFailsUnique(final Executable executable, final FunctionField<?> field)
	{
		final UniqueViolationException e =
				assertThrows(UniqueViolationException.class, executable);
		assertSame(field.getImplicitUniqueConstraint(), e.getFeature());
	}

	@WrapperType(indent=2, comments=false)
	static final class MyItem extends Item
	{
		@WrapperInitial static final BooleanField booleanField = new BooleanField().optional().unique();
		@WrapperInitial static final IntegerField integerField = new IntegerField().optional().unique();
		@WrapperInitial static final    LongField    longField = new    LongField().optional().unique();
		@WrapperInitial static final  DoubleField  doubleField = new  DoubleField().optional().unique();
		@WrapperInitial static final  StringField  stringField = new  StringField().optional().unique();
		@WrapperInitial static final    DateField    dateField = new  DateField()  .optional().unique();
		@WrapperInitial static final     DayField     dayField = new  DayField()   .optional().unique();

		@WrapperInitial static final EnumField<MyEnum> enumField = EnumField.create(MyEnum.class).optional().unique();
		@WrapperInitial static final ItemField<MyItem> itemField = ItemField.create(MyItem.class).optional().unique();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		MyItem(
					@javax.annotation.Nullable final java.lang.Boolean booleanField,
					@javax.annotation.Nullable final java.lang.Integer integerField,
					@javax.annotation.Nullable final java.lang.Long longField,
					@javax.annotation.Nullable final java.lang.Double doubleField,
					@javax.annotation.Nullable final java.lang.String stringField,
					@javax.annotation.Nullable final java.util.Date dateField,
					@javax.annotation.Nullable final com.exedio.cope.util.Day dayField,
					@javax.annotation.Nullable final MyEnum enumField,
					@javax.annotation.Nullable final MyItem itemField)
				throws
					com.exedio.cope.StringLengthViolationException,
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.booleanField.map(booleanField),
				MyItem.integerField.map(integerField),
				MyItem.longField.map(longField),
				MyItem.doubleField.map(doubleField),
				MyItem.stringField.map(stringField),
				MyItem.dateField.map(dateField),
				MyItem.dayField.map(dayField),
				MyItem.enumField.map(enumField),
				MyItem.itemField.map(itemField),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.Boolean getBooleanField()
		{
			return MyItem.booleanField.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setBooleanField(@javax.annotation.Nullable final java.lang.Boolean booleanField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.booleanField.set(this,booleanField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forBooleanField(@javax.annotation.Nonnull final java.lang.Boolean booleanField)
		{
			return MyItem.booleanField.searchUnique(MyItem.class,booleanField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forBooleanFieldStrict(@javax.annotation.Nonnull final java.lang.Boolean booleanField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.booleanField.searchUniqueStrict(MyItem.class,booleanField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.Integer getIntegerField()
		{
			return MyItem.integerField.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setIntegerField(@javax.annotation.Nullable final java.lang.Integer integerField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.integerField.set(this,integerField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forIntegerField(@javax.annotation.Nonnull final java.lang.Integer integerField)
		{
			return MyItem.integerField.searchUnique(MyItem.class,integerField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forIntegerFieldStrict(@javax.annotation.Nonnull final java.lang.Integer integerField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.integerField.searchUniqueStrict(MyItem.class,integerField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.Long getLongField()
		{
			return MyItem.longField.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setLongField(@javax.annotation.Nullable final java.lang.Long longField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.longField.set(this,longField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forLongField(@javax.annotation.Nonnull final java.lang.Long longField)
		{
			return MyItem.longField.searchUnique(MyItem.class,longField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forLongFieldStrict(@javax.annotation.Nonnull final java.lang.Long longField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.longField.searchUniqueStrict(MyItem.class,longField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.Double getDoubleField()
		{
			return MyItem.doubleField.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDoubleField(@javax.annotation.Nullable final java.lang.Double doubleField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.doubleField.set(this,doubleField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forDoubleField(@javax.annotation.Nonnull final java.lang.Double doubleField)
		{
			return MyItem.doubleField.searchUnique(MyItem.class,doubleField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forDoubleFieldStrict(@javax.annotation.Nonnull final java.lang.Double doubleField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.doubleField.searchUniqueStrict(MyItem.class,doubleField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.String getStringField()
		{
			return MyItem.stringField.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setStringField(@javax.annotation.Nullable final java.lang.String stringField)
				throws
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.stringField.set(this,stringField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forStringField(@javax.annotation.Nonnull final java.lang.String stringField)
		{
			return MyItem.stringField.searchUnique(MyItem.class,stringField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forStringFieldStrict(@javax.annotation.Nonnull final java.lang.String stringField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.stringField.searchUniqueStrict(MyItem.class,stringField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.util.Date getDateField()
		{
			return MyItem.dateField.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDateField(@javax.annotation.Nullable final java.util.Date dateField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.dateField.set(this,dateField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forDateField(@javax.annotation.Nonnull final java.util.Date dateField)
		{
			return MyItem.dateField.searchUnique(MyItem.class,dateField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forDateFieldStrict(@javax.annotation.Nonnull final java.util.Date dateField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.dateField.searchUniqueStrict(MyItem.class,dateField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void touchDateField()
		{
			MyItem.dateField.touch(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		com.exedio.cope.util.Day getDayField()
		{
			return MyItem.dayField.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDayField(@javax.annotation.Nullable final com.exedio.cope.util.Day dayField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.dayField.set(this,dayField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forDayField(@javax.annotation.Nonnull final com.exedio.cope.util.Day dayField)
		{
			return MyItem.dayField.searchUnique(MyItem.class,dayField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forDayFieldStrict(@javax.annotation.Nonnull final com.exedio.cope.util.Day dayField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.dayField.searchUniqueStrict(MyItem.class,dayField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void touchDayField(@javax.annotation.Nonnull final java.util.TimeZone zone)
		{
			MyItem.dayField.touch(this,zone);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		MyEnum getEnumField()
		{
			return MyItem.enumField.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setEnumField(@javax.annotation.Nullable final MyEnum enumField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.enumField.set(this,enumField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forEnumField(@javax.annotation.Nonnull final MyEnum enumField)
		{
			return MyItem.enumField.searchUnique(MyItem.class,enumField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forEnumFieldStrict(@javax.annotation.Nonnull final MyEnum enumField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.enumField.searchUniqueStrict(MyItem.class,enumField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		MyItem getItemField()
		{
			return MyItem.itemField.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setItemField(@javax.annotation.Nullable final MyItem itemField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.itemField.set(this,itemField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forItemField(@javax.annotation.Nonnull final MyItem itemField)
		{
			return MyItem.itemField.searchUnique(MyItem.class,itemField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forItemFieldStrict(@javax.annotation.Nonnull final MyItem itemField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.itemField.searchUniqueStrict(MyItem.class,itemField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	enum MyEnum {F1, F2, F3}

	static final Model MODEL = new Model(MyItem.TYPE);
}

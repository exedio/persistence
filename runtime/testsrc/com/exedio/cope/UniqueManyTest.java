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

import static com.exedio.cope.UniqueManyTest.MyItem.constraint;
import static com.exedio.cope.UniqueManyTest.MyItem.field;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class UniqueManyTest extends TestWithEnvironment
{
	public UniqueManyTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		final int maximum = 13;

		for(int index = 2; index<=maximum; index++)
		{
			final ArrayList<StringField> fields = new ArrayList<>();
			for(int i = 1; i<=index; i++)
				fields.add(field(i));

			assertEquals(fields, constraint(index).getFields(), "constraint" + index);
		}

		final ArrayList<SetValue<?>> sv = new ArrayList<>();
		for(int index = 1; index<=maximum; index++)
			sv.add(field(index).map("value" + index));

		final MyItem i = new MyItem(sv.toArray(new SetValue<?>[sv.size()]));
		for(int index = 1; index<=maximum; index++)
			assertEquals("value" + index, field(index).get(i));

		restartTransaction();
		for(int index = 1; index<=maximum; index++)
			assertEquals("value" + index, field(index).get(i));
	}

	@com.exedio.cope.instrument.WrapperType(indent=2, constructor=NONE, genericConstructor=PACKAGE, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		static final StringField field1 = new StringField();
		static final StringField field2 = new StringField();
		static final StringField field3 = new StringField();
		static final StringField field4 = new StringField();
		static final StringField field5 = new StringField();
		static final StringField field6 = new StringField();
		static final StringField field7 = new StringField().lengthMax(10);
		static final StringField field8 = new StringField().lengthMax(10);
		static final StringField field9 = new StringField().lengthMax(10);
		static final StringField field10= new StringField().lengthMax(10);
		static final StringField field11= new StringField().lengthMax(10);
		static final StringField field12= new StringField().lengthMax(10);
		static final StringField field13= new StringField().lengthMax(10);

		@SuppressWarnings("deprecation")
		static final UniqueConstraint constraint2 = new UniqueConstraint(field1, field2);
		@SuppressWarnings("deprecation")
		static final UniqueConstraint constraint3 = new UniqueConstraint(field1, field2, field3);
		@SuppressWarnings("deprecation")
		static final UniqueConstraint constraint4 = new UniqueConstraint(field1, field2, field3, field4);
		@SuppressWarnings("deprecation")
		static final UniqueConstraint constraint5 = new UniqueConstraint(field1, field2, field3, field4, field5);
		@SuppressWarnings("deprecation")
		static final UniqueConstraint constraint6 = new UniqueConstraint(field1, field2, field3, field4, field5, field6);
		@SuppressWarnings("deprecation")
		static final UniqueConstraint constraint7 = new UniqueConstraint(field1, field2, field3, field4, field5, field6, field7);
		@SuppressWarnings("deprecation")
		static final UniqueConstraint constraint8 = new UniqueConstraint(field1, field2, field3, field4, field5, field6, field7, field8);
		@SuppressWarnings("deprecation")
		static final UniqueConstraint constraint9 = new UniqueConstraint(field1, field2, field3, field4, field5, field6, field7, field8, field9);
		@SuppressWarnings("deprecation")
		static final UniqueConstraint constraint10= new UniqueConstraint(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10);
		@SuppressWarnings("deprecation")
		static final UniqueConstraint constraint11= new UniqueConstraint(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
		@SuppressWarnings("deprecation")
		static final UniqueConstraint constraint12= new UniqueConstraint(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12);
		static final UniqueConstraint constraint13= UniqueConstraint.create(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13);

		static StringField field(final int index)
		{
			final String name = "field" + index;
			final StringField result = (StringField)TYPE.getFeature(name);
			assertNotNull(result, name);
			return result;
		}

		static UniqueConstraint constraint(final int index)
		{
			final String name = "constraint" + index;
			final UniqueConstraint result = (UniqueConstraint)TYPE.getFeature(name);
			assertNotNull(result, name);
			return result;
		}


		@javax.annotation.Generated("com.exedio.cope.instrument")
		MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField1()
		{
			return MyItem.field1.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField1(@javax.annotation.Nonnull final java.lang.String field1)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field1.set(this,field1);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField2()
		{
			return MyItem.field2.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField2(@javax.annotation.Nonnull final java.lang.String field2)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field2.set(this,field2);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField3()
		{
			return MyItem.field3.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField3(@javax.annotation.Nonnull final java.lang.String field3)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field3.set(this,field3);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField4()
		{
			return MyItem.field4.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField4(@javax.annotation.Nonnull final java.lang.String field4)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field4.set(this,field4);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField5()
		{
			return MyItem.field5.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField5(@javax.annotation.Nonnull final java.lang.String field5)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field5.set(this,field5);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField6()
		{
			return MyItem.field6.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField6(@javax.annotation.Nonnull final java.lang.String field6)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field6.set(this,field6);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField7()
		{
			return MyItem.field7.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField7(@javax.annotation.Nonnull final java.lang.String field7)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field7.set(this,field7);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField8()
		{
			return MyItem.field8.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField8(@javax.annotation.Nonnull final java.lang.String field8)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field8.set(this,field8);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField9()
		{
			return MyItem.field9.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField9(@javax.annotation.Nonnull final java.lang.String field9)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field9.set(this,field9);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField10()
		{
			return MyItem.field10.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField10(@javax.annotation.Nonnull final java.lang.String field10)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field10.set(this,field10);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField11()
		{
			return MyItem.field11.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField11(@javax.annotation.Nonnull final java.lang.String field11)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field11.set(this,field11);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField12()
		{
			return MyItem.field12.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField12(@javax.annotation.Nonnull final java.lang.String field12)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field12.set(this,field12);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getField13()
		{
			return MyItem.field13.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField13(@javax.annotation.Nonnull final java.lang.String field13)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field13.set(this,field13);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint2(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2)
		{
			return MyItem.constraint2.search(MyItem.class,field1,field2);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraint2Strict(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint2.searchStrict(MyItem.class,field1,field2);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint3(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3)
		{
			return MyItem.constraint3.search(MyItem.class,field1,field2,field3);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraint3Strict(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint3.searchStrict(MyItem.class,field1,field2,field3);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint4(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4)
		{
			return MyItem.constraint4.search(MyItem.class,field1,field2,field3,field4);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraint4Strict(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint4.searchStrict(MyItem.class,field1,field2,field3,field4);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint5(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5)
		{
			return MyItem.constraint5.search(MyItem.class,field1,field2,field3,field4,field5);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraint5Strict(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint5.searchStrict(MyItem.class,field1,field2,field3,field4,field5);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint6(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6)
		{
			return MyItem.constraint6.search(MyItem.class,field1,field2,field3,field4,field5,field6);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraint6Strict(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint6.searchStrict(MyItem.class,field1,field2,field3,field4,field5,field6);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint7(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7)
		{
			return MyItem.constraint7.search(MyItem.class,field1,field2,field3,field4,field5,field6,field7);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraint7Strict(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint7.searchStrict(MyItem.class,field1,field2,field3,field4,field5,field6,field7);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint8(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7,@javax.annotation.Nonnull final java.lang.String field8)
		{
			return MyItem.constraint8.search(MyItem.class,field1,field2,field3,field4,field5,field6,field7,field8);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraint8Strict(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7,@javax.annotation.Nonnull final java.lang.String field8)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint8.searchStrict(MyItem.class,field1,field2,field3,field4,field5,field6,field7,field8);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint9(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7,@javax.annotation.Nonnull final java.lang.String field8,@javax.annotation.Nonnull final java.lang.String field9)
		{
			return MyItem.constraint9.search(MyItem.class,field1,field2,field3,field4,field5,field6,field7,field8,field9);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraint9Strict(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7,@javax.annotation.Nonnull final java.lang.String field8,@javax.annotation.Nonnull final java.lang.String field9)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint9.searchStrict(MyItem.class,field1,field2,field3,field4,field5,field6,field7,field8,field9);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint10(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7,@javax.annotation.Nonnull final java.lang.String field8,@javax.annotation.Nonnull final java.lang.String field9,@javax.annotation.Nonnull final java.lang.String field10)
		{
			return MyItem.constraint10.search(MyItem.class,field1,field2,field3,field4,field5,field6,field7,field8,field9,field10);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraint10Strict(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7,@javax.annotation.Nonnull final java.lang.String field8,@javax.annotation.Nonnull final java.lang.String field9,@javax.annotation.Nonnull final java.lang.String field10)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint10.searchStrict(MyItem.class,field1,field2,field3,field4,field5,field6,field7,field8,field9,field10);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint11(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7,@javax.annotation.Nonnull final java.lang.String field8,@javax.annotation.Nonnull final java.lang.String field9,@javax.annotation.Nonnull final java.lang.String field10,@javax.annotation.Nonnull final java.lang.String field11)
		{
			return MyItem.constraint11.search(MyItem.class,field1,field2,field3,field4,field5,field6,field7,field8,field9,field10,field11);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraint11Strict(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7,@javax.annotation.Nonnull final java.lang.String field8,@javax.annotation.Nonnull final java.lang.String field9,@javax.annotation.Nonnull final java.lang.String field10,@javax.annotation.Nonnull final java.lang.String field11)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint11.searchStrict(MyItem.class,field1,field2,field3,field4,field5,field6,field7,field8,field9,field10,field11);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint12(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7,@javax.annotation.Nonnull final java.lang.String field8,@javax.annotation.Nonnull final java.lang.String field9,@javax.annotation.Nonnull final java.lang.String field10,@javax.annotation.Nonnull final java.lang.String field11,@javax.annotation.Nonnull final java.lang.String field12)
		{
			return MyItem.constraint12.search(MyItem.class,field1,field2,field3,field4,field5,field6,field7,field8,field9,field10,field11,field12);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraint12Strict(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7,@javax.annotation.Nonnull final java.lang.String field8,@javax.annotation.Nonnull final java.lang.String field9,@javax.annotation.Nonnull final java.lang.String field10,@javax.annotation.Nonnull final java.lang.String field11,@javax.annotation.Nonnull final java.lang.String field12)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint12.searchStrict(MyItem.class,field1,field2,field3,field4,field5,field6,field7,field8,field9,field10,field11,field12);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forConstraint13(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7,@javax.annotation.Nonnull final java.lang.String field8,@javax.annotation.Nonnull final java.lang.String field9,@javax.annotation.Nonnull final java.lang.String field10,@javax.annotation.Nonnull final java.lang.String field11,@javax.annotation.Nonnull final java.lang.String field12,@javax.annotation.Nonnull final java.lang.String field13)
		{
			return MyItem.constraint13.search(MyItem.class,field1,field2,field3,field4,field5,field6,field7,field8,field9,field10,field11,field12,field13);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forConstraint13Strict(@javax.annotation.Nonnull final java.lang.String field1,@javax.annotation.Nonnull final java.lang.String field2,@javax.annotation.Nonnull final java.lang.String field3,@javax.annotation.Nonnull final java.lang.String field4,@javax.annotation.Nonnull final java.lang.String field5,@javax.annotation.Nonnull final java.lang.String field6,@javax.annotation.Nonnull final java.lang.String field7,@javax.annotation.Nonnull final java.lang.String field8,@javax.annotation.Nonnull final java.lang.String field9,@javax.annotation.Nonnull final java.lang.String field10,@javax.annotation.Nonnull final java.lang.String field11,@javax.annotation.Nonnull final java.lang.String field12,@javax.annotation.Nonnull final java.lang.String field13)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraint13.searchStrict(MyItem.class,field1,field2,field3,field4,field5,field6,field7,field8,field9,field10,field11,field12,field13);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);
}

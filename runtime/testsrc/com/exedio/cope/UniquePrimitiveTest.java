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

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class UniquePrimitiveTest extends TestWithEnvironment
{
	public UniquePrimitiveTest()
	{
		super(MODEL);
	}

	// TODO implement and test searchUniqueStrict
	@Test void test()
	{
		final MyItem i1 = new MyItem(false, 1, 11l, 1.1);
		assertEquals(null, MyItem.forBooleanField(true));
		final MyItem i2 = new MyItem(true,  2, 22l, 2.2);

		assertEquals(i1, MyItem.forBooleanField(false));
		assertEquals(i1, MyItem.forIntegerField(1));
		assertEquals(i1, MyItem.forLongField(11l));
		assertEquals(i1, MyItem.forDoubleField(1.1));

		assertEquals(i2, MyItem.forBooleanField(true));
		assertEquals(i2, MyItem.forIntegerField(2));
		assertEquals(i2, MyItem.forLongField(22l));
		assertEquals(i2, MyItem.forDoubleField(2.2));

		assertEquals(null, MyItem.forIntegerField(3));
		assertEquals(null, MyItem.forLongField(33l));
		assertEquals(null, MyItem.forDoubleField(3.3));
	}

	@WrapperType(indent=2, comments=false)
	static final class MyItem extends Item
	{
		static final BooleanField booleanField = new BooleanField().unique();
		static final IntegerField integerField = new IntegerField().unique();
		static final    LongField    longField = new    LongField().unique();
		static final  DoubleField  doubleField = new  DoubleField().unique();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		MyItem(
					final boolean booleanField,
					final int integerField,
					final long longField,
					final double doubleField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.booleanField.map(booleanField),
				MyItem.integerField.map(integerField),
				MyItem.longField.map(longField),
				MyItem.doubleField.map(doubleField),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		boolean getBooleanField()
		{
			return MyItem.booleanField.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setBooleanField(final boolean booleanField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.booleanField.set(this,booleanField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forBooleanField(final boolean booleanField)
		{
			return MyItem.booleanField.searchUnique(MyItem.class,booleanField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		int getIntegerField()
		{
			return MyItem.integerField.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setIntegerField(final int integerField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.integerField.set(this,integerField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forIntegerField(final int integerField)
		{
			return MyItem.integerField.searchUnique(MyItem.class,integerField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		long getLongField()
		{
			return MyItem.longField.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setLongField(final long longField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.longField.set(this,longField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forLongField(final long longField)
		{
			return MyItem.longField.searchUnique(MyItem.class,longField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		double getDoubleField()
		{
			return MyItem.doubleField.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDoubleField(final double doubleField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.doubleField.set(this,doubleField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forDoubleField(final double doubleField)
		{
			return MyItem.doubleField.searchUnique(MyItem.class,doubleField);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE);
}

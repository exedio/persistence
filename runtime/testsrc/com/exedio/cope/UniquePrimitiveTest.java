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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class UniquePrimitiveTest extends TestWithEnvironment
{
	public UniquePrimitiveTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		final MyItem i1 = new MyItem(false, 1, 11l, 1.1);
		assertEquals(null, MyItem.forBooleanField(true));
		assertFailsStrict(() -> MyItem.forBooleanFieldStrict(true));
		final MyItem i2 = new MyItem(true,  2, 22l, 2.2);

		assertEquals(i1, MyItem.forBooleanField(false));
		assertEquals(i1, MyItem.forIntegerField(1));
		assertEquals(i1, MyItem.forLongField(11l));
		assertEquals(i1, MyItem.forDoubleField(1.1));

		assertEquals(i1, MyItem.forBooleanFieldStrict(false));
		assertEquals(i1, MyItem.forIntegerFieldStrict(1));
		assertEquals(i1, MyItem.forLongFieldStrict(11l));
		assertEquals(i1, MyItem.forDoubleFieldStrict(1.1));

		assertEquals(i2, MyItem.forBooleanField(true));
		assertEquals(i2, MyItem.forIntegerField(2));
		assertEquals(i2, MyItem.forLongField(22l));
		assertEquals(i2, MyItem.forDoubleField(2.2));

		assertEquals(i2, MyItem.forBooleanFieldStrict(true));
		assertEquals(i2, MyItem.forIntegerFieldStrict(2));
		assertEquals(i2, MyItem.forLongFieldStrict(22l));
		assertEquals(i2, MyItem.forDoubleFieldStrict(2.2));

		assertEquals(null, MyItem.forIntegerField(3));
		assertEquals(null, MyItem.forLongField(33l));
		assertEquals(null, MyItem.forDoubleField(3.3));

		assertFailsStrict(() -> MyItem.forIntegerFieldStrict(3));
		assertFailsStrict(() -> MyItem.forLongFieldStrict(33l));
		assertFailsStrict(() -> MyItem.forDoubleFieldStrict(3.3));
	}

	private static void assertFailsStrict(final Executable executable)
	{
		final IllegalArgumentException e =
				assertThrows(IllegalArgumentException.class, executable);
		assertTrue(
				e.getMessage().startsWith("expected result of size one, but was empty for query: "),
				e.getMessage());
	}

	@WrapperType(indent=2)
	private static final class MyItem extends Item
	{
		static final BooleanField booleanField = new BooleanField().unique();
		static final IntegerField integerField = new IntegerField().unique();
		static final    LongField    longField = new    LongField().unique();
		static final  DoubleField  doubleField = new  DoubleField().unique();

		/**
		 * Creates a new MyItem with all the fields initially needed.
		 * @param booleanField the initial value for field {@link #booleanField}.
		 * @param integerField the initial value for field {@link #integerField}.
		 * @param longField the initial value for field {@link #longField}.
		 * @param doubleField the initial value for field {@link #doubleField}.
		 * @throws com.exedio.cope.UniqueViolationException if booleanField, integerField, longField, doubleField is not unique.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
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

		/**
		 * Creates a new MyItem and sets the given fields initially.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		/**
		 * Returns the value of {@link #booleanField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean getBooleanField()
		{
			return MyItem.booleanField.getMandatory(this);
		}

		/**
		 * Sets a new value for {@link #booleanField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setBooleanField(final boolean booleanField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.booleanField.set(this,booleanField);
		}

		/**
		 * Finds a myItem by it's {@link #booleanField}.
		 * @param booleanField shall be equal to field {@link #booleanField}.
		 * @return null if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static MyItem forBooleanField(final boolean booleanField)
		{
			return MyItem.booleanField.searchUnique(MyItem.class,booleanField);
		}

		/**
		 * Finds a myItem by its {@link #booleanField}.
		 * @param booleanField shall be equal to field {@link #booleanField}.
		 * @throws java.lang.IllegalArgumentException if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static MyItem forBooleanFieldStrict(final boolean booleanField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.booleanField.searchUniqueStrict(MyItem.class,booleanField);
		}

		/**
		 * Returns the value of {@link #integerField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getIntegerField()
		{
			return MyItem.integerField.getMandatory(this);
		}

		/**
		 * Sets a new value for {@link #integerField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setIntegerField(final int integerField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.integerField.set(this,integerField);
		}

		/**
		 * Finds a myItem by it's {@link #integerField}.
		 * @param integerField shall be equal to field {@link #integerField}.
		 * @return null if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static MyItem forIntegerField(final int integerField)
		{
			return MyItem.integerField.searchUnique(MyItem.class,integerField);
		}

		/**
		 * Finds a myItem by its {@link #integerField}.
		 * @param integerField shall be equal to field {@link #integerField}.
		 * @throws java.lang.IllegalArgumentException if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static MyItem forIntegerFieldStrict(final int integerField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.integerField.searchUniqueStrict(MyItem.class,integerField);
		}

		/**
		 * Returns the value of {@link #longField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		long getLongField()
		{
			return MyItem.longField.getMandatory(this);
		}

		/**
		 * Sets a new value for {@link #longField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setLongField(final long longField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.longField.set(this,longField);
		}

		/**
		 * Finds a myItem by it's {@link #longField}.
		 * @param longField shall be equal to field {@link #longField}.
		 * @return null if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static MyItem forLongField(final long longField)
		{
			return MyItem.longField.searchUnique(MyItem.class,longField);
		}

		/**
		 * Finds a myItem by its {@link #longField}.
		 * @param longField shall be equal to field {@link #longField}.
		 * @throws java.lang.IllegalArgumentException if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static MyItem forLongFieldStrict(final long longField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.longField.searchUniqueStrict(MyItem.class,longField);
		}

		/**
		 * Returns the value of {@link #doubleField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		double getDoubleField()
		{
			return MyItem.doubleField.getMandatory(this);
		}

		/**
		 * Sets a new value for {@link #doubleField}.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setDoubleField(final double doubleField)
				throws
					com.exedio.cope.UniqueViolationException
		{
			MyItem.doubleField.set(this,doubleField);
		}

		/**
		 * Finds a myItem by it's {@link #doubleField}.
		 * @param doubleField shall be equal to field {@link #doubleField}.
		 * @return null if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static MyItem forDoubleField(final double doubleField)
		{
			return MyItem.doubleField.searchUnique(MyItem.class,doubleField);
		}

		/**
		 * Finds a myItem by its {@link #doubleField}.
		 * @param doubleField shall be equal to field {@link #doubleField}.
		 * @throws java.lang.IllegalArgumentException if there is no matching item.
		 */
		@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static MyItem forDoubleFieldStrict(final double doubleField)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.doubleField.searchUniqueStrict(MyItem.class,doubleField);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for myItem.
		 */
		@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE);
}

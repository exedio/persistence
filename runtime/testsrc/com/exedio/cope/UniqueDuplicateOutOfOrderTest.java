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

import static com.exedio.cope.RuntimeAssert.failingActivator;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class UniqueDuplicateOutOfOrderTest
{
	@Test void test()
	{
		try
		{
			TypesBound.newType(MyItem.class, failingActivator());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"duplicate unique constraints at type MyItem: " +
					"MyItem.constraintA and MyItem.constraintB with fields " +
					"[MyItem.one, MyItem.two] and " +
					"[MyItem.two, MyItem.one].",
					e.getMessage());
		}
	}

	@WrapperType(type=NONE, constructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		static final StringField one = new StringField();
		static final StringField two = new StringField();

		static final UniqueConstraint constraintA = UniqueConstraint.create(one, two);
		static final UniqueConstraint constraintB = UniqueConstraint.create(two, one);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getOne()
		{
			return MyItem.one.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setOne(@javax.annotation.Nonnull final java.lang.String one)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.one.set(this,one);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getTwo()
		{
			return MyItem.two.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setTwo(@javax.annotation.Nonnull final java.lang.String two)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.two.set(this,two);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static MyItem forConstraintA(@javax.annotation.Nonnull final java.lang.String one,@javax.annotation.Nonnull final java.lang.String two)
		{
			return MyItem.constraintA.search(MyItem.class,one,two);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static MyItem forConstraintAStrict(@javax.annotation.Nonnull final java.lang.String one,@javax.annotation.Nonnull final java.lang.String two)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraintA.searchStrict(MyItem.class,one,two);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static MyItem forConstraintB(@javax.annotation.Nonnull final java.lang.String two,@javax.annotation.Nonnull final java.lang.String one)
		{
			return MyItem.constraintB.search(MyItem.class,two,one);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static MyItem forConstraintBStrict(@javax.annotation.Nonnull final java.lang.String two,@javax.annotation.Nonnull final java.lang.String one)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.constraintB.searchStrict(MyItem.class,two,one);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class UniqueDuplicateTest
{
	@Test public void test()
	{
		try
		{
			TypesBound.newType(MyItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"duplicate unique constraints at type MyItem: " +
					"MyItem.constraintA and MyItem.constraintB with fields " +
					"[MyItem.one, MyItem.two].",
					e.getMessage());
		}
	}

	@com.exedio.cope.instrument.WrapperType(type=NONE, constructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		static final StringField one = new StringField();
		static final StringField two = new StringField();

		static final UniqueConstraint constraintA = new UniqueConstraint(one, two);
		static final UniqueConstraint constraintB = new UniqueConstraint(one, two);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		final java.lang.String getOne()
		{
			return MyItem.one.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void setOne(@javax.annotation.Nonnull final java.lang.String one)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.one.set(this,one);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		final java.lang.String getTwo()
		{
			return MyItem.two.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final void setTwo(@javax.annotation.Nonnull final java.lang.String two)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.two.set(this,two);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static final MyItem forConstraintA(@javax.annotation.Nonnull final java.lang.String one,@javax.annotation.Nonnull final java.lang.String two)
		{
			return MyItem.constraintA.search(MyItem.class,one,two);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static final MyItem forConstraintB(@javax.annotation.Nonnull final java.lang.String one,@javax.annotation.Nonnull final java.lang.String two)
		{
			return MyItem.constraintB.search(MyItem.class,one,two);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
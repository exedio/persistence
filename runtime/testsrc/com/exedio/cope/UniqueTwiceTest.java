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

import static com.exedio.cope.UniqueTwiceTest.MyItem.TYPE;
import static com.exedio.cope.UniqueTwiceTest.MyItem.one;
import static com.exedio.cope.UniqueTwiceTest.MyItem.oneDup;
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class UniqueTwiceTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(UniqueTwiceTest.class, "MODEL");
	}

	@Test void test()
	{
		final UniqueConstraint constraintOne    = (UniqueConstraint)TYPE.getFeature("oneImplicitUnique");
		final UniqueConstraint constraintOneDup = (UniqueConstraint)TYPE.getFeature("oneDupImplicitUnique");
		assertNotNull(constraintOne);
		assertNotNull(constraintOneDup);

		assertEquals(asList(constraintOne, constraintOneDup), TYPE.getUniqueConstraints());

		assertEquals(asList(constraintOne   ), one   .getUniqueConstraints());
		assertEquals(asList(constraintOneDup), oneDup.getUniqueConstraints());
		assertEquals(constraintOne   , one   .getImplicitUniqueConstraint());
		assertEquals(constraintOneDup, oneDup.getImplicitUniqueConstraint());
		assertEquals(asList(one   ), constraintOne   .getFields());
		assertEquals(asList(oneDup), constraintOneDup.getFields());
	}

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		static final StringField one    = new StringField().unique();
		static final StringField oneDup = new StringField().unique().unique();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getOne()
		{
			return MyItem.one.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setOne(@javax.annotation.Nonnull final java.lang.String one)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.one.set(this,one);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forOne(@javax.annotation.Nonnull final java.lang.String one)
		{
			return MyItem.one.searchUnique(MyItem.class,one);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forOneStrict(@javax.annotation.Nonnull final java.lang.String one)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.one.searchUniqueStrict(MyItem.class,one);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		java.lang.String getOneDup()
		{
			return MyItem.oneDup.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setOneDup(@javax.annotation.Nonnull final java.lang.String oneDup)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.oneDup.set(this,oneDup);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		static MyItem forOneDup(@javax.annotation.Nonnull final java.lang.String oneDup)
		{
			return MyItem.oneDup.searchUnique(MyItem.class,oneDup);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		static MyItem forOneDupStrict(@javax.annotation.Nonnull final java.lang.String oneDup)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.oneDup.searchUniqueStrict(MyItem.class,oneDup);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

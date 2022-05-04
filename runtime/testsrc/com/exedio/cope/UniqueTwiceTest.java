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

import com.exedio.cope.instrument.WrapperType;
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

	@WrapperType(constructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		static final StringField one    = new StringField().unique();
		static final StringField oneDup = new StringField().unique().unique();

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
		@javax.annotation.Nullable
		static MyItem forOne(@javax.annotation.Nonnull final java.lang.String one)
		{
			return MyItem.one.searchUnique(MyItem.class,one);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static MyItem forOneStrict(@javax.annotation.Nonnull final java.lang.String one)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.one.searchUniqueStrict(MyItem.class,one);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getOneDup()
		{
			return MyItem.oneDup.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setOneDup(@javax.annotation.Nonnull final java.lang.String oneDup)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.UniqueViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.oneDup.set(this,oneDup);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		static MyItem forOneDup(@javax.annotation.Nonnull final java.lang.String oneDup)
		{
			return MyItem.oneDup.searchUnique(MyItem.class,oneDup);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		static MyItem forOneDupStrict(@javax.annotation.Nonnull final java.lang.String oneDup)
				throws
					java.lang.IllegalArgumentException
		{
			return MyItem.oneDup.searchUniqueStrict(MyItem.class,oneDup);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

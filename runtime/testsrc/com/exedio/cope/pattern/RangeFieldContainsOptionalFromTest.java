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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.Range.valueOf;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RangeFieldContainsOptionalFromTest extends TestWithEnvironment
{
	public RangeFieldContainsOptionalFromTest()
	{
		super(MODEL);
	}

	@Test
	void testIt()
	{
		final MyItem ab = new MyItem(valueOf(10,   20));
		final MyItem nb = new MyItem(valueOf(null, 20));

		assertIt( 9,     nb);
		assertIt(10, ab, nb);
		assertIt(11, ab, nb);
		assertIt(19, ab, nb);
		assertIt(20, ab, nb);
		assertIt(21        );

		for(final MyItem item : MyItem.TYPE.search())
		{
			assertFails(
					() -> item.doesFieldContain(null),
					NullPointerException.class,
					"value");
		}
	}

	private static void assertIt(final int value, final MyItem... actual)
	{
		final List<MyItem> actualList = Arrays.asList(actual);
		assertEquals(actualList, MyItem.TYPE.search(MyItem.field.contains(value), MyItem.TYPE.getThis(), true));
		for(final MyItem item : MyItem.TYPE.search())
		{
			final boolean contains = actualList.contains(item);
			assertEquals(contains, item.getField().contains(value));
			assertEquals(contains, item.doesFieldContain(value));
		}
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@WrapperInitial
		static final RangeField<Integer> field = RangeField.create(new IntegerField()).optionalFrom();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nonnull final com.exedio.cope.pattern.Range<Integer> field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		com.exedio.cope.pattern.Range<Integer> getField()
		{
			return MyItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nonnull final com.exedio.cope.pattern.Range<? extends Integer> field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		Integer getFieldFrom()
		{
			return MyItem.field.getFrom(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		Integer getFieldTo()
		{
			return MyItem.field.getTo(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setFieldFrom(@javax.annotation.Nullable final Integer field)
		{
			MyItem.field.setFrom(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setFieldTo(@javax.annotation.Nonnull final Integer field)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			MyItem.field.setTo(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		boolean doesFieldContain(@javax.annotation.Nonnull final Integer field)
		{
			return MyItem.field.doesContain(this,field);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);
}

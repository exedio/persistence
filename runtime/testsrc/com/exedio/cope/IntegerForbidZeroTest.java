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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class IntegerForbidZeroTest extends TestWithEnvironment
{
	public IntegerForbidZeroTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		MyItem.field.check(null);
		final MyItem iNull = new MyItem((Integer)null);
		assertEquals(null, iNull.getField());

		MyItem.field.check(-1);
		final MyItem iBelow = new MyItem(-1);
		assertEquals(-1, iBelow.getField());

		MyItem.field.check(1);
		final MyItem iAbove = new MyItem(1);
		assertEquals(1, iAbove.getField());

		{
			final var ex = assertFails(
					() -> MyItem.field.check(0),
					IntegerForbidViolationException.class,
					"forbid violation, 0 is forbidden for MyItem.field");
			assertEquals(null, ex.getItem());
			assertSame(MyItem.field, ex.getFeature());
			assertEquals(0, ex.getValue());
		}
		{
			final var ex = assertFails(
					() -> new MyItem(0),
					IntegerForbidViolationException.class,
					"forbid violation, 0 is forbidden for MyItem.field");
			assertEquals(null, ex.getItem());
			assertSame(MyItem.field, ex.getFeature());
			assertEquals(0, ex.getValue());
		}
		{
			final var ex = assertFails(
					() -> iBelow.setField(0),
					IntegerForbidViolationException.class,
					"forbid violation on " + iBelow + ", 0 is forbidden for MyItem.field");
			assertEquals(iBelow, ex.getItem());
			assertSame(MyItem.field, ex.getFeature());
			assertEquals(0, ex.getValue());
		}
		assertEquals(-1, iBelow.getField());
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@WrapperInitial static final IntegerField field = new IntegerField().optional().forbidZero();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nullable final java.lang.Integer field)
				throws
					com.exedio.cope.IntegerForbidViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.Integer getField()
		{
			return MyItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nullable final java.lang.Integer field)
				throws
					com.exedio.cope.IntegerForbidViolationException
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);
}

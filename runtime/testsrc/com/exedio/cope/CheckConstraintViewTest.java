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

import static com.exedio.cope.CheckConstraintViewTest.AnItem.TYPE;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.MainRule;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class CheckConstraintViewTest extends TestWithEnvironment
{
	public CheckConstraintViewTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		final AnItem item = new AnItem(33, 44, 77);
		assertEquals(33, item.getOne());
		assertEquals(44, item.getTwo());
		assertEquals(77, item.getSum());

		item.set(333, 444, 777);
		assertEquals(333, item.getOne());
		assertEquals(444, item.getTwo());
		assertEquals(777, item.getSum());

		assertFails(
				() -> item.set(33, 44, 88),
				CheckViolationException.class,
				"check violation on " + item + " for AnItem.check");
		assertEquals(333, item.getOne());
		assertEquals(444, item.getTwo());
		assertEquals(777, item.getSum());
	}

	@Test void testCreateFails()
	{
		assertFails(
				() -> new AnItem(33, 44, 88),
				CheckViolationException.class,
				"check violation for AnItem.check");
		assertEquals(asList(), TYPE.search());
	}

	@WrapperType(indent=2, comments=false)
	static final class AnItem extends Item
	{
		@Wrapper(wrap="set", visibility=NONE)
		static final IntegerField one = new IntegerField();

		@Wrapper(wrap="set", visibility=NONE)
		static final IntegerField two = new IntegerField();

		@Wrapper(wrap="set", visibility=NONE)
		static final IntegerField sum = new IntegerField();

		@SuppressWarnings("unused") // OK: CheckConstraint
		static final CheckConstraint check = new CheckConstraint(one.plus(two).equal(sum));


		void set(
				final int one,
				final int two,
				final int sum)
		{
			set(
					SetValue.map(AnItem.one, one),
					SetValue.map(AnItem.two, two),
					SetValue.map(AnItem.sum, sum)
			);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		AnItem(
					final int one,
					final int two,
					final int sum)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(AnItem.one,one),
				com.exedio.cope.SetValue.map(AnItem.two,two),
				com.exedio.cope.SetValue.map(AnItem.sum,sum),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getOne()
		{
			return AnItem.one.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getTwo()
		{
			return AnItem.two.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getSum()
		{
			return AnItem.sum.getMandatory(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(TYPE);
}

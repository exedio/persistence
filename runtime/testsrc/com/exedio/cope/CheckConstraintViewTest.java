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
		assertFails(
				() -> new AnItem(33, 44, 77),
				NullPointerException.class,
				"backingItem");
	}

	@Test void testCreateFails()
	{
		assertFails(
				() -> new AnItem(33, 44, 88),
				NullPointerException.class,
				"backingItem");
		assertEquals(asList(), TYPE.search());
	}

	@com.exedio.cope.instrument.WrapperType(indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class AnItem extends Item
	{
		@Wrapper(wrap="set", visibility=NONE)
		static final IntegerField one = new IntegerField();

		@Wrapper(wrap="set", visibility=NONE)
		static final IntegerField two = new IntegerField();

		@Wrapper(wrap="set", visibility=NONE)
		static final IntegerField sum = new IntegerField();

		static final CheckConstraint check = new CheckConstraint(one.plus(two).equal(sum));


		void set(
				final int one,
				final int two,
				final int sum)
		{
			set(
					AnItem.one.map(one),
					AnItem.two.map(two),
					AnItem.sum.map(sum)
			);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		AnItem(
					final int one,
					final int two,
					final int sum)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnItem.one.map(one),
				AnItem.two.map(two),
				AnItem.sum.map(sum),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final int getOne()
		{
			return AnItem.one.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final int getTwo()
		{
			return AnItem.two.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		final int getSum()
		{
			return AnItem.sum.getMandatory(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(TYPE);
}

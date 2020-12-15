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

import static com.exedio.cope.TypesBound.newType;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Day;
import java.util.TimeZone;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DayFieldWrongDefaultNowTest
{
	private final LogRule log = new LogRule(DayField.class);

	@Test void testIt()
	{
		assertBefore(AnItem.past,  AnItem.wrong);
		assertBefore(AnItem.wrong, AnItem.future);

		assertEqualsUnmodifiable(asList(
				"Very probably you called \"DayField.defaultTo(new Day())\". " +
				"This will not work as expected, use \"defaultToNow()\" instead."),
				AnItem.wrong.getSuspicions());
		assertEqualsUnmodifiable(asList(), AnItem.past.getSuspicions());
		assertEqualsUnmodifiable(asList(), AnItem.future.getSuspicions());

		log.assertEmpty();
		final Type<?> type = newType(AnItem.class);
		log.assertEmpty();

		new Model(type);
		log.assertError(
				"AnItem.wrong: Very probably you called \"DayField.defaultTo(new Day())\". " +
				"This will not work as expected, use \"defaultToNow()\" instead.");
		log.assertEmpty();

		assertEqualsUnmodifiable(asList(
				"Very probably you called \"DayField.defaultTo(new Day())\". " +
				"This will not work as expected, use \"defaultToNow()\" instead."),
				AnItem.wrong.getSuspicions());
		assertEqualsUnmodifiable(asList(), AnItem.past.getSuspicions());
		assertEqualsUnmodifiable(asList(), AnItem.future.getSuspicions());
	}

	@WrapperType(type=NONE, constructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@WrapperIgnore
		static final DayField wrong = new DayField().defaultTo(new Day(TimeZone.getDefault()));
		@WrapperIgnore
		static final DayField past = new DayField().defaultTo(new Day(2005, 10, 10));
		@WrapperIgnore
		static final DayField future = new DayField().defaultTo(new Day(8005, 10, 10));

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static void assertBefore(final DayField before, final DayField after)
	{
		assertTrue(
				before.getDefaultConstant().isBefore(
				after.getDefaultConstant()),
				before.getDefaultConstant() + " / " +
				after.getDefaultConstant());
	}
}

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

import static com.exedio.cope.DatePrecisionConditionTest.AnItem.TYPE;
import static com.exedio.cope.DatePrecisionConditionTest.AnItem.exact;
import static com.exedio.cope.DatePrecisionConditionTest.AnItem.rounded;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DatePrecisionConditionTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public DatePrecisionConditionTest()
	{
		super(MODEL);
	}

	@Test void testIt()
	{
		final AnItem i = new AnItem(date(12, 21,  0,  0,  0));

		assertLess(list( ), date(12, 20, 59, 59,999),  "<'1970-01-13 20:59:59.999'",  "<'1970-01-13 21:00:00.000'");
		assertLess(list( ), date(12, 21,  0,  0,  0),  "<'1970-01-13 21:00:00.000'",  "<'1970-01-13 21:00:00.000'");
		assertLess(list(i), date(12, 21,  0,  0,  1),  "<'1970-01-13 21:00:00.001'",  "<'1970-01-13 22:00:00.000'");
		assertLesE(list( ), date(12, 20, 59, 59,999), "<='1970-01-13 20:59:59.999'", "<='1970-01-13 20:00:00.000'");
		assertLesE(list(i), date(12, 21,  0,  0,  0), "<='1970-01-13 21:00:00.000'", "<='1970-01-13 21:00:00.000'");
		assertLesE(list(i), date(12, 21,  0,  0,  1), "<='1970-01-13 21:00:00.001'", "<='1970-01-13 21:00:00.000'");

		assertGreater(list(i), date(12, 20, 59, 59,999),  ">'1970-01-13 20:59:59.999'",  ">'1970-01-13 20:00:00.000'");
		assertGreater(list( ), date(12, 21,  0,  0,  0),  ">'1970-01-13 21:00:00.000'",  ">'1970-01-13 21:00:00.000'");
		assertGreater(list( ), date(12, 21,  0,  0,  1),  ">'1970-01-13 21:00:00.001'",  ">'1970-01-13 21:00:00.000'");
		assertGreateE(list(i), date(12, 20, 59, 59,999), ">='1970-01-13 20:59:59.999'", ">='1970-01-13 21:00:00.000'");
		assertGreateE(list(i), date(12, 21,  0,  0,  0), ">='1970-01-13 21:00:00.000'", ">='1970-01-13 21:00:00.000'");
		assertGreateE(list( ), date(12, 21,  0,  0,  1), ">='1970-01-13 21:00:00.001'", ">='1970-01-13 22:00:00.000'");
	}

	private static void assertLess(
			final List<AnItem> expected,
			final Date bound,
			final String   exactCondition,
			final String roundedCondition)
	{
		assertCondition(expected, exact.less(bound), rounded.less(bound), exactCondition, roundedCondition);
	}

	private static void assertLesE(
			final List<AnItem> expected,
			final Date bound,
			final String   exactCondition,
			final String roundedCondition)
	{
		assertCondition(expected, exact.lessOrEqual(bound), rounded.lessOrEqual(bound), exactCondition, roundedCondition);
	}

	private static void assertGreater(
			final List<AnItem> expected,
			final Date bound,
			final String   exactCondition,
			final String roundedCondition)
	{
		assertCondition(expected, exact.greater(bound), rounded.greater(bound), exactCondition, roundedCondition);
	}

	private static void assertGreateE(
			final List<AnItem> expected,
			final Date bound,
			final String   exactCondition,
			final String roundedCondition)
	{
		assertCondition(expected, exact.greaterOrEqual(bound), rounded.greaterOrEqual(bound), exactCondition, roundedCondition);
	}


	private static void assertCondition(
			final List<AnItem> expected,
			final Condition   exactCondition,
			final Condition roundedCondition,
			final String   exactConditionExpected,
			final String roundedConditionExpected)
	{
		assertEquals("AnItem.exact"   +   exactConditionExpected,   exactCondition.toString());
		assertEquals("AnItem.rounded" + roundedConditionExpected, roundedCondition.toString() );
		assertEquals(expected, TYPE.search(  exactCondition, TYPE.getThis(), true), "exact");
		assertEquals(expected, TYPE.search(roundedCondition, TYPE.getThis(), true), "rounded");
	}

	private static List<AnItem> list(final AnItem... elements)
	{
		return asList(elements);
	}

	static Date date(
			final long days,
			final int hours,
			final int minutes,
			final int seconds,
			final int milliseconds)
	{
		return new Date(
				(((days *
				  24 + less(  24, hours)) *
				  60 + less(  60, minutes)) *
				  60 + less(  60, seconds)) *
				1000 + less(1000, milliseconds));
	}

	private static int less(final int limit, final int value)
	{
		assertTrue(value<limit, "" + value + " " + limit);
		return value;
	}

	static final class AnItem extends Item
	{
		static final DateField exact   = new DateField().toFinal();
		static final DateField rounded = new DateField().toFinal().precisionHour();

		AnItem(final Date date)
		{
			this(date, date);
		}

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param exact the initial value for field {@link #exact}.
	 * @param rounded the initial value for field {@link #rounded}.
	 * @throws com.exedio.cope.DatePrecisionViolationException if rounded violates its precision constraint.
	 * @throws com.exedio.cope.MandatoryViolationException if exact, rounded is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AnItem(
				@javax.annotation.Nonnull final java.util.Date exact,
				@javax.annotation.Nonnull final java.util.Date rounded)
			throws
				com.exedio.cope.DatePrecisionViolationException,
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(AnItem.exact,exact),
			com.exedio.cope.SetValue.map(AnItem.rounded,rounded),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #exact}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getExact()
	{
		return AnItem.exact.get(this);
	}

	/**
	 * Returns the value of {@link #rounded}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Date getRounded()
	{
		return AnItem.rounded.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

}

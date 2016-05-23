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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Date;
import java.util.List;
import org.junit.Test;

public class DatePrecisionConditionTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public DatePrecisionConditionTest()
	{
		super(MODEL);
	}

	@Test public void testIt()
	{
		final AnItem i = new AnItem(date(12, 21,  0,  0,  0));

		assertLess(list( ), date(12, 20, 59, 59,999),  "<'1970/01/13 20:59:59.999'",  "<'1970/01/13 20:59:59.999'");
		assertLess(list( ), date(12, 21,  0,  0,  0),  "<'1970/01/13 21:00:00.000'",  "<'1970/01/13 21:00:00.000'");
		assertLess(list(i), date(12, 21,  0,  0,  1),  "<'1970/01/13 21:00:00.001'",  "<'1970/01/13 21:00:00.001'");
		assertLesE(list( ), date(12, 20, 59, 59,999), "<='1970/01/13 20:59:59.999'", "<='1970/01/13 20:59:59.999'");
		assertLesE(list(i), date(12, 21,  0,  0,  0), "<='1970/01/13 21:00:00.000'", "<='1970/01/13 21:00:00.000'");
		assertLesE(list(i), date(12, 21,  0,  0,  1), "<='1970/01/13 21:00:00.001'", "<='1970/01/13 21:00:00.001'");

		assertGreater(list(i), date(12, 20, 59, 59,999),  ">'1970/01/13 20:59:59.999'",  ">'1970/01/13 20:59:59.999'");
		assertGreater(list( ), date(12, 21,  0,  0,  0),  ">'1970/01/13 21:00:00.000'",  ">'1970/01/13 21:00:00.000'");
		assertGreater(list( ), date(12, 21,  0,  0,  1),  ">'1970/01/13 21:00:00.001'",  ">'1970/01/13 21:00:00.001'");
		assertGreateE(list(i), date(12, 20, 59, 59,999), ">='1970/01/13 20:59:59.999'", ">='1970/01/13 20:59:59.999'");
		assertGreateE(list(i), date(12, 21,  0,  0,  0), ">='1970/01/13 21:00:00.000'", ">='1970/01/13 21:00:00.000'");
		assertGreateE(list( ), date(12, 21,  0,  0,  1), ">='1970/01/13 21:00:00.001'", ">='1970/01/13 21:00:00.001'");
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
		assertEquals("exact"  , expected, TYPE.search(  exactCondition, TYPE.getThis(), true));
		assertEquals("rounded", expected, TYPE.search(roundedCondition, TYPE.getThis(), true));
	}

	private static List<AnItem> list(final AnItem... elements)
	{
		return asList(elements);
	}

	@SuppressFBWarnings("ICAST_INT_2_LONG_AS_INSTANT")
	static Date date(
			final int days,
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
		assertTrue("" + value + " " + limit, value<limit);
		return value;
	}

	static final class AnItem extends Item
	{
		static final DateField exact   = new DateField().toFinal();
		static final DateField rounded = new DateField().toFinal().hours();

		AnItem(final Date date)
		{
			this(date, date);
		}

	/**

	 **
	 * Creates a new AnItem with all the fields initially needed.
	 * @param exact the initial value for field {@link #exact}.
	 * @param rounded the initial value for field {@link #rounded}.
	 * @throws com.exedio.cope.MandatoryViolationException if exact, rounded is null.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	AnItem(
				final java.util.Date exact,
				final java.util.Date rounded)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.exact.map(exact),
			AnItem.rounded.map(rounded),
		});
	}/**

	 **
	 * Creates a new AnItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #exact}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.util.Date getExact()
	{
		return AnItem.exact.get(this);
	}/**

	 **
	 * Returns the value of {@link #rounded}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.util.Date getRounded()
	{
		return AnItem.rounded.get(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for anItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

}

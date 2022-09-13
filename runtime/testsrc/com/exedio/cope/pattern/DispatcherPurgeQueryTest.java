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

import static java.time.Duration.ofDays;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.Sources;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DispatcherPurgeQueryTest
{
	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();


	@BeforeEach void setUp()
	{
		@SuppressWarnings("unused")
		final Model model = DispatcherModelTest.MODEL; // initialize model

		clockRule.override(clock);
	}


	@Test void testDefault()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"and toTarget-noPurge='false' " +
				"and (" +
					"(toTarget-unpend-success='true' and toTarget-unpend-date<'1966-01-02 00:00:00.555') or " +
					"(toTarget-unpend-success='false' and toTarget-unpend-date<'1964-01-03 00:00:00.555')))",
				query(null, null));
		clock.assertEmpty();
	}

	@Test void testSame()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"and toTarget-noPurge='false' " +
				"and toTarget-unpend-date<'1969-12-12 00:00:00.555')",
				query(20, 20));
		clock.assertEmpty();
	}

	@Test void testDifferent()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"and toTarget-noPurge='false' " +
				"and (" +
					"(toTarget-unpend-success='true' and toTarget-unpend-date<'1969-12-12 00:00:00.555') or " +
					"(toTarget-unpend-success='false' and toTarget-unpend-date<'1969-12-22 00:00:00.555')))",
				query(20, 10));
		clock.assertEmpty();
	}

	@Test void testMinimum()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"and toTarget-noPurge='false' " +
				"and toTarget-unpend-date<'1969-12-31 00:00:00.555')",
				query(1, 1));
		clock.assertEmpty();
	}

	@Test void testOmitSuccess()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"and toTarget-noPurge='false' " +
				"and toTarget-unpend-success='false' and toTarget-unpend-date<'1969-12-22 00:00:00.555')",
				query(0, 10));
		clock.assertEmpty();
	}

	@Test void testOmitFinalFailure()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"and toTarget-noPurge='false' " +
				"and toTarget-unpend-success='true' and toTarget-unpend-date<'1969-12-12 00:00:00.555')",
				query(20, 0));
		clock.assertEmpty();
	}

	@Test void testOmitBoth()
	{
		assertEquals(
				null,
				query(0, 0));
		clock.assertEmpty();
	}

	@Test void testRestriction()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"and toTarget-noPurge='false' " +
				"and body='bodyValue' " +
				"and (" +
					"(toTarget-unpend-success='true' and toTarget-unpend-date<'1969-12-12 00:00:00.555') or " +
					"(toTarget-unpend-success='false' and toTarget-unpend-date<'1969-12-22 00:00:00.555')))",
				query(20, 10, DispatcherItem.body.equal("bodyValue")));
		clock.assertEmpty();
	}

	@Test void testRestrictionTrue()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"and toTarget-noPurge='false' " +
				"and (" +
					"(toTarget-unpend-success='true' and toTarget-unpend-date<'1969-12-12 00:00:00.555') or " +
					"(toTarget-unpend-success='false' and toTarget-unpend-date<'1969-12-22 00:00:00.555')))",
				query(20, 10, Condition.TRUE));
		clock.assertEmpty();
	}

	@Test void testRestrictionFalse()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where FALSE",
				query(20, 10, Condition.FALSE));
		clock.assertEmpty();
	}


	private static String query(final Integer success, final Integer failure)
	{
		return query(success, failure, Condition.TRUE);
	}

	private static String query(final Integer success, final Integer failure, final Condition restriction)
	{
		final Properties props = new Properties();
		if(success!=null)
			props.setProperty("retain.success",      ofDays(success).toString());
		if(failure!=null)
			props.setProperty("retain.finalFailure", ofDays(failure).toString());

		final Query<? extends Item> query = DispatcherItem.toTarget.purgeQuery(
				DispatcherPurgeProperties.factory().retainDaysDefault(4*365, 6*365).create(Sources.view(props, "description")),
				restriction);
		return query!=null ? query.toString() : null;
	}
}

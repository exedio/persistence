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

import static org.junit.Assert.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.util.Sources;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Properties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class DispatcherPurgeQueryTest
{
	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	@Rule public final RuleChain ruleChain = RuleChain.outerRule(clockRule);


	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
	@Before public void setUp()
	{
		@SuppressWarnings("unused")
		final Model model = DispatcherModelTest.MODEL; // initialize model

		clockRule.override(clock);
	}


	@Test public void testDefault()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND (" +
					"(toTarget-unpendSuccess='true' AND toTarget-unpendDate<'1966/01/02 00:00:00.555') OR " +
					"(toTarget-unpendSuccess='false' AND toTarget-unpendDate<'1964/01/03 00:00:00.555')))",
				query(null, null));
		clock.assertEmpty();
	}

	@Test public void testSame()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND toTarget-unpendDate<'1969/12/12 00:00:00.555')",
				query(20, 20));
		clock.assertEmpty();
	}

	@Test public void testDifferent()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND (" +
					"(toTarget-unpendSuccess='true' AND toTarget-unpendDate<'1969/12/12 00:00:00.555') OR " +
					"(toTarget-unpendSuccess='false' AND toTarget-unpendDate<'1969/12/22 00:00:00.555')))",
				query(20, 10));
		clock.assertEmpty();
	}

	@Test public void testMinimum()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND toTarget-unpendDate<'1969/12/31 00:00:00.555')",
				query(1, 1));
		clock.assertEmpty();
	}

	@Test public void testOmitSuccess()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND toTarget-unpendSuccess='false' AND toTarget-unpendDate<'1969/12/22 00:00:00.555')",
				query(0, 10));
		clock.assertEmpty();
	}

	@Test public void testOmitFinalFailure()
	{
		clock.add(555);
		assertEquals(
				"select this from DispatcherItem " +
				"where (toTarget-pending='false' " +
				"AND toTarget-noPurge='false' " +
				"AND toTarget-unpendSuccess='true' AND toTarget-unpendDate<'1969/12/12 00:00:00.555')",
				query(20, 0));
		clock.assertEmpty();
	}

	@Test public void testOmitBoth()
	{
		assertEquals(
				null,
				query(0, 0));
		clock.assertEmpty();
	}

	@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
	private static String query(final Integer success, final Integer failure)
	{
		final Properties props = new Properties();
		if(success!=null)
			props.setProperty("delayDays.success",      Integer.toString(success));
		if(failure!=null)
			props.setProperty("delayDays.finalFailure", Integer.toString(failure));

		final Query<? extends Item> query = DispatcherItem.toTarget.purgeQuery(
				DispatcherPurgeProperties.factory().delayDaysDefault(4*365, 6*365).create(Sources.view(props, "description")));
		return query!=null ? query.toString() : null;
	}
}

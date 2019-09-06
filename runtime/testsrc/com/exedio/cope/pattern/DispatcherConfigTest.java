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

import static com.exedio.cope.Condition.TRUE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Condition;
import com.exedio.cope.IntegerField;
import com.exedio.cope.pattern.Dispatcher.Config;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
public class DispatcherConfigTest
{
	@Test void testDefault()
	{
		final Config config = new Config();
		assertEquals(5, config.getFailureLimit());
		assertEquals(1000, config.getSearchSize());
		assertSame(TRUE, config.getNarrowCondition());
	}
	@Test void testOk()
	{
		final Config config = new Config(3, 2);
		assertEquals(3, config.getFailureLimit());
		assertEquals(2, config.getSearchSize());
		assertSame(TRUE, config.getNarrowCondition());
	}
	@Test void testMinimal()
	{
		final Config config = new Config(1, 1);
		assertEquals(1, config.getFailureLimit());
		assertEquals(1, config.getSearchSize());
		assertSame(TRUE, config.getNarrowCondition());
	}
	@Test void testFailureLimitZero()
	{
		assertFails(() ->
			new Config(0, 0),
			IllegalArgumentException.class,
			"failureLimit must be greater zero, but was 0");
	}
	@Test void testFailureLimitNegative()
	{
		assertFails(() ->
			new Config(-10, 0),
			IllegalArgumentException.class,
			"failureLimit must be greater zero, but was -10");
	}
	@Test void testSearchSizeZero()
	{
		assertFails(() ->
			new Config(1000, 0),
			IllegalArgumentException.class,
			"searchSize must be greater zero, but was 0");
	}
	@Test void testSearchSizeNegative()
	{
		assertFails(() ->
			new Config(1000, -10),
			IllegalArgumentException.class,
			"searchSize must be greater zero, but was -10");
	}
	@Test void testNarrow()
	{
		final Config config0 = new Config(55, 66);
		assertEquals(55, config0.getFailureLimit());
		assertEquals(66, config0.getSearchSize());
		assertSame(TRUE, config0.getNarrowCondition());

		final IntegerField f = new IntegerField();
		final Condition condition1 = f.equal(1);
		final Config config1 = config0.narrow(condition1);
		assertNotSame(config0, config1);
		assertEquals(55, config1.getFailureLimit());
		assertEquals(66, config1.getSearchSize());
		assertSame(condition1, config1.getNarrowCondition());
		assertEquals(f+"='1'", config1.getNarrowCondition().toString());

		final Condition condition2 = f.equal(2);
		final Config config2 = config1.narrow(condition2);
		assertNotSame(config1, config2);
		assertEquals(55, config2.getFailureLimit());
		assertEquals(66, config2.getSearchSize());
		assertEquals(condition1.and(condition2), config2.getNarrowCondition());
		assertEquals("("+f+"='1' AND "+f+"='2')", config2.getNarrowCondition().toString());
	}
	@Test void testNarrowReset()
	{
		final Config config0 = new Config(55, 66);
		assertEquals(55, config0.getFailureLimit());
		assertEquals(66, config0.getSearchSize());
		assertSame(TRUE, config0.getNarrowCondition());

		final Condition condition = new IntegerField().equal(1);
		final Config config1 = config0.narrow(condition);
		assertNotSame(config0, config1);
		assertEquals(55, config1.getFailureLimit());
		assertEquals(66, config1.getSearchSize());
		assertSame(condition, config1.getNarrowCondition());

		final Config configR = config1.resetNarrow();
		assertNotSame(config1, configR);
		assertNotSame(config0, configR);
		assertEquals(55, configR.getFailureLimit());
		assertEquals(66, configR.getSearchSize());
		assertSame(TRUE, configR.getNarrowCondition());
	}
	@Test void testNarrowNull()
	{
		final Config c = new Config();
		assertFails(() ->
			c.narrow(null),
			NullPointerException.class,
			"other");
	}
}

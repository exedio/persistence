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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Condition;
import com.exedio.cope.IntegerField;
import com.exedio.cope.pattern.Dispatcher.Config;
import org.junit.jupiter.api.Test;

public class DispatcherConfigTest
{
	@Test void testDefault()
	{
		final Config config = new Config();
		assertEquals(5, config.getFailureLimit());
		assertEquals(1000, config.getSearchSize());
		assertEquals(15, config.getSessionLimit());
		assertSame(TRUE, config.getNarrowCondition());
		assertEquals(false, config.deleteOnSuccess);
	}
	@Test void testOk()
	{
		final Config config = new Config(3, 2);
		assertEquals(3, config.getFailureLimit());
		assertEquals(2, config.getSearchSize());
		assertEquals(15, config.getSessionLimit());
		assertSame(TRUE, config.getNarrowCondition());
		assertEquals(false, config.deleteOnSuccess);
	}
	@Test void testMinimal()
	{
		final Config config = new Config(1, 1);
		assertEquals(1, config.getFailureLimit());
		assertEquals(1, config.getSearchSize());
		assertEquals(15, config.getSessionLimit());
		assertSame(TRUE, config.getNarrowCondition());
		assertEquals(false, config.deleteOnSuccess);
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
	@Test void testSessionLimit()
	{
		final Config config0 = new Config();
		assertEquals(5, config0.getFailureLimit());
		assertEquals(1000, config0.getSearchSize());
		assertEquals(15, config0.getSessionLimit());
		assertSame(TRUE, config0.getNarrowCondition());
		assertEquals(false, config0.deleteOnSuccess);

		final Config config1 = config0.sessionLimit(88);
		assertEquals(5, config1.getFailureLimit());
		assertEquals(1000, config1.getSearchSize());
		assertEquals(88, config1.getSessionLimit());
		assertSame(TRUE, config1.getNarrowCondition());
		assertEquals(false, config1.deleteOnSuccess);
	}
	@Test void testSessionLimitZero()
	{
		final Config config = new Config();
		assertFails(() ->
			config.sessionLimit(0),
			IllegalArgumentException.class,
			"sessionLimit must be greater zero, but was 0");
	}
	@Test void testSessionLimitNegative()
	{
		final Config config = new Config();
		assertFails(() ->
			config.sessionLimit(-10),
			IllegalArgumentException.class,
			"sessionLimit must be greater zero, but was -10");
	}
	@Test void testNarrow()
	{
		final Config config0 = new Config(55, 66);
		assertEquals(55, config0.getFailureLimit());
		assertEquals(66, config0.getSearchSize());
		assertEquals(15, config0.getSessionLimit());
		assertSame(TRUE, config0.getNarrowCondition());
		assertEquals(false, config0.deleteOnSuccess);

		final IntegerField f = new IntegerField();
		final Condition condition1 = f.equal(1);
		final Config config1 = config0.narrow(condition1);
		assertNotSame(config0, config1);
		assertEquals(55, config1.getFailureLimit());
		assertEquals(66, config1.getSearchSize());
		assertEquals(15, config1.getSessionLimit());
		assertSame(condition1, config1.getNarrowCondition());
		assertEquals(f+"='1'", config1.getNarrowCondition().toString());
		assertEquals(false, config1.deleteOnSuccess);

		final Condition condition2 = f.equal(2);
		final Config config2 = config1.narrow(condition2);
		assertNotSame(config1, config2);
		assertEquals(55, config2.getFailureLimit());
		assertEquals(66, config2.getSearchSize());
		assertEquals(15, config2.getSessionLimit());
		assertEquals(condition1.and(condition2), config2.getNarrowCondition());
		assertEquals("("+f+"='1' and "+f+"='2')", config2.getNarrowCondition().toString());
		assertEquals(false, config2.deleteOnSuccess);
	}
	@Test void testNarrowReset()
	{
		final Config config0 = new Config(55, 66);
		assertEquals(55, config0.getFailureLimit());
		assertEquals(66, config0.getSearchSize());
		assertEquals(15, config0.getSessionLimit());
		assertSame(TRUE, config0.getNarrowCondition());
		assertEquals(false, config0.deleteOnSuccess);

		final Condition condition = new IntegerField().equal(1);
		final Config config1 = config0.narrow(condition);
		assertNotSame(config0, config1);
		assertEquals(55, config1.getFailureLimit());
		assertEquals(66, config1.getSearchSize());
		assertEquals(15, config1.getSessionLimit());
		assertSame(condition, config1.getNarrowCondition());
		assertEquals(false, config1.deleteOnSuccess);

		final Config configR = config1.resetNarrow();
		assertNotSame(config1, configR);
		assertNotSame(config0, configR);
		assertEquals(55, configR.getFailureLimit());
		assertEquals(66, configR.getSearchSize());
		assertEquals(15, configR.getSessionLimit());
		assertSame(TRUE, configR.getNarrowCondition());
		assertEquals(false, configR.deleteOnSuccess);
	}
	@Test void testNarrowNull()
	{
		final Config c = new Config();
		assertFails(() ->
			c.narrow(null),
			NullPointerException.class,
			"other");
	}
	@Test void testDeleteOnSuccess()
	{
		final Config config0 = new Config();
		assertEquals(5, config0.getFailureLimit());
		assertEquals(1000, config0.getSearchSize());
		assertEquals(15, config0.getSessionLimit());
		assertSame(TRUE, config0.getNarrowCondition());
		assertEquals(false, config0.deleteOnSuccess);

		final Config config1 = config0.deleteOnSuccess();
		assertEquals(5, config1.getFailureLimit());
		assertEquals(1000, config1.getSearchSize());
		assertEquals(15, config1.getSessionLimit());
		assertSame(TRUE, config1.getNarrowCondition());
		assertEquals(true, config1.deleteOnSuccess);
	}

	private static final Condition TRUE = Condition.ofTrue();
}

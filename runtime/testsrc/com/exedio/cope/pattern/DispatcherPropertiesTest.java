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

import static com.exedio.cope.pattern.DispatcherProperties.factory;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.pattern.Dispatcher.Config;
import com.exedio.cope.pattern.DispatcherProperties.Factory;
import com.exedio.cope.util.Sources;
import org.junit.jupiter.api.Test;

public class DispatcherPropertiesTest
{
	@Test void testDefault()
	{
		final Config config = factory().create(Sources.EMPTY).get();
		assertEquals(5,    config.getFailureLimit());
		assertEquals(1000, config.getSearchSize());
		assertEquals(15,   config.getSessionLimit());
		assertEquals(false, config.deleteOnSuccess);
	}

	@Test void testCustom()
	{
		final Config config = factory().create(cascade(
				single("failureLimit", 55),
				single("searchSize", 66),
				single("sessionLimit", 77),
				single("deleteOnSuccess", true))).get();
		assertEquals(55, config.getFailureLimit());
		assertEquals(66, config.getSearchSize());
		assertEquals(77, config.getSessionLimit());
		assertEquals(true, config.deleteOnSuccess);
	}

	@Test void testFactoryFailureLimit()
	{
		final Config config = factory().failureLimit(55).create(Sources.EMPTY).get();
		assertEquals(55,   config.getFailureLimit());
		assertEquals(1000, config.getSearchSize());
		assertEquals(15,   config.getSessionLimit());
		assertEquals(false, config.deleteOnSuccess);
	}
	@Test void testFactoryFailureLimitMinimum()
	{
		final Config config = factory().failureLimit(1).create(Sources.EMPTY).get();
		assertEquals(1,    config.getFailureLimit());
		assertEquals(1000, config.getSearchSize());
		assertEquals(15,   config.getSessionLimit());
		assertEquals(false, config.deleteOnSuccess);
	}
	@Test void testFactoryFailureLimitMinimumExceeded()
	{
		final Factory factory = factory();
		assertFails(
				() -> factory.failureLimit(0),
				IllegalArgumentException.class,
				"failureLimit must be greater zero, but was 0");
	}

	@Test void testFactorySessionLimit()
	{
		final Config config = factory().sessionLimit(77).create(Sources.EMPTY).get();
		assertEquals(5,    config.getFailureLimit());
		assertEquals(1000, config.getSearchSize());
		assertEquals(77,   config.getSessionLimit());
		assertEquals(false, config.deleteOnSuccess);
	}
	@Test void testFactorySessionLimitMinimum()
	{
		final Config config = factory().sessionLimit(1).create(Sources.EMPTY).get();
		assertEquals(5,    config.getFailureLimit());
		assertEquals(1000, config.getSearchSize());
		assertEquals(1,    config.getSessionLimit());
		assertEquals(false, config.deleteOnSuccess);
	}
	@Test void testFactorySessionLimitMinimumExceeded()
	{
		final Factory factory = factory();
		assertFails(
				() -> factory.sessionLimit(0),
				IllegalArgumentException.class,
				"sessionLimit must be greater zero, but was 0");
	}

	@Test void testFactoryDeleteOnSuccess()
	{
		final Config config = factory().deleteOnSuccess().create(Sources.EMPTY).get();
		assertEquals(5,    config.getFailureLimit());
		assertEquals(1000, config.getSearchSize());
		assertEquals(15,   config.getSessionLimit());
		assertEquals(true, config.deleteOnSuccess);
	}
}

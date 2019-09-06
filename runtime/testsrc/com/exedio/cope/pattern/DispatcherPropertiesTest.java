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
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.pattern.Dispatcher.Config;
import com.exedio.cope.util.Sources;
import org.junit.jupiter.api.Test;

public class DispatcherPropertiesTest
{
	@Test void testDefault()
	{
		final Config config = factory().create(Sources.EMPTY).get();
		assertEquals(5,    config.getFailureLimit());
		assertEquals(1000, config.getSearchSize());
	}

	@Test void testCustom()
	{
		final Config config = factory().create(cascade(
				single("failureLimit", 55),
				single("searchSize", 66))).get();
		assertEquals(55, config.getFailureLimit());
		assertEquals(66, config.getSearchSize());
	}
}

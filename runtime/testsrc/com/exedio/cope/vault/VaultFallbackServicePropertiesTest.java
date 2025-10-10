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

package com.exedio.cope.vault;

import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.junit.AssertionErrorVaultService;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vault.VaultFallbackService.Props;
import org.junit.jupiter.api.Test;

public class VaultFallbackServicePropertiesTest
{
	@Test
	void testOrder()
	{
		final Source source = describe("DESC", cascade(
				single("main", MyService.class),
				single("fallbacks.0", MyService.class)
		));

		final Props p = new Props(source);
		assertEquals(asList(
				"main",
				"fallbacks.count",
				"fallbacks.0",
				"copyReferenceToMain"),
				p.getFields().stream().map(Properties.Field::getKey).toList());
	}

	@Test
	void testOrder3()
	{
		final Source source = describe("DESC", cascade(
				single("main", MyService.class),
				single("fallbacks.count", 3),
				single("fallbacks.0", MyService.class),
				single("fallbacks.1", MyService.class),
				single("fallbacks.2", MyService.class)
		));

		final Props p = new Props(source);
		assertEquals(asList(
						"main",
						"fallbacks.count",
						"fallbacks.0",
						"fallbacks.1",
						"fallbacks.2",
						"copyReferenceToMain"),
				p.getFields().stream().map(Properties.Field::getKey).toList());
	}

	private static final class MyService extends AssertionErrorVaultService
	{
		MyService(@SuppressWarnings("unused") final VaultServiceParameters parameters)
		{
		}
	}
}

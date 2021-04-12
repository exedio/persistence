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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.vault.VaultDirectory.Properties;
import com.exedio.cope.vaultmock.VaultMockService;
import org.junit.jupiter.api.Test;

public class VaultDirectoryTest
{
	@Test void flat()
	{
		final VaultDirectory vd = VaultDirectory.instance(
				null,
				SERVICE_PARAMETERS);

		assertEquals(null, vd.directoryToBeCreated("0123456"));
		assertEquals("0123456", vd.path("0123456"));
		assertEquals("flat", vd.toString());
	}

	@Test void lengthOne()
	{
		final VaultDirectory vd = VaultDirectory.instance(
				new Properties(cascade(single("length", 1)), true),
				SERVICE_PARAMETERS);

		assertEquals("0", vd.directoryToBeCreated("0123456"));
		assertEquals("0/123456", vd.path("0123456"));
		assertEquals("l=1", vd.toString());
	}

	@Test void lengthTwo()
	{
		final VaultDirectory vd = VaultDirectory.instance(
				new Properties(cascade(single("length", 2)), true),
				SERVICE_PARAMETERS);

		assertEquals("01", vd.directoryToBeCreated("0123456"));
		assertEquals("01/23456", vd.path("0123456"));
		assertEquals("l=2", vd.toString());
	}

	@Test void lengthThree()
	{
		final VaultDirectory vd = VaultDirectory.instance(
				new Properties(cascade(single("length", 3)), true),
				SERVICE_PARAMETERS);

		assertEquals("012", vd.directoryToBeCreated("0123456"));
		assertEquals("012/3456", vd.path("0123456"));
		assertEquals("l=3", vd.toString());
	}

	@Test void lengthMax()
	{
		final VaultDirectory vd = VaultDirectory.instance(
				new Properties(cascade(single("length", 31)), true),
				SERVICE_PARAMETERS);

		assertEquals("0123456789abcdef0123456789abcde", vd.directoryToBeCreated("0123456789abcdef0123456789abcdef"));
		assertEquals("0123456789abcdef0123456789abcde/f", vd.path("0123456789abcdef0123456789abcdef"));
		assertEquals("l=31", vd.toString());
	}

	@Test void lengthExceeded()
	{
		final Properties properties =
				new Properties(cascade(single("length", 32)), true);
		assertFails(
				() -> VaultDirectory.instance(properties, SERVICE_PARAMETERS),
				IllegalArgumentException.class,
				"directory.length must be less the length of algorithm MD5, but was 32>=32");
	}

	private static final VaultServiceParameters SERVICE_PARAMETERS =
			new VaultServiceParameters(
					VaultProperties.factory().create(cascade(
							single("algorithm", "MD5"),
							single("service", VaultMockService.class))),
					"testServiceKey",
					true);
}

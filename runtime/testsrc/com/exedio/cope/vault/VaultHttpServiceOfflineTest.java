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

import com.exedio.cope.vault.VaultHttpService.Props;
import java.io.InputStream;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class VaultHttpServiceOfflineTest
{
	@Test void mustNotBeWritable()
	{
		final Props props = new Props(cascade(
				single("root", "http://VaultHttpServicePropertiesTest.invalid")));
		final VaultServiceParameters params = new VaultServiceParameters(VaultProperties.factory().create(cascade(
				single("algorithm", "MD5"),
				single("default.service", VaultHttpService.class),
				single("default.service.root", "http://VaultHttpServicePropertiesTest.invalid"))),
				"testBucket",
				true,  // writable
				() -> { throw new AssertionFailedError(); }); // markPut
		//noinspection resource OK: does not allocate resources
		assertFails(
				() -> new VaultHttpService(params, props),
				IllegalArgumentException.class,
				CLASS_NAME + " does not support isWritable");
	}

	@Test void isNotWritable()
	{
		final Props props = new Props(cascade(
				single("root", "http://VaultHttpServicePropertiesTest.invalid")));
		final VaultServiceParameters params = new VaultServiceParameters(VaultProperties.factory().create(cascade(
				single("algorithm", "MD5"),
				single("default.service", VaultHttpService.class),
				single("default.service.root", "http://VaultHttpServicePropertiesTest.invalid"))),
				"testBucket",
				false,  // writable
				() -> { throw new AssertionFailedError(); }); // markPut
		try(VaultHttpService s = new VaultHttpService(params, props))
		{
			assertFails(
					() -> s.put("abcdefghijklmnopq", (byte[])null),
					IllegalStateException.class,
					"not writable: abcdefghijklmnopxx17 " + CLASS_NAME);
			assertFails(
					() -> s.put("abcdefghijklmnopq", (InputStream)null),
					IllegalStateException.class,
					"not writable: abcdefghijklmnopxx17 " + CLASS_NAME);
			assertFails(
					() -> s.put("abcdefghijklmnopq", (Path)null),
					IllegalStateException.class,
					"not writable: abcdefghijklmnopxx17 " + CLASS_NAME);
		}
	}

	private static final String CLASS_NAME = VaultHttpService.class.getName();
}

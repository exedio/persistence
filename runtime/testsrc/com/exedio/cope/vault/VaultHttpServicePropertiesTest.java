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

import static com.exedio.cope.RuntimeAssert.probes;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vault.VaultHttpService.Props;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.Test;

public class VaultHttpServicePropertiesTest
{
	@Test void probe()
	{
		final Source source = describe("DESC", cascade(
				single("root", "http://VaultHttpServicePropertiesTest.invalid")));

		final Props p = new Props(source);
		assertEquals(asList(
				"root",
				"directory",
				"directory.length",
				"connectTimeout",
				"readTimeout",
				"followRedirects"),
				p.getFields().stream().map(Field::getKey).collect(toList()));
		assertEquals("http://VaultHttpServicePropertiesTest.invalid", p.root);

		final Map<String,Callable<?>> probes = probes(p);
		assertEquals(asList(
				"root.Exists"),
				new ArrayList<>(probes.keySet()));

		final Callable<?> rootExists = probes.get("root.Exists");
		assertFails(
				rootExists::call,
				UnknownHostException.class,
				"VaultHttpServicePropertiesTest.invalid");
	}
	@Test void rootTrailingSlash()
	{
		final Source source = describe("DESC", cascade(
				single("root", "http://VaultHttpServicePropertiesTest.invalid/")));

		assertFails(
				() -> new Props(source),
				IllegalPropertiesException.class,
				"property root in DESC must not end with slash, " +
				"but was >http://VaultHttpServicePropertiesTest.invalid/<");
	}
	@Test void rootMalformed()
	{
		final Source source = describe("DESC", cascade(
				single("root", "http//VaultHttpServicePropertiesTest.invalid")));

		final IllegalPropertiesException e = assertFails(
				() -> new Props(source),
				IllegalPropertiesException.class,
				"property root in DESC is malformed: >http//VaultHttpServicePropertiesTest.invalid<");
		assertEquals("no protocol: http//VaultHttpServicePropertiesTest.invalid", e.getCause().getMessage());
		assertEquals(MalformedURLException.class, e.getCause().getClass());
	}
}

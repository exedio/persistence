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

import static com.exedio.cope.RuntimeAssert.assumeNotGithub;
import static com.exedio.cope.RuntimeAssert.probes;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.describe;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static java.time.Duration.ofSeconds;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Field;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.vault.VaultHttpService.Props;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.junit.jupiter.api.Test;

public class VaultHttpServicePropertiesTest
{
	@Test void probe() throws URISyntaxException
	{
		final Source source = describe("DESC", cascade(
				single("root", "http://VaultHttpServicePropertiesTest.invalid")));

		final Props p = new Props(source);
		assertEquals(asList(
				"root",
				"directory",
				"directory.length",
				"version",
				"connectTimeout",
				"requestTimeout",
				"followRedirects"),
				p.getFields().stream().map(Field::getKey).collect(toList()));
		assertEquals("http://VaultHttpServicePropertiesTest.invalid", p.root);
		assertEquals(HttpClient.Version.HTTP_2, p.client.version());
		assertEquals(Optional.of(ofSeconds(3)), p.client.connectTimeout());
		assertEquals(Optional.of(ofSeconds(3)), p.newRequest(new URI(p.root), null).timeout());
		assertEquals(HttpClient.Redirect.NEVER, p.client.followRedirects());

		final Map<String,Callable<?>> probes = probes(p);
		assertEquals(asList(
				"root.Exists"),
				new ArrayList<>(probes.keySet()));

		final Callable<?> rootExists = probes.get("root.Exists");
		assumeNotGithub();
		assertFails(
				rootExists::call,
				ConnectException.class, // other exception happens outside of jenkins when network is available
				"Connection refused");
	}
	@Test void nonDefault() throws URISyntaxException
	{
		final Source source = describe("DESC", cascade(
				single("root", "http://VaultHttpServicePropertiesTest.invalid"),
				single("version", "HTTP_1_1"),
				single("connectTimeout", "PT33S"),
				single("requestTimeout", "PT44S"),
				single("followRedirects", "ALWAYS")));

		final Props p = new Props(source);
		assertEquals("http://VaultHttpServicePropertiesTest.invalid", p.root);
		assertEquals(HttpClient.Version.HTTP_1_1, p.client.version());
		assertEquals(Optional.of(ofSeconds(33)), p.client.connectTimeout());
		assertEquals(Optional.of(ofSeconds(44)), p.newRequest(new URI(p.root), null).timeout());
		assertEquals(HttpClient.Redirect.ALWAYS, p.client.followRedirects());
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
	@Test void uriSyntax()
	{
		final Source source = describe("DESC", cascade(
				single("root", ":VaultHttpServicePropertiesTest.invalid")));

		final IllegalPropertiesException e = assertFails(
				() -> new Props(source),
				IllegalPropertiesException.class,
				"property root in DESC syntax exception: >:VaultHttpServicePropertiesTest.invalid<");
		assertEquals("Expected scheme name at index 0: :VaultHttpServicePropertiesTest.invalid", e.getCause().getMessage());
		assertEquals(URISyntaxException.class, e.getCause().getClass());
	}
	@Test void rootNonHttp()
	{
		final Source source = describe("DESC", cascade(
				single("root", "mailto:oops@VaultHttpServicePropertiesTest.invalid")));

		final IllegalPropertiesException e = assertFails(
				() -> new Props(source),
				IllegalPropertiesException.class,
				"property root in DESC must be a uri with scheme http(s), " +
				"but was >mailto:oops@VaultHttpServicePropertiesTest.invalid< with scheme >mailto<");
		assertEquals(null, e.getCause());
	}
}

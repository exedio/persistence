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

import static com.exedio.cope.pattern.TextUrlFilterDelegatorItem.fertig;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.nio.charset.StandardCharsets.UTF_8;

import org.junit.jupiter.api.Test;

public class TextUrlFilterDelegatorModelTest
{
	@Test void testRawNull()
	{
		assertFails(
				() -> new TextUrlFilterDelegator(null, null, null, null, null, null),
				NullPointerException.class,
				"source");
	}

	@Test void testDelegateNull()
	{
		final Media roh2 = new Media();
		assertFails(
				() -> new TextUrlFilterDelegator(roh2, null, null, null, null, null),
				NullPointerException.class,
				"delegate");
	}


	@Test void testSupportedContentTypeNull()
	{
		final Media roh2 = new Media();
		assertFails(
				() -> new TextUrlFilterDelegator(roh2, fertig, null, null, null, null),
				NullPointerException.class,
				"supportedContentType");
	}

	@Test void testSupportedContentTypeEmpty()
	{
		final Media roh2 = new Media();
		assertFails(
				() -> new TextUrlFilterDelegator(roh2, fertig, "", null, null, null),
				IllegalArgumentException.class,
				"supportedContentType must not be empty");
	}

	@Test void testCharsetNull()
	{
		final Media roh2 = new Media();
		assertFails(
				() -> new TextUrlFilterDelegator(roh2, fertig, "text/plain", null, null, null),
				NullPointerException.class,
				"charset");
	}

	@Test void testPasteStartNull()
	{
		final Media roh2 = new Media();
		assertFails(
				() -> new TextUrlFilterDelegator(roh2, fertig, "text/plain", UTF_8, null, null),
				NullPointerException.class,
				"pasteStart");
	}

	@Test void testPasteStartEmpty()
	{
		final Media roh2 = new Media();
		assertFails(
				() -> new TextUrlFilterDelegator(roh2, fertig, "text/plain", UTF_8, "", null),
				IllegalArgumentException.class,
				"pasteStart must not be empty");
	}

	@Test void testPasteStopNull()
	{
		final Media roh2 = new Media();
		assertFails(
				() -> new TextUrlFilterDelegator(roh2, fertig, "text/plain", UTF_8, "(", null),
				NullPointerException.class,
				"pasteStop");
	}

	@Test void testPasteStopEmpty()
	{
		final Media roh2 = new Media();
		assertFails(
				() -> new TextUrlFilterDelegator(roh2, fertig, "text/plain", UTF_8, "(", ""),
				IllegalArgumentException.class,
				"pasteStop must not be empty");
	}
}

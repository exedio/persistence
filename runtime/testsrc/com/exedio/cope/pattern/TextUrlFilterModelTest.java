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
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.StringField;
import org.junit.jupiter.api.Test;

public class TextUrlFilterModelTest
{
	@Test void testRawNull()
	{
		assertFails(
				() -> new TextUrlFilter(null, null, null, null, null, null, null),
				NullPointerException.class,
				"source");
	}
	@Test void testSupportedContentTypeNull()
	{
		final Media roh = new Media();
		assertFails(
				() -> new TextUrlFilter(roh, null, null, null, null, null, null),
				NullPointerException.class,
				"supportedContentType");
	}
	@Test void testSupportedContentTypeEmpty()
	{
		final Media roh = new Media();
		assertFails(
				() -> new TextUrlFilter(roh, "", null, null, null, null, null),
				IllegalArgumentException.class,
				"supportedContentType must not be empty");
	}
	@Test void testCharsetNull()
	{
		final Media roh = new Media();
		assertFails(
				() -> new TextUrlFilter(roh, "text/plain", null, null, null, null, null),
				NullPointerException.class,
				"charset");
	}
	@Test void testPasteStartNull()
	{
		final Media roh = new Media();
		assertFails(
				() -> new TextUrlFilter(roh, "text/plain", UTF_8, null, null, null, null),
				NullPointerException.class,
				"pasteStart");
	}
	@Test void testPasteStartEmpty()
	{
		final Media roh = new Media();
		assertFails(
				() -> new TextUrlFilter(roh, "text/plain", UTF_8, "", null, null, null),
				IllegalArgumentException.class,
				"pasteStart must not be empty");
	}
	@Test void testPasteStopNull()
	{
		final Media roh = new Media();
		assertFails(
				() -> new TextUrlFilter(roh, "text/plain", UTF_8, "(", null, null, null),
				NullPointerException.class,
				"pasteStop");
	}
	@Test void testPasteStopEmpty()
	{
		final Media roh = new Media();
		assertFails(
				() -> new TextUrlFilter(roh, "text/plain", UTF_8, "(", "", null, null),
				IllegalArgumentException.class,
				"pasteStop must not be empty");
	}
	@Test void testPasteKeyNull()
	{
		final Media roh = new Media();
		assertFails(
				() -> new TextUrlFilter(roh, "text/plain", UTF_8, "(", ")", null, null),
				NullPointerException.class,
				"pasteKey");
	}
	@Test void testPasteKeyOptional()
	{
		final Media roh = new Media();
		final StringField pasteKey = new StringField().optional();
		final Media pasteValue = new Media();
		assertFails(
				() -> new TextUrlFilter(roh, "text/plain", UTF_8, "(", ")", pasteKey, pasteValue),
				IllegalArgumentException.class,
				"pasteKey must be mandatory");
	}
	@Test void testPasteKeyUnique()
	{
		final Media roh = new Media();
		final StringField pasteKey = new StringField().unique();
		final Media pasteValue = new Media();
		assertFails(
				() -> new TextUrlFilter(roh, "text/plain", UTF_8, "(", ")", pasteKey, pasteValue),
				IllegalArgumentException.class,
				"pasteKey must not be unique");
	}
	@Test void testPasteValueNull()
	{
		final Media roh = new Media();
		assertFails(
				() -> new TextUrlFilter(roh, "text/plain", UTF_8, "(", ")", new StringField(), null),
				NullPointerException.class,
				"pasteValue");
	}
	@Test void testPasteValueFinal()
	{
		final Media roh = new Media();
		final StringField pasteKey = new StringField();
		final Media pasteValue = new Media().toFinal();
		assertFails(
				() -> new TextUrlFilter(roh, "text/plain", UTF_8, "(", ")", pasteKey, pasteValue),
				IllegalArgumentException.class,
				"pasteValue must not be final");
	}
	@Test void testPasteValueOptional()
	{
		final Media roh = new Media();
		final StringField pasteKey = new StringField();
		final Media pasteValue = new Media().optional();
		assertFails(
				() -> new TextUrlFilter(roh, "text/plain", UTF_8, "(", ")", pasteKey, pasteValue),
				IllegalArgumentException.class,
				"pasteValue must be mandatory");
	}
	@Test void testMandatory()
	{
		assertEquals(false, TextUrlFilterItem.fertig.isMandatory());
		assertEquals(
			true,
			new TextUrlFilter(new Media().contentType("eins"), "eins", UTF_8, "{", "}", new StringField(), new Media()).isMandatory()
		);
		assertEquals(
			false,
			new TextUrlFilter(new Media().contentTypes("eins", "zwei"), "eins", UTF_8, "{", "}", new StringField(), new Media()).isMandatory()
		);
		assertEquals(
			false,
			new TextUrlFilter(new Media().contentType("eins").optional(), "eins", UTF_8, "{", "}", new StringField(), new Media()).isMandatory()
		);
	}
}

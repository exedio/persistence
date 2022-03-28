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
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MediaResponseTest
{
	private ArrayList<String> headers;
	private MediaResponse response;

	@BeforeEach void setUp()
	{
		headers = new ArrayList<>();
		response = new MediaResponse(new AssertionFailedHttpServletResponse()
		{
			@Override public void addHeader(final String name, final String value)
			{
				headers.add(name);
				headers.add(value);
			}
		});
	}

	@Test void testOk()
	{
		response.addHeader("Blah", "Blubb");
		assertEquals(asList("Blah", "Blubb"), headers);
	}
	@Test void testNameNull()
	{
		assertFails(
				() -> response.addHeader(null, null),
				NullPointerException.class, "name");
		assertEquals(asList(), headers);
	}
	@Test void testNameEmpty()
	{
		assertFails(
				() -> response.addHeader("", null),
				IllegalArgumentException.class,
				"name must not be empty");
		assertEquals(asList(), headers);
	}
	@Test void testValueNull()
	{
		assertFails(
				() -> response.addHeader("MyName", null),
				NullPointerException.class, "value");
		assertEquals(asList(), headers);
	}
	@Test void testValueEmpty()
	{
		assertFails(
				() -> response.addHeader("MyName", ""),
				IllegalArgumentException.class,
				"value must not be empty");
		assertEquals(asList(), headers);
	}
	@Test void testNameTrimmed()
	{
		assertFails(
				() -> response.addHeader(" MyName", "MyValue"),
				IllegalArgumentException.class,
				"name must be trimmed, but was > MyName<");
		assertEquals(asList(), headers);
	}
	@Test void testNameForbidden()
	{
		assertFails(
				() -> response.addHeader("Content-Type", "MyValue"),
				IllegalArgumentException.class,
				"name is forbidden, was >Content-Type<");
		assertEquals(asList(), headers);
	}
	@Test void testNameForbiddenLowercase()
	{
		assertFails(
				() -> response.addHeader("content-type", "MyValue"),
				IllegalArgumentException.class,
				"name is forbidden, was >content-type<");
		assertEquals(asList(), headers);
	}
	@Test void testNameForbiddenUppercase()
	{
		assertFails(
				() -> response.addHeader("CONTENT-TYPE", "MyValue"),
				IllegalArgumentException.class,
				"name is forbidden, was >CONTENT-TYPE<");
		assertEquals(asList(), headers);
	}

	@Test void testCacheControlNoTransformOff()
	{
		assertCacheControl("", "prefix");
	}
	@Test void testCacheControlNoTransformOn()
	{
		response.addCacheControlNoTransform();
		assertCacheControl("no-transform", "prefix,no-transform");
	}
	@Test void testCacheControlNoTransformOnTwice()
	{
		response.addCacheControlNoTransform();
		response.addCacheControlNoTransform();
		assertCacheControl("no-transform", "prefix,no-transform");
	}
	private void assertCacheControl(final String expected, final String expectedTrailing)
	{
		{
			final StringBuilder bf = new StringBuilder();
			response.addToCacheControl(bf);
			assertEquals(expected, bf.toString());
		}
		{
			final StringBuilder bf = new StringBuilder("prefix");
			response.addToCacheControl(bf);
			assertEquals(expectedTrailing, bf.toString(), "with prefix");
		}
	}
}

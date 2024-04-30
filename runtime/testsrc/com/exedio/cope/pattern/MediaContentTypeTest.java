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
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class MediaContentTypeTest
{
	@Test void testEnumNullArray()
	{
		final Media m = new Media();
		assertFails(
				() -> m.contentTypes((String[])null),
				NullPointerException.class,
				null // TODO
		);
	}
	@Test void testEnumNull()
	{
		final Media m = new Media();
		assertFails(
				() -> m.contentTypes("a/b", null),
				IllegalArgumentException.class,
				"null is not allowed in content type enumeration position 1"
		);
	}
	@Test void testEnumEmpty()
	{
		final Media m = new Media();
		assertEquals("a/b,",
				m.contentTypes("a/b", "").getContentTypeDescription()); // TODO should fail
	}
	@Test void testEnumDuplicate()
	{
		final Media m = new Media();
		assertFails(
				() -> m.contentTypes("a/b", "a/b"),
				IllegalArgumentException.class,
				"duplicates are not allowed for content type enumeration: a/b");
	}
	@Test void testSubNull()
	{
		final Media m = new Media();
		assertFails(
				() -> m.contentTypeSub(null),
				NullPointerException.class,
				"fixedMimeMajor" // TODO
		);
	}
	@Test void testSubEmpty()
	{
		final Media m = new Media();
		assertEquals("/*",
				m.contentTypeSub("").getContentTypeDescription()); // TODO should fail
	}
	@Test void testFixedNull()
	{
		final Media m = new Media();
		assertEquals(null,
				m.contentType(null).getContentTypeDescription()); // TODO should fail
	}
	@Test void testFixedEmpty()
	{
		final Media m = new Media();
		assertEquals("",
				m.contentType("").getContentTypeDescription()); // TODO should fail
	}

	@Deprecated
	@Test void testContentType()
	{
		final Media[] medias = {
			new Media(),
			new Media().contentType("ct/0"),
			new Media().contentType("ct/0", "ct/1"),
			new Media().contentType("ct/0", "ct/1", "ct/2"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3", "ct/4"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3", "ct/4", "ct/5"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3", "ct/4", "ct/5", "ct/6"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3", "ct/4", "ct/5", "ct/6", "ct/7"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3", "ct/4", "ct/5", "ct/6", "ct/7", "ct/8"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3", "ct/4", "ct/5", "ct/6", "ct/7", "ct/8", "ct/9"),
		};

		assertEquals(null, medias[0].getContentTypesAllowed());

		for(int index = 1; index<medias.length; index++)
		{
			final ArrayList<String> expected = new ArrayList<>();
			for(int i = 0; i<index; i++)
				expected.add("ct/" + i);

			assertEquals(expected, medias[index].getContentTypesAllowed());
		}
	}

	@Test void testContentTypes()
	{
		final Media[] medias = {
			new Media().contentTypes("ct/0"),
			new Media().contentTypes("ct/0", "ct/1"),
			new Media().contentTypes("ct/0", "ct/1", "ct/2"),
			new Media().contentTypes("ct/0", "ct/1", "ct/2", "ct/3"),
		};

		for(int index = 0; index<medias.length; index++)
		{
			final ArrayList<String> expected = new ArrayList<>();
			for(int i = 0; i<index+1; i++)
				expected.add("ct/" + i);

			assertEquals(expected, medias[index].getContentTypesAllowed());
		}
	}

	@Test void testContentTypesEmpty()
	{
		final Media m = new Media();
		assertFails(
				m::contentTypes,
				IllegalArgumentException.class,
				"must provide at least one content type"
		);
	}
}

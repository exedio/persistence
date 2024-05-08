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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.FunctionField;
import com.exedio.cope.StringField;
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
				null); // TODO
	}
	@Test void testEnumNull()
	{
		final Media m = new Media();
		assertFails(
				() -> m.contentTypes("a/b", null),
				NullPointerException.class,
				"types[1]");
	}
	@Test void testEnumEmpty()
	{
		final Media m = new Media();
		assertFails(
				() -> m.contentTypes("a/b", ""),
				IllegalArgumentException.class,
				"types[1] must not be empty");
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
				"majorContentType");
	}
	@Test void testSubEmpty()
	{
		final Media m = new Media();
		assertFails(
				() -> m.contentTypeSub(""),
				IllegalArgumentException.class,
				"majorContentType must not be empty");
	}
	@Test void testFixedNull()
	{
		final Media m = new Media();
		assertFails(
				() -> m.contentType(null),
				NullPointerException.class,
				"contentType");
	}
	@Test void testFixedEmpty()
	{
		final Media m = new Media();
		assertFails(
				() -> m.contentType(""),
				IllegalArgumentException.class,
				"contentType must not be empty");
	}

	@Test void testEnumFinalFirst()
	{
		final Media m1 = new Media().toFinal();
		assertIt(true, false, "*/*", m1);
		final Media m2 = m1.contentTypes("ct/0", "ct/1");
		assertIt(true, false, "ct/0,ct/1", m2);
	}
	@Test void testEnumOptionalFirst()
	{
		final Media m1 = new Media().optional();
		assertIt(false, true, "*/*", m1);
		final Media m2 = m1.contentTypes("ct/0", "ct/1");
		assertIt(false, true, "ct/0,ct/1", m2);
	}
	@Test void testEnumFinalAfter()
	{
		final Media m1 = new Media().contentTypes("ct/0", "ct/1");
		assertIt(false, false, "ct/0,ct/1", m1);
		final Media m2 = m1.toFinal();
		assertIt(true, false, "ct/0,ct/1", m2);
	}
	@Test void testEnumOptionalAfter()
	{
		final Media m1 = new Media().contentTypes("ct/0", "ct/1");
		assertIt(false, false, "ct/0,ct/1", m1);
		final Media m2 = m1.optional();
		assertIt(false, true, "ct/0,ct/1", m2);
	}
	@Test void testSubFinalFirst()
	{
		final Media m1 = new Media().toFinal();
		assertIt(true, false, "*/*", m1);
		final Media m2 = m1.contentTypeSub("ct");
		assertIt(true, false, "ct/*", m2);
	}
	@Test void testSubOptionalFirst()
	{
		final Media m1 = new Media().optional();
		assertIt(false, true, "*/*", m1);
		final Media m2 = m1.contentTypeSub("ct");
		assertIt(false, true, "ct/*", m2);
	}
	@Test void testSubFinalAfter()
	{
		final Media m1 = new Media().contentTypeSub("ct");
		assertIt(false, false, "ct/*", m1);
		final Media m2 = m1.toFinal();
		assertIt(true, false, "ct/*", m2);
	}
	@Test void testSubOptionalAfter()
	{
		final Media m1 = new Media().contentTypeSub("ct");
		assertIt(false, false, "ct/*", m1);
		final Media m2 = m1.optional();
		assertIt(false, true, "ct/*", m2);
	}
	@Test void testFixedFinalFirst()
	{
		final Media m1 = new Media().toFinal();
		assertIt(true, false, "*/*", m1);
		final Media m2 = m1.contentType("ct/x");
		assertIt(true, false, "ct/x", m2);
	}
	@Test void testFixedOptionalFirst()
	{
		final Media m1 = new Media().optional();
		assertIt(false, true, "*/*", m1);
		final Media m2 = m1.contentType("ct/x");
		assertIt(false, true, "ct/x", m2);
	}
	@Test void testFixedFinalAfter()
	{
		final Media m1 = new Media().contentType("ct/x");
		assertIt(false, false, "ct/x", m1);
		final Media m2 = m1.toFinal();
		assertIt(true, false, "ct/x", m2);
	}
	@Test void testFixedOptionalAfter()
	{
		final Media m1 = new Media().contentType("ct/x");
		assertIt(false, false, "ct/x", m1);
		final Media m2 = m1.optional();
		assertIt(false, true, "ct/x", m2);
	}
	private static void assertIt(
			final boolean expectedFinal,
			final boolean expectedOptional,
			final String expectedContentTypeDescription,
			final Media media)
	{
		assertAll(
				() -> assertEquals(expectedFinal, media.isFinal(), "final"),
				() -> assertEquals(expectedOptional, !media.isMandatory(), "optional"),
				() -> assertEquals(expectedContentTypeDescription, media.getContentTypeDescription(), "contentTypes"));
		final FunctionField<?> ct = media.getContentType();
		assertAll(
				() -> assertEquals(expectedFinal,     media.getBody        ().isFinal(),     "final"),
				() -> assertEquals(expectedOptional, !media.getBody        ().isMandatory(), "optional"),
				() -> { if(ct!=null) assertEquals(expectedFinal,     ct.isFinal(),     "final");    },
				() -> { if(ct!=null) assertEquals(expectedOptional, !ct.isMandatory(), "optional"); },
				() -> assertEquals(expectedFinal,     media.getLastModified().isFinal(),     "final"),
				() -> assertEquals(expectedOptional, !media.getLastModified().isMandatory(), "optional"));
	}

	/**
	 * @see MediaContentTypeMaxLengthTest
	 */
	@Test void testDefaultLengthFinalFirst()
	{
		final Media m1 = new Media().toFinal();
		assertIt(true, false, "*/*",  61,  61, m1);
		final Media m2 = m1.contentTypeLengthMax(100);
		assertIt(true, false, "*/*", 100, 100, m2);
	}
	@Test void testDefaultLengthOptionalFirst()
	{
		final Media m1 = new Media().optional();
		assertIt(false, true, "*/*",  61,  61, m1);
		final Media m2 = m1.contentTypeLengthMax(100);
		assertIt(false, true, "*/*", 100, 100, m2);
	}
	@Test void testDefaultLengthFinalAfter()
	{
		final Media m1 = new Media().contentTypeLengthMax(100);
		assertIt(false, false, "*/*", 100, 100, m1);
		final Media m2 = m1.toFinal();
		assertIt(true,  false, "*/*", 100, 100, m2);
	}
	@Test void testDefaultLengthOptionalAfter()
	{
		final Media m1 = new Media().contentTypeLengthMax(100);
		assertIt(false, false, "*/*", 100, 100, m1);
		final Media m2 = m1.optional();
		assertIt(false, true,  "*/*", 100, 100, m2);
	}
	@Test void testSubLengthFinalFirst()
	{
		final Media m1 = new Media().toFinal().contentTypeSub("ct");
		assertIt(true, false, "ct/*",  61, 58, m1);
		final Media m2 = m1.contentTypeLengthMax(100);
		assertIt(true, false, "ct/*", 100, 97, m2);
	}
	@Test void testSubLengthOptionalFirst()
	{
		final Media m1 = new Media().optional().contentTypeSub("ct");
		assertIt(false, true, "ct/*",  61, 58, m1);
		final Media m2 = m1.contentTypeLengthMax(100);
		assertIt(false, true, "ct/*", 100, 97, m2);
	}
	@Test void testSubLengthFinalAfter()
	{
		final Media m1 = new Media().contentTypeSub("ct").contentTypeLengthMax(100);
		assertIt(false, false, "ct/*", 100, 97, m1);
		final Media m2 = m1.toFinal();
		assertIt(true,  false, "ct/*", 100, 97, m2);
	}
	@Test void testSubLengthOptionalAfter()
	{
		final Media m1 = new Media().contentTypeSub("ct").contentTypeLengthMax(100);
		assertIt(false, false, "ct/*", 100, 97, m1);
		final Media m2 = m1.optional();
		assertIt(false, true,  "ct/*", 100, 97, m2);
	}
	@Test void testDefaultLengthToSub()
	{
		final Media m1 = new Media().contentTypeLengthMax(100);
		assertIt(false, false, "*/*", 100, 100, m1);
		final Media m2 = m1.contentTypeSub("ct");
		assertIt(false, false, "ct/*", 61,  58, m2); // TODO should be a expectedContentTypeMaximumLength==100 inherited from DefaultContentType
	}
	private static void assertIt(
			final boolean expectedFinal,
			final boolean expectedOptional,
			final String expectedContentTypeDescription,
			final int expectedContentTypeMaximumLength,
			final int expectedContentTypeFieldMaximumLength,
			final Media media)
	{
		assertIt(expectedFinal, expectedOptional, expectedContentTypeDescription, media);
		assertEquals(expectedContentTypeMaximumLength, media.getContentTypeMaximumLength());
		assertEquals(expectedContentTypeFieldMaximumLength, ((StringField)media.getContentType()).getMaximumLength());
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
				"must provide at least one content type");
	}
}

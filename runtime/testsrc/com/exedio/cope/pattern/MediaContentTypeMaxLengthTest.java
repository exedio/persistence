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

import com.exedio.cope.IntegerField;
import com.exedio.cope.StringField;
import org.junit.jupiter.api.Test;

/**
 * @see MediaContentTypeTest#testDefaultLengthFinalFirst
 */
public class MediaContentTypeMaxLengthTest
{
	@Test void testDefault()
	{
		final Media m = new Media();
		assertEquals(61, m.getContentTypeMaximumLength());
		assertEquals(61, ((StringField)m.getContentType()).getMaximumLength());

		final Media l40 = m.contentTypeLengthMax(40);
		assertEquals(40, l40.getContentTypeMaximumLength());
		assertEquals(40, ((StringField)l40.getContentType()).getMaximumLength());

		final Media l1 = m.contentTypeLengthMax(1);
		assertEquals(1, l1.getContentTypeMaximumLength());
		assertEquals(1, ((StringField)l1.getContentType()).getMaximumLength());

		assertFails(
				() -> m.contentTypeLengthMax(0),
				IllegalArgumentException.class,
				"maximumLength must be greater zero, but was 0");
	}
	@Test void testSub()
	{
		final Media m = new Media().contentTypeSub("1234");
		assertEquals(61, m.getContentTypeMaximumLength());
		assertEquals(56, ((StringField)m.getContentType()).getMaximumLength());

		final Media l40 = m.contentTypeLengthMax(40);
		assertEquals(40, l40.getContentTypeMaximumLength());
		assertEquals(35, ((StringField)l40.getContentType()).getMaximumLength());

		final Media l6 = m.contentTypeLengthMax(6);
		assertEquals(6, l6.getContentTypeMaximumLength());
		assertEquals(1, ((StringField)l6.getContentType()).getMaximumLength());

		assertFails(
				() -> m.contentTypeLengthMax(5),
				IllegalArgumentException.class,
				// TODO message maximumLength must be greater 5, but was 5
				"maximumLength must be greater zero, but was 0");
	}
	@Test void testSubMax()
	{
		final String major = "01234567890123456789012345678901234567890123456789012345678";
		assertEquals(59, major.length());
		final Media m = new Media().contentTypeSub(major);
		assertEquals(61, m.getContentTypeMaximumLength());
		assertEquals(1, ((StringField)m.getContentType()).getMaximumLength());
	}
	@Test void testSubMaxExceeded()
	{
		final String major = "012345678901234567890123456789012345678901234567890123456789";
		assertEquals(60, major.length());
		final Media m = new Media();
		assertFails(
				() -> m.contentTypeSub(major),
				IllegalArgumentException.class,
				// TODO message majorContentType must be at most 59 characters, but was <major>
				"maximumLength must be greater zero, but was 0");
	}
	@Test void testFixed()
	{
		final Media m = new Media().contentType("1234/678");
		assertEquals(8, m.getContentTypeMaximumLength());
		assertEquals(null, m.getContentType());
		assertFails(
				() -> m.contentTypeLengthMax(40),
				IllegalArgumentException.class,
				"not allowed for 1234/678");
	}
	@Test void testEnum()
	{
		final Media m = new Media().contentTypes("1234/678", "1234/6789", "1234/6");
		assertEquals(9, m.getContentTypeMaximumLength());
		assertEquals(2, ((IntegerField)m.getContentType()).getMaximum());
		assertFails(
				() -> m.contentTypeLengthMax(40),
				IllegalArgumentException.class,
				"not allowed for 1234/678,1234/6789,1234/6");
	}
}

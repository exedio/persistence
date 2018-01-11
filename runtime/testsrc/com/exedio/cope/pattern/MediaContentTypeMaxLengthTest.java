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

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.IntegerField;
import com.exedio.cope.StringField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
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

		try
		{
			m.contentTypeLengthMax(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("maximumLength must be greater zero, but was 0", e.getMessage());
		}
	}
	@Test void testSub()
	{
		final Media m = new Media().contentTypeSub("1234");
		assertEquals(35, m.getContentTypeMaximumLength());
		assertEquals(30, ((StringField)m.getContentType()).getMaximumLength());

		final Media l40 = m.contentTypeLengthMax(40);
		assertEquals(40, l40.getContentTypeMaximumLength());
		assertEquals(35, ((StringField)l40.getContentType()).getMaximumLength());

		final Media l6 = m.contentTypeLengthMax(6);
		assertEquals(6, l6.getContentTypeMaximumLength());
		assertEquals(1, ((StringField)l6.getContentType()).getMaximumLength());

		try
		{
			m.contentTypeLengthMax(5);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			// TODO message maximumLength must be greater 5, but was 5
			assertEquals("maximumLength must be greater zero, but was 0", e.getMessage());
		}
	}
	@Test void testFixed()
	{
		final Media m = new Media().contentType("1234/678");
		assertEquals(8, m.getContentTypeMaximumLength());
		assertEquals(null, m.getContentType());
		try
		{
			m.contentTypeLengthMax(40);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not allowed for 1234/678", e.getMessage());
		}
	}
	@Test void testEnum()
	{
		final Media m = new Media().contentTypes("1234/678", "1234/6789", "1234/6");
		assertEquals(9, m.getContentTypeMaximumLength());
		assertEquals(2, ((IntegerField)m.getContentType()).getMaximum());
		try
		{
			m.contentTypeLengthMax(40);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not allowed for 1234/678,1234/6789,1234/6", e.getMessage());
		}
	}
}

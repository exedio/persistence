/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static org.junit.Assert.assertEquals;

import com.exedio.cope.IntegerField;
import com.exedio.cope.StringField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;

@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
public class MediaContentTypeMaxLengthTest
{
	@Test public void testDefault()
	{
		final Media m = new Media();
		assertEquals(61, ((StringField)m.getContentType()).getMaximumLength());
	}
	@Test public void testSub()
	{
		final Media m = new Media().contentTypeSub("1234");
		assertEquals(30, ((StringField)m.getContentType()).getMaximumLength());
	}
	@Test public void testFixed()
	{
		final Media m = new Media().contentType("1234/678");
		assertEquals(null, m.getContentType());
	}
	@Test public void testEnum()
	{
		final Media m = new Media().contentType("1234/678", "1234/6789", "1234/6");
		assertEquals(2, ((IntegerField)m.getContentType()).getMaximum());
	}
}

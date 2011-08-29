/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.MediaType.forName;

import com.exedio.cope.Condition;
import com.exedio.cope.DataField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;

public class MediaTypeMediaTest extends CopeAssert
{
	private static final String JPEG = "ffd8ff";
	private static final String GIF = "47494638";
	private static final String PNG = "89504e470d0a1a0a";
	private static final String ICO = "00000100";
	private static final String ZIP = "504b0304";
	private static final String PDF = "25504446";

	public void testDefault()
	{
		final Media m = new Media();
		final DataField b = m.getBody();
		final StringField c = (StringField)m.getContentType();
		assertEquals(
				"(" +
				"(("+c+"='image/jpeg' OR "+c+"='image/pjpeg') AND !("+b+" startsWith '"+JPEG+"')) OR " +
				"("+c+"='image/png' AND !("+b+" startsWith '"+PNG+"')) OR " +
				"("+c+"='image/gif' AND !("+b+" startsWith '"+GIF+"')) OR " +
				"(("+c+"='image/vnd.microsoft.icon' OR "+c+"='image/icon' OR "+c+"='image/x-icon') AND !("+b+" startsWith '"+ICO+"')) OR " +
				"(("+c+"='application/zip' OR "+c+"='application/java-archive') AND !("+b+" startsWith '"+ZIP+"')) OR " +
				"(("+c+"='application/pdf' OR "+c+"='text/pdf') AND !("+b+" startsWith '"+PDF+"'))" +
				")",
				m.bodyMismatchesContentType().toString());
	}

	public void testFixed()
	{
		final Media m = new Media().contentType("image/jpeg");
		final DataField b = m.getBody();
		assertEquals(
				"!("+b+" startsWith '"+JPEG+"')",
				m.bodyMismatchesContentType().toString());
	}

	public void testFixedNone()
	{
		final Media m = new Media().contentType("ding/dong");
		assertEquals(Condition.FALSE, m.bodyMismatchesContentType());
	}

	public void testEnum()
	{
		final Media m = new Media().contentType("image/jpeg", "image/pjpeg", "image/png", "ding/dong");
		final DataField b = m.getBody();
		final IntegerField c = (IntegerField)m.getContentType();
		assertEquals(
				"(" +
				"(("+c+"='0' OR "+c+"='1') AND !("+b+" startsWith '"+JPEG+"')) OR " +
				"("+c+"='2' AND !("+b+" startsWith '"+PNG+"'))" +
				")",
				m.bodyMismatchesContentType().toString());
	}

	public void testEnumUnique()
	{
		final Media m = new Media().contentType("image/jpeg", "image/png", "ding/dong");
		final DataField b = m.getBody();
		final IntegerField c = (IntegerField)m.getContentType();
		assertEquals(
				"(" +
				"("+c+"='0' AND !("+b+" startsWith '"+JPEG+"')) OR " +
				"("+c+"='1' AND !("+b+" startsWith '"+PNG+"'))" +
				")",
				m.bodyMismatchesContentType().toString());
	}

	public void testSub()
	{
		final Media m = new Media().contentTypeSub("image");
		final DataField b = m.getBody();
		final StringField c = (StringField)m.getContentType();
		assertEquals(
				"(" +
				"(("+c+"='jpeg' OR "+c+"='pjpeg') AND !("+b+" startsWith '"+JPEG+"')) OR " +
				"("+c+"='png' AND !("+b+" startsWith '"+PNG+"')) OR " +
				"("+c+"='gif' AND !("+b+" startsWith '"+GIF+"')) OR " +
				"(("+c+"='vnd.microsoft.icon' OR "+c+"='icon' OR "+c+"='x-icon') AND !("+b+" startsWith '"+ICO+"'))" +
				")",
				m.bodyMismatchesContentType().toString());
	}

	public void testSubNone()
	{
		final Media m = new Media().contentTypeSub("ding");
		assertEquals(Condition.FALSE, m.bodyMismatchesContentType());
	}

	public void testAllowed()
	{
		final MediaType jpg = forName("image/jpeg");
		final MediaType pdf = forName("application/pdf");
		final MediaType png = forName("image/png");

		{
			final Media m = new Media();
			assertEquals("image/jpeg", jpg.getAllowed(m));
			assertEquals("application/pdf", pdf.getAllowed(m));
			assertEquals("image/png", png.getAllowed(m));
		}

		assertEquals("image/jpeg", jpg.getAllowed(new Media().contentType("image/jpeg")));
		assertEquals("image/pjpeg", jpg.getAllowed(new Media().contentType("image/pjpeg")));
		assertEquals("image/png", png.getAllowed(new Media().contentType("image/png")));
		assertEquals(null, jpg.getAllowed(new Media().contentType("image/png")));
		assertEquals(null, png.getAllowed(new Media().contentType("image/jpeg")));

		{
			final Media m = new Media().contentType("image/jpeg", "application/pdf");
			assertEquals("image/jpeg", jpg.getAllowed(m));
			assertEquals("application/pdf", pdf.getAllowed(m));
			assertEquals(null, png.getAllowed(m));
		}
		{
			final Media m = new Media().contentType("image/pjpeg", "application/pdf");
			assertEquals("image/pjpeg", jpg.getAllowed(m));
			assertEquals("application/pdf", pdf.getAllowed(m));
			assertEquals(null, png.getAllowed(m));
		}

		try
		{
			jpg.getAllowed(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}

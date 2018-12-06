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

import static com.exedio.cope.pattern.MediaType.forName;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Condition;
import com.exedio.cope.DataField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.StringField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

public class MediaTypeMediaTest
{
	private static final String JPEG = "ffd8ff";
	private static final String GIF = "47494638";
	private static final String PNG = "89504e470d0a1a0a";
	private static final String WEBP = "52494646";
	private static final String TIFF = "49492a00";
	private static final String ICO = "00000100";
	private static final String ZIP = "504b0304";
	private static final String PDF = "25504446";

	@Test void testDefault()
	{
		final Media m = new Media();
		final DataField b = m.getBody();
		final StringField c = (StringField)m.getContentType();
		assertEquals(
				"(" +
				"(("+c+"='image/jpeg' OR "+c+"='image/pjpeg') AND !("+b+" startsWith '"+JPEG+"')) OR " +
				"(("+c+"='image/png' OR "+c+"='image/x-png') AND !("+b+" startsWith '"+PNG+"')) OR " +
				"("+c+"='image/gif' AND !("+b+" startsWith '"+GIF+"')) OR " +
				"("+c+"='image/webp' AND !("+b+" startsWith '"+WEBP+"')) OR " +
				"("+c+"='image/tiff' AND !("+b+" startsWith '"+TIFF+"')) OR " +
				"(("+c+"='image/vnd.microsoft.icon' OR "+c+"='image/icon' OR "+c+"='image/x-icon') AND !("+b+" startsWith '"+ICO+"')) OR " +
				"(("+c+"='application/zip' OR "+c+"='application/java-archive' OR "+c+"='application/vnd.openxmlformats-officedocument.wordprocessingml.document' OR "+c+"='application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' OR "+c+"='application/x-zip-compressed') AND !("+b+" startsWith '"+ZIP+"')) OR " +
				"(("+c+"='application/font-woff' OR "+c+"='font/woff' OR "+c+"='font/x-woff') AND !("+b+" startsWith '774f4646')) OR " +
				"("+c+"='font/woff2' AND !("+b+" startsWith '774f4632')) OR " +
				"(("+c+"='application/x-font-ttf' OR "+c+"='application/x-font-truetype' OR "+c+"='font/ttf') AND !("+b+" startsWith '0001000000')) OR " +
				"(("+c+"='application/pdf' OR "+c+"='text/pdf') AND !("+b+" startsWith '"+PDF+"'))" +
				")",
				m.bodyMismatchesContentTypeIfSupported().toString());
	}

	@Test void testFixed()
	{
		final Media m = new Media().contentType("image/jpeg");
		final DataField b = m.getBody();
		assertEquals(
				"!("+b+" startsWith '"+JPEG+"')",
				m.bodyMismatchesContentTypeIfSupported().toString());
	}

	@Test void testFixedNone()
	{
		final Media m = new Media().contentType("ding/dong");
		assertEquals(Condition.FALSE, m.bodyMismatchesContentTypeIfSupported());
	}

	@Test void testEnum()
	{
		final Media m = new Media().contentType("image/jpeg", "image/pjpeg", "image/png", "ding/dong");
		final DataField b = m.getBody();
		final IntegerField c = (IntegerField)m.getContentType();
		assertEquals(
				"(" +
				"(("+c+"='0' OR "+c+"='1') AND !("+b+" startsWith '"+JPEG+"')) OR " +
				"("+c+"='2' AND !("+b+" startsWith '"+PNG+"'))" +
				")",
				m.bodyMismatchesContentTypeIfSupported().toString());
	}

	@Test void testEnumUnique()
	{
		final Media m = new Media().contentType("image/jpeg", "image/png", "ding/dong");
		final DataField b = m.getBody();
		final IntegerField c = (IntegerField)m.getContentType();
		assertEquals(
				"(" +
				"("+c+"='0' AND !("+b+" startsWith '"+JPEG+"')) OR " +
				"("+c+"='1' AND !("+b+" startsWith '"+PNG+"'))" +
				")",
				m.bodyMismatchesContentTypeIfSupported().toString());
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testEnumFailNull()
	{
		final Media m = new Media();
		try
		{
			m.contentType("image/jpeg", null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("null is not allowed in content type enumeration position 1", e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testEnumFailDuplicate()
	{
		final Media m = new Media();
		try
		{
			m.contentType("image/jpeg", "image/jpeg");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("duplicates are not allowed for content type enumeration: image/jpeg", e.getMessage());
		}
	}

	@Test void testSub()
	{
		final Media m = new Media().contentTypeSub("image");
		final DataField b = m.getBody();
		final StringField c = (StringField)m.getContentType();
		assertEquals(
				"(" +
				"(("+c+"='jpeg' OR "+c+"='pjpeg') AND !("+b+" startsWith '"+JPEG+"')) OR " +
				"(("+c+"='png' OR "+c+"='x-png') AND !("+b+" startsWith '"+PNG+"')) OR " +
				"("+c+"='gif' AND !("+b+" startsWith '"+GIF+"')) OR " +
				"("+c+"='webp' AND !("+b+" startsWith '"+WEBP+"')) OR " +
				"("+c+"='tiff' AND !("+b+" startsWith '"+TIFF+"')) OR " +
				"(("+c+"='vnd.microsoft.icon' OR "+c+"='icon' OR "+c+"='x-icon') AND !("+b+" startsWith '"+ICO+"'))" +
				")",
				m.bodyMismatchesContentTypeIfSupported().toString());
	}

	@Test void testSubNone()
	{
		final Media m = new Media().contentTypeSub("ding");
		assertEquals(Condition.FALSE, m.bodyMismatchesContentTypeIfSupported());
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	@Test void testAllowed()
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

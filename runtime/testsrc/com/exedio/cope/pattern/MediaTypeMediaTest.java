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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Condition;
import com.exedio.cope.DataField;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.StringField;
import org.junit.jupiter.api.Test;

public class MediaTypeMediaTest
{
	private static final String JPEG = "ffd8ff";
	private static final String PNG = "89504e470d0a1a0a";
	private static final String GIF = "47494638";
	private static final String WEBP = "52494646";
	private static final String AVIF = "6674797061766966";
	private static final String TIFF = "49492a00";
	private static final String ICO = "00000100";
	private static final String MP4 = "6674797069736f6d";
	private static final String WEBM = "1a45dfa3";
	private static final String OGG = "4f676753";
	private static final String ZIP = "504b0304";
	private static final String PDF = "25504446";
	private static final String STL = "736f6c6964";

	@Test void testDefault()
	{
		final Media m = new Media();
		final DataField b = m.getBody();
		final StringField c = (StringField)m.getContentType();
		assertEquals(
				"(" +
				"(("+c+"='image/jpeg' or "+c+"='image/pjpeg') and !("+b+" startsWith '"+JPEG+"')) or " +
				"(("+c+"='image/png' or "+c+"='image/x-png') and !("+b+" startsWith '"+PNG+"')) or " +
				"("+c+"='image/gif' and !("+b+" startsWith '"+GIF+"')) or " +
				"("+c+"='image/webp' and !("+b+" startsWith '"+WEBP+"')) or " +
				"("+c+"='image/avif' and !("+b+" startsWith offset 4 '"+AVIF+"')) or " +
				"("+c+"='image/tiff' and !("+b+" startsWith '"+TIFF+"')) or " +
				"(("+c+"='image/vnd.microsoft.icon' or "+c+"='image/icon' or "+c+"='image/x-icon') and !("+b+" startsWith '"+ICO+"')) or " +
				"("+c+"='video/mp4' and !("+b+" startsWith offset 4 '"+MP4+"')) or " +
				"("+c+"='video/webm' and !("+b+" startsWith '"+WEBM+"')) or " +
				"("+c+"='video/ogg' and !("+b+" startsWith '"+OGG+"')) or " +
				"(("+c+"='application/zip' or "+c+"='application/java-archive' or "+c+"='application/vnd.openxmlformats-officedocument.wordprocessingml.document' or "+c+"='application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' or "+c+"='application/x-zip-compressed') and !("+b+" startsWith '"+ZIP+"')) or " +
				"(("+c+"='application/font-woff' or "+c+"='font/woff' or "+c+"='font/x-woff') and !("+b+" startsWith '774f4646')) or " +
				"("+c+"='font/woff2' and !("+b+" startsWith '774f4632')) or " +
				"(("+c+"='application/x-font-ttf' or "+c+"='application/x-font-truetype' or "+c+"='font/ttf') and !("+b+" startsWith '0001000000')) or " +
				"(("+c+"='application/pdf' or "+c+"='text/pdf') and !("+b+" startsWith '"+PDF+"')) or " +
				"(("+c+"='model/stl' or "+c+"='model/x.stl-ascii') and !("+b+" startsWith '"+STL+"'))" +
				")",
				m.bodyMismatchesContentTypeIfSupported().toString());
	}

	@Test void testFixed()
	{
		final Media m = new Media().contentType("image/jpeg");
		final DataField b = m.getBody();
		final DateField l = m.getLastModified();
		assertEquals(
				"("+l+" is not null and !("+b+" startsWith '"+JPEG+"'))",
				m.bodyMismatchesContentTypeIfSupported().toString());
	}

	@Test void testFixedNone()
	{
		final Media m = new Media().contentType("ding/dong");
		assertEquals(Condition.ofFalse(), m.bodyMismatchesContentTypeIfSupported());
	}

	@Test void testEnum()
	{
		final Media m = new Media().contentTypes("image/jpeg", "image/pjpeg", "image/png", "ding/dong");
		final DataField b = m.getBody();
		final IntegerField c = (IntegerField)m.getContentType();
		assertEquals(
				"(" +
				"(("+c+"='0' or "+c+"='1') and !("+b+" startsWith '"+JPEG+"')) or " +
				"("+c+"='2' and !("+b+" startsWith '"+PNG+"'))" +
				")",
				m.bodyMismatchesContentTypeIfSupported().toString());
	}

	@Test void testEnumUnique()
	{
		final Media m = new Media().contentTypes("image/jpeg", "image/png", "ding/dong");
		final DataField b = m.getBody();
		final IntegerField c = (IntegerField)m.getContentType();
		assertEquals(
				"(" +
				"("+c+"='0' and !("+b+" startsWith '"+JPEG+"')) or " +
				"("+c+"='1' and !("+b+" startsWith '"+PNG+"'))" +
				")",
				m.bodyMismatchesContentTypeIfSupported().toString());
	}

	@Test void testEnumFailNull()
	{
		final Media m = new Media();
		try
		{
			m.contentTypes("image/jpeg", null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("null is not allowed in content type enumeration position 1", e.getMessage());
		}
	}

	@Test void testEnumFailDuplicate()
	{
		final Media m = new Media();
		try
		{
			m.contentTypes("image/jpeg", "image/jpeg");
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
				"(("+c+"='jpeg' or "+c+"='pjpeg') and !("+b+" startsWith '"+JPEG+"')) or " +
				"(("+c+"='png' or "+c+"='x-png') and !("+b+" startsWith '"+PNG+"')) or " +
				"("+c+"='gif' and !("+b+" startsWith '"+GIF+"')) or " +
				"("+c+"='webp' and !("+b+" startsWith '"+WEBP+"')) or " +
				"("+c+"='avif' and !("+b+" startsWith offset 4 '"+AVIF+"')) or " +
				"("+c+"='tiff' and !("+b+" startsWith '"+TIFF+"')) or " +
				"(("+c+"='vnd.microsoft.icon' or "+c+"='icon' or "+c+"='x-icon') and !("+b+" startsWith '"+ICO+"'))" +
				")",
				m.bodyMismatchesContentTypeIfSupported().toString());
	}

	@Test void testSubNone()
	{
		final Media m = new Media().contentTypeSub("ding");
		assertEquals(Condition.ofFalse(), m.bodyMismatchesContentTypeIfSupported());
	}

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
			final Media m = new Media().contentTypes("image/jpeg", "application/pdf");
			assertEquals("image/jpeg", jpg.getAllowed(m));
			assertEquals("application/pdf", pdf.getAllowed(m));
			assertEquals(null, png.getAllowed(m));
		}
		{
			final Media m = new Media().contentTypes("image/pjpeg", "application/pdf");
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

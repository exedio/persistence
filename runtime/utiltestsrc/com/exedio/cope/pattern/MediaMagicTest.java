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

import com.exedio.cope.Condition;
import com.exedio.cope.DataField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;

public class MediaMagicTest extends CopeAssert
{
	public void testDefault()
	{
		final Media m = new Media();
		final DataField b = m.getBody();
		final StringField c = (StringField)m.getContentType();
		assertEquals(
				"(" +
				"(("+c+"='image/jpeg' OR "+c+"='image/pjpeg') AND !("+b+" startsWith 'ffd8ff')) OR " +
				"("+c+"='image/gif' AND !("+b+" startsWith '47494638')) OR " +
				"("+c+"='image/png' AND !("+b+" startsWith '89504e470d0a1a0a')) OR " +
				"(("+c+"='image/icon' OR "+c+"='image/x-icon' OR "+c+"='image/vnd.microsoft.icon') AND !("+b+" startsWith '00000100')) OR " +
				"(("+c+"='application/zip' OR "+c+"='application/java-archive') AND !("+b+" startsWith '504b0304')) OR " +
				"("+c+"='application/pdf' AND !("+b+" startsWith '25504446'))" +
				")",
				m.bodyMismatchesContentType().toString());
	}

	public void testFixed()
	{
		final Media m = new Media().contentType("image/jpeg");
		final DataField b = m.getBody();
		assertEquals(
				"!("+b+" startsWith 'ffd8ff')",
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
				"(("+c+"='0' OR "+c+"='1') AND !("+b+" startsWith 'ffd8ff')) OR " +
				"("+c+"='2' AND !("+b+" startsWith '89504e470d0a1a0a'))" +
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
				"("+c+"='0' AND !("+b+" startsWith 'ffd8ff')) OR " +
				"("+c+"='1' AND !("+b+" startsWith '89504e470d0a1a0a'))" +
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
				"(("+c+"='jpeg' OR "+c+"='pjpeg') AND !("+b+" startsWith 'ffd8ff')) OR " +
				"("+c+"='gif' AND !("+b+" startsWith '47494638')) OR " +
				"("+c+"='png' AND !("+b+" startsWith '89504e470d0a1a0a')) OR " +
				"(("+c+"='icon' OR "+c+"='x-icon' OR "+c+"='vnd.microsoft.icon') AND !("+b+" startsWith '00000100'))" +
				")",
				m.bodyMismatchesContentType().toString());
	}

	public void testSubNone()
	{
		final Media m = new Media().contentTypeSub("ding");
		assertEquals(Condition.FALSE, m.bodyMismatchesContentType());
	}
}

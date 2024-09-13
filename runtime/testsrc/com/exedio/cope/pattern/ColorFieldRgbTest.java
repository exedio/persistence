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

import static com.exedio.cope.pattern.ColorField.fromRGB;
import static com.exedio.cope.pattern.ColorField.toRGB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Color;
import org.junit.jupiter.api.Test;

public class ColorFieldRgbTest
{
	@Test void test()
	{
		assertIt(new Color( 55,  66,  77),  3621453);
		assertIt(new Color(255, 255, 255), 16777215);
		assertIt(new Color(  0,   0,   0),        0);
		assertIt(new Color(255,   0,   0), 16711680);
		assertIt(new Color(  0, 255,   0),    65280);
		assertIt(new Color(  0,   0, 255),      255);
		assertIt(new Color(  0, 255, 255),    65535);
		assertIt(new Color(255,   0, 255), 16711935);
		assertIt(new Color(255, 255,   0), 16776960);

		assertIt(new Color(255, 255,   0,  60), 16776960, -1006633216);
		assertIt(new Color(  0,   0,   0,  60),        0, -1023410176);
		assertIt(new Color(255, 255, 255,  60), 16777215, -1006632961);
		assertIt(new Color( 55,  66,  77,  60),  3621453, -1019788723);
		assertIt(new Color( 55,  66,  77,   0),  3621453,   -13155763);
		assertIt(new Color( 55,  66,  77, 255),  3621453,     3621453);
	}

	@Test void testNull()
	{
		try
		{
			toRGB(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("Cannot invoke \"java.awt.Color.getRGB()\" because \"color\" is null", e.getMessage());
		}
	}

	private static void assertIt(final Color color, final int rgb)
	{
		assertEquals(rgb, toRGB(color));
		assertEquals(color, fromRGB(rgb));
	}

	private static void assertIt(final Color color, final int rgbWithoutAlpha, final int rgb)
	{
		final Color colorWithoutAlpha = new Color(color.getRed(), color.getGreen(), color.getBlue());
		assertEquals(rgbWithoutAlpha, toRGB(colorWithoutAlpha));
		assertEquals(colorWithoutAlpha, fromRGB(rgbWithoutAlpha));
		assertEquals(rgb, toRGB(color));
		assertEquals(color, fromRGB(rgb));

		// NOTE
		// The following mask is the transition from ColorField with alpha
		// allowed, to a ColorField with alpha not allowed.
		assertEquals(rgbWithoutAlpha, rgb & 0xffffff);
	}
}

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

import static com.exedio.cope.pattern.ColorFieldItem.TYPE;
import static com.exedio.cope.pattern.ColorFieldItem.color;

import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.junit.CopeTest;
import java.awt.Color;

public class ColorFieldTest extends CopeTest
{
	public ColorFieldTest()
	{
		super(ColorFieldModelTest.MODEL);
	}

	private ColorFieldItem i;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		i = deleteOnTearDown(new ColorFieldItem(new Color(1, 2, 3)));
	}

	public void testMandatory()
	{
		assertEquals(new Color(1, 2, 3), i.getColor());

		i.setColor(new Color(11, 12, 13));
		assertEquals(new Color(11, 12, 13), i.getColor());

		i.set(color.map(new Color(21, 22, 23)));
		assertEquals(new Color(21, 22, 23), i.getColor());

		i.setColor(new Color(31, 32, 33, 255));
		assertEquals(new Color(31, 32, 33), i.getColor());
	}

	public void testMandatoryViolation()
	{
		try
		{
			i.setColor(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + i + " for ColorFieldItem.color", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(color, e.getFeature());
		}
		assertEquals(new Color(1, 2, 3), i.getColor());

		try
		{
			i.set(color.map(null));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + i + " for ColorFieldItem.color", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(color, e.getFeature());
		}
		assertEquals(new Color(1, 2, 3), i.getColor());

		try
		{
			new ColorFieldItem(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation for ColorFieldItem.color", e.getMessage());
			assertEquals(null, e.getItem());
			assertEquals(color, e.getFeature());
		}
		assertContains(i, TYPE.search());
	}

	public void testColorTransparencyViolation()
	{
		final Color transparent = new Color(55, 66, 77, 254);
		try
		{
			i.setColor(transparent);
			fail();
		}
		catch(final ColorTransparencyViolationException e)
		{
			assertEquals("transparency violation on " + i + ", java.awt.Color[r=55,g=66,b=77] is transparent for ColorFieldItem.color", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(color, e.getFeature());
		}
		assertEquals(new Color(1, 2, 3), i.getColor());

		try
		{
			i.set(color.map(transparent));
			fail();
		}
		catch(final ColorTransparencyViolationException e)
		{
			assertEquals("transparency violation on " + i + ", java.awt.Color[r=55,g=66,b=77] is transparent for ColorFieldItem.color", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(color, e.getFeature());
		}
		assertEquals(new Color(1, 2, 3), i.getColor());

		try
		{
			new ColorFieldItem(transparent);
			fail();
		}
		catch(final ColorTransparencyViolationException e)
		{
			assertEquals("transparency violation, java.awt.Color[r=55,g=66,b=77] is transparent for ColorFieldItem.color", e.getMessage());
			assertEquals(null, e.getItem());
			assertEquals(color, e.getFeature());
		}
		assertContains(i, TYPE.search());
	}
}

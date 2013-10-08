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
import static com.exedio.cope.pattern.ColorFieldItem.defaultTo;
import static com.exedio.cope.pattern.ColorFieldItem.mandatory;
import static com.exedio.cope.pattern.ColorFieldItem.optional;

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
		assertEquals(new Color(1, 2, 3), i.getMandatory());

		assertMandatory(new Color(11, 12, 13));

		i.set(mandatory.map(new Color(21, 22, 23)));
		assertEquals(new Color(21, 22, 23), i.getMandatory());

		assertMandatory(new Color( 31,  32,  33, 255));
		assertMandatory(new Color(255, 255, 255, 255));
		assertMandatory(new Color(  0,   0,   0, 255));

		assertMandatory(new Color(255,   0,   0, 255));
		assertMandatory(new Color(  0, 255,   0, 255));
		assertMandatory(new Color(  0,   0, 255, 255));

		assertMandatory(new Color(  0, 255, 255, 255));
		assertMandatory(new Color(255,   0, 255, 255));
		assertMandatory(new Color(255, 255,   0, 255));
	}

	private void assertMandatory(final Color color)
	{
		i.setMandatory(color);
		assertEquals(color, i.getMandatory());
	}

	public void testMandatoryViolation()
	{
		try
		{
			i.setMandatory(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + i + " for ColorFieldItem.mandatory", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(mandatory, e.getFeature());
		}
		assertEquals(new Color(1, 2, 3), i.getMandatory());

		try
		{
			i.set(mandatory.map(null));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + i + " for ColorFieldItem.mandatory", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(mandatory, e.getFeature());
		}
		assertEquals(new Color(1, 2, 3), i.getMandatory());

		try
		{
			new ColorFieldItem(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation for ColorFieldItem.mandatory", e.getMessage());
			assertEquals(null, e.getItem());
			assertEquals(mandatory, e.getFeature());
		}
		assertContains(i, TYPE.search());
	}

	public void testColorTransparency()
	{
		assertEquals(new Color(77, 88, 99, 254), i.getAlpha());

		assertAlpha(null);
		assertAlpha(new Color( 55,  66,  77, 254));
		assertAlpha(new Color(  0,   0,   0,   0));
		assertAlpha(new Color(  0,   0,   0, 255));
		assertAlpha(new Color(  0,   0, 255,   0));
		assertAlpha(new Color(  0,   0, 255, 255));
		assertAlpha(new Color(  0, 255,   0,   0));
		assertAlpha(new Color(  0, 255,   0, 255));
		assertAlpha(new Color(  0, 255, 255,   0));
		assertAlpha(new Color(  0, 255, 255, 255));
		assertAlpha(new Color(255,   0,   0,   0));
		assertAlpha(new Color(255,   0,   0, 255));
		assertAlpha(new Color(255,   0, 255,   0));
		assertAlpha(new Color(255,   0, 255, 255));
		assertAlpha(new Color(255, 255,   0,   0));
		assertAlpha(new Color(255, 255,   0, 255));
		assertAlpha(new Color(255, 255, 255,   0));
		assertAlpha(new Color(255, 255, 255, 255));

		// TODO test same db values
	}

	private void assertAlpha(final Color color)
	{
		i.setAlpha(color);
		assertEquals(color, i.getAlpha());
	}

	public void testColorTransparencyViolation()
	{
		final Color transparent = new Color(55, 66, 77, 254);
		try
		{
			i.setMandatory(transparent);
			fail();
		}
		catch(final ColorTransparencyViolationException e)
		{
			assertEquals("transparency violation on " + i + ", java.awt.Color[r=55,g=66,b=77] is transparent for ColorFieldItem.mandatory", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(mandatory, e.getFeature());
			assertEquals(transparent, e.getValue());
		}
		assertEquals(new Color(1, 2, 3), i.getMandatory());

		try
		{
			i.set(mandatory.map(transparent));
			fail();
		}
		catch(final ColorTransparencyViolationException e)
		{
			assertEquals("transparency violation on " + i + ", java.awt.Color[r=55,g=66,b=77] is transparent for ColorFieldItem.mandatory", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(mandatory, e.getFeature());
			assertEquals(transparent, e.getValue());
		}
		assertEquals(new Color(1, 2, 3), i.getMandatory());

		try
		{
			new ColorFieldItem(transparent);
			fail();
		}
		catch(final ColorTransparencyViolationException e)
		{
			assertEquals("transparency violation, java.awt.Color[r=55,g=66,b=77] is transparent for ColorFieldItem.mandatory", e.getMessage());
			assertEquals(null, e.getItem());
			assertEquals(mandatory, e.getFeature());
			assertEquals(transparent, e.getValue());
		}
		assertContains(i, TYPE.search());
	}

	public void testOptional()
	{
		assertEquals(null, i.getOptional());

		i.setOptional(new Color(11, 12, 13));
		assertEquals(new Color(11, 12, 13), i.getOptional());

		i.setOptional(null);
		assertEquals(null, i.getOptional());

		i.setOptional(new Color(11, 12, 13));
		assertEquals(new Color(11, 12, 13), i.getOptional());

		i.set(optional.map(null));
		assertEquals(null, i.getOptional());
	}

	public void testDefaultTo()
	{
		assertEquals(new Color(22, 33, 44), i.getDefaultTo());

		i.setDefaultTo(new Color(11, 12, 13));
		assertEquals(new Color(11, 12, 13), i.getDefaultTo());

		try
		{
			i.setDefaultTo(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + i + " for ColorFieldItem.defaultTo", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(defaultTo, e.getFeature());
		}
		assertEquals(new Color(11, 12, 13), i.getDefaultTo());
	}
}

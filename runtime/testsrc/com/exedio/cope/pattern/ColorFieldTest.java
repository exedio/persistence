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

import static com.exedio.cope.pattern.ColorFieldItem.TYPE;
import static com.exedio.cope.pattern.ColorFieldItem.defaultTo;
import static com.exedio.cope.pattern.ColorFieldItem.mandatory;
import static com.exedio.cope.pattern.ColorFieldItem.mandatoryAlpha;
import static com.exedio.cope.pattern.ColorFieldItem.optional;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.FinalViolationException;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.TestWithEnvironment;
import java.awt.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColorFieldTest extends TestWithEnvironment
{
	public ColorFieldTest()
	{
		super(ColorFieldModelTest.MODEL);
	}

	private ColorFieldItem i;

	@BeforeEach final void setUp()
	{
		i = new ColorFieldItem(new Color(1, 2, 3), new Color (3, 4, 5));
	}

	@Test void testMandatory()
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

	@Test void testMandatoryViolation()
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

	@Test void testAlpha()
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
	}

	private void assertAlpha(final Color color)
	{
		i.setAlpha(color);
		assertEquals(color, i.getAlpha());
	}

	@Test void testMandatoryAlpha()
	{
		assertEquals(new Color(122, 133, 199, 253), i.getMandatoryAlpha());

		try
		{
			i.setMandatoryAlpha(null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals("mandatory violation on " + i + " for ColorFieldItem.mandatoryAlpha", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(mandatoryAlpha, e.getFeature());
		}
		assertMandatoryAlpha(new Color( 55,  66,  77, 254));
		assertMandatoryAlpha(new Color(  0,   0,   0,   0));
		assertMandatoryAlpha(new Color(  0,   0,   0, 255));
		assertMandatoryAlpha(new Color(  0,   0, 255,   0));
		assertMandatoryAlpha(new Color(  0,   0, 255, 255));
		assertMandatoryAlpha(new Color(  0, 255,   0,   0));
		assertMandatoryAlpha(new Color(  0, 255,   0, 255));
		assertMandatoryAlpha(new Color(  0, 255, 255,   0));
		assertMandatoryAlpha(new Color(  0, 255, 255, 255));
		assertMandatoryAlpha(new Color(255,   0,   0,   0));
		assertMandatoryAlpha(new Color(255,   0,   0, 255));
		assertMandatoryAlpha(new Color(255,   0, 255,   0));
		assertMandatoryAlpha(new Color(255,   0, 255, 255));
		assertMandatoryAlpha(new Color(255, 255,   0,   0));
		assertMandatoryAlpha(new Color(255, 255,   0, 255));
		assertMandatoryAlpha(new Color(255, 255, 255,   0));
		assertMandatoryAlpha(new Color(255, 255, 255, 255));
	}

	private void assertMandatoryAlpha(final Color color)
	{
		i.setMandatoryAlpha(color);
		assertEquals(color, i.getMandatoryAlpha());
	}

	@Test void testAlphaViolation()
	{
		final Color alpha = new Color(55, 66, 77, 254);
		try
		{
			i.setMandatory(alpha);
			fail();
		}
		catch(final ColorAlphaViolationException e)
		{
			assertEquals("alpha violation on " + i + ", java.awt.Color[r=55,g=66,b=77] has alpha of 254 for ColorFieldItem.mandatory", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(mandatory, e.getFeature());
			assertEquals(alpha, e.getValue());
		}
		assertEquals(new Color(1, 2, 3), i.getMandatory());

		try
		{
			i.set(mandatory.map(alpha));
			fail();
		}
		catch(final ColorAlphaViolationException e)
		{
			assertEquals("alpha violation on " + i + ", java.awt.Color[r=55,g=66,b=77] has alpha of 254 for ColorFieldItem.mandatory", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(mandatory, e.getFeature());
			assertEquals(alpha, e.getValue());
		}
		assertEquals(new Color(1, 2, 3), i.getMandatory());

		try
		{
			new ColorFieldItem(alpha);
			fail();
		}
		catch(final ColorAlphaViolationException e)
		{
			assertEquals("alpha violation, java.awt.Color[r=55,g=66,b=77] has alpha of 254 for ColorFieldItem.mandatory", e.getMessage());
			assertEquals(null, e.getItem());
			assertEquals(mandatory, e.getFeature());
			assertEquals(alpha, e.getValue());
		}
		assertContains(i, TYPE.search());
	}

	@Test void testFinalViolation()
	{
		final Color c = new Color(11, 12, 13);
		try
		{
			ColorFieldItem.finalColor.set(i, c);
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertEquals("final violation on " + i + " for ColorFieldItem.finalColor", e.getMessage());
			assertEquals(i, e.getItem());
			assertEquals(ColorFieldItem.finalColor, e.getFeature());
		}
		assertEquals(new Color(3, 4, 5), i.getFinalColor());
	}

	@Test void testOptional()
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

	@Test void testDefaultTo()
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

	@Test void testPersistence()
	{
		assertPersistence(new Color( 55,  66,  77),  3621453);
		assertPersistence(new Color(255, 255, 255), 16777215);
		assertPersistence(new Color(  0,   0,   0),        0);
		assertPersistence(new Color(255,   0,   0), 16711680);
		assertPersistence(new Color(  0, 255,   0),    65280);
		assertPersistence(new Color(  0,   0, 255),      255);
		assertPersistence(new Color(  0, 255, 255),    65535);
		assertPersistence(new Color(255,   0, 255), 16711935);
		assertPersistence(new Color(255, 255,   0), 16776960);

		assertPersistence(new Color(255, 255,   0,  60), 16776960, -1006633216);
		assertPersistence(new Color(  0,   0,   0,  60),        0, -1023410176);
		assertPersistence(new Color(255, 255, 255,  60), 16777215, -1006632961);
		assertPersistence(new Color( 55,  66,  77,  60),  3621453, -1019788723);
		assertPersistence(new Color( 55,  66,  77,   0),  3621453,   -13155763);
		assertPersistence(new Color( 55,  66,  77, 255),  3621453,     3621453);
	}

	private void assertPersistence(final Color color, final int value)
	{
		i.setOptionalAndAlpha(color);
		assertEquals(value, i.getOptionalRGB(), "optional");
		assertEquals(value, i.getAlphaRGB   (), "alpha");
	}

	private void assertPersistence(final Color color, final int optional, final int alpha)
	{
		i.setOptional(new Color(color.getRed(), color.getGreen(), color.getBlue()));
		i.setAlpha(color);
		assertEquals(optional, i.getOptionalRGB(), "optional");
		assertEquals(alpha,    i.getAlphaRGB   (), "alpha");

		// NOTE
		// The following mask is the transition from ColorField with alpha
		// allowed, to a ColorField with alpha not allowed.
		assertEquals(optional, alpha & 0xffffff);
	}
}

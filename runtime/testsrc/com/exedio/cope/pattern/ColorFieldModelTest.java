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

import static com.exedio.cope.Assert.list;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.ColorFieldItem.TYPE;
import static com.exedio.cope.pattern.ColorFieldItem.alpha;
import static com.exedio.cope.pattern.ColorFieldItem.defaultTo;
import static com.exedio.cope.pattern.ColorFieldItem.mandatory;
import static com.exedio.cope.pattern.ColorFieldItem.mandatoryAlpha;
import static com.exedio.cope.pattern.ColorFieldItem.optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.Color;
import java.util.Arrays;
import org.junit.Test;

public class ColorFieldModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(ColorFieldModelTest.class, "MODEL");
	}

	private static final IntegerField mandatoryRGB      = mandatory     .getRGB();
	private static final IntegerField optionalRGB       = optional      .getRGB();
	private static final IntegerField defaultToRGB      = defaultTo     .getRGB();
	private static final IntegerField alphaRGB          = alpha         .getRGB();
	private static final IntegerField mandatoryAlphaRGB = mandatoryAlpha.getRGB();

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test public void testIt()
	{
		assertEquals(Arrays.asList(new Type<?>[]{
				TYPE,
		}), MODEL.getTypes());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				mandatory,
				mandatoryRGB,
				optional,
				optionalRGB,
				defaultTo,
				defaultToRGB,
				alpha,
				alphaRGB,
				mandatoryAlpha,
				mandatoryAlphaRGB,
		}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				mandatory,
				mandatoryRGB,
				optional,
				optionalRGB,
				defaultTo,
				defaultToRGB,
				alpha,
				alphaRGB,
				mandatoryAlpha,
				mandatoryAlphaRGB,
		}), TYPE.getDeclaredFeatures());

		assertEquals(TYPE, mandatory.getType());
		assertEquals(TYPE, mandatoryRGB.getType());
		assertEquals("mandatory", mandatory.getName());
		assertEquals("mandatory-rgb", mandatoryRGB.getName());
		assertEquals("optional", optional.getName());
		assertEquals("optional-rgb", optionalRGB.getName());
		assertEquals("defaultTo", defaultTo.getName());
		assertEquals("defaultTo-rgb", defaultToRGB.getName());
		assertEquals("alpha", alpha.getName());
		assertEquals("alpha-rgb", alphaRGB.getName());
		assertEquals("mandatoryAlpha", mandatoryAlpha.getName());
		assertEquals("mandatoryAlpha-rgb", mandatoryAlphaRGB.getName());

		assertEquals(list(mandatoryRGB), mandatory.getSourceFeatures());
		assertEquals(mandatory, mandatoryRGB.getPattern());
		assertEquals(list(optionalRGB), optional.getSourceFeatures());
		assertEquals(optional, optionalRGB.getPattern());
		assertEquals(list(defaultToRGB), defaultTo.getSourceFeatures());
		assertEquals(defaultTo, defaultToRGB.getPattern());
		assertEquals(list(alphaRGB), alpha.getSourceFeatures());
		assertEquals(alpha, alphaRGB.getPattern());
		assertEquals(list(mandatoryAlphaRGB), mandatoryAlpha.getSourceFeatures());
		assertEquals(mandatoryAlpha, mandatoryAlphaRGB.getPattern());

		assertSerializedSame(mandatory, 392);
		assertSerializedSame(mandatoryRGB, 396);
		assertSerializedSame(optional, 391);
		assertSerializedSame(optionalRGB, 395);
		assertSerializedSame(defaultTo, 392);
		assertSerializedSame(defaultToRGB, 396);
		assertSerializedSame(alpha, 388);
		assertSerializedSame(alphaRGB, 392);
		assertSerializedSame(mandatoryAlpha, 397);
		assertSerializedSame(mandatoryAlphaRGB, 401);

		assertEquals(0, mandatoryRGB.getMinimum());
		assertEquals(0xffffff, mandatoryRGB.getMaximum());
		assertEquals(0, optionalRGB.getMinimum());
		assertEquals(0xffffff, optionalRGB.getMaximum());
		assertEquals(0, defaultToRGB.getMinimum());
		assertEquals(0xffffff, defaultToRGB.getMaximum());
		assertEquals(Integer.MIN_VALUE, alphaRGB.getMinimum());
		assertEquals(Integer.MAX_VALUE, alphaRGB.getMaximum());
		assertEquals(Integer.MIN_VALUE, mandatoryAlphaRGB.getMinimum());
		assertEquals(Integer.MAX_VALUE, mandatoryAlphaRGB.getMaximum());

		assertEquals(null, mandatory.getDefaultConstant());
		assertEquals(null, optional .getDefaultConstant());
		assertEquals(new Color( 22,  33,  44     ), defaultTo     .getDefaultConstant());
		assertEquals(new Color( 77,  88,  99, 254), alpha         .getDefaultConstant());
		assertEquals(new Color(122, 133, 199, 253), mandatoryAlpha.getDefaultConstant());

		assertEquals(false, new ColorField().optional().allowAlpha().isMandatory());
		assertEquals(true,  new ColorField().optional().allowAlpha().isAlphaAllowed());
		assertEquals(false, new ColorField().allowAlpha().optional().isMandatory());
		assertEquals(true,  new ColorField().allowAlpha().optional().isAlphaAllowed());

		final ColorField field = new ColorField();
		try
		{
			field.defaultTo(new Color(11, 22, 33, 44));
			fail();
		}
		catch(final ColorAlphaViolationException e)
		{
			assertEquals("alpha violation, java.awt.Color[r=11,g=22,b=33] has alpha of 44 for " + field, e.getMessage());
			assertEquals(null, e.getItem());
			// TODO
			// feature is wrong, should be without feature,
			// since feature is not yet mounted.
			assertEquals(field, e.getFeature());
			assertEquals(new Color(11, 22, 33, 44), e.getValue());
		}
	}
}

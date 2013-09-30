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

import static com.exedio.cope.AbstractRuntimeTest.assertSerializedSame;
import static com.exedio.cope.pattern.ColorFieldItem.TYPE;
import static com.exedio.cope.pattern.ColorFieldItem.mandatory;
import static com.exedio.cope.pattern.ColorFieldItem.optional;

import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.junit.CopeAssert;
import java.util.Arrays;

public class ColorFieldModelTest extends CopeAssert
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(ColorFieldModelTest.class, "MODEL");
	}

	private static final IntegerField colorRgb = (IntegerField)TYPE.getFeature("mandatory-rgb");
	private static final IntegerField optionalRgb = (IntegerField)TYPE.getFeature("optional-rgb");

	public void testIt()
	{
		assertEquals(Arrays.asList(new Type<?>[]{
				TYPE,
		}), MODEL.getTypes());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				mandatory,
				colorRgb,
				optional,
				optionalRgb,
		}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				mandatory,
				colorRgb,
				optional,
				optionalRgb,
		}), TYPE.getDeclaredFeatures());

		assertEquals(TYPE, mandatory.getType());
		assertEquals(TYPE, colorRgb.getType());
		assertEquals("mandatory", mandatory.getName());
		assertEquals("mandatory-rgb", colorRgb.getName());
		assertEquals("optional", optional.getName());
		assertEquals("optional-rgb", optionalRgb.getName());

		assertEquals(list(colorRgb), mandatory.getSourceFeatures());
		assertEquals(mandatory, colorRgb.getPattern());
		assertEquals(list(optionalRgb), optional.getSourceFeatures());
		assertEquals(optional, optionalRgb.getPattern());

		assertSerializedSame(mandatory, 392);
		assertSerializedSame(colorRgb, 396);
		assertSerializedSame(optional, 391);
		assertSerializedSame(optionalRgb, 395);

		assertEquals(0, colorRgb.getMinimum());
		assertEquals(0xffffff, colorRgb.getMaximum());
		assertEquals(0, optionalRgb.getMinimum());
		assertEquals(0xffffff, optionalRgb.getMaximum());
	}
}

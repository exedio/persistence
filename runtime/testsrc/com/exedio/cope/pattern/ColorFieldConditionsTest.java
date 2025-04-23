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

import static com.exedio.cope.pattern.ColorFieldItem.alpha;
import static com.exedio.cope.pattern.ColorFieldItem.optional;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Condition;
import com.exedio.cope.Query;
import com.exedio.cope.TestWithEnvironment;
import java.awt.Color;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ColorFieldConditionsTest extends TestWithEnvironment
{
	public ColorFieldConditionsTest()
	{
		super(ColorFieldModelTest.MODEL);
	}

	private static final Color
				cNull = null, //N
				cBlack = new Color(0,0,0), //B
				cWhite = new Color(0xFF,0xFF,0xFF), //W
				cTransparent = new Color(0xFF,0xFF,0xFF, 0), //T
				cTranslucent = new Color(0xFF,0xFF,0xFF, 0xFF - 1);//L

	// named by the colors for fields optional and alpha
	private ColorFieldItem iNN, iWW, iBB, iNT, iNL;

	@BeforeEach final void setUp()
	{
		iNN  = new ColorFieldItem(cWhite);
		iNN.setOptionalAndAlpha(null);
		iBB  = new ColorFieldItem(cWhite);
		iBB.setOptionalAndAlpha(cBlack);
		iWW  = new ColorFieldItem(cWhite);
		iWW.setOptionalAndAlpha(cWhite);
		iNT  = new ColorFieldItem(cWhite);
		iNT.setAlpha(cTransparent);
		iNL  = new ColorFieldItem(cWhite);
		iNL.setAlpha(cTranslucent);

	}

	@Test void testIt()
	{
		final ColorField fieldOpt = optional;
		final ColorField fieldAlpha = alpha;

		// null searches
		assertIt(fieldOpt.isNull(), iNN, iNT, iNL);
		assertIt(fieldOpt.isNotNull(), iBB, iWW);
		assertIt(fieldAlpha.isNull(), iNN);
		assertIt(fieldAlpha.isNotNull(), iBB, iWW, iNT, iNL);

		// equals searches fieldOpt
		assertIt(fieldOpt.is(cNull), iNN, iNT, iNL);
		assertIt(fieldOpt.isNot(cNull), iBB, iWW);
		assertIt(fieldOpt.is(cWhite), iWW);
		assertIt(fieldOpt.isNot(cWhite), iBB);
		assertIt(fieldOpt.is(cBlack), iBB);
		assertIt(fieldOpt.isNot(cBlack), iWW );
		assertIt(fieldOpt.is(cTransparent));
		assertIt(fieldOpt.isNot(cTransparent), iBB, iWW );

		// equals searches fieldAlpha
		assertIt(fieldAlpha.is(cNull), iNN);
		assertIt(fieldAlpha.isNot(cNull), iBB, iWW, iNT, iNL);
		assertIt(fieldAlpha.is(cWhite), iWW);
		assertIt(fieldAlpha.isNot(cWhite), iBB, iNT, iNL );
		assertIt(fieldAlpha.is(cBlack), iBB);
		assertIt(fieldAlpha.isNot(cBlack), iWW, iNT, iNL );
		assertIt(fieldAlpha.is(cTransparent), iNT);
		assertIt(fieldAlpha.isNot(cTransparent), iBB, iWW, iNL );
		assertIt(fieldAlpha.is(cTranslucent), iNL);
		assertIt(fieldAlpha.isNot(cTranslucent), iBB, iWW, iNT );

		// opaque searches
		assertIt(fieldOpt.isOpaque(), iBB, iWW);
		assertIt(fieldOpt.isNotOpaque());
		assertIt(fieldAlpha.isOpaque(), iBB, iWW);
		assertIt(fieldAlpha.isNotOpaque(), iNT, iNL);
	}

	private static void assertIt(final Condition condition, final ColorFieldItem... expected)
	{
		final Query<ColorFieldItem> query = ColorFieldItem.TYPE.newQuery(condition);
		query.setOrderBy(ColorFieldItem.TYPE.getThis(), true);
		assertEquals(Arrays.asList(expected), query.search());
	}
}

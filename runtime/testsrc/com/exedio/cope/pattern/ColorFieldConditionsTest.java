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

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.Condition;
import com.exedio.cope.Query;
import java.awt.Color;
import java.util.Arrays;
import org.junit.Test;

public class ColorFieldConditionsTest extends AbstractRuntimeModelTest
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

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
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

	@Test public void testIt()
	{
		final ColorField fieldOpt = optional;
		final ColorField fieldAlpha = alpha;

		// null searches
		assertIt(fieldOpt.isNull(), iNN, iNT, iNL);
		assertIt(fieldOpt.isNotNull(), iBB, iWW);
		assertIt(fieldAlpha.isNull(), iNN);
		assertIt(fieldAlpha.isNotNull(), iBB, iWW, iNT, iNL);

		// equals searches fieldOpt
		assertIt(fieldOpt.equal(cNull), iNN, iNT, iNL);
		assertIt(fieldOpt.notEqual(cNull), iBB, iWW);
		assertIt(fieldOpt.equal(cWhite), iWW);
		assertIt(fieldOpt.notEqual(cWhite), iBB);
		assertIt(fieldOpt.equal(cBlack), iBB);
		assertIt(fieldOpt.notEqual(cBlack), iWW );
		assertIt(fieldOpt.equal(cTransparent));
		assertIt(fieldOpt.notEqual(cTransparent), iBB, iWW );

		// equals searches fieldAlpha
		assertIt(fieldAlpha.equal(cNull), iNN);
		assertIt(fieldAlpha.notEqual(cNull), iBB, iWW, iNT, iNL);
		assertIt(fieldAlpha.equal(cWhite), iWW);
		assertIt(fieldAlpha.notEqual(cWhite), iBB, iNT, iNL );
		assertIt(fieldAlpha.equal(cBlack), iBB);
		assertIt(fieldAlpha.notEqual(cBlack), iWW, iNT, iNL );
		assertIt(fieldAlpha.equal(cTransparent), iNT);
		assertIt(fieldAlpha.notEqual(cTransparent), iBB, iWW, iNL );
		assertIt(fieldAlpha.equal(cTranslucent), iNL);
		assertIt(fieldAlpha.notEqual(cTranslucent), iBB, iWW, iNT );

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

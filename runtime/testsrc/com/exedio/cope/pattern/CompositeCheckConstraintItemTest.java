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

import static com.exedio.cope.pattern.CompositeCheckConstraintFieldTest.MODEL;
import static com.exedio.cope.pattern.CompositeCheckConstraintFieldTest.fieldAlpha;
import static com.exedio.cope.pattern.CompositeCheckConstraintFieldTest.fieldCheck;
import static com.exedio.cope.pattern.CompositeCheckConstraintFieldTest.fieldGamma;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.CheckViolationException;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.CompositeCheckConstraintFieldTest.MyItem;
import com.exedio.cope.pattern.CompositeCheckConstraintTest.MyComposite;
import org.junit.jupiter.api.Test;

public class CompositeCheckConstraintItemTest extends TestWithEnvironment
{
	public CompositeCheckConstraintItemTest()
	{
		super(MODEL);
	}

	@Test void testIt()
	{
		final MyItem i = new MyItem(new MyComposite(1, 2));
		assertEquals(1, i.getField().getAlpha());
		assertEquals(2, i.getField().getGamma());
		assertEquals(new MyComposite(1, 2), i.getField());

		i.setField(new MyComposite(4, 5));
		assertEquals(4, i.getField().getAlpha());
		assertEquals(5, i.getField().getGamma());
		assertEquals(new MyComposite(4, 5), i.getField());

		i.setField(new MyComposite(-1, 0));
		assertEquals(-1, i.getField().getAlpha());
		assertEquals(0,  i.getField().getGamma());
		assertEquals(new MyComposite(-1, 0), i.getField());

		i.setField(new MyComposite(8, null));
		assertEquals(8, i.getField().getAlpha());
		assertEquals(null, i.getField().getGammaInternal());
		assertEquals(new MyComposite(8, null), i.getField());

		assertFails(
				() -> i.set(fieldAlpha.map(5), fieldGamma.map(5)),
				CheckViolationException.class,
				"check violation on " + i + " for " + fieldCheck,
				fieldCheck, i);
		assertEquals(8, i.getField().getAlpha());
		assertEquals(null, i.getField().getGammaInternal());
		assertEquals(new MyComposite(8, null), i.getField());
	}
}


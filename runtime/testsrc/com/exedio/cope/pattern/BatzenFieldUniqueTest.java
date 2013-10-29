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

import static com.exedio.cope.pattern.BatzenFieldUniqueModelTest.ABatzen.constraint;
import static com.exedio.cope.pattern.BatzenFieldUniqueModelTest.AnItem.eins;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.pattern.BatzenFieldUniqueModelTest.ABatzen;
import com.exedio.cope.pattern.BatzenFieldUniqueModelTest.AnItem;

public class BatzenFieldUniqueTest extends AbstractRuntimeModelTest
{
	public BatzenFieldUniqueTest()
	{
		super(BatzenFieldUniqueModelTest.MODEL);
	}

	public void testIt()
	{
		final AnItem i1 = new AnItem("item1", 1);
		final AnItem i2 = new AnItem("item2", 2);
		assertEquals("item1", i1.getCode());
		assertEquals("item2", i2.getCode());

		final ABatzen b1a = i1.eins();
		final ABatzen b1b = i1.zwei();
		final ABatzen b2a = i2.eins();
		final ABatzen b2b = i2.zwei();
		assertEquals("item1-1A", b1a.getAlpha());
		assertEquals("item1-1B", b1b.getAlpha());
		assertEquals("item2-2A", b2a.getAlpha());
		assertEquals("item2-2B", b2b.getAlpha());
		assertEquals( 1, b1a.getBeta());
		assertEquals(11, b1b.getBeta());
		assertEquals( 2, b2a.getBeta());
		assertEquals(12, b2b.getBeta());

		b1a.setAlpha("item2-2A");
		try
		{
			b1a.setBeta(2);
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals("unique violation on AnItem-0 for AnItem.eins-constraint", e.getMessage());
			assertEquals(eins.of(constraint), e.getFeature());
			assertEquals(i1, e.getItem());
		}
	}
}


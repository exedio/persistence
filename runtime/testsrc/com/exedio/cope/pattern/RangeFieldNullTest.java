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

import static com.exedio.cope.pattern.Range.valueOf;
import static com.exedio.cope.pattern.RangeFieldItem.TYPE;
import static com.exedio.cope.pattern.RangeFieldItem.valid;

import com.exedio.cope.AbstractRuntimeTest;
import java.util.Arrays;
import java.util.List;

public class RangeFieldNullTest extends AbstractRuntimeTest
{
	public RangeFieldNullTest()
	{
		super(RangeFieldModelTest.MODEL);
	}

	RangeFieldItem ab, nb, an, nn;

	@Override()
	protected void setUp() throws Exception
	{
		super.setUp();
		final Range<String> s = valueOf("a", "b");
		ab = deleteOnTearDown(new RangeFieldItem(valueOf(10,   20), s));
		nb = deleteOnTearDown(new RangeFieldItem(valueOf(null, 20), s));
		an = deleteOnTearDown(new RangeFieldItem(valueOf(10, null), s));
		nn = deleteOnTearDown(new RangeFieldItem(valueOf((Integer)null, null), s));
	}

	public void testIt()
	{
		assertContainsCondition( 9,     nb,     nn);
		assertContainsCondition(10, ab, nb, an, nn);
		assertContainsCondition(11, ab, nb, an, nn);
		assertContainsCondition(19, ab, nb, an, nn);
		assertContainsCondition(20, ab, nb, an, nn);
		assertContainsCondition(21,         an, nn);

		for(final RangeFieldItem item : TYPE.search())
		{
			try
			{
				item.doesValidContain(null);
				fail();
			}
			catch(final NullPointerException e)
			{
				assertEquals("value", e.getMessage());
			}
		}
	}

	private static void assertContainsCondition(final int value, final RangeFieldItem... actual)
	{
		final List<RangeFieldItem> actualList = Arrays.asList(actual);
		assertEquals(actualList, TYPE.search(valid.contains(value), TYPE.getThis(), true));
		for(final RangeFieldItem item : TYPE.search())
		{
			final boolean contains = actualList.contains(item);
			assertEquals(contains, item.getValid().contains(value));
			assertEquals(contains, item.doesValidContain(value));
		}
	}
}

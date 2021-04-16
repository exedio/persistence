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

import static com.exedio.cope.pattern.RangeFieldItem.TYPE;
import static com.exedio.cope.pattern.RangeFieldItem.valid;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.TestWithEnvironment;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RangeFieldNullTest extends TestWithEnvironment
{
	public RangeFieldNullTest()
	{
		super(RangeFieldModelTest.MODEL);
	}

	RangeFieldItem ab, nb, an, nn;

	@BeforeEach final void setUp()
	{
		ab = new RangeFieldItem(10,   20);
		nb = new RangeFieldItem(null, 20);
		an = new RangeFieldItem(10, null);
		nn = new RangeFieldItem((Integer)null, null);
	}

	@Test void testIt()
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

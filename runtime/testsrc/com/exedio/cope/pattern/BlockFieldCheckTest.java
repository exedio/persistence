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

import static com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock.less;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.AnItem.eins;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CheckViolationException;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock;
import com.exedio.cope.pattern.BlockFieldCheckModelTest.AnItem;
import org.junit.jupiter.api.Test;

public class BlockFieldCheckTest extends TestWithEnvironment
{
	public BlockFieldCheckTest()
	{
		super(BlockFieldCheckModelTest.MODEL);
	}

	@Test void testField()
	{
		final AnItem i1 = new AnItem("item1", 1, 1, 2, 2);
		final AnItem i2 = new AnItem("item2", 3, 3, 4, 4);
		assertEquals("item1", i1.getCode());
		assertEquals("item2", i2.getCode());

		final ABlock b1a = i1.eins();
		assertEquals(1, b1a.getAlpha());

		b1a.setAlpha(1);
		try
		{
			b1a.setAlpha(2);
			fail();
		}
		catch(final CheckViolationException e)
		{
			assertEquals("check violation on AnItem-0 for AnItem.eins-less", e.getMessage());
			assertEquals(eins.of(less), e.getFeature());
			assertEquals(i1, e.getItem());
		}
	}
}


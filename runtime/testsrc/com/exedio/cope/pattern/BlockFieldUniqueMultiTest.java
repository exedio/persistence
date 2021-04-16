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

import static com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.ABlock.constraint;
import static com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.ABlock.constraintPrice;
import static com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.AnItem.eins;
import static com.exedio.cope.pattern.Price.storeOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.ABlock;
import com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.AnItem;
import org.junit.jupiter.api.Test;

public class BlockFieldUniqueMultiTest extends TestWithEnvironment
{
	public BlockFieldUniqueMultiTest()
	{
		super(BlockFieldUniqueMultiModelTest.MODEL);
	}

	@Test void testField()
	{
		final AnItem i1 = new AnItem("item1", 1);
		final AnItem i2 = new AnItem("item2", 2);
		assertEquals("item1", i1.getCode());
		assertEquals("item2", i2.getCode());

		final ABlock b1a = i1.eins();
		final ABlock b1b = i1.zwei();
		final ABlock b2a = i2.eins();
		final ABlock b2b = i2.zwei();
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

	@Test void testPrice()
	{
		final AnItem i1 = new AnItem("item1", 1);
		final AnItem i2 = new AnItem("item2", 2);
		assertEquals("item1", i1.getCode());
		assertEquals("item2", i2.getCode());

		final ABlock b1a = i1.eins();
		final ABlock b1b = i1.zwei();
		final ABlock b2a = i2.eins();
		final ABlock b2b = i2.zwei();
		assertEquals(storeOf(151), b1a.getAlphaPrice());
		assertEquals(storeOf(251), b1b.getAlphaPrice());
		assertEquals(storeOf(152), b2a.getAlphaPrice());
		assertEquals(storeOf(252), b2b.getAlphaPrice());
		assertEquals(storeOf(161), b1a.getBetaPrice());
		assertEquals(storeOf(261), b1b.getBetaPrice());
		assertEquals(storeOf(162), b2a.getBetaPrice());
		assertEquals(storeOf(262), b2b.getBetaPrice());

		b1a.setAlphaPrice(storeOf(152));
		try
		{
			b1a.setBetaPrice(storeOf(162));
			fail();
		}
		catch(final UniqueViolationException e)
		{
			assertEquals("unique violation on AnItem-0 for AnItem.eins-constraintPrice", e.getMessage());
			assertEquals(eins.of(constraintPrice), e.getFeature());
			assertEquals(i1, e.getItem());
		}
	}
}


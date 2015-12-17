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
import static com.exedio.cope.Assert.reserialize;
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.pattern.BlockFieldStandardModelTest.ABlock;
import com.exedio.cope.pattern.BlockFieldStandardModelTest.ABlock.AnEnum;
import com.exedio.cope.pattern.BlockFieldStandardModelTest.AnItem;
import java.awt.Color;
import org.junit.Test;

public class BlockFieldStandardTest extends AbstractRuntimeModelTest
{
	public BlockFieldStandardTest()
	{
		super(BlockFieldStandardModelTest.MODEL);
	}

	@Test public void testIt()
	{
		final AnItem i1 = new AnItem("item1", 1);
		final AnItem i2 = new AnItem("item2", 2);
		assertEquals("item1", i1.getCode());
		assertEquals("item2", i2.getCode());

		final ABlock b1a = i1.eins();
		final ABlock b1b = i1.zwei();
		final ABlock b2a = i2.eins();
		final ABlock b2b = i2.zwei();
		assertEquals("item1-1A", b1a.getAString());
		assertEquals("item1-1B", b1b.getAString());
		assertEquals("item2-2A", b2a.getAString());
		assertEquals("item2-2B", b2b.getAString());
		assertEquals( 1, b1a.getAnInt());
		assertEquals(11, b1b.getAnInt());
		assertEquals( 2, b2a.getAnInt());
		assertEquals(12, b2b.getAnInt());
		assertEquals(AnEnum.facet1, b1a.getAnEnum());
		assertEquals(AnEnum.facet2, b1b.getAnEnum());
		assertEquals(AnEnum.facet1, b2a.getAnEnum());
		assertEquals(AnEnum.facet2, b2b.getAnEnum());
		assertEquals(null, b1a.getAnItem());
		assertEquals(null, b1b.getAnItem());
		assertEquals(null, b2a.getAnItem());
		assertEquals(null, b2b.getAnItem());
		assertEquals(new Color( 10,  20,  30), b1a.getAColor());
		assertEquals(new Color(110, 120, 130), b1b.getAColor());
		assertEquals(new Color( 10,  20,  30), b2a.getAColor());
		assertEquals(new Color(110, 120, 130), b2b.getAColor());
		assertLocator(null, b1a.getAMediaLocator());
		assertLocator(null, b1b.getAMediaLocator());
		assertLocator(null, b2a.getAMediaLocator());
		assertLocator(null, b2b.getAMediaLocator());
		assertEquals(list(), b1a.getAList());
		assertEquals(list(), b1b.getAList());
		assertEquals(list(), b2a.getAList());
		assertEquals(list(), b2b.getAList());
		final ABlock b1A = i1.eins();
		assertEquals(null, b1A.getAnItem());

		b1a.setAnItem(i1);
		assertEquals(i1,   b1a.getAnItem());
		assertEquals(null, b1b.getAnItem());
		assertEquals(i1,   b1A.getAnItem());
		assertEquals(null, b2a.getAnItem());
		assertEquals(null, b2b.getAnItem());

		b1a.setAMedia(new byte[]{1, 2, 3}, "text/plain");
		assertLocator("AnItem/eins-aMedia/AnItem-0.txt", b1a.getAMediaLocator());
		assertLocator(null, b1b.getAMediaLocator());
		assertLocator(null, b2a.getAMediaLocator());
		assertLocator(null, b2b.getAMediaLocator());

		b1a.addToAList("aListElement1");
		assertEquals(list("aListElement1"), b1a.getAList());
		assertEquals(list(), b1b.getAList());
		assertEquals(list(), b2a.getAList());
		assertEquals(list(), b2b.getAList());

		// hashCode
		assertEquals(b1a, b1a);
		assertEquals(b1a, b1A);
		assertNotSame(b1a, b1A);
		assertFalse(b1a.equals(b1b));
		assertFalse(b1a.equals(b2a));
		assertFalse(b1a.equals(null));
		assertFalse(b1a.equals("hallo"));

		// hashCode
		assertEquals(b1a.hashCode(), b1A.hashCode());
		assertFalse(b1a.hashCode()==b1b.hashCode());
		assertFalse(b1a.hashCode()==b2a.hashCode());

		// toString
		assertEquals("AnItem.eins#AnItem-0", b1a.toString());
		assertEquals("AnItem.zwei#AnItem-0", b1b.toString());
		assertEquals("AnItem.eins#AnItem-1", b2a.toString());
		assertEquals("AnItem.zwei#AnItem-1", b2b.toString());

		// serialization
		final ABlock b1aS = reserialize(b1a, 704);
		assertEquals(b1aS, b1a);
		assertNotSame(b1aS, b1a);
		assertFalse(b1aS.equals(b1b));
		assertFalse(b1aS.equals(b2a));
		assertEquals(b1aS.hashCode(), b1a.hashCode());
		assertEquals("AnItem.eins#AnItem-0", b1aS.toString());
	}
}


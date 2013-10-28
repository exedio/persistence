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

import com.exedio.cope.AbstractRuntimeModelTest;
import com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen;
import com.exedio.cope.pattern.BatzenFieldModelTest.ABatzen.AnEnum;
import com.exedio.cope.pattern.BatzenFieldModelTest.AnItem;
import java.awt.Color;

public class BatzenFieldTest extends AbstractRuntimeModelTest
{
	public BatzenFieldTest()
	{
		super(BatzenFieldModelTest.MODEL);
	}

	public void testIt()
	{
		final AnItem i1 = new AnItem("item1", 1);
		final AnItem i2 = new AnItem("item2", 2);
		assertEquals("item1", i1.getCode());
		assertEquals("item2", i2.getCode());

		final ABatzen b1a = i1.getEins();
		final ABatzen b1b = i1.getZwei();
		final ABatzen b2a = i2.getEins();
		final ABatzen b2b = i2.getZwei();
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
		assertEquals(list(), b1a.getAList());
		assertEquals(list(), b1b.getAList());
		assertEquals(list(), b2a.getAList());
		assertEquals(list(), b2b.getAList());
		final ABatzen b1A = i1.getEins();
		assertEquals(null, b1A.getAnItem());

		b1a.setAnItem(i1);
		assertEquals(i1,   b1a.getAnItem());
		assertEquals(null, b1b.getAnItem());
		assertEquals(i1,   b1A.getAnItem());
		assertEquals(null, b2a.getAnItem());
		assertEquals(null, b2b.getAnItem());

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
		final ABatzen b1aS = reserialize(b1a, 686);
		assertEquals(b1aS, b1a);
		assertNotSame(b1aS, b1a);
		assertFalse(b1aS.equals(b1b));
		assertFalse(b1aS.equals(b2a));
		assertEquals(b1aS.hashCode(), b1a.hashCode());
		assertEquals("AnItem.eins#AnItem-0", b1aS.toString());
	}
}


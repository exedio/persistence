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

package com.exedio.cope;

import static com.exedio.cope.PatternComputedItem.TYPE;
import static com.exedio.cope.PatternComputedItem.compuComp;
import static com.exedio.cope.PatternComputedItem.virgnComp;

import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;
import org.junit.Test;

public class PatternComputedTest extends CopeAssert
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(PatternComputedTest.class, "MODEL");
	}

	@Test public void testIt()
	{
		assertEquals(false, comp(virgnComp));
		assertEquals(true,  comp(compuComp));

		assertEquals(false, comp(virgnComp.virgnSource));
		assertEquals(true,  comp(virgnComp.compuSource));
		assertEquals(true,  comp(compuComp.virgnSource));
		assertEquals(true,  comp(compuComp.compuSource));

		assertEquals(false, comp(virgnComp.virgnType));
		assertEquals(true,  comp(virgnComp.compuType));
		assertEquals(true,  comp(compuComp.virgnType));
		assertEquals(true,  comp(compuComp.compuType));

		assertEquals(false, comp(virgnComp.virgnTypeVirgnField));
		assertEquals(true,  comp(virgnComp.virgnTypeCompuField));
		assertEquals(false, comp(virgnComp.compuTypeVirgnField));
		assertEquals(true,  comp(virgnComp.compuTypeCompuField));
		assertEquals(true,  comp(compuComp.virgnTypeVirgnField));
		assertEquals(true,  comp(compuComp.virgnTypeCompuField));
		assertEquals(true,  comp(compuComp.compuTypeVirgnField));
		assertEquals(true,  comp(compuComp.compuTypeCompuField));
	}

	private static boolean comp(final Feature f)
	{
		final boolean result = f.isAnnotationPresent(Computed.class);
		assertEquals(result, f.getAnnotation(Computed.class)!=null);
		return result;
	}

	private static boolean comp(final Type<?> f)
	{
		final boolean result = f.isAnnotationPresent(Computed.class);
		assertEquals(result, f.getAnnotation(Computed.class)!=null);
		return result;
	}
}

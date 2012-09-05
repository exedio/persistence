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

import static com.exedio.cope.pattern.CompositeFieldComputedComposite.virgnTemp;
import static com.exedio.cope.pattern.CompositeFieldComputedComposite.wrongTemp;
import static com.exedio.cope.pattern.CompositeFieldComputedItem.TYPE;
import static com.exedio.cope.pattern.CompositeFieldComputedItem.virgnComp;
import static com.exedio.cope.pattern.CompositeFieldComputedItem.wrongComp;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;

public final class CompositeFieldComputedTest extends CopeAssert
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(CompositeFieldComputedTest.class, "MODEL");
	}

	public static void testIt()
	{
		assertEquals(false, comp(virgnTemp));
		assertEquals(true,  comp(wrongTemp));
		assertEquals(false, comp(virgnComp));
		assertEquals(true,  comp(wrongComp));

		assertEquals(true,  comp(virgnComp.of(virgnTemp))); // TODO should be false
		assertEquals(true,  comp(virgnComp.of(wrongTemp)));
		assertEquals(true,  comp(wrongComp.of(virgnTemp)));
		assertEquals(true,  comp(wrongComp.of(wrongTemp)));
	}

	private static boolean comp(final Feature f)
	{
		final boolean result = f.isAnnotationPresent(Computed.class);
		assertEquals(result, f.getAnnotation(Computed.class)!=null);
		return result;
	}
}

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

package com.exedio.cope.instrument.testmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class GenericTest
{
	@Test public void testType()
	{
		assertEquals("GenericSimpleSuper" , GenericSimpleSuper .TYPE.getID());
		assertEquals("GenericSimpleSub"   , GenericSimpleSub   .TYPE.getID());
		assertEquals("GenericComplexSuper", GenericComplexSuper.TYPE.getID());
		assertEquals("GenericComplexMid"  , GenericComplexMid  .TYPE.getID());
		assertEquals("GenericComplexSub"  , GenericComplexSub  .TYPE.getID());
	}

	@Test public void testValueClass()
	{
		assertEquals(GenericSimpleSub   .class, GenericSimpleSuper .toSub  .valueClass);
		assertEquals(GenericSimpleSuper .class, GenericSimpleSuper .toSuper.valueClass);
		assertEquals(GenericComplexSub  .class, GenericComplexSuper.toSub  .valueClass);
		assertEquals(GenericComplexMid  .class, GenericComplexSuper.toMid  .valueClass);
		assertEquals(GenericComplexSuper.class, GenericComplexSuper.toSuper.valueClass);
	}
}

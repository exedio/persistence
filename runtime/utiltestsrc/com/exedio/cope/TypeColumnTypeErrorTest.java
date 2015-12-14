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

import com.exedio.cope.junit.CopeAssert;

public final class TypeColumnTypeErrorTest extends CopeAssert
{
	@SuppressWarnings("static-method")
	@Test public void testIt()
	{
		try
		{
			TypesBound.newType(AnItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"illegal @CopeTypeColumnMinLength for type AnItem, " +
					"must be greater zero, but was 0",
					e.getMessage());
		}
	}

	@CopeTypeColumnMinLength(0)
	private static class AnItem extends Item
	{
		private static final long serialVersionUID = 1l;
		AnItem(final ActivationParameters ap) { super(ap); }
	}
}

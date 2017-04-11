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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.WrapperIgnore;
import org.junit.Test;

public class TypeSetModelErrorTest
{
	@Test public void testNull()
	{
		try
		{
			new TypeSet((Type<?>[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("explicitTypes", e.getMessage());
		}
	}

	@Test public void testEmpty()
	{
		try
		{
			new TypeSet(new Type<?>[0]);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("explicitTypes must not be empty", e.getMessage());
		}
	}

	@Test public void testNullElement()
	{
		try
		{
			new TypeSet(new Type<?>[]{null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("explicitTypes", e.getMessage());
		}
	}

	@Test public void testDuplicate()
	{
		final Type<Item1> type1 = TypesBound.newType(Item1.class);
		final Type<Item2> type2 = TypesBound.newType(Item2.class);
		try
		{
			new TypeSet(type1, type2, type1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("duplicate type Item1", e.getMessage());
		}
	}

	@WrapperIgnore
	static final class Item1 extends Item
	{
		private static final long serialVersionUID = 1l;
		private Item1(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	static final class Item2 extends Item
	{
		private static final long serialVersionUID = 1l;
		private Item2(final ActivationParameters ap) { super(ap); }
	}
}

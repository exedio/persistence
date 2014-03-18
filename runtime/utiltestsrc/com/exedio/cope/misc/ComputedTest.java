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

package com.exedio.cope.misc;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.junit.CopeAssert;

public class ComputedTest extends CopeAssert
{
	public void testIt()
	{
		assertEquals(true,  comp(Super.TYPE));
		assertEquals(true,  comp(Sub.TYPE));
		assertEquals(false, comp(Single.TYPE));
	}

	@Computed
	private static class Super extends Item
	{

		private static final long serialVersionUID = 1l;
		static final Type<Super> TYPE = TypesBound.newType(Super.class);
		Super(final ActivationParameters ap) { super(ap); }
	}

	private static class Sub extends Super
	{
		private static final long serialVersionUID = 1l;
		static final Type<Sub> TYPE = TypesBound.newType(Sub.class);
		Sub(final ActivationParameters ap) { super(ap); }
	}

	private static class Single extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<Single> TYPE = TypesBound.newType(Single.class);
		Single(final ActivationParameters ap) { super(ap); }
	}

	private static boolean comp(final Type<?> f)
	{
		final boolean result = f.isAnnotationPresent(Computed.class);
		assertEquals(result, f.getAnnotation(Computed.class)!=null);
		return result;
	}
}

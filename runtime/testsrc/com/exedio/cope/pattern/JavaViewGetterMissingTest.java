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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Item;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import org.junit.jupiter.api.Test;

public class JavaViewGetterMissingTest
{
	@WrapperIgnore
	private static final class AnItem extends Item
	{
		@SuppressWarnings("unused")
		static final JavaView schauHer = new JavaView();
		private AnItem(final ActivationParameters ap) { super(ap); }
		private static final long serialVersionUID = 1l;
	}

	@Test void testIt()
	{
		try
		{
			TypesBound.newType(AnItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no suitable getter method getSchauHer found for java view schauHer", e.getMessage());
			assertEquals(IllegalArgumentException.class, e.getClass());

			assertEquals("com.exedio.cope.pattern.JavaViewGetterMissingTest$AnItem.getSchauHer()", e.getCause().getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
	}
}

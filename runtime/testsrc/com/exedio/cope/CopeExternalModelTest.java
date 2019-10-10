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

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.CopeExternalTest.CachedItem;
import com.exedio.cope.CopeExternalTest.NoCacheItem;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class CopeExternalModelTest
{
	@Test void testInvalidCached() throws ClassNotFoundException
	{
		Class.forName(InvalidCachedItem.class.getName(), true, getClass().getClassLoader());
		try
		{
			TypesBound.newType(InvalidCachedItem.class);
			fail();
		}
		catch (final IllegalArgumentException e)
		{
			assertEquals("@CopeExternal must be set consistently at type and supertype", e.getMessage());
		}
	}

	@Test void testInvalidUncached() throws ClassNotFoundException
	{
		Class.forName(InvalidUncachedItem.class.getName(), true, getClass().getClassLoader());
		try
		{
			TypesBound.newType(InvalidUncachedItem.class);
			fail();
		}
		catch (final IllegalArgumentException e)
		{
			assertEquals("@CopeExternal must be set consistently at type and supertype", e.getMessage());
		}
	}


	@WrapperType(indent=2, comments=false, type=NONE, genericConstructor=NONE, constructor=NONE)
	static class InvalidCachedItem extends NoCacheItem
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		protected InvalidCachedItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false, type=NONE, genericConstructor=NONE, constructor=NONE)
	@CopeExternal
	static class InvalidUncachedItem extends CachedItem
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		protected InvalidUncachedItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

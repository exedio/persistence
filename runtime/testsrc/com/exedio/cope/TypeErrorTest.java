/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.util.ReactivationConstructorDummy;

public class TypeErrorTest extends CopeAssert
{
	public void testErrors()
	{
		try
		{
			TypesExclusive.newType(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("javaClass", e.getMessage());
		}
		try
		{
			TypesExclusive.newType(Item.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("Cannot make a type for " + Item.class + " itself, but only for subclasses.", e.getMessage());
		}
		try
		{
			TypesExclusive.newType(castItemClass(NoItem.class));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(NoItem.class.toString() + " is not a subclass of Item", e.getMessage());
		}
		try
		{
			TypesExclusive.newType(NoCreationConstructor.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(
					NoCreationConstructor.class.getName() +
					" does not have a creation constructor NoCreationConstructor(" + SetValue.class.getName() + "[])", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
		try
		{
			TypesExclusive.newType(NoReactivationConstructor.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(e.getMessage(),
					NoReactivationConstructor.class.getName() +
					" does not have a reactivation constructor NoReactivationConstructor(" + ReactivationConstructorDummy.class.getName() + ",int)", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
	}
	
	static class NoItem
	{
		NoItem()
		{
			// just a dummy constructor
		}
	}
	
	static class NoCreationConstructor extends Item
	{
		private static final long serialVersionUID = 1l;

		NoCreationConstructor()
		{
			super(new SetValue[]{});
		}
	}

	static class NoReactivationConstructor extends Item
	{
		private static final long serialVersionUID = 1l;

		NoReactivationConstructor(final SetValue[] setValues)
		{
			super(setValues);
		}
	}
	
	@SuppressWarnings("unchecked") // OK: test bad API usage
	private static final Class<Item> castItemClass(Class c)
	{
		return c;
	}
}

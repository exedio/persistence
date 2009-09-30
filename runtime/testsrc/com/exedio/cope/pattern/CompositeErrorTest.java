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

package com.exedio.cope.pattern;

import com.exedio.cope.Field;
import com.exedio.cope.SetValue;
import com.exedio.cope.junit.CopeAssert;

public class CompositeErrorTest extends CopeAssert
{
	public void testIt()
	{
		try
		{
			Composite.newComposite(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("valueClass", e.getMessage());
		}
		try
		{
			Composite.newComposite(ValueWithoutConstructor.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals(ValueWithoutConstructor.class.getName() + " does not have a constructor [class [Lcom.exedio.cope.SetValue;]", e.getMessage());
		}
		try
		{
			Composite.newComposite(ValueWithoutFields.class);
			//fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("", e.getMessage());
		}
		try
		{
			Composite.newComposite(NullField.class);
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("nullField", e.getMessage());
		}
		try
		{
			Composite.newComposite(Composite.Value.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("is not a subclass of " + Composite.Value.class.getName() + " but Composite.Value itself", e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			Composite.newComposite((Class)CompositeTest.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("is not a subclass of " + Composite.Value.class.getName() + ": " + CompositeTest.class.getName(), e.getMessage());
		}
	}
	
	static class ValueWithoutConstructor extends Composite.Value
	{
		private static final long serialVersionUID = 1l;
	}
	
	static class ValueWithoutFields extends Composite.Value
	{
		private static final long serialVersionUID = 1l;
		
		private ValueWithoutFields(final SetValue[] setValues)
		{
			super(setValues);
		}
	}
	
	static class NullField extends Composite.Value
	{
		private static final long serialVersionUID = 1l;
		
		private NullField(final SetValue[] setValues)
		{
			super(setValues);
		}
		
		static final Field nullField = null;
	}
}

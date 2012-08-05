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

import com.exedio.cope.BooleanField;
import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.FunctionField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;

public class CompositeErrorTest extends CopeAssert
{
	public void testIt()
	{
		try
		{
			CompositeField.create(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("valueClass", e.getMessage());
		}
		try
		{
			CompositeField.create(NonFinal.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(e.getMessage(),
					"is not final: " +
					NonFinal.class.getName(), e.getMessage());
		}
		try
		{
			CompositeField.create(NoConstructor.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(e.getMessage(),
					NoConstructor.class.getName() +
					" does not have a constructor NoConstructor(" + SetValue.class.getName() + "[])", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
		try
		{
			CompositeField.create(NoFields.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("composite has no templates", e.getMessage());
		}
		try
		{
			CompositeField.create(NullField.class);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(NullField.class.getName() + "#nullField", e.getMessage());
		}
		try
		{
			CompositeField.create(PatternField.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(PatternField.class.getName() + "#patternField must be an instance of " + FunctionField.class, e.getMessage());
		}
		try
		{
			CompositeField.create(Composite.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("is not a subclass of " + Composite.class.getName() + " but Composite itself", e.getMessage());
		}
		try
		{
			CompositeField.create(FinalField.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("final fields not supported: " + FinalField.class.getName() + "#finalField", e.getMessage());
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			CompositeField.create((Class)CompositeErrorTest.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("is not a subclass of " + Composite.class.getName() + ": " + CompositeErrorTest.class.getName(), e.getMessage());
		}
	}

	static class NonFinal extends Composite
	{
		private static final long serialVersionUID = 1l;
	}

	static final class NoConstructor extends Composite
	{
		private static final long serialVersionUID = 1l;
	}

	static final class NoFields extends Composite
	{
		private static final long serialVersionUID = 1l;

		private NoFields(final SetValue<?>[] setValues)
		{
			super(setValues);
		}
	}

	static final class NullField extends Composite
	{
		private static final long serialVersionUID = 1l;

		private NullField(final SetValue<?>[] setValues)
		{
			super(setValues);
		}

		static final Field<?> nullField = null;
	}

	static final class PatternField extends Composite
	{
		private static final long serialVersionUID = 1l;

		private PatternField(final SetValue<?>[] setValues)
		{
			super(setValues);
		}

		static final Feature patternField = MapField.create(new StringField(), new StringField());
	}

	static final class FinalField extends Composite
	{
		private static final long serialVersionUID = 1l;

		private FinalField(final SetValue<?>[] setValues)
		{
			super(setValues);
		}

		static final BooleanField finalField = new BooleanField().toFinal();
	}
}

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

import static com.exedio.cope.pattern.BlockType.newType;

import com.exedio.cope.Copyable;
import com.exedio.cope.Feature;
import com.exedio.cope.Field;
import com.exedio.cope.StringField;
import com.exedio.cope.junit.CopeAssert;

public class BlockErrorTest extends CopeAssert
{
	public void testNull()
	{
		try
		{
			newType(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("valueClass", e.getMessage());
		}
	}

	public void testNonFinal()
	{
		try
		{
			newType(NonFinal.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(e.getMessage(),
					"is not final: " +
					NonFinal.class.getName(), e.getMessage());
		}
	}

	public void testNoConstructor()
	{
		try
		{
			newType(NoConstructor.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(e.getMessage(),
					NoConstructor.class.getName() +
					" does not have a constructor NoConstructor(" + BlockActivationParameters.class.getName() + ")", e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
	}

	public void testNoFields()
	{
		try
		{
			newType(NoFields.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("composite has no templates", e.getMessage());
		}
	}

	public void testNullField()
	{
		try
		{
			newType(NullField.class);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(NullField.class.getName() + "#nullField", e.getMessage());
		}
	}

	public void testPatternField()
	{
		try
		{
			newType(PatternField.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(PatternField.class.getName() + "#patternField must be an instance of " + Copyable.class, e.getMessage());
		}
	}

	public void testBlockItself()
	{
		try
		{
			newType(Block.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("is not a subclass of " + Block.class.getName() + " but Block itself", e.getMessage());
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	public void testNoBlock()
	{
		try
		{
			newType((Class)BlockErrorTest.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("is not a subclass of " + Block.class.getName() + ": " + BlockErrorTest.class.getName(), e.getMessage());
		}
	}

	static class NonFinal extends Block
	{
		private static final long serialVersionUID = 1l;
		private NonFinal(final BlockActivationParameters ap) { super(ap); }
	}

	static final class NoConstructor extends Block
	{
		private static final long serialVersionUID = 1l;
		private NoConstructor() { super((BlockActivationParameters)null); }
	}

	static final class NoFields extends Block
	{
		private static final long serialVersionUID = 1l;

		private NoFields(final BlockActivationParameters ap)
		{
			super(ap);
		}
	}

	static final class NullField extends Block
	{
		private static final long serialVersionUID = 1l;

		private NullField(final BlockActivationParameters ap)
		{
			super(ap);
		}

		static final Field<?> nullField = null;
	}

	static final class PatternField extends Block
	{
		private static final long serialVersionUID = 1l;

		private PatternField(final BlockActivationParameters ap)
		{
			super(ap);
		}

		static final Feature patternField = MapField.create(new StringField(), new StringField());
	}
}

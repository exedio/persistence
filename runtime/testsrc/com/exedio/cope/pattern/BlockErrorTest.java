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

import static com.exedio.cope.pattern.BlockType.newType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Copyable;
import com.exedio.cope.Field;
import com.exedio.cope.Pattern;
import org.junit.Test;

public class BlockErrorTest
{
	@Test public void testNull()
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


	@Test public void testNonFinal()
	{
		try
		{
			newType(NonFinal.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(e.getMessage(),
					"BlockField requires a final class: " +
					NonFinal.class.getName(), e.getMessage());
		}
	}

	static class NonFinal extends Block
	{
		private static final long serialVersionUID = 1l;
		private NonFinal(final BlockActivationParameters ap) { super(ap); }
	}


	@Test public void testNoConstructor()
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
					" does not have a constructor NoConstructor(" + BlockActivationParameters.class.getName() + ")",
					e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
	}

	static final class NoConstructor extends Block
	{
		private static final long serialVersionUID = 1l;
		private NoConstructor() { super((BlockActivationParameters)null); }
	}


	@Test public void testNoFields()
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

	static final class NoFields extends Block
	{
		private static final long serialVersionUID = 1l;
		private NoFields(final BlockActivationParameters ap) { super(ap); }
	}


	@Test public void testNullField()
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

	static final class NullField extends Block
	{
		private static final long serialVersionUID = 1l;
		private NullField(final BlockActivationParameters ap) { super(ap); }
		static final Field<?> nullField = null;
	}


	@Test public void testNotCopyable()
	{
		try
		{
			newType(NotCopyableField.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					NotCopyableField.class.getName() +
					"#notCopyableField must be an instance of " + Copyable.class +
					", but was com.exedio.cope.pattern.BlockErrorTest$NotCopyable",
					e.getMessage());
		}
	}

	static final class NotCopyableField extends Block
	{
		private static final long serialVersionUID = 1l;
		private NotCopyableField(final BlockActivationParameters ap) { super(ap); }
		static final NotCopyable notCopyableField = new NotCopyable();
	}

	static final class NotCopyable extends Pattern
	{
		private static final long serialVersionUID = 1l;
	}


	@Test public void testBlockItself()
	{
		try
		{
			newType(Block.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"BlockField requires a subclass of " + Block.class.getName() +
					" but not Block itself",
					e.getMessage());
		}
	}


	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	@Test public void testNoBlock()
	{
		try
		{
			newType((Class)BlockErrorTest.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"BlockField requires a subclass of " + Block.class.getName() + ": " +
					BlockErrorTest.class.getName(),
					e.getMessage());
		}
	}


	@Test public void testAlreadyBound()
	{
		final BlockType<AlreadyBound> TYPE = AlreadyBound.TYPE;
		assertEquals(AlreadyBound.class.getName(), TYPE.toString());
		try
		{
			newType(AlreadyBound.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("class is already bound to a type: " + AlreadyBound.class.getName(), e.getMessage());
		}
	}

	static final class AlreadyBound extends Block
	{
		private static final long serialVersionUID = 1l;
		private AlreadyBound(final BlockActivationParameters ap) { super(ap); }
		static final BooleanField field = new BooleanField();
		static final BlockType<AlreadyBound> TYPE = newType(AlreadyBound.class);
	}
}

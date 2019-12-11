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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.pattern.BlockType.newType;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Copyable;
import com.exedio.cope.Field;
import com.exedio.cope.Pattern;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

public class BlockErrorTest
{
	@Test void testNull()
	{
		try
		{
			newType(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("javaClass", e.getMessage());
		}
	}


	@Test void testNonFinal()
	{
		try
		{
			newType(NonFinal.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"BlockField requires a final class: " +
					NonFinal.class.getName(), e.getMessage(),
					e.getMessage()
			);
		}
	}

	@WrapperType(type=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class NonFinal extends Block
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		protected NonFinal(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}


	@Test void testNoConstructor()
	{
		try
		{
			newType(NoConstructor.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					NoConstructor.class.getName() +
					" does not have a constructor NoConstructor(" + BlockActivationParameters.class.getName() + ")", e.getMessage(),
					e.getMessage());
			assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		}
	}

	@WrapperType(type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	static final class NoConstructor extends Block
	{
		@WrapInterim
		@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
		private NoConstructor() { super(null); }

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}


	@Test void testNoFields()
	{
		try
		{
			newType(NoFields.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("block has no templates: " + NoFields.class.getName(), e.getMessage());
		}
	}

	@WrapperType(type=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class NoFields extends Block
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private NoFields(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}


	@Test void testNullField()
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

	@WrapperIgnore // instrumentor fails on null field
	static final class NullField extends Block
	{
		private static final long serialVersionUID = 1l;
		private NullField(final BlockActivationParameters ap) { super(ap); }
		@SuppressWarnings("unused") // OK: test bad API usage
		static final Field<?> nullField = null;
	}


	@Test void testNotCopyable()
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

	@WrapperType(type=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class NotCopyableField extends Block
	{
		@SuppressWarnings("unused") // OK: test bad API usage
		static final NotCopyable notCopyableField = new NotCopyable();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private NotCopyableField(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	static final class NotCopyable extends Pattern
	{
		private static final long serialVersionUID = 1l;
	}


	@Test void testBlockItself()
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


	@SuppressWarnings("unchecked") // OK: test bad API usage
	@Test void testNoBlock()
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


	@Test void testAlreadyBound()
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

	@WrapperType(indent=2, comments=false)
	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // OK: both in instrumented and non-instrumented code
	static final class AlreadyBound extends Block
	{
		@SuppressWarnings("unused") // OK: Block must not be empty
		@WrapperIgnore
		static final BooleanField field = new BooleanField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.pattern.BlockType<AlreadyBound> TYPE = com.exedio.cope.pattern.BlockType.newType(AlreadyBound.class);

		@com.exedio.cope.instrument.Generated
		private AlreadyBound(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}
}

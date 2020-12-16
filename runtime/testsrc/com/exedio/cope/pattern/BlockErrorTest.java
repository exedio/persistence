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
import static com.exedio.cope.tojunit.Assert.assertFails;
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
	@Test void newTypeNull()
	{
		assertFails(
				() -> newType(null),
				NullPointerException.class,
				"javaClass");
	}


	@Test void newTypeNonFinal()
	{
		assertFails(
				() -> newType(NonFinal.class),
				IllegalArgumentException.class,
				"BlockField requires a final class: " + NonFinal.class.getName());
	}

	@WrapperType(type=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class NonFinal extends Block
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		protected NonFinal(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}


	@Test void newTypeNoConstructor()
	{
		final Exception e = assertFails(
				() -> newType(NoConstructor.class),
				IllegalArgumentException.class,
				NoConstructor.class.getName() +
				" does not have a constructor NoConstructor(" + BlockActivationParameters.class.getName() + ")");
		assertEquals(NoSuchMethodException.class, e.getCause().getClass());
	}

	@WrapperType(type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static final class NoConstructor extends Block
	{
		@WrapInterim
		@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
		private NoConstructor() { super(null); }

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}


	@Test void newTypeNoFields()
	{
		assertFails(
				() -> newType(NoFields.class),
				IllegalArgumentException.class,
				"block has no templates: " + NoFields.class.getName());
	}

	@WrapperType(type=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class NoFields extends Block
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private NoFields(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}


	@Test void newTypeNullField()
	{
		assertFails(
				() -> newType(NullField.class),
				NullPointerException.class,
				NullField.class.getName() + "#nullField");
	}

	@WrapperIgnore // instrumentor fails on null field
	private static final class NullField extends Block
	{
		private static final long serialVersionUID = 1l;
		private NullField(final BlockActivationParameters ap) { super(ap); }
		@SuppressWarnings("unused") // OK: test bad API usage
		static final Field<?> nullField = null;
	}


	@Test void newTypeNotCopyable()
	{
		assertFails(
				() -> newType(NotCopyableField.class),
				IllegalArgumentException.class,
				NotCopyableField.class.getName() +
				"#notCopyableField must be an instance of " + Copyable.class +
				", but was com.exedio.cope.pattern.BlockErrorTest$NotCopyable");
	}

	@WrapperType(type=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class NotCopyableField extends Block
	{
		@SuppressWarnings("unused") // OK: test bad API usage
		static final NotCopyable notCopyableField = new NotCopyable();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private NotCopyableField(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	private static final class NotCopyable extends Pattern
	{
		private static final long serialVersionUID = 1l;
	}


	@Test void newTypeBlockItself()
	{
		assertFails(
				() -> newType(Block.class),
				IllegalArgumentException.class,
				"BlockField requires a subclass of " + Block.class.getName() +
				" but not Block itself");
	}


	@SuppressWarnings({"unchecked","rawtypes"}) // OK: test bad API usage
	@Test void newTypeNoBlock()
	{
		assertFails(
				() -> newType((Class)BlockErrorTest.class),
				IllegalArgumentException.class,
				"BlockField requires a subclass of " + Block.class.getName() + ": " +
				BlockErrorTest.class.getName());
	}


	@Test void newTypeAlreadyBound()
	{
		final BlockType<AlreadyBound> TYPE = AlreadyBound.TYPE;
		assertEquals(AlreadyBound.class.getName(), TYPE.toString());
		assertFails(
				() -> newType(AlreadyBound.class),
				IllegalArgumentException.class,
				"class is already bound to a type: " + AlreadyBound.class.getName());
	}

	@WrapperType(indent=2, comments=false)
	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // OK: both in instrumented and non-instrumented code
	private static final class AlreadyBound extends Block
	{
		@SuppressWarnings("unused") // OK: Block must not be empty
		@WrapperIgnore
		static final BooleanField field = new BooleanField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.pattern.BlockType<AlreadyBound> TYPE = com.exedio.cope.pattern.BlockType.newType(AlreadyBound.class);

		@com.exedio.cope.instrument.Generated
		private AlreadyBound(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}
}

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
import static com.exedio.cope.pattern.BlockType.forClass;
import static com.exedio.cope.pattern.BlockType.forClassUnchecked;
import static com.exedio.cope.pattern.BlockType.newType;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Copyable;
import com.exedio.cope.Field;
import com.exedio.cope.Pattern;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.UsageEntryPoint;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class BlockErrorTest
{
	@Test void asNull()
	{
		assertFails(
				() -> Normal.TYPE.as(null),
				NullPointerException.class,
				"javaClass");
	}

	@WrapperType(indent=2, comments=false,
			typeSuppressWarnings="UnnecessarilyQualifiedStaticallyImportedElement")
	private static final class Normal extends Block
	{
		@SuppressWarnings("unused") // OK: must not be empty
		@WrapperIgnore static final BooleanField field = new BooleanField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement")
		private static final com.exedio.cope.pattern.BlockType<Normal> TYPE = com.exedio.cope.pattern.BlockType.newType(Normal.class,Normal::new);

		@com.exedio.cope.instrument.Generated
		private Normal(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}


	@Test void asOther()
	{
		assertFails(
				() -> Normal.TYPE.as(Other.class),
				ClassCastException.class,
				"expected " + Other.class.getName() + ", " +
				"but was " + Normal.class.getName());
	}

	@WrapperType(indent=2, comments=false,
			typeSuppressWarnings="UnnecessarilyQualifiedStaticallyImportedElement")
	private static final class Other extends Block
	{
		@SuppressWarnings("unused") // OK: must not be empty
		@WrapperIgnore static final BooleanField field = new BooleanField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement")
		private static final com.exedio.cope.pattern.BlockType<Other> TYPE = com.exedio.cope.pattern.BlockType.newType(Other.class,Other::new);

		@com.exedio.cope.instrument.Generated
		private Other(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}


	@Test void asTopClass()
	{
		assertFails(
				() -> Normal.TYPE.as(Block.class),
				ClassCastException.class,
				"expected " + Block.class.getName() + ", " +
				"but was " + Normal.class.getName());
	}


	@Test void forClassUncheckedNull()
	{
		assertFails(
				() -> forClassUnchecked(null),
				NullPointerException.class,
				"javaClass");
	}

	@Test void forClassNull()
	{
		assertFails(
				() -> forClass(null),
				NullPointerException.class,
				"javaClass");
	}


	@Test void forClassUncheckedNotExists()
	{
		assertFails(
				() -> forClassUnchecked(NotExists.class),
				IllegalArgumentException.class,
				"there is no type for class " + NotExists.class.getName());
	}

	@Test void forClassNotExists()
	{
		assertFails(
				() -> forClass(NotExists.class),
				IllegalArgumentException.class,
				"there is no type for class " + NotExists.class.getName());
	}

	@WrapperType(type=NONE, indent=2, comments=false)
	private static final class NotExists extends Block
	{
		@SuppressWarnings("unused") // OK: test bad API usage
		@WrapperIgnore static final BooleanField field = new BooleanField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private NotExists(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}


	@Test void newTypeNull()
	{
		assertFails(
				() -> newType(null, null),
				NullPointerException.class,
				"javaClass");
	}


	@Test void newTypeActivatorNull()
	{
		assertFails(
				() -> newType(ActivatorNull.class, null),
				NullPointerException.class, "activator");
		assertNotExists(ActivatorNull.class);
	}

	@WrapperType(type=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ActivatorNull extends Block
	{
		@WrapperIgnore
		@SuppressWarnings("unused") // OK: Block must not be empty
		static final StringField field = new StringField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private ActivatorNull(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}


	@Test void newTypeNonFinal()
	{
		assertFails(
				() -> newType(NonFinal.class, null),
				IllegalArgumentException.class,
				"BlockField requires a final class: " + NonFinal.class.getName());
		assertNotExists(NonFinal.class);
	}

	@WrapperType(type=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class NonFinal extends Block
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		protected NonFinal(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}


	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void newTypeNoConstructor()
	{
		final Exception e = assertFails(
				() -> newType(NoConstructor.class),
				IllegalArgumentException.class,
				NoConstructor.class.getName() +
				" does not have a constructor NoConstructor(" + BlockActivationParameters.class.getName() + ")");
		assertEquals(NoSuchMethodException.class, e.getCause().getClass());
		assertNotExists(NoConstructor.class);
	}

	@WrapperType(type=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static final class NoConstructor extends Block
	{
		@WrapInterim
		private NoConstructor() { super(null); }

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}


	@Test void newTypeNoFields()
	{
		assertFails(
				() -> newType(NoFields.class, failingActivator()),
				IllegalArgumentException.class,
				"block has no templates: " + NoFields.class.getName());
		assertNotExists(NoFields.class);
	}

	@WrapperType(type=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class NoFields extends Block
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private NoFields(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}


	@Test void newTypeNullField()
	{
		assertFails(
				() -> newType(NullField.class, failingActivator()),
				NullPointerException.class,
				NullField.class.getName() + "#nullField");
		assertNotExists(NullField.class);
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
				() -> newType(NotCopyableField.class, failingActivator()),
				IllegalArgumentException.class,
				NotCopyableField.class.getName() +
				"#notCopyableField must be an instance of " + Copyable.class +
				", but was com.exedio.cope.pattern.BlockErrorTest$NotCopyable");
		assertNotExists(NotCopyableField.class);
	}

	@WrapperType(type=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class NotCopyableField extends Block
	{
		@UsageEntryPoint // OK: test bad API usage
		static final NotCopyable notCopyableField = new NotCopyable();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
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
				() -> newType(Block.class, null),
				IllegalArgumentException.class,
				"BlockField requires a subclass of " + Block.class.getName() +
				" but not Block itself");
		assertNotExists(Block.class);
	}


	@SuppressWarnings({"unchecked","rawtypes"}) // OK: test bad API usage
	@Test void newTypeNoBlock()
	{
		assertFails(
				() -> newType((Class)BlockErrorTest.class, null),
				IllegalArgumentException.class,
				"BlockField requires a subclass of " + Block.class.getName() + ": " +
				BlockErrorTest.class.getName());
		assertNotExists((Class)BlockErrorTest.class);
	}


	@Test void newTypeAlreadyBound()
	{
		final BlockType<AlreadyBound> TYPE = AlreadyBound.TYPE;
		assertEquals(AlreadyBound.class.getName(), TYPE.toString());
		assertSame(AlreadyBound.TYPE, forClassUnchecked(AlreadyBound.class));
		assertFails(
				() -> newType(AlreadyBound.class, failingActivator()),
				IllegalArgumentException.class,
				"class is already bound to a type: " + AlreadyBound.class.getName());
		assertSame(AlreadyBound.TYPE, forClassUnchecked(AlreadyBound.class));
	}

	@WrapperType(indent=2, comments=false,
			typeSuppressWarnings="UnnecessarilyQualifiedStaticallyImportedElement")
	private static final class AlreadyBound extends Block
	{
		@SuppressWarnings("unused") // OK: Block must not be empty
		@WrapperIgnore
		static final BooleanField field = new BooleanField();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement")
		private static final com.exedio.cope.pattern.BlockType<AlreadyBound> TYPE = com.exedio.cope.pattern.BlockType.newType(AlreadyBound.class,AlreadyBound::new);

		@com.exedio.cope.instrument.Generated
		private AlreadyBound(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}


	private static void assertNotExists(final Class<? extends Block> javaClass)
	{
		assertFails(
				() -> forClassUnchecked(javaClass),
				IllegalArgumentException.class,
				"there is no type for class " + javaClass.getName());
	}

	private static <T> Function<BlockActivationParameters,T> failingActivator()
	{
		return (ap) -> { throw new AssertionFailedError(ap!=null ? ap.toString() : null); };
	}
}

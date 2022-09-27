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
import static java.lang.Integer.toHexString;
import static java.lang.System.identityHashCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class ModelNameTest
{
	private static final Model ANONYMOUS = Model.builder().add(Anonymous.TYPE).build();

	@Test void testAnonymous()
	{
		assertEquals(Model.class.getName() + '@' + toHexString(identityHashCode(ANONYMOUS)), ANONYMOUS.toString());

		ANONYMOUS.enableSerialization(ModelNameTest.class, "ANONYMOUS");
		assertEquals(ModelNameTest.class.getName() + "#ANONYMOUS", ANONYMOUS.toString());
	}


	private static final Model LITERAL = Model.builder().add(Literal.TYPE).name("literalTestName").build();

	@Test void testLiteral()
	{
		assertEquals("literalTestName", LITERAL.toString());

		LITERAL.enableSerialization(ModelNameTest.class, "LITERAL");
		assertEquals("literalTestName", LITERAL.toString());
	}


	private static final Model CLASS_NAME = Model.builder().add(ClassNameItem.TYPE).name(ClassNameName.class).build();

	@Test void testClassName()
	{
		assertEquals(ClassNameName.class.getName(), CLASS_NAME.toString());

		CLASS_NAME.enableSerialization(ModelNameTest.class, "CLASS_NAME");
		assertEquals(ClassNameName.class.getName(), CLASS_NAME.toString());
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class Anonymous extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Anonymous> TYPE = com.exedio.cope.TypesBound.newType(Anonymous.class,Anonymous::new);

		@com.exedio.cope.instrument.Generated
		protected Anonymous(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class Literal extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Literal> TYPE = com.exedio.cope.TypesBound.newType(Literal.class,Literal::new);

		@com.exedio.cope.instrument.Generated
		protected Literal(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class ClassNameItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ClassNameItem> TYPE = com.exedio.cope.TypesBound.newType(ClassNameItem.class,ClassNameItem::new);

		@com.exedio.cope.instrument.Generated
		protected ClassNameItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static class ClassNameName
	{
		// empty
	}


	@Test void testNull()
	{
		//noinspection WriteOnlyObject OK: tested for throwing exception
		final ModelBuilder b = Model.builder();
		try
		{
			b.name((String)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("name", e.getMessage());
		}
	}

	@Test void testEmpty()
	{
		//noinspection WriteOnlyObject OK: tested for throwing exception
		final ModelBuilder b = Model.builder();
		try
		{
			b.name("");
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("name must not be empty", e.getMessage());
		}
	}

	@Test void testClassNull()
	{
		final ModelBuilder b = Model.builder();
		try
		{
			b.name((Class<?>)null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
}

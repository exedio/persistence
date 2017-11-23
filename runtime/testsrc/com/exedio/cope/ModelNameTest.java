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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class ModelNameTest
{
	private static final Model ANONYMOUS = Model.builder().add(Anonymous.TYPE).build();

	@Test public void testAnonymous()
	{
		assertTrue(ANONYMOUS.toString().startsWith(Model.class.getName() + '@'), ANONYMOUS.toString());

		ANONYMOUS.enableSerialization(ModelNameTest.class, "ANONYMOUS");
		assertEquals(ModelNameTest.class.getName() + "#ANONYMOUS", ANONYMOUS.toString());
	}


	private static final Model LITERAL = Model.builder().add(Literal.TYPE).name("literalTestName").build();

	@Test public void testLiteral()
	{
		assertEquals("literalTestName", LITERAL.toString());

		LITERAL.enableSerialization(ModelNameTest.class, "LITERAL");
		assertEquals("literalTestName", LITERAL.toString());
	}


	private static final Model CLASS_NAME = Model.builder().add(ClassNameItem.TYPE).name(ClassNameName.class).build();

	@Test public void testClassName()
	{
		assertEquals(ClassNameName.class.getName(), CLASS_NAME.toString());

		CLASS_NAME.enableSerialization(ModelNameTest.class, "CLASS_NAME");
		assertEquals(ClassNameName.class.getName(), CLASS_NAME.toString());
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class Anonymous extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<Anonymous> TYPE = com.exedio.cope.TypesBound.newType(Anonymous.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected Anonymous(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class Literal extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<Literal> TYPE = com.exedio.cope.TypesBound.newType(Literal.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected Literal(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class ClassNameItem extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<ClassNameItem> TYPE = com.exedio.cope.TypesBound.newType(ClassNameItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected ClassNameItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static class ClassNameName
	{
		// empty
	}


	@Test public void testNull()
	{
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

	@Test public void testEmpty()
	{
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

	@Test public void testClassNull()
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

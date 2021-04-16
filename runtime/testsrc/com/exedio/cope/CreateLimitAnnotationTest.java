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

import static com.exedio.cope.TypesBound.newType;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class CreateLimitAnnotationTest
{
	@Test void testDefault()
	{
		assertEquals(Integer.MAX_VALUE, DefaultItem.TYPE.getCreateLimit());
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // OK: instrumented code
	private static class DefaultItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<DefaultItem> TYPE = com.exedio.cope.TypesBound.newType(DefaultItem.class);

		@com.exedio.cope.instrument.Generated
		protected DefaultItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test void testOk()
	{
		assertEquals(5, OkItem.TYPE.getCreateLimit());
	}

	@CopeCreateLimit(5)
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // OK: instrumented code
	private static class OkItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<OkItem> TYPE = com.exedio.cope.TypesBound.newType(OkItem.class);

		@com.exedio.cope.instrument.Generated
		protected OkItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test void testMinimum()
	{
		assertEquals(0, MinimumItem.TYPE.getCreateLimit());
	}

	@CopeCreateLimit(0)
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // OK: instrumented code
	private static class MinimumItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MinimumItem> TYPE = com.exedio.cope.TypesBound.newType(MinimumItem.class);

		@com.exedio.cope.instrument.Generated
		protected MinimumItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test void testLessMinimum()
	{
		try
		{
			newType(LessMinimumItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"@CopeCreateLimit of LessMinimumItem must not be negative, but was -1",
					e.getMessage());
		}
	}

	@CopeCreateLimit(-1)
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // OK: instrumented code
	private static class LessMinimumItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<LessMinimumItem> TYPE = com.exedio.cope.TypesBound.newType(LessMinimumItem.class);

		@com.exedio.cope.instrument.Generated
		protected LessMinimumItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test void testSubOk()
	{
		assertEquals(5, SubOkItem.TYPE.getCreateLimit());
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // OK: instrumented code
	private static class SubOkItem extends OkItem
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<SubOkItem> TYPE = com.exedio.cope.TypesBound.newType(SubOkItem.class);

		@com.exedio.cope.instrument.Generated
		protected SubOkItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test void testSub()
	{
		assertNotNull(SubOkItem.TYPE.getID()); // just load type
		try
		{
			newType(SubItem.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"@CopeCreateLimit is allowed on top-level types only, " +
					"but SubItem has super type OkItem",
					e.getMessage());
		}
	}

	@CopeCreateLimit(5)
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class SubItem extends OkItem
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		protected SubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

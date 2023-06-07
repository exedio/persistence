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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class EnumSingleTest
{
	@Test void testOk()
	{
		assertEquals(AnEnum.class, Ok.optional.getValueClass());
		assertEquals(false, Ok.optional.isMandatory());
		assertSame(Ok.TYPE, Ok.optional.getType());
	}

	@Test void testFails()
	{
		assertFails(
				() -> newType(Fails.class, Fails::new),
				IllegalArgumentException.class,
				"mandatory enum field is not allowed on valueClass with one enum value only: " +
				"Fails.field on " + AnEnum.class.getName());
	}

	private enum AnEnum
	{
		onlyFacet
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // OK: instrumented code
	private static final class Ok extends Item
	{
		@WrapperIgnore
		static final EnumField<AnEnum> optional = EnumField.create(AnEnum.class).optional();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Ok> TYPE = com.exedio.cope.TypesBound.newType(Ok.class,Ok::new);

		@com.exedio.cope.instrument.Generated
		private Ok(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Fails extends Item
	{
		@WrapperIgnore
		@SuppressWarnings("unused") // OK: collected by reflection
		static final EnumField<AnEnum> field = EnumField.create(AnEnum.class);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private Fails(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
